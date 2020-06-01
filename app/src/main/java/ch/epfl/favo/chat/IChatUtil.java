package ch.epfl.favo.chat;

import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.chat.Model.Message;

/**
 * Chat Utility acts as a utility repository for the chat. It allows us to communicate with
 * Firestore and manage the chat collection
 */
public interface IChatUtil {

  /**
   * Save chat message to the corresponding collection in the database
   *
   * @param message: message to save
   * @return: completable future
   */
  CompletableFuture<Void> addChatMessage(Message message);

  /**
   * Returns custom URL to Google Maps
   *
   * @param latitude: latitude coordinate
   * @param longitude: longitude coordinate
   * @return: string url
   */
  String generateGoogleMapsPath(double latitude, double longitude);
}
