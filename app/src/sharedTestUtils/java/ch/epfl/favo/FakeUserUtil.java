package ch.epfl.favo;

import android.content.res.Resources;

import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.user.IUserUtil;
import ch.epfl.favo.user.User;

public class FakeUserUtil implements IUserUtil {
  private CompletableFuture successfulCompletableFuture = new CompletableFuture(){{complete(null);}};
  private CompletableFuture failedCompletableFuture = new CompletableFuture(){{completeExceptionally(new RuntimeException());}};
  private CompletableFuture<User> successfulUserFuture = new CompletableFuture(){{complete(FakeItemFactory.getUser());}};
  private boolean isThrowingError=false;
  public void setThrowResult(Throwable throwable){
    isThrowingError = true;
    failedCompletableFuture.completeExceptionally(throwable);
  }
  @Override
  public CompletableFuture postUser(User user) {
    return defaultResult();
  }

  @Override
  public CompletableFuture changeActiveFavorCount(boolean isRequested, int change) {
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
  private boolean isFailedFindUser = false;
  public void setFindUserResult(User user){
    successfulUserFuture.complete(user);
  }
  public void setFindUserFail(boolean flag){isFailedFindUser=true;}
  @Override
  public CompletableFuture<User> findUser(String id) throws Resources.NotFoundException {
    if (isThrowingError || isFailedFindUser) return failedCompletableFuture;
    return successfulUserFuture;
  }

  @Override
  public CompletableFuture retrieveUserRegistrationToken(User user) {
    return defaultResult();
  }
}
