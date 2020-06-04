package ch.epfl.favo.user;

import android.content.res.Resources;

import com.google.firebase.firestore.DocumentReference;

import java.util.concurrent.CompletableFuture;

public interface IUserUtil {

  CompletableFuture<Void> postUser(User user);

  CompletableFuture<Void> updateUser(User user);

  CompletableFuture<Void> deleteUser(User user);

  CompletableFuture<Void> incrementFieldForUser(String userId, String field, int change);

  CompletableFuture<User> findUser(String id) throws Resources.NotFoundException;

  CompletableFuture retrieveUserRegistrationToken(User user);

  DocumentReference getCurrentUserReference(String userId);
}
