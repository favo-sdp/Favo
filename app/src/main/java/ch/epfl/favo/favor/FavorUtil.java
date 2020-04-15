package ch.epfl.favo.favor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.common.DatabaseUpdater;
import ch.epfl.favo.common.DatabaseWrapper;
import ch.epfl.favo.common.NotImplementedException;
import ch.epfl.favo.user.UserUtil;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.util.TaskToFutureAdapter;

/*
This models the favor request.
*/
@SuppressLint("NewApi")
public class FavorUtil {
  private static final String TAG = "FavorUtil";
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
    return collection.updateDocument(favor.getId(), favor.toMap());
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  public CompletableFuture updateFavorStatus(String favorId, Favor.Status newStatus) {
    Map updates =
        new HashMap<String, String>() {
          {
            put(Favor.ACCEPTER_ID, UserUtil.currentUserId);
            put(Favor.STATUS_ID, newStatus.toString());
          }
        };
    return updateFavor(favorId, updates);
  }

  /**
   * @param favorId the id of the favor to retrieve from DB.
   * @return CompletableFuture<Favor>
   */
  public CompletableFuture<Favor> retrieveFavor(String favorId) {
    return collection.getDocument(favorId);
  }

  /**
   * @param favorId the id of the favor to retrieve from DB.
   * @return CompletableFuture<Favor>
   */
  public CompletableFuture updateFavor(String favorId, Map<String, Object> updates) {
    return collection.updateDocument(favorId, updates);
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

  public CompletableFuture<List<Favor>> retrieveAllFavorsInGivenRadius(Location loc, double radius, Activity activity) {
    /**It is a temporary, simpler version to retrieve favors in a **square area** on sphere surface**/
    return collection.getAllDocumentsLongitudeLatitudeBounded(loc, radius, activity);
  }
}
