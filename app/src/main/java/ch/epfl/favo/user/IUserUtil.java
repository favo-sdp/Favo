package ch.epfl.favo.user;

import android.content.res.Resources;

import java.util.concurrent.CompletableFuture;

public interface IUserUtil {
  CompletableFuture postUser(User user);

  CompletableFuture changeActiveFavorCount(boolean isRequested, int change);

  CompletableFuture updateUser(User user);

  CompletableFuture<User> findUser(String id) throws Resources.NotFoundException;

  CompletableFuture retrieveUserRegistrationToken(User user);
}
