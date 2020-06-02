package ch.epfl.favo.chat;

import com.google.firebase.firestore.Query;

import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.MainActivity;
import ch.epfl.favo.chat.Model.Message;
import ch.epfl.favo.database.ICollectionWrapper;
import ch.epfl.favo.util.DependencyFactory;

public class ChatUtil implements IChatUtil {
  private static final ChatUtil SINGLE_INSTANCE = new ChatUtil();
  private static ICollectionWrapper collection =
      DependencyFactory.getCurrentCollectionWrapper("chats", Message.class);

  public static ChatUtil getSingleInstance() {
    return SINGLE_INSTANCE;
  }
  // Private Constructor
  private ChatUtil() {}

  @Override
  public CompletableFuture<Void> addChatMessage(Message message) {
    return collection.addDocument(message);
  }

  @Override
  public String generateGoogleMapsPath(double latitude, double longitude) {
    return "https://maps.googleapis.com/maps/api/staticmap?center="
        + latitude
        + ","
        + longitude
        + "&zoom=15&markers=color:blue|"
        + latitude
        + ","
        + longitude
        + "&size=300x300&sensor=false"
        + "&key="
        + MainActivity.GOOGLE_API_KEY;
  }

  Query getAllChatMessagesForFavor(String favorId) {
    return collection
        .getReference()
        .whereEqualTo(Message.FAVOR_ID, favorId)
        .orderBy(Message.TIME_STAMP, Query.Direction.DESCENDING);
  }
}
