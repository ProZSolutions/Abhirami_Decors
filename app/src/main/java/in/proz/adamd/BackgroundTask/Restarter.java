package in.proz.adamd.BackgroundTask;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import in.proz.adamd.R;

public class Restarter  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("FuseService", "Service tried to stop");
       // Toast.makeText(context, "Service restarted", Toast.LENGTH_SHORT).show();
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            Intent serviceIntent = new Intent(context, YourService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
               // context.startForegroundService(serviceIntent);

                context.stopService(serviceIntent);
            }

        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, YourService.class));
            } else {
                context.startService(new Intent(context, YourService.class));
            }
        }

    }

    public static NotificationCompat.Builder getNotificationBuilder(Context context, String channelId, int importance) {
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            prepareChannel(context, channelId, importance);
            builder = new NotificationCompat.Builder(context, channelId);
        } else {
            builder = new NotificationCompat.Builder(context);
        }
        return builder;
    }

    @TargetApi(26)
    private static void prepareChannel(Context context, String id, int importance) {
        final String appName = context.getString(R.string.app_name);
        String description = context.getString(R.string.notifications_channel_description);
        final NotificationManager nm = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);

        if(nm != null) {
            NotificationChannel nChannel = nm.getNotificationChannel(id);

            if (nChannel == null) {
                nChannel = new NotificationChannel(id, appName, importance);
                nChannel.setDescription(description);
                nm.createNotificationChannel(nChannel);
            }
        }
    }
}