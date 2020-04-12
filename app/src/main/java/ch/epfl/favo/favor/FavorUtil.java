package ch.epfl.favo.favor;

import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.common.DatabaseUpdater;
import ch.epfl.favo.common.FavorNoLongerAvailableException;
import ch.epfl.favo.common.NotImplementedException;
import ch.epfl.favo.user.UserUtil;
import ch.epfl.favo.util.DependencyFactory;

/*
This models the favor request.
*/
public class FavorUtil {
  private static final String TAG = "FavorUtil";
  private static final String FAVOR_COLLECTION = "favors";
  private static final FavorUtil SINGLE_INSTANCE = new FavorUtil();
  private static DatabaseUpdater collection =
      DependencyFactory.getCurrentCollectionWrapper("favors", Favor.class);

  // Private Constructor
  private FavorUtil() {}

  public void updateCollectionWrapper(DatabaseUpdater collectionWrapper) {
    collection = collectionWrapper;
  }

  public static FavorUtil getSingleInstance() {
    return SINGLE_INSTANCE;
  }

  /**
   * Allows user to post a favor with a title, description and location.
   *
   * @param favor A favor object.
   * @throws RuntimeException Unable to post to DB.
   */
  public void postFavor(Favor favor) throws RuntimeException {

    try {
      collection.addDocument(favor);
    } catch (RuntimeException e) {
      Log.d(TAG, "unable to add document to db.");
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  public CompletableFuture updateFavor(Favor favor) {
    CompletableFuture completableFuture = new CompletableFuture();
    try {
      completableFuture = collection.updateDocument(favor.getId(), favor.toMap());
    } catch (Exception e) {
      Log.d(TAG, Objects.requireNonNull(e.getMessage()));
    }
    return completableFuture;
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  public CompletableFuture updateFavorStatus(String favorId, Favor.Status newStatus) {
    CompletableFuture updateFuture = new CompletableFuture();
    try {
      updateFuture =
          collection.updateDocument(
              favorId,
              new HashMap<String, String>() {
                {
                  put(Favor.ACCEPTER_ID, UserUtil.currentUserId);
                  put(Favor.STATUS_ID, newStatus.toString());
                }
              });
    } catch (Exception e) {
      Log.d(TAG, Objects.requireNonNull(e.getMessage()));
    }
    return updateFuture;
  }

  /**
   * Returns all the favors for a given user (accepted + requested)
   *
   * @param userId Id of the user
   */
  public ArrayList<Favor> retrieveAllFavorsForGivenUser(String userId) {

    throw new NotImplementedException();
  }

  /**
   * Returns all active favors for a given user.
   *
   * @param userId Id of the user
   */
  public ArrayList<Favor> retrieveAllActiveFavorsForGivenUser(String userId) {

    // ArrayList allFavors = retrieveAllFavorsForGivenUser(userId);
    // Filter out all favors except active ones
    throw new NotImplementedException();
  }

  /**
   * Returns all inactive (past) favors for a given user.
   *
   * @param userId Id of the user
   */
  public ArrayList<Favor> retrieveAllPastFavorsForGivenUser(String userId) {

    // ArrayList allFavors = retrieveAllFavorsForGivenUser(userId);
    // Filter out all favors except inactive (past) ones
    throw new NotImplementedException();
  }

  /**
   * Returns all the favors a given user has requested.
   *
   * @param userId Id of the user
   */
  public ArrayList<Favor> retrieveAllRequestedFavorsForGivenUser(String userId) {

    // ArrayList allFavors = retrieveAllFavorsForGivenUser(userId);
    // Filter out all favors except requested ones
    throw new NotImplementedException();
  }

  /**
   * Returns all the favors a given user has accepted.
   *
   * @param userId Id of the user
   */
  public ArrayList<Favor> retrieveAllAcceptedFavorsForGivenUser(String userId) {

    // ArrayList allFavors = retrieveAllFavorsForGivenUser(userId);
    // Filter out all favors except accepted
    throw new NotImplementedException();
  }

  /**
   * Returns all the favors that are active in a given radius.
   *
   * @param loc a given Location (Android location type)
   * @param radius a given radius to search within
   */
  public ArrayList<Favor> retrieveAllFavorsInGivenRadius(Location loc, double radius) {

    throw new NotImplementedException();
  }

  public CompletableFuture<Favor> retrieveFavor(String favorId) {
    return collection.getDocument(favorId);
  }
}
