package ch.epfl.favo.viewmodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;

import androidx.lifecycle.LiveData;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.user.User;

/**
 * This class is the sole dependency of all the fragments and activities. It contains Util classes
 * as members.
 */
public interface IFavorViewModel {

  CompletableFuture<Void> requestFavor(final Favor favor, int change);

  // Upload/download pictures
  void uploadOrUpdatePicture(Favor favor, Bitmap picture);

  CompletableFuture<Void> commitFavor(Favor favor, boolean isCancelled);

  CompletableFuture<Void> acceptFavor(final Favor favor, User user);

  CompletableFuture<Void> completeFavor(final Favor favor, boolean isRequested);

  CompletableFuture<Void> cancelFavor(final Favor favor, boolean isRequested);

  CompletableFuture<Void> deleteFavor(final Favor favor);

  CompletableFuture<Bitmap> downloadPicture(Favor favor);

  LiveData<Map<String, Favor>> getFavorsAroundMe(Location loc, double radius); // used in map view

  LiveData<Map<String, Favor>> getFavorsAroundMe(); // used in nearbylistview

  LiveData<Favor> setObservedFavor(String favorId);

  LiveData<Favor> getObservedFavor();

  void setFavorValue(Favor favor);

  void setShowObservedFavor(Boolean show);

  boolean isShowObservedFavor();

  // Save/load pictures
  void savePictureToLocal(Context context, Favor favor, Bitmap picture);

  CompletableFuture<Bitmap> loadPictureFromLocal(Context context, Favor favor);
}
