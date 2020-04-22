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

import ch.epfl.favo.common.FavoLocation;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorStatus;
import ch.epfl.favo.favor.FavorUtil;
import ch.epfl.favo.util.DependencyFactory;

public class FavorViewModel extends ViewModel {
  String TAG = "FIRESTORE_VIEW_MODEL";
  FavorUtil favorRepository = FavorUtil.getSingleInstance();

  MutableLiveData<Map<String, Favor>> myActiveFavors = new MutableLiveData<>();
  MutableLiveData<Map<String, Favor>> myPastFavors = new MutableLiveData<>();
  MutableLiveData<Map<String, Favor>> activeFavorsAroundMe = new MutableLiveData<>();


  // MutableLiveData<Favor> observedFavor = new MutableLiveData<>();
  MediatorLiveData<Favor> observedFavor = new MediatorLiveData<>();


  // save address to firebase
  public CompletableFuture postFavor(Favor favor) {
    return favorRepository.postFavor(favor);
  }

  public LiveData<Map<String, Favor>> getFavorsAroundMe(Location loc, double radius) {
    favorRepository
        .getNearbyFavors(loc, radius)
        .addSnapshotListener(
            (queryDocumentSnapshots, e) -> {
              try {
                if (e != null) throw new RuntimeException(e.getMessage());
                Map<String, Favor> favors = new HashMap<>();
                // Filter latitude because Firebase only filters longitude
                double latDif = Math.toDegrees(radius / FavoLocation.EARTH_RADIUS);
                for (Favor favor : queryDocumentSnapshots.toObjects(Favor.class))
                  if (!favor
                          .getRequesterId()
                          .equals(DependencyFactory.getCurrentFirebaseUser().getUid())
                      && favor.getStatusId() == FavorStatus.REQUESTED.toInt()
                      && favor.getLocation().getLatitude() > loc.getLatitude() - latDif
                      && favor.getLocation().getLatitude() < loc.getLatitude() + latDif) {
                    favors.put(favor.getId(), favor);
                  }
                activeFavorsAroundMe.setValue(favors);
              } catch (Exception ex) {
                Log.d(TAG, ex.getMessage());
              }
            });
    return activeFavorsAroundMe;
  }

  public void handleException(FirebaseFirestoreException e) {
    if (e != null) {
      Log.w(TAG, "Listen Failed", e);
      throw new RuntimeException(e.getMessage());
    }
  }

  public LiveData<Map<String, Favor>> getMyActiveFavors() {
    favorRepository
        .retrieveAllActiveFavorsForGivenUser(DependencyFactory.getCurrentFirebaseUser().getUid())
        .addSnapshotListener(
            (queryDocumentSnapshots, e) -> {
              myActiveFavors.setValue(getFavorMapFromQuery(queryDocumentSnapshots, e));
            });
    return myActiveFavors;
  }

  public LiveData<Map<String, Favor>> getMyPastFavors() {
    favorRepository
        .retrieveAllPastFavorsForGivenUser(DependencyFactory.getCurrentFirebaseUser().getUid())
        .addSnapshotListener(
            (queryDocumentSnapshots, e) ->
                myPastFavors.setValue(getFavorMapFromQuery(queryDocumentSnapshots, e)));
    return myPastFavors;
  }

  private Map<String, Favor> getFavorMapFromQuery(
      QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
    handleException(e);

    List<Favor> favors = queryDocumentSnapshots.toObjects(Favor.class);
    Map<String, Favor> favorMap =
        new HashMap<String, Favor>() {
          {
            for (Favor favor : favors) {
              put(favor.getId(), favor);
            }
          }
        };

    return favorMap;
  }

  public LiveData<Favor> setObservedFavor(String favorId) {
    favorRepository
            .getFavorReference(favorId)
            .addSnapshotListener(
                    (documentSnapshot, e) -> {
                      if (e != null) {
                        Log.w(TAG, "Listen Failed", e);
                        observedFavor = null;
                        return;
                      }
                      // observedFavor.postValue(documentSnapshot.toObject(Favor.class));
                      observedFavor.setValue(documentSnapshot.toObject(Favor.class));
                    });
    return observedFavor;
  }

  public LiveData<Favor> getObservedFavor() {
    return observedFavor;
  }
}
