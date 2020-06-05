package ch.epfl.favo.user;

import android.content.res.Resources;

import com.google.firebase.firestore.DocumentReference;

import java.util.concurrent.CompletableFuture;

/** Acts as a repository to retreive and update documents in the user firestore collection */
public interface IUserUtil {
  /**
   * Posts new user to firestore
   *
   * @param user to be posted
   * @return future that can be successful or not
   */
  CompletableFuture<Void> postUser(User user);

  /**
   * Updates the whole user document
   *
   * @param user user to be updated
   * @return future
   */
  CompletableFuture<Void> updateUser(User user);

  /**
   * This method allows us to quickly update other fields that can be increased less rigorously like
   * the total favors requested, the likes and dislikes and the favors completed.
   *
   * @param userId user to be updated
   * @param field string key corresponding to the attribute to be updated
   * @param change integer value corresponding to the increment or decrement count (can be negative)
   * @return a future that can be successful or not.
   */
  CompletableFuture<Void> incrementFieldForUser(String userId, String field, int change);

  /**
   * Allows us to delete a user completely from the database. Doing so also trigers a cloud function
   * externally that gets rid of associated user history like chat messages and favors.
   *
   * @param user user to be deleted
   * @return future that can be successful or not.
   */
  CompletableFuture<Void> deleteUser(User user);

  /**
   * Retrieves user document from firestore
   *
   * @param userId user id
   * @return future with user object fetched from firestore
   * @throws Resources.NotFoundException if user not found
   */
  CompletableFuture<User> findUser(String userId) throws Resources.NotFoundException;

  /**
   * Retrieves the registration token of a FirebaseUser for notifications. This value needs to be
   * fetched after a new user is created. Then the value is transferred to the User document in
   * firestore.
   *
   * @param user new user
   * @return future
   */
  CompletableFuture<Void> postUserRegistrationToken(User user);

  /**
   * Updates user balance. Occurs when user posts request and when favor is successfully completed.
   *
   * @param userId id of user updated
   * @param reward value of favor in FavoCoins currency
   */
  CompletableFuture<Void> updateCoinBalance(String userId, double reward);

  /**
   * Gets a Firestore query that references the user. We can then attach observers to this query.
   *
   * @param userId user to be observed
   * @return reference of user.
   */
  DocumentReference getUserReference(String userId);
}
