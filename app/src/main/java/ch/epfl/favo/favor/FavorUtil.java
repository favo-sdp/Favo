package ch.epfl.favo.favor;

import android.location.Location;
import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.favo.common.DatabaseUpdater;
import ch.epfl.favo.common.NotImplementedException;
import ch.epfl.favo.util.DependencyFactory;

/*
This models the favor request.
*/
public class FavorUtil {
  private static final String TAG = "FavorUtil";
  private static final FavorUtil SINGLE_INSTANCE = new FavorUtil();
  private static DatabaseUpdater collection = DependencyFactory.getCurrentDatabaseUpdater("favors");

  // Private Constructor
  private FavorUtil() {}

  public static FavorUtil getSingleInstance() {
    return SINGLE_INSTANCE;
  }

  /**
   * Allows user to post a favor with a title, description and location.
   *
   * @param f A favor object.
   */
  public void postFavor(Favor f) {

    Map<String, Object> favor = new HashMap<>();
    Location loc = f.getLocation();
    favor.put("title", f.getTitle());
    favor.put("description", f.getDescription());
    favor.put("statusId", 0);
    favor.put("location", new GeoPoint(loc.getLatitude(), loc.getLongitude()));
    favor.put("postedTime", new Timestamp(f.getPostedTime()));
    try {
      collection.addDocument(f.getId(), favor);
    } catch (RuntimeException e) {
      Log.d(TAG, "unable to add document to db.");
    }
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

  public Favor retrieveFavor(String favorId) throws NotImplementedException {
    return new Favor(collection.getDocument(favorId));
  }
}
