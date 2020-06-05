package ch.epfl.favo;

import android.content.res.Resources;

import androidx.annotation.VisibleForTesting;

import com.google.firebase.firestore.DocumentReference;

import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.user.IUserUtil;
import ch.epfl.favo.user.User;

public class FakeUserUtil implements IUserUtil {
  private DocumentReference currentUserReference;
  private final CompletableFuture successfulCompletableFuture =
      new CompletableFuture<Void>() {
        {
          complete(null);
        }
      };
  private final CompletableFuture failedCompletableFuture =
      new CompletableFuture() {
        {
          completeExceptionally(new RuntimeException());
        }
      };
  private final CompletableFuture<User> successfulUserFuture =
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
  public CompletableFuture<Void> updateUser(User user) {
    return defaultResult();
  }

  @Override
  public CompletableFuture<Void> incrementFieldForUser(String userId, String field, int change) {
    if (isThrowingError) return failedCompletableFuture;
    return successfulCompletableFuture;
  }

  @Override
  public CompletableFuture deleteUser(User user) {
    if (isThrowingError) return failedCompletableFuture;
    return successfulCompletableFuture;
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

  public CompletableFuture updateCoinBalance(String userId, int reward) {
    return defaultResult();
  }

  @Override
  public CompletableFuture<Void> postUserRegistrationToken(User user) {
    return defaultResult();
  }

  @Override
  public DocumentReference getUserReference(String userId) {
    return currentUserReference;
  }

  @VisibleForTesting
  public void setCurrentUserReference(DocumentReference dependency) {
    currentUserReference = dependency;
  }
}
