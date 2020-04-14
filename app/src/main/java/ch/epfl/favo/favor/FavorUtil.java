package ch.epfl.favo.favor;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.common.DatabaseUpdater;
import ch.epfl.favo.common.DatabaseWrapper;
import ch.epfl.favo.common.NotImplementedException;
import ch.epfl.favo.map.GpsTracker;
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

  /**
   * @param favorId the id of the favor to retrieve from DB.
   * @return CompletableFuture<Favor>
   */
  public CompletableFuture<Favor> retrieveFavor(String favorId) {
    try {
      return collection.getDocument(favorId);
    } catch (RuntimeException e) {
      Log.d(TAG, "unable to retrieve document from db.");
    }
    return new CompletableFuture<>();
  }

  /**
   * @param favorId the id of the favor to retrieve from DB.
   * @return CompletableFuture<Favor>
   */
  public void updateFavor(String favorId, Map<String, Object> updates) {
    try {
      collection.updateDocument(favorId, updates);
    } catch (RuntimeException e) {
      Log.d(TAG, "unable to retrieve document from db.");
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
   * @param loc a given Location (Android location type)
   * @param radius a given radius to search within
   */

  public CompletableFuture<List<Favor>> retrieveAllFavorsInGivenRadius(Location loc, double radius) {

  /**
   * I currently implement a temporary, simpler version to retrieve favors in a **square area** on
   * sphere surface. This function only return longitude bounded favors, the rest query is done
   * by the caller of this function.
   *  TODO: use firebase functions or other server code to write logic that performs customized filtering and fetch the result
   * */

    double longDif = Math.toDegrees(radius / (6371 * Math.cos(Math.toRadians(loc.getLatitude()))));
    double longitude_lower = loc.getLongitude() - longDif;
    double longitude_upper = loc.getLongitude() + longDif;
    Task<QuerySnapshot> getAllTask = DatabaseWrapper.getCollectionReference("favors")
            /** For some reason, reading from db is very slow when adding one more whereEqualTo query field
             * or not without .limit(..) **/
            // .whereEqualTo("statusId", Favor.Status.REQUESTED)
            .whereGreaterThan("location.longitude", longitude_lower)
            .whereLessThan("location.longitude", longitude_upper).limit(20).get();
    CompletableFuture<QuerySnapshot> getAllFuture = new TaskToFutureAdapter<>(getAllTask);
    return getAllFuture.thenApply(
            querySnapshot -> querySnapshot.toObjects(Favor.class));

  }
}
