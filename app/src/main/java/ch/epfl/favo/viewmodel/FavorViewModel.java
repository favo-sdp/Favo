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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.cache.CacheUtil;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorStatus;
import ch.epfl.favo.favor.IFavorUtil;
import ch.epfl.favo.gps.FavoLocation;
import ch.epfl.favo.user.IUserUtil;
import ch.epfl.favo.user.User;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.util.IPictureUtil;
import ch.epfl.favo.util.IPictureUtil.Folder;

import static ch.epfl.favo.favor.FavorStatus.ACCEPTED;
import static ch.epfl.favo.favor.FavorStatus.CANCELLED_ACCEPTER;
import static ch.epfl.favo.favor.FavorStatus.CANCELLED_REQUESTER;
import static ch.epfl.favo.favor.FavorStatus.COMPLETED_ACCEPTER;
import static ch.epfl.favo.favor.FavorStatus.COMPLETED_REQUESTER;
import static ch.epfl.favo.favor.FavorStatus.REQUESTED;
import static ch.epfl.favo.favor.FavorStatus.SUCCESSFULLY_COMPLETED;

@SuppressLint("NewApi")
public class FavorViewModel extends ViewModel implements IFavorViewModel {
  private String currentUserId = DependencyFactory.getCurrentFirebaseUser().getUid();

  private boolean showFavor = false;
  private Location mCurrentLocation;
  private double mRadius = -1.0;
  private int numActiveRequestedFavor = -1;
  private int numActiveAcceptedFavor = -1;
  private static final String InvalidPicUrl = "Invalid picture url in Favor";

  private MutableLiveData<Map<String, Favor>> activeFavorsAroundMe = new MutableLiveData<>();

  private MutableLiveData<Favor> observedFavor = new MutableLiveData<>();
  private MutableLiveData<User> observedUser = new MutableLiveData<>();

  private IFavorUtil getFavorRepository() {
    return DependencyFactory.getCurrentFavorRepository();
  }

  private IUserUtil getUserRepository() {
    return DependencyFactory.getCurrentUserRepository();
  }

  private IPictureUtil getPictureUtility() {
    return DependencyFactory.getCurrentPictureUtility();
  }

  /**
   * Update favor to DB
   *
   * @param favor should be a clone of the actual object
   * @return
   */
  private CompletableFuture<Void> updateFavorForCurrentUser(Favor favor) {
    return getFavorRepository().updateFavor(favor);
  }

  @Override
  public CompletableFuture<Void> commitFavor(Favor favor, boolean isCancel) {
    Favor tempFavor = new Favor(favor);
    if (isCancel) tempFavor.getUserIds().remove(currentUserId);
    else tempFavor.setAccepterId(currentUserId);
    return updateFavorForCurrentUser(tempFavor);
  }

  // save address to firebase
  @Override
  public CompletableFuture<Void> requestFavor(final Favor favor, int change) {
    Favor tempFavor = new Favor(favor);
    tempFavor.setStatusIdToInt(REQUESTED);
    setFavorValue(tempFavor);
    // if the favor has been observed, then this is during edit flow, do not add 1.
    return getFavorRepository()
        .requestFavor(tempFavor)
        .thenCompose(
            avoid ->
                getUserRepository().incrementFieldForUser(currentUserId, User.REQUESTED_FAVORS, change))
            .thenCompose(
                    (aVoid) ->
                            getUserRepository()
                                    .updateCoinBalance(tempFavor.getUserIds().get(0), -tempFavor.getReward()));
  }

  public CompletableFuture<Void> cancelFavor(final Favor favor, boolean isRequested) {
    Favor tempFavor = new Favor(favor);
    FavorStatus cancelledStatus = isRequested ? CANCELLED_REQUESTER : CANCELLED_ACCEPTER;
    tempFavor.setStatusIdToInt(cancelledStatus);
    // if favor is in requested status, then clear the list of committed helpers, so their
    // archived favors will not counted in this favor
    getUserRepository().updateCoinBalance(tempFavor.getRequesterId(), favor.getReward());
    if (favor.getStatusId() == REQUESTED.toInt()) favor.setAccepterId("");
    return updateFavorForCurrentUser(tempFavor);
  }

  public CompletableFuture<Void> completeFavor(final Favor favor, boolean isRequested) {
    Favor tempFavor = new Favor(favor);
    if ((tempFavor.getStatusId() == COMPLETED_ACCEPTER.toInt())
        || (tempFavor.getStatusId() == COMPLETED_REQUESTER.toInt())) {
      tempFavor.setStatusIdToInt(SUCCESSFULLY_COMPLETED);
      if (tempFavor.getUserIds().size() > 1) {
        getUserRepository().updateCoinBalance(tempFavor.getAccepterId(), tempFavor.getReward());
      }
    } else if (tempFavor.getStatusId() == ACCEPTED.toInt()) {
      tempFavor.setStatusIdToInt(isRequested ? COMPLETED_REQUESTER : COMPLETED_ACCEPTER);
    }
    return updateFavorForCurrentUser(tempFavor)
            .thenCompose(
            avoid ->
                    getUserRepository().incrementFieldForUser(currentUserId, User.COMPLETED_FAVORS, 1));
  }

  /**
   * @param favor
   * @param user could be requester
   */
  public CompletableFuture<Void> acceptFavor(final Favor favor, User user) {
    Favor tempFavor = new Favor(favor);
    tempFavor.setAccepterId(user.getId());
    tempFavor.setStatusIdToInt(ACCEPTED);
    return getFavorRepository()
        .updateFavor(tempFavor)
            .thenCompose(
                    avoid ->
                            getUserRepository().incrementFieldForUser(user.getId(), User.ACCEPTED_FAVORS, 1));
  }

  public CompletableFuture<Void> deleteFavor(final Favor favor) {
    CompletableFuture<Void> removeFavorFuture = getFavorRepository().removeFavor(favor.getId());
    if (favor.getPictureUrl() != null) {
      removeFavorFuture.thenCompose(
          (aVoid) -> getPictureUtility().deletePicture(favor.getPictureUrl()));
    }
    return removeFavorFuture;
  }

  // Upload/download pictures
  @Override
  public CompletableFuture<Void> uploadOrUpdatePicture(Favor favor, Bitmap picture) {
    CompletableFuture<String> pictureUrl = getPictureUtility().uploadPicture(Folder.FAVOR, picture);
    return pictureUrl.thenAccept(url -> getFavorRepository().updateFavorPhoto(favor, url));
  }

  @Override
  public CompletableFuture<Bitmap> downloadPicture(Favor favor) {
    String url = favor.getPictureUrl();
    if (url == null) {
      return new CompletableFuture<Bitmap>() {
        {
          completeExceptionally(new RuntimeException(InvalidPicUrl));
        }
      };
    } else {
      return getPictureUtility().downloadPicture(url);
    }
  }

  @Override
  public void ObserveAllUserActiveFavorsAndCurrentUser() {
    getUserRepository()
            .getUserReference(currentUserId)
            .addSnapshotListener(
                    MetadataChanges.INCLUDE,
                    (documentSnapshot, e) -> {
                      handleException(e);
                      observedUser.setValue(documentSnapshot.toObject(User.class));
                    });

    getFavorRepository()
        .getAllUserFavors(currentUserId)
        .whereEqualTo(Favor.IS_ARCHIVED, false)
        .addSnapshotListener(
            MetadataChanges.EXCLUDE,
            (queryDocumentSnapshots, e) -> {
              handleException(e);
              List<Favor> favorsList = queryDocumentSnapshots.toObjects(Favor.class);
              numActiveRequestedFavor = 0;
              for (Favor favor : favorsList) {
                if (favor.getRequesterId().equals(currentUserId)) numActiveRequestedFavor += 1;
              }
              numActiveAcceptedFavor = favorsList.size() - numActiveRequestedFavor;
            });
  }

  @Override
  public int getActiveAcceptedFavors() {
    return numActiveAcceptedFavor;
  }

  @Override
  public int getActiveRequestedFavors() {
    return numActiveRequestedFavor;
  }

  @Override
  public LiveData<Map<String, Favor>> getFavorsAroundMe(Location loc, double radiusInKm) {
    if (mCurrentLocation == null) mCurrentLocation = loc;
    if (mRadius == -1) mRadius = radiusInKm;
      getFavorRepository()
          .getLongitudeBoundedFavorsAroundMe(loc, radiusInKm)
          .addSnapshotListener(
              MetadataChanges.EXCLUDE,
              (queryDocumentSnapshots, e) ->
                  activeFavorsAroundMe.postValue(
                      getNearbyFavorsFromQuery(loc, radiusInKm, queryDocumentSnapshots, e)));
    return getFavorsAroundMe();
  }

  @Override
  public LiveData<Map<String, Favor>> getFavorsAroundMe() {
    return activeFavorsAroundMe;
  }

  Map<String, Favor> getNearbyFavorsFromQuery(
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
      if (favor.getRequesterId() != null
          && !favor.getUserIds().contains(currentUserId)
          && favor.getStatusId() == REQUESTED.toInt()
          && favor.getLocation().getLatitude() > loc.getLatitude() - latDif
          && favor.getLocation().getLatitude() < loc.getLatitude() + latDif) {
        favorsMap.put(favor.getId(), favor);
      }
    }
    return favorsMap;
  }

  void handleException(FirebaseFirestoreException e) {
    if (e != null) {
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
  public LiveData<User> getObservedUser() {
    return observedUser;
  }

  @Override
  public void setShowObservedFavor(Boolean show) {
    showFavor = show;
  }

  private CacheUtil getCacheUtility() {
    return DependencyFactory.getCurrentCacheUtility();
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
  public boolean showsObservedFavor() {
    return showFavor;
  }
}
