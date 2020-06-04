package ch.epfl.favo.user;

import android.annotation.SuppressLint;
import android.content.res.Resources;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.database.CollectionWrapper;
import ch.epfl.favo.database.ICollectionWrapper;
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
  private static ICollectionWrapper<User> collection =
      DependencyFactory.getCurrentCollectionWrapper("users", User.class);

  public void updateCollectionWrapper(ICollectionWrapper<User> newWrapper) {
    collection = newWrapper;
  }
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
  public CompletableFuture<Void> postUser(User user) {
    return collection.addDocument(user);
  }

  /**
   * @param isRequested : if true favor is requested. If false favor is accepted
   * @return
   */
  @Override
  public CompletableFuture<Void> changeActiveFavorCount(
      String userId, boolean isRequested, int change) {
    return findUser(userId)
        .thenCompose(
            (user) -> {
              if (isRequested) {
                user.setActiveRequestingFavors(user.getActiveRequestingFavors() + change);
              } else {
                user.setActiveAcceptingFavors(user.getActiveAcceptingFavors() + change);
              }
              return updateUser(user);
            });
  }

  @Override
  public CompletableFuture<Void> updateUser(User user) {
    return collection.updateDocument(user.getId(), user.toMap());
  }

  @Override
  public CompletableFuture<Void> incrementFieldForUser(String userId, String field, int change) {
    Task<Void> updateTask = getUserReference(userId).update(field, FieldValue.increment(change));
    return new TaskToFutureAdapter<>(updateTask).getInstance();
  }

  @Override
  public CompletableFuture<Void> deleteUser(User user) {
    return collection.removeDocument(user.getId());
  }

  /** @param id A FireBase Uid to search for in Users table. */
  @Override
  public CompletableFuture<User> findUser(String id) throws Resources.NotFoundException {
    return collection.getDocument(id);
  }

  @Override
  public DocumentReference getUserReference(String userId) {
    return collection.getDocumentQuery(userId);
  }

  public CompletableFuture<Void> postUserRegistrationToken(User user) {
    FirebaseInstanceId instance = DependencyFactory.getCurrentFirebaseNotificationInstanceId();
    Task<InstanceIdResult> task = instance.getInstanceId();
    CompletableFuture<InstanceIdResult> futureIdTask =
        new TaskToFutureAdapter<>(task).getInstance();
    return futureIdTask.thenCompose(
        (instanceIdResult) -> {
          String token = Objects.requireNonNull(instanceIdResult).getToken();
          user.setNotificationId(token);
          Map<String, Object> notifMap = new HashMap<>();
          notifMap.put(User.NOTIFICATION_ID, token);
          return collection.updateDocument(user.getId(), notifMap);
        });
  }

  public void setCollectionWrapper(CollectionWrapper collectionWrapper) {
    collection = collectionWrapper;
  }

  @Override
  public CompletableFuture<Void> updateCoinBalance(String userId, int reward) {
    return findUser(userId)
        .thenCompose(
            (user) -> {
              user.setBalance(user.getBalance() + reward);
              return updateUser(user);
            });
  }
}
