package in.proz.adamd.BackgroundTask;

import android.Manifest;
import in.proz.adamd.R;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.Timer;
import java.util.TimerTask;

public class LocationForegroundService extends Service {
    private static final String TAG = "LocationForegroundServic";
    private static final int LOCATION_REQUEST_INTERVAL = 1 * 60 * 1000; // 5 minutes

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    Timer timer;
    private TimerTask timerTask;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"oncreate ");
        createLocationRequest();
        createLocationCallback();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"on start command ");
        startForeground(12345678, createNotification()); // Notification ID
        requestLocationUpdates();
        startTimer();

        return START_STICKY;
    }
    public void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                Log.d("FuseService", "=========  "  );
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Log.d("FuseService"," location schedule ");
                requestLocationUpdates();

            }
        };
        timer.schedule(timerTask, 0, 60000); //
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"ondestroy ");
        stopLocationUpdates();
    }

    private void createLocationRequest() {
        Log.d(TAG,"create location req ");
        locationRequest = new LocationRequest();
        locationRequest.setInterval(LOCATION_REQUEST_INTERVAL);
        locationRequest.setFastestInterval(LOCATION_REQUEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback() {
        Log.d(TAG," create location call back ");
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // Handle location updates here
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    Log.d(TAG, "Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude());
                    // You can send this location to your server or perform any other action
                }
            }
        };
    }

    private void requestLocationUpdates() {
        Log.d(TAG,"requewt location updated ");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        Log.d(TAG,"stop location updated ");
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
    }

    private Notification createNotification() {
        Log.d(TAG,"create notification ");
        String channelId = "location_service_channel";
        String channelName = "Location Service Channel";

        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Location Service")
                .setContentText("Running")
                .setSmallIcon(R.mipmap.ic_launcher);

        return builder.build();
    }
}
