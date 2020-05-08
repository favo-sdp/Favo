package ch.epfl.favo.viewmodel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.cache.CacheUtil;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorStatus;
import ch.epfl.favo.favor.FavorUtil;
import ch.epfl.favo.gps.FavoLocation;
import ch.epfl.favo.user.IUserUtil;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.util.PictureUtil;

import static android.graphics.BitmapFactory.decodeFile;
import static ch.epfl.favo.favor.FavorStatus.ACCEPTED;
import static ch.epfl.favo.favor.FavorStatus.CANCELLED_ACCEPTER;
import static ch.epfl.favo.favor.FavorStatus.CANCELLED_REQUESTER;
import static ch.epfl.favo.favor.FavorStatus.COMPLETED_ACCEPTER;
import static ch.epfl.favo.favor.FavorStatus.COMPLETED_REQUESTER;
import static ch.epfl.favo.favor.FavorStatus.REQUESTED;
import static ch.epfl.favo.favor.FavorStatus.SUCCESSFULLY_COMPLETED;

@SuppressLint("NewApi")
public class FavorViewModel extends ViewModel implements IFavorViewModel {
  private String TAG = "FIRESTORE_VIEW_MODEL";

  private boolean showFavor = false;
  private Location mCurrentLocation;
  private double mRadius = -1.0;

  private MutableLiveData<Map<String, Favor>> activeFavorsAroundMe = new MutableLiveData<>();

  MutableLiveData<Favor> observedFavor = new MutableLiveData<>();
  // MediatorLiveData<Favor> observedFavor = new MediatorLiveData<>();

  public FavorUtil getFavorRepository() {
    return DependencyFactory.getCurrentFavorRepository();
  }

  public IUserUtil getUserRepository() {
    return DependencyFactory.getCurrentUserRepository();
  }

  private PictureUtil getPictureUtility() {
    return DependencyFactory.getCurrentPictureUtility();
  }

  private CacheUtil getCacheUtility() { return DependencyFactory.getCurrentCacheUtility(); }

  /**
   * Tries to update the number of active favors for a given user. Detailed implementation in
   * UserUtil
   *
   * @param isRequested is/are the favors requested?
   * @param change number of favors being updated
   * @returnf
   */
  private CompletableFuture<Void> changeUserActiveFavorCount(
      String userId, boolean isRequested, int change) {
    return getUserRepository().changeActiveFavorCount(userId, isRequested, change);
  }
  /**
   * Checks if it's possible for user to update, if so, updates his/her status. Then posts favor to
   * DB
   *
   * @param favor should be a clone of the actual object
   * @param isRequested is the favor requested?
   * @param activeFavorsCountChange Relative to actual amount
   * @return
   */
  private CompletableFuture updateFavorForCurrentUser(
      Favor favor, boolean isRequested, int activeFavorsCountChange) {
    return changeUserActiveFavorCount(
            DependencyFactory.getCurrentFirebaseUser().getUid(),
            isRequested,
            activeFavorsCountChange)
        .thenCompose(o -> getFavorRepository().updateFavor(favor));
  }

  // save address to firebase
  @Override
  public CompletableFuture requestFavor(Favor favor) {
    Favor tempFavor = (Favor) favor.clone();
    tempFavor.setStatusIdToInt(REQUESTED);
    return changeUserActiveFavorCount(
            DependencyFactory.getCurrentFirebaseUser().getUid(),
            true,
            1) // if user can request favor then post it in the favor collection
        .thenCompose((f) -> getFavorRepository().requestFavor(tempFavor));
  }

  public CompletableFuture cancelFavor(final Favor favor, boolean isRequested) {
    Favor tempFavor = (Favor) favor.clone();
    FavorStatus cancelledStatus = isRequested ? CANCELLED_REQUESTER : CANCELLED_ACCEPTER;
    String otherUserId = isRequested ? tempFavor.getAccepterId() : tempFavor.getRequesterId();
    tempFavor.setStatusIdToInt(cancelledStatus);
    CompletableFuture resultFuture = updateFavorForCurrentUser(tempFavor, isRequested, -1);
    if (otherUserId != null) // update other user
    {
      resultFuture.thenCompose(o -> changeUserActiveFavorCount(otherUserId, !isRequested, -1));
    }

    return resultFuture;
  }

  public CompletableFuture reEnableFavor(final Favor favor) {
    Favor tempFavor = (Favor) favor.clone();
    int countUpdate = (tempFavor.getIsArchived()) ? 1 : 0;
    tempFavor.setStatusIdToInt(REQUESTED);
    return updateFavorForCurrentUser(tempFavor, true, countUpdate);
  }

  public CompletableFuture completeFavor(final Favor favor, boolean isRequested) {
    Favor tempFavor = (Favor) favor.clone();
    if ((tempFavor.getStatusId() == COMPLETED_ACCEPTER.toInt())
        || (tempFavor.getStatusId() == COMPLETED_REQUESTER.toInt()))
      tempFavor.setStatusIdToInt(SUCCESSFULLY_COMPLETED);
    else if (tempFavor.getStatusId() == ACCEPTED.toInt()) {
      tempFavor.setStatusIdToInt(isRequested ? COMPLETED_REQUESTER : COMPLETED_ACCEPTER);
    } else { // not sure if this will be wrapped by completablefuture
      throw new IllegalStateException();
    }
    return updateFavorForCurrentUser(tempFavor, isRequested, -1);
  }

  /**
   * @param favor should be a clone of the original object in the UI!
   * @return
   */
  public CompletableFuture acceptFavor(final Favor favor) {
    Favor tempFavor = (Favor) favor.clone();
    tempFavor.setAccepterId(DependencyFactory.getCurrentFirebaseUser().getUid());
    tempFavor.setStatusIdToInt(ACCEPTED);
    return updateFavorForCurrentUser(tempFavor, false, 1);
  }

  public CompletableFuture deleteFavor(final Favor favor) {
    CompletableFuture removeFavorFuture = getFavorRepository().removeFavor(favor.getId());
    if (favor.getPictureUrl() != null) {
      removeFavorFuture.thenCompose(o -> getPictureUtility().deletePicture(favor.getPictureUrl()));
    }
    return removeFavorFuture;
  }

  // Upload/download pictures
  @Override
  public void uploadOrUpdatePicture(Favor favor, Bitmap picture) {
    CompletableFuture<String> pictureUrl = getPictureUtility().uploadPicture(picture);
    pictureUrl.thenAccept(url -> getFavorRepository().updateFavorPhoto(favor, url));
  } // check what happens if updateFavorFoto fails

  @Override
  public CompletableFuture<Bitmap> downloadPicture(Favor favor) throws RuntimeException {
    String url = favor.getPictureUrl();
    if (url == null) {
      return new CompletableFuture<Bitmap>() {
        {
          completeExceptionally(new RuntimeException("Invalid picture url in Favor"));
        }
      };
    } else {
      return getPictureUtility().downloadPicture(url);
    }
  }

  // Save/load pictures from local storage
  @Override
  public void savePictureToLocal(Context context, Favor favor, Bitmap picture) {
    getCacheUtility().saveToInternalStorage(context, picture, favor.getId(), 0);
  }

  @Override
  public CompletableFuture<Bitmap> loadPictureFromLocal(Context context, Favor favor) {
    String baseDir = context.getFilesDir().getAbsolutePath();
    String favorId = favor.getId();
    String pathToFolder = baseDir + "/" + favorId + "/";
    return getCacheUtility().loadFromInternalStorage(pathToFolder, 0);
  }

  @Override
  public LiveData<Map<String, Favor>> getFavorsAroundMe(Location loc, double radiusInKm) {
    if (mCurrentLocation == null) mCurrentLocation = loc;
    if (mRadius == -1) mRadius = radiusInKm;
    if (activeFavorsAroundMe.getValue() == null
        || (mCurrentLocation.distanceTo(loc)) > 1000 * radiusInKm) {
      getFavorRepository()
          .getNearbyFavors(loc, radiusInKm)
          .addSnapshotListener(
              MetadataChanges.EXCLUDE,
              (queryDocumentSnapshots, e) ->
                  activeFavorsAroundMe.postValue(
                      getNearbyFavorsFromQuery(loc, radiusInKm, queryDocumentSnapshots, e)));
    }
    return getFavorsAroundMe();
  }

  @Override
  public LiveData<Map<String, Favor>> getFavorsAroundMe() {
    return activeFavorsAroundMe;
  }

  public Map<String, Favor> getNearbyFavorsFromQuery(
      Location loc,
      double radius,
      QuerySnapshot queryDocumentSnapshots,
      FirebaseFirestoreException e) {
    handleException(e);
    List<Favor> favorsList = queryDocumentSnapshots.toObjects(Favor.class);

    Map<String, Favor> favorsMap = new HashMap<>();
    // Filter latitude because Firebase only filters longitude
    double latDif = Math.toDegrees(radius / FavoLocation.EARTH_RADIUS);
    for (Favor favor : favorsList) {
      if (!favor.getRequesterId().equals(DependencyFactory.getCurrentFirebaseUser().getUid())
          && favor.getStatusId() == REQUESTED.toInt()
          && favor.getLocation().getLatitude() > loc.getLatitude() - latDif
          && favor.getLocation().getLatitude() < loc.getLatitude() + latDif) {
        favorsMap.put(favor.getId(), favor);
      }
    }
    return favorsMap;
  }

  public void handleException(FirebaseFirestoreException e) {
    if (e != null) {
      Log.w(TAG, "Listen Failed", e);
      throw new RuntimeException(e.getMessage());
    }
  }

  @Override
  public LiveData<Favor> setObservedFavor(String favorId) {
    getFavorRepository()
        .getFavorReference(favorId)
        .addSnapshotListener(
            MetadataChanges.EXCLUDE,
            (documentSnapshot, e) -> {
              handleException(e);
              assert documentSnapshot != null;
              setFavorValue(documentSnapshot.toObject(Favor.class));
            });
    return getObservedFavor();
  }

  @Override
  public void setFavorValue(Favor favor) {
    observedFavor.setValue(favor);
  }

  @Override
  public LiveData<Favor> getObservedFavor() {
    return observedFavor;
  }

  @Override
  public void setShowObservedFavor(Boolean show) {
    showFavor = show;
  }

  @Override
  public boolean isShowObservedFavor() {
    return showFavor;
  }
}
