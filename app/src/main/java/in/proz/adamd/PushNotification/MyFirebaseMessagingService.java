package in.proz.adamd.PushNotification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("message_received"," ,essage called ");
        // Extract data payload
      /*  if (remoteMessage.getData().size() > 0) {
            // Handle custom data payload
            String customData = remoteMessage.getData().get("key");
        }*/
        if(remoteMessage.getNotification()!=null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            Log.d("message_received", " title " + title + " body " + body);

            // Show notification
            showNotification(title, body);
        }
    }

    private void showNotification(String title, String body) {
        String channelId = "nfty_notifications";
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Create notification channel for Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "NFTY Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Create notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Show notification
        notificationManager.notify(0, builder.build());
    }
}
