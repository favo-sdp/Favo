package ch.epfl.favo.user;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.database.CollectionWrapper;
import ch.epfl.favo.database.ICollectionWrapper;
import ch.epfl.favo.exception.NotImplementedException;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.util.TaskToFutureAdapter;

@SuppressLint("NewApi")
public class UserUtil implements IUserUtil {
  /*
  TODO: Design singleton constructor and logic
   */
  // Single private instance
  private static final String TAG = "UserUtil";
  private static final UserUtil SINGLE_INSTANCE = new UserUtil();
  private static ICollectionWrapper collection =
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
  @Override
  public CompletableFuture postUser(User user) { // TODO: catch exception in view not here
    return collection.addDocument(user);
  }

  /**
   * @param isRequested : if true favor is requested. If false favor is accepted
   * @return
   */
  @Override
  public CompletableFuture changeActiveFavorCount(String userId,boolean isRequested, int change) {
    return findUser(userId)//.thenAccept( user -> Log.d("UserUtilAA", "works "));
       .thenCompose(
            (object) -> {
              User user = object;
              if (isRequested){
                Log.d("UserUtilAA", " still alive 11");
                user.setActiveRequestingFavors(user.getActiveRequestingFavors() + change);
                Log.d("UserUtilAA", " dead");
              }
                //user.setActiveRequestingFavors(user.getActiveRequestingFavors() + change);
              else {
                Log.d("UserUtilAA", " still alive 22  " + user.getActiveAcceptingFavors());
                user.setActiveAcceptingFavors(user.getActiveAcceptingFavors() + change);
                Log.d("UserUtilAA", " dead");
              }
              Log.d("UserUtilAA", user.getActiveRequestingFavors() + " requested " + isRequested);
              return updateUser(user);
            });
  }

  @Override
  public CompletableFuture updateUser(User user) {
    return collection.updateDocument(user.getId(), user.toMap());
  }

  /** @param id A FireBase Uid to search for in Users table. */
  @Override
  public CompletableFuture<User> findUser(String id) throws Resources.NotFoundException {
    Log.d("UserUtilAA", "run in modelView findUser");
    return collection.getDocument(id);
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
  public CompletableFuture retrieveUserRegistrationToken(User user) {
    FirebaseInstanceId instance = DependencyFactory.getCurrentFirebaseNotificationInstanceId();
    Task<InstanceIdResult> task = instance.getInstanceId();
    CompletableFuture<InstanceIdResult> futureIdTask =
        new TaskToFutureAdapter<>(task).getInstance();
    return futureIdTask.thenCompose(
        (instanceIdResult) -> {
          String token = Objects.requireNonNull(instanceIdResult).getToken();
          user.setNotificationId(token);
          Map<String, String> notifMap = new HashMap<>();
          notifMap.put(User.NOTIFICATION_ID, token);
          return collection.updateDocument(user.getId(), notifMap);
        });
  }
  //        addOnCompleteListener(
  //            task -> {
  //              if (!task.isSuccessful()) {
  //                return;
  //              }
  //              String token = Objects.requireNonNull(task.getResult()).getToken();
  //              user.setNotificationId(token);
  //
  //              Map<String, String> notifMap = new HashMap<String, String>();
  //              notifMap.put("notificationId", token);
  //              collection.updateDocument(user.getId(), notifMap);
  //            });

  public void setCollectionWrapper(CollectionWrapper collectionWrapper) {
    collection = collectionWrapper;
  }
}
