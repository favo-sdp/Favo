package ch.epfl.favo.viewmodel;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.favor.Favor;

public interface FavorDataController {
  // save address to firebase
  CompletableFuture postFavor(Favor favor);
  CompletableFuture updateFavor(Favor favor);

  LiveData<Map<String, Favor>> getFavorsAroundMe(Location loc, double radius); //used in map view
  LiveData<Map<String, Favor>> getFavorsAroundMe();//used in nearbylistview

  LiveData<Favor> setObservedFavor(String favorId);
  MutableLiveData<Favor> getObservedFavor();

  void setShowObservedFavor(Boolean show);
  boolean isShowObservedFavor();
}
