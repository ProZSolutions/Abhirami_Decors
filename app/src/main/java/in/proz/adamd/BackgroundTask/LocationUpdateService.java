package in.proz.adamd.BackgroundTask;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;

import org.jetbrains.annotations.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class LocationUpdateService extends Service {

    //region data
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 3000;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationSettingsRequest locationSettingsRequest;

    //endregion

    //onCreate
    @Override
    public void onCreate() {
        Log.d("LocationsTrack"," oncreate ");
        super.onCreate();
        initData();
    }


    //Location Callback
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location currentLocation = locationResult.getLastLocation();
            Log.d("LocationsTrack", currentLocation.getLatitude() + "," + currentLocation.getLongitude());
            //Share/Publish Location
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            Log.d("LocationsTrack"," on start command ");
         startLocationUpdates();
         startTimer();

        return START_STICKY;
    }

    private void startLocationUpdates() {
        Log.d("LocationsTrack"," start location updated ");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.requestLocationUpdates(this.locationRequest,
                this.locationCallback, Looper.myLooper());
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("LocationsTrack"," on bind ");
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("LocationsTrack"," on destroy called ");
        //its uncomand -> command -> 20-04-24
      //  mFusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void initData() {
        Log.d("LocationsTrack"," init Data" );
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(getApplicationContext());

    }
    private Timer timer;
    private TimerTask timerTask;
    int counter=0;

    public void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @SuppressLint("SuspiciousIndentation")
            public void run() {
                Log.d("LocationsTrack", "=========  " + (counter++));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
              //  initData();
               // startLocationUpdates();

            }
        };
        timer.schedule(timerTask, 0, 60000); //
    }
}
