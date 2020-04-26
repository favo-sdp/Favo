package ch.epfl.favo.viewmodel;

import android.location.Location;

import androidx.lifecycle.LiveData;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.favor.Favor;

/**
 * This class is the sole dependency of all the fragments and activities.
 * It contains Util classes as members.
 */
public interface FavorDataController {
  CompletableFuture requestFavor(Favor favor);
  CompletableFuture updateFavor(
          Favor favor, boolean isRequested, int activeFavorsCountChange);

  LiveData<Map<String, Favor>> getFavorsAroundMe(Location loc, double radius); //used in map view
  LiveData<Map<String, Favor>> getFavorsAroundMe();//used in nearbylistview

  LiveData<Favor> setObservedFavor(String favorId);

  LiveData<Favor> getObservedFavor();
}
