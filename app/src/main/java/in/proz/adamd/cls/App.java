package in.proz.adamd.cls;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class App extends Application {
    public static final String Channel_Id="Payroll_Channel";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        Create_Notification_Basic();

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void Create_Notification_Basic(){
        if(Build.VERSION.SDK_INT==Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel(
                    Channel_Id,"Payroll", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("");
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

    }
}
