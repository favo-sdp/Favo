package ch.epfl.favo.viewmodel;

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

import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorStatus;
import ch.epfl.favo.favor.FavorUtil;
import ch.epfl.favo.gps.FavoLocation;
import ch.epfl.favo.util.DependencyFactory;

public class FavorViewModel extends ViewModel implements FavorDataController {
  private String TAG = "FIRESTORE_VIEW_MODEL";

  private boolean showFavor = false;

  private MutableLiveData<Map<String, Favor>> activeFavorsAroundMe = new MutableLiveData<>();

  // MutableLiveData<Favor> observedFavor = new MutableLiveData<>();
  private MediatorLiveData<Favor> observedFavor = new MediatorLiveData<>();

  private FavorUtil getRepository() {
    return DependencyFactory.getCurrentRepository();
  }

  // save address to firebase
  @Override
  public CompletableFuture postFavor(Favor favor) {
    return getRepository().postFavor(favor);
  }

  @Override
  public CompletableFuture updateFavor(Favor favor) {
    return getRepository().updateFavor(favor);
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
  public void setObservedFavorLocally(Favor favor) {
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
