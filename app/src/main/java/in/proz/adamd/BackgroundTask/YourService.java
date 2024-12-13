package in.proz.adamd.BackgroundTask;


import android.Manifest;

import in.proz.adamd.AttendanceSQLite.AttendanceListLocation;
import in.proz.adamd.Map.MapCurrentLocation;
import in.proz.adamd.R;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YourService extends Service {
    public int counter = 0;
    private static final String TAG = YourService.class.getSimpleName();
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 300000; // 10 seconds
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private FusedLocationProviderClient fusedLocationClient;
    private static final long UPDATE_INTERVAL = 5* 60 * 1000; // 5 minutes in milliseconds
    private LocationCallback locationCallback;
    Location locationValue = null;
    CommonClass commonClass = new CommonClass();
    int sync_value = 0;
    AttendanceListLocation attendanceListLocation;


    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        attendanceListLocation = new AttendanceListLocation(this);
        attendanceListLocation.getWritableDatabase();
        createLocationCallback();
        requestLocationUpdates();

    }


    public Location createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d("FuseService", " create location " + locationResult);
                if (locationResult == null) {
                    locationValue = null;
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Handle location updates here, e.g., send location to server
                    locationValue = location;
                    Log.d("FuseService", " creare Location: " + location.getLatitude() + ", " + location.getLongitude());
                }
            }
        };
        return locationValue;
    }


        private void requestLocationUpdates() {
            Log.d("FuseService"," request location updates");
            LocationRequest locationRequest = LocationRequest.create()
                    .setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
                    .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    null);
    }

    private void stopLocationUpdates() {
        Log.d("FuseService"," stop updated ");

        //its already comment i just uncommand it => 20-04-2024
        if(fusedLocationClient!=null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    private void cancelLocationUpdates() {
        Log.d("FuseService"," cancel updates ");
        timer.cancel();
    }






    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        Log.d("FuseService", " show notification ");
        String NOTIFICATION_CHANNEL_ID = "ForegroundServiceChannel";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                channelName, NotificationManager.IMPORTANCE_HIGH);
        chan.setLightColor(Color.BLUE);
        //chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
        int channelID = 100001;


        Intent attendanceIntent = new Intent(this, MapCurrentLocation.class); // replace AttendanceActivity.class with your actual activity
        attendanceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

// Step 2: Create a PendingIntent using the Intent
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, attendanceIntent, PendingIntent.FLAG_IMMUTABLE);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("ADAMS App")
                .setSmallIcon(R.drawable.plogo)
                .setOngoing(true)
                .setContentIntent(pendingIntent) // Set the PendingIntent here
                .setContentText("Your Live Location Tracking By Proz")
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        manager.notify(channelID, builder.build());

        // Start foreground service
        startForeground(channelID, builder.build());

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        requestLocationUpdates();
      //  scheduleLocationUpdateTask();
      //  scheduleLocationUpdates();
        startTimer();
        callNotification();
       /* if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            Log.d("FuseService", " come to not if ");
            startMyOwnForeground();
            // createAndShowForegroundNotification(this,123);
        } else {
            Log.d("FuseService", " com to not else ");
            startForeground(100001, new Notification());
        }*/
        Log.d("FuseService", " start command ");
        return START_STICKY;
    }

/*
    private void scheduleLocationUpdateTask() {
        TimerTask locationUpdateTask = new TimerTask() {
            @Override
            public void run() {
                // Get location and send to server
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        Log.d("FuseServie"," send loation ");
                       // sendLocationToServer(location.getLatitude(), location.getLongitude());
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(locationUpdateTask, 0, UPDATE_INTERVAL);
    }
*/


    private void sendLocationToServer(double latitude, double longitude) {
        Log.d("FuseService"," send to server ");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String today = simpleDateFormat.format(new Date());
        Log.d("FuseService"," token "+commonClass.getSharedPref(getApplicationContext(), "token")+" deviec "+
                commonClass.getDeviceID(this)+" uuid "+commonClass.getSharedPref(getApplicationContext(),"uuid")+
                " date "+today);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(), "token"),
                commonClass.getDeviceID(this)).create(ApiInterface.class);
        Call<CommonPojo> call = apiInterface.LiveTrackingGeoLocation(commonClass.getSharedPref(getApplicationContext(),"uuid"),
                String.valueOf(latitude),String.valueOf(longitude),today);
        Log.d("FuseService"," url "+call.request().url());
        call.enqueue(new Callback<CommonPojo>() {
            @Override
            public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                    Log.d("FuseService"," respone as "+response.code());
                    if(response.isSuccessful()){
                        if(response.code()==200){
                            Log.d("FuseService"," status "+response.body().getStatus());
                        }
                    }
            }

            @Override
            public void onFailure(Call<CommonPojo> call, Throwable t) {
                Log.d("FuseService"," error "+t.getMessage());
            }
        });









    }

    @Override
    public void onDestroy() {
        Log.d("FuseService"," des in ser");
        super.onDestroy();
        stoptimertask();
         stopLocationUpdates();
          cancelStopService();


          // its uncommand to command -> 20-04-24
      /*if(!TextUtils.isEmpty(getApplicationContext())) {
              if (!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(), "uuid")) &&
                      !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(), "sync_id"))) {*/
                 /* Intent broadcastIntent = new Intent();
                  broadcastIntent.setAction("restartservice");
                  broadcastIntent.setClass(this, Restarter.class);
                  this.sendBroadcast(broadcastIntent);*/
             /* }
          }*/
    }

    private void cancelStopService() {
        if(timer!=null) {
             timer.cancel();
        }
    }


    private Timer timer;
    private TimerTask timerTask;


    public void callNotification(){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            Log.d("FuseService", " come to not if ");
            startMyOwnForeground();
            // createAndShowForegroundNotification(this,123);
        } else {
            Log.d("FuseService", " com to not else ");
            startForeground(100001, new Notification());
        }
    }
    public void startTimer() {
        callNotification();
        timer = new Timer();
        timerTask = new TimerTask() {
            @SuppressLint("SuspiciousIndentation")
            public void run() {
                Log.d("FuseService", "=========  " + (counter++));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                      return;
                }
                Log.d("FuseService"," location schedule ");
                Log.d("FuseService", " uuid "+commonClass.getSharedPref(getApplicationContext(),"uuid")+
                        " sync id "+commonClass.getSharedPref(getApplicationContext(),"sync_id"));
                //requestLocationUpdates();
                 createLocationCallback();
                sync_value=0;
                  fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        sync_value = 1;

                        if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"sync_id")) &&
                        !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"uuid"))) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            String date = dateFormat.format(new Date());
                            if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"dsr_date"))){
                                if(!date.equals(commonClass.getSharedPref(getApplicationContext(),"dsr_date"))){
                                    onDestroy();
                                }
                            }
                            ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo netInfo = cm.getActiveNetworkInfo();
                            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                                sendLocationToServer(location.getLatitude(), location.getLongitude());
                            } else {
                                SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                                SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss");
                                attendanceListLocation.insertBulk(dateFormat1.format(new Date()),dateFormat2.format(new Date()),
                                        String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()),
                                        commonClass.getSharedPref(getApplicationContext(),"sync_id"),commonClass.getSharedPref(getApplicationContext(),"m_uuid"));
                             }

                        }else{
                            //added on 20-04-24

                            SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                            SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss");
                            attendanceListLocation.insertBulk(dateFormat1.format(new Date()),dateFormat2.format(new Date()),
                                    String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()),
                                    commonClass.getSharedPref(getApplicationContext(),"sync_id"),commonClass.getSharedPref(getApplicationContext(),"m_uuid"));


                        }
                    }else{
                        stopLocationUpdates();
                        stoptimertask();
                        stopForeground(STOP_FOREGROUND_REMOVE);
                        stopSelf();
                        cancelStopService();
                    }
                });

            }
        };
        timer.schedule(timerTask, 0, 300000); //
    }

    private void requestSingleLocationUpdate() {
        Log.d("FuseService"," request update ");
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


            fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        Log.d("FuseService", "single Location: " + location.getLatitude() + ", " + location.getLongitude());
                        // Here you can handle the obtained location, for example, send it to a server.
                        //sendLocationToServer(location.getLatitude(), location.getLongitude());
                    }
                }
            }, null);
        }
    }

    public void stoptimertask() {
         if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}