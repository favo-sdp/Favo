package ch.epfl.favo.viewmodel;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.common.FavoLocation;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorStatus;
import ch.epfl.favo.favor.FavorUtil;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.util.PictureUtil;

@SuppressLint("NewApi")
public class FavorViewModel extends ViewModel implements FavorDataController {
  String TAG = "FIRESTORE_VIEW_MODEL";

  private MutableLiveData<Map<String, Favor>> activeFavorsAroundMe = new MutableLiveData<>();

  // MutableLiveData<Favor> observedFavor = new MutableLiveData<>();
  private MediatorLiveData<Favor> observedFavor = new MediatorLiveData<>();

  private FavorUtil getRepository() {
    return DependencyFactory.getCurrentRepository();
  }

  private PictureUtil getPictureUtility() {
    return DependencyFactory.getCurrentPictureUtility();
  }

  // save address to firebase
  @Override
  public CompletableFuture postFavor(Favor favor) {
    return getRepository().postFavor(favor);
  }

  public CompletableFuture updateFavor(Favor favor) {
    return getRepository().updateFavor(favor);
  }

  // Upload/download pictures
  @Override
  public void uploadOrUpdatePicture(Favor favor, Bitmap picture) {
    CompletableFuture<String> pictureUrl = getPictureUtility().uploadPicture(picture);
    pictureUrl.thenAccept(url -> FavorUtil.getSingleInstance().updateFavorPhoto(favor, url));
  }

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

  @Override
  public LiveData<Map<String, Favor>> getFavorsAroundMe(Location loc, double radius) {
    getRepository()
        .getNearbyFavors(loc, radius)
        .addSnapshotListener(
            (queryDocumentSnapshots, e) ->
                activeFavorsAroundMe.setValue(
                    getNearbyFavorsFromQuery(loc, radius, queryDocumentSnapshots, e)));
    return getFavorsAroundMe();
  }

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
      if (favor.getRequesterId() != null
          && !favor.getRequesterId().equals(DependencyFactory.getCurrentFirebaseUser().getUid())
          && favor.getStatusId() == FavorStatus.REQUESTED.toInt()
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
    getRepository()
        .getFavorReference(favorId)
        .addSnapshotListener(
            (documentSnapshot, e) -> {
              handleException(e);
              observedFavor.setValue(documentSnapshot.toObject(Favor.class));
            });
    return observedFavor;
  }

  @Override
  public LiveData<Favor> getObservedFavor() {
    return observedFavor;
  }
}
