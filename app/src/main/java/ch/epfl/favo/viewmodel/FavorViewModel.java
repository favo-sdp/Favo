package ch.epfl.favo.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorUtil;
import ch.epfl.favo.util.DependencyFactory;

public class FavorViewModel extends ViewModel {
  String TAG = "FIRESTORE_VIEW_MODEL";
  FavorUtil favorRepository = FavorUtil.getSingleInstance();
  MutableLiveData<Map<String, Favor>> myActiveFavors = new MutableLiveData<>();
  MutableLiveData<Map<String, Favor>> myPastFavors = new MutableLiveData<>();
  MutableLiveData<List<Favor>> myActiveFavorList = new MutableLiveData<>();

  // save address to firebase
  public void postFavor(Favor favor) {
    favorRepository.postFavor(favor);
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
            (queryDocumentSnapshots, e) -> {
              myPastFavors.setValue(getFavorMapFromQuery(queryDocumentSnapshots, e));
            });
    return myPastFavors;
  }

  private Map<String, Favor> getFavorMapFromQuery(
      QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
    if (e != null) {
      Log.w(TAG, "Listen Failed", e);
      return new HashMap<>();
    }

    List<Favor> favors = queryDocumentSnapshots.toObjects(Favor.class);
    Map<String, Favor> favorMap = new HashMap<String,Favor>(){{
      for (Favor favor : favors) {
        put(favor.getId(), favor);
      }
    }};

    return favorMap;
  }
}

//  // get realtime updates from firebase regarding saved addresses
//  fun getSavedAddresses(): LiveData<List<AddressItem>>{
//    firebaseRepository.getSavedAddress().addSnapshotListener(EventListener<QuerySnapshot> { value,
// e ->
//    if (e != null) {
//      Log.w(TAG, "Listen failed.", e)
//      savedAddresses.value = null
//      return@EventListener
//    }
//
//    var savedAddressList : MutableList<AddressItem> = mutableListOf()
//    for (doc in value!!) {
//      var addressItem = doc.toObject(AddressItem::class.java)
//      savedAddressList.add(addressItem)
//    }
//    savedAddresses.value = savedAddressList
//        })
//
//    return savedAddresses
//  }
//
//  // delete an address from firebase
//  fun deleteAddress(addressItem: AddressItem){
//    firebaseRepository.deleteAddress(addressItem).addOnFailureListener {
//      Log.e(TAG,"Failed to delete Address")
//    }
