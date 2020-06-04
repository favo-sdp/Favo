package ch.epfl.favo;

import android.content.res.Resources;

import androidx.annotation.VisibleForTesting;

import com.google.firebase.firestore.DocumentReference;

import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.user.IUserUtil;
import ch.epfl.favo.user.User;

public class FakeUserUtil implements IUserUtil {
  private DocumentReference currentUserReference;
  private CompletableFuture successfulCompletableFuture =
      new CompletableFuture<Void>() {
        {
          complete(null);
        }
      };
  private CompletableFuture failedCompletableFuture =
      new CompletableFuture() {
        {
          completeExceptionally(new RuntimeException());
        }
      };
  private CompletableFuture<User> successfulUserFuture =
      new CompletableFuture<User>() {
        {
          complete(FakeItemFactory.getUser());
        }
      };
  private boolean isThrowingError = false;

  public void setThrowResult(Throwable throwable) {
    isThrowingError = true;
    failedCompletableFuture.completeExceptionally(throwable);
  }

  @Override
  public CompletableFuture postUser(User user) {
    return defaultResult();
  }


  public CompletableFuture defaultResult() {
    if (isThrowingError) return failedCompletableFuture;
    return successfulCompletableFuture;
  }

  @Override
  public CompletableFuture updateUser(User user) {
    return defaultResult();
  }

  @Override
  public CompletableFuture deleteUser(User user) {
    if (isThrowingError) return failedCompletableFuture;
    return successfulCompletableFuture;
  }

  @Override
  public CompletableFuture<Void> incrementFieldForUser(String userId, String field, int change) {
    return null;
  }

  private boolean isFailedFindUser = false;

  public void setFindUserResult(User user) {
    successfulUserFuture.complete(user);
  }

  public void setFindUserFail(boolean flag) {
    isFailedFindUser = true;
  }

  @Override
  public CompletableFuture<User> findUser(String id) throws Resources.NotFoundException {
    if (isThrowingError || isFailedFindUser) return failedCompletableFuture;
    return successfulUserFuture;
  }

  @Override
  public CompletableFuture retrieveUserRegistrationToken(User user) {
    return defaultResult();
  }

  @Override
  public DocumentReference getCurrentUserReference(String userId) {
    return currentUserReference;
  }

  @VisibleForTesting
  public void setCurrentUserReference(DocumentReference dependency) {
    currentUserReference = dependency;
  }
}
