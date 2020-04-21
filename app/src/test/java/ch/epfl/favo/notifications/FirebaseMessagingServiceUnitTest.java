package ch.epfl.favo.notifications;

import android.os.Bundle;

import com.google.firebase.messaging.RemoteMessage;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Date;
import java.util.UUID;

import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.view.MockGpsTracker;

import static ch.epfl.favo.TestConstants.NOTIFICATION_BODY;
import static ch.epfl.favo.TestConstants.NOTIFICATION_TITLE;

public class FirebaseMessagingServiceUnitTest {

  private FirebaseMessagingService fms;

  @Before
  public void setup() {
    fms = new FirebaseMessagingService();
  }

  @Test
  public void testOnMessageReceived() {
    Bundle bundle = new Bundle();
    bundle.putString("google.delivered_priority", "high");
    bundle.putLong("google.sent_time", (new Date()).getTime());
    bundle.putLong("google.ttl", 2419200);
    bundle.putString("google.original_priority", "high");
    bundle.putString("google.message_id", UUID.randomUUID().toString());
    bundle.putString("from", "533932732600");
    bundle.putString("gcm.notification.title", NOTIFICATION_TITLE);
    bundle.putString("gcm.notification.body", NOTIFICATION_BODY);
    bundle.putString("gcm.notification.e", "1");

    fms.onMessageReceived(new RemoteMessage(bundle));
  }

  @Test
  public void testOnNewToken() {
    fms.onNewToken("New token");
  }
}
