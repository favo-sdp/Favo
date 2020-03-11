package ch.epfl.favo.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;
import java.util.Random;

import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;

public class FirebaseMessagingService
    extends com.google.firebase.messaging.FirebaseMessagingService {

  private static final String TAG = "MyFirebaseMsgService";

  // method called when new message (notification or data message) is received
  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    Log.d(TAG, "From: " + remoteMessage.getFrom());

    // Check if message contains a data payload.
    if (remoteMessage.getData().size() > 0) {
      Log.d(TAG, "Message data payload: " + remoteMessage.getData());
      // do something with message data, like chat (TODO for later sprints)
    }

    // Check if message contains a notification payload.
    if (remoteMessage.getNotification() != null) {
      Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
      showNotification(remoteMessage.getNotification());
    }
  }

  // onNewToken callback fires whenever a new token is generated
  @Override
  public void onNewToken(@NonNull String token) {
    Log.d(TAG, "Refreshed token: " + token);
    // TODO send new refreshed token to db
  }

  // show notification received
  private void showNotification(RemoteMessage.Notification notification) {
    Intent intent = new Intent(this, MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    PendingIntent pendingIntent =
        PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

    String channelId = getString(R.string.default_notification_channel_id);
    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    NotificationCompat.Builder notificationBuilder =
        new NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(notification.getTitle())
            .setContentText(notification.getBody())
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent);

    NotificationManager notificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    // Since android Oreo notification channel is needed
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel channel =
          new NotificationChannel(
              channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
      Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
    }

    Objects.requireNonNull(notificationManager)
        .notify(new Random().nextInt(), notificationBuilder.build());
  }
}
