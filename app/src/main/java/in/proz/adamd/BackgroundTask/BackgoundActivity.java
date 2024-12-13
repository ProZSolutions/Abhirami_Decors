package in.proz.adamd.BackgroundTask;

 import in.proz.adamd.R;
 import in.proz.adamd.Retrofit.CommonClass;

 import androidx.appcompat.app.AppCompatActivity;

 import android.app.ActivityManager;
 import android.content.Context;
 import android.content.Intent;
 import android.os.Bundle;
 import android.text.TextUtils;
 import android.util.Log;

public class BackgoundActivity extends AppCompatActivity {

    Intent mServiceIntent;
    private YourService mYourService;
   // private FucedLocationServiceClass mYourService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Check for location permission
        mYourService = new YourService();
        mServiceIntent = new Intent(this, mYourService.getClass());
        if (!isMyServiceRunning(mYourService.getClass())) {
            Log.d("FuseService"," service not runnning ");
            startService(mServiceIntent);
         }else{
            Log.d("FuseService"," service running ");
        }


      //  this.stopService(mServiceIntent);

    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.d("FuseService", "servicr Running");
                return true;
            }
        }
        Log.d ("FuseService", "servic Not running");
        return false;
    }
    @Override
    protected void onDestroy() {
        //stopService(mServiceIntent);
        Log.d("FuseService"," destroy service");
        CommonClass commonClass=new CommonClass();
        if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"uuid")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"sync_id"))){
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("restartservice");
            broadcastIntent.setClass(this, Restarter.class);
            this.sendBroadcast(broadcastIntent);
        }
        super.onDestroy();
    }


}
