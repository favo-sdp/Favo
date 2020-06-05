package ch.epfl.favo;

import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.chat.IChatUtil;
import ch.epfl.favo.chat.Model.Message;

public class FakeChatUtil implements IChatUtil {
  boolean throwsError = false;
  final CompletableFuture failedResult = new CompletableFuture();

  public void setThrowsError(Throwable e) {
    throwsError = true;
    failedResult.completeExceptionally(e);
  }

  @Override
  public CompletableFuture<Void> addChatMessage(Message message) {
    if (throwsError) return failedResult;
    else return CompletableFuture.supplyAsync(() -> null);
  }

  @Override
  public String generateGoogleMapsPath(double latitude, double longitude) {
    return "somegooglemaps";
  }
}
