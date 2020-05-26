package ch.epfl.favo.chat;

import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.chat.Model.Message;

public interface IChatUtil {
  CompletableFuture<Void> addChatMessage(Message message);

  String generateGoogleMapsPath(double latitude, double longitude);
}
