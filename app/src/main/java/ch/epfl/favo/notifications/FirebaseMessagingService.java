package ch.epfl.favo.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;
import java.util.Random;

import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;

public class FirebaseMessagingService
        extends com.google.firebase.messaging.FirebaseMessagingService {
    // private static final String TAG = "MyFirebaseMsgService";
    public static String CHANNEL_NAME = "Default channel name";
    // show notification received
    public static void showNotification(
            Context context, RemoteMessage notification, String channelId) {
        Intent intent = new Intent(context, MainActivity.class);
        // add favor id as an argument to main activity
        intent.putExtra("FavorId", notification.getData().get("FavorId"));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(notification.getNotification().getTitle())
                        .setContentText(notification.getNotification().getBody())
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // Since android Oreo notification channel is needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(channelId, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }
        Objects.requireNonNull(notificationManager)
                .notify(new Random().nextInt(), notificationBuilder.build());
    }
    // method called when new message (notification or data message) is received
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if message contains a data payload.
        //if (remoteMessage.getData().size() > 0) {
        // Log.d("FirebaseMessagingService", "Message data payload: " + remoteMessage.getData());
        // do something with message data, like chat (TODO for later sprints)
        //}
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            showNotification(
                    this,
                    remoteMessage,
                    getString(R.string.default_notification_channel_id));
        }
    }
    // onNewToken callback fires whenever a new token is generated
    @Override
    public void onNewToken(@NonNull String token) {
        // Log.d(TAG, "Refreshed token: " + token);
        // TODO send new refreshed token to db
        // simply set the new notificationID for the current user and sync with the db
    }
}
