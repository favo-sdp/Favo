package ch.epfl.favo.user;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.location.Location;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.common.CollectionWrapper;
import ch.epfl.favo.common.DatabaseUpdater;
import ch.epfl.favo.common.NotImplementedException;
import ch.epfl.favo.util.DependencyFactory;

@SuppressLint("NewApi")
public class UserUtil {
  /*
  TODO: Design singleton constructor and logic
   */
  // Single private instance
  private static final String TAG = "UserUtil";
  private static final UserUtil SINGLE_INSTANCE = new UserUtil();
  private static DatabaseUpdater collection =
      DependencyFactory.getCurrentCollectionWrapper("users", User.class);

  // Private constructor
  private UserUtil() {}

  // Single instance getter
  public static UserUtil getSingleInstance() {
    return SINGLE_INSTANCE;
  }

  /**
   * @param user A user object.
   * @throws RuntimeException Unable to post to DB.
   */
  public void postUser(User user)
      throws RuntimeException { // TODO: catch exception in view not here
    try {
      collection.addDocument(user);
    } catch (RuntimeException e) {
      Log.d(TAG, "unable to add document to db.");
    }
  }

  /**
   * @param isRequested : if true favor is requested. If false favor is accepted
   * @return
   */
  public CompletableFuture addFavorToUser(boolean isRequested) {
    return collection
        .getDocument(DependencyFactory.getCurrentFirebaseUser().getUid())
        .thenCompose(
            (object) -> {
              User user = ((User) object);
              if (isRequested) user.changeActiveRequestedFavorCount(1);
              else user.changeActiveAcceptedFavorCount(1);
              return updateUser(user);
            });
  }

  public CompletableFuture updateUser(User user) {
    return collection.updateDocument(
        DependencyFactory.getCurrentFirebaseUser().getUid(), user.toMap());
  }

  /** @param id A FireBase Uid to search for in Users table. */
  public CompletableFuture<User> findUser(String id) throws Resources.NotFoundException {

    try {
      return collection.getDocument(id);
    } catch (Exception e) {
      Log.d(TAG, "unable to find document in db.");
      throw new Resources.NotFoundException();
    }
  }

  /**
   * Allows user to login to their account.
   *
   * @param username Valid username in DB.
   * @param password Valid Pw in DB.
   */
  public void logInAccount(String username, String password) {
    /*
    TODO: Login with Google Firebase. If not found, throw NotInDBException()
     */
    throw new NotImplementedException();
  }

  public void logOutAccount() {
    throw new NotImplementedException();
  }

  public void deleteAccount() {
    throw new NotImplementedException();
  }

  /**
   * Returns all the favors that are active in a given radius.
   *
   * @param loc Location to search around (Android location type)
   * @param radius a given radius to search within
   */
  public ArrayList<User> retrieveOtherUsersInGivenRadius(Location loc, double radius) {

    throw new NotImplementedException();
  }

  /**
   * Retrieves current registration token for the notification system.
   *
   * @param user A user object.
   */
  public void retrieveUserRegistrationToken(User user) {
    FirebaseInstanceId.getInstance()
        .getInstanceId()
        .addOnCompleteListener(
            task -> {
              if (!task.isSuccessful()) {
                return;
              }
              String token = Objects.requireNonNull(task.getResult()).getToken();
              user.setNotificationId(token);

              Map<String, String> notifMap = new HashMap<String, String>();
              notifMap.put("notificationId", token);
              collection.updateDocument(user.getId(), notifMap);
            });
  }

  public void setCollectionWrapper(CollectionWrapper collectionWrapper) {
    collection = collectionWrapper;
  }
}
