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
 * This class tries to use all repositories and services into single methods that can be called from
 * the view.
 */
public interface IFavorViewModel {
  /**
   * Posts favor in the favor collection. Updates user requested favor count Updates user active
   * requested favors Could be called when favor is edited .
   *
   * @param favor favor that is requested
   * @param change Will update the count depending on this value
   * @return future that can be completed
   */
  CompletableFuture<Void> requestFavor(Favor favor, int change);

  /**
   * Uploads picture to firebase storage Assigns url to favor object
   *
   * @param favor
   * @param picture
   * @return
   */
  CompletableFuture<Void> uploadOrUpdatePicture(Favor favor, Bitmap picture);

  /**
   * User can commit to a request. Requester then needs to accept the user who commited. This
   * function allows user to commit to another's requested favor. It also allows user to remove
   * commit. When user commits, their active accepted favors is increased by 1.
   *
   * @param favor
   * @param isCancelled
   * @return
   */
  CompletableFuture<Void> commitFavor(Favor favor, boolean isCancelled);

  /**
   * Requester finally chooses one of the users who commited
   *
   * @param favor
   * @param user
   * @return
   */
  CompletableFuture<Void> acceptFavor(final Favor favor, User user);

  /**
   * Requester or helper can complete a favor. If favor is already completed by the other party,
   * then completing it will update the favor status to successfully completed.
   *
   * @param favor favor to be completed
   * @param isRequested is favor requested?
   * @return completablefuture
   */
  CompletableFuture<Void> completeFavor(Favor favor, boolean isRequested);

  /**
   * Requester or helper can cancel a favor.
   *
   * @param favor favor to be cancelled
   * @param isRequested is favor requested?
   * @return completable future
   */
  CompletableFuture<Void> cancelFavor(Favor favor, boolean isRequested);

  /**
   * Delete favor only if favor is requested with no users commited.
   *
   * @param favor favor to be deleted from firestore
   * @return completable future
   */
  CompletableFuture<Void> deleteFavor(final Favor favor);

  /**
   * Downloads the picture associated to the favor
   *
   * @param favor containing url
   * @return future with bitmap object
   */
  CompletableFuture<Bitmap> downloadPicture(Favor favor);

  /**
   * Get longitude and latitude bounded active favors according to radius in kms
   *
   * @param loc
   * @param radius value in kms
   * @return livedata object containing map with id and favor values
   */
  LiveData<Map<String, Favor>> getFavorsAroundMe(Location loc, double radius); // used in map view

  /**
   * Returns longitude and latitude bounded active favors according to radius in kms already queried
   * before. This just returns the current value.
   *
   * @return current favors around values.
   */
  LiveData<Map<String, Favor>> getFavorsAroundMe(); // used in nearbylistview


  /**
   * Listen for current User and all his active favors, this is used for
   * get user information from db, and calculate active accepted favor and requested favors locally
   *
   */
  void ObserveAllUserActiveFavorsAndCurrentUser();

  int getActiveAcceptedFavors();

  int getActiveRequestedFavors();

  /**
   * Queries firestore for favor object with id. S
   *
   * @param favorId id of observed favor
   * @return observed favor
   */
  LiveData<Favor> setObservedFavor(String favorId);

  /**
   * Gets currently observed favor requested in the setter
   *
   * @return observed favor
   */
  LiveData<Favor> getObservedFavor();

  User getOwnUser();

  /**
   * Set observed favor value on UI thread
   *
   * @param favor object to be set.
   */
  void setFavorValue(Favor favor);

  /**
   * Set boolean object that allows user to observe favor in map page.
   *
   * @param show
   */
  void setShowObservedFavor(Boolean show);

  /**
   * Check whether favor is currently being shown.
   *
   * @return whether favor is observed
   */
  boolean showsObservedFavor();

  /**
   * Caches picture
   *
   * @param context
   * @param favor
   * @param picture
   */
  void savePictureToLocal(Context context, Favor favor, Bitmap picture);

  /**
   * Gets picture from local.
   *
   * @param context
   * @param favor
   * @return
   */
  CompletableFuture<Bitmap> loadPictureFromLocal(Context context, Favor favor);
}
