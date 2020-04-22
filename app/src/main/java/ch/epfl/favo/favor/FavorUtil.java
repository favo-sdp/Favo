package ch.epfl.favo.favor;

import android.annotation.SuppressLint;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.common.CollectionWrapper;
import ch.epfl.favo.common.DatabaseUpdater;
import ch.epfl.favo.common.NotImplementedException;
import ch.epfl.favo.util.DependencyFactory;

/*
This models the favor request.
*/
@SuppressLint("NewApi")
public class FavorUtil {
  private static final String TAG = "FavorUtil";
  private static final String COLLECTION_NAME = "favors";
  private static final FavorUtil SINGLE_INSTANCE = new FavorUtil();
  private static DatabaseUpdater collection =
      DependencyFactory.getCurrentCollectionWrapper(DependencyFactory.getCurrentFavorCollection(), Favor.class);

  // Private Constructor
  private FavorUtil() {}

  public void updateCollectionWrapper(DatabaseUpdater collectionWrapper) {
    collection = collectionWrapper;
  }

  public static FavorUtil getSingleInstance() {
    collection = DependencyFactory.getCurrentCollectionWrapper(DependencyFactory.getCurrentFavorCollection(), Favor.class);
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

  public CompletableFuture updateFavor(Favor favor) {
    return collection.updateDocument(favor.getId(), favor.toMap());
  }

  /**
   * @param favorId the id of the favor to retrieve from DB.
   * @return CompletableFuture<Favor>
   */
  public CompletableFuture<Favor> retrieveFavor(String favorId) {
    return collection.getDocument(favorId);
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
   * @param loc a given Location (Android location type)
   * @param radius a given radius to search within
   */

  public CompletableFuture<List<Favor>> retrieveAllFavorsInGivenRadius(Location loc, double radius) {
    /**It is a temporary, simpler version to retrieve favors in a **square area** on sphere surface**/
    return collection.getAllDocumentsLongitudeBounded(loc, radius);
  }
}
