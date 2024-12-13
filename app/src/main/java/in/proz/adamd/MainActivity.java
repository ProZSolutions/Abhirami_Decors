package in.proz.adamd;

import static androidx.fragment.app.FragmentManager.TAG;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import in.proz.adamd.ModalClass.ConstructionModal;
import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private final int interval = 5000; // 1 Second
    CommonClass commonClass;
    RelativeLayout outer_layout_3,outer_layout_2,outer_layout_1;
    private static long back_pressed;

     @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            finishAndRemoveTask();
            System.exit(0);
            moveTaskToBack(true);
        } else {
            Toast.makeText(getBaseContext(), "Press once again to exit", Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_new);
        Bundle b = getIntent().getExtras();



        if(b!=null){
            String tim =b.getString("timeelapse");
            if(!TextUtils.isEmpty(tim)){

            }
        }

         commonClass=new CommonClass();
        Log.d("getDeviceID"," is as "+commonClass.getDeviceID(MainActivity.this));
        //TextView welcome=  findViewById(R.id.welcome);
        String mystring=new String("Welcome !");
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
       // welcome.setText(content);
        Log.d("getToken"," token "+commonClass.getSharedPref(getApplicationContext(),"token")+
                " device "+commonClass.getDeviceID(MainActivity.this));
       /* Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);*/
       // outer_layout_3 = findViewById(R.id.outer_layout_3);
       /* outer_layout_2 = findViewById(R.id.outer_layout_2);
        outer_layout_1 = findViewById(R.id.outer_layout_1);*/
        /*applyPulseAnimation(outer_layout_3);
        applyPulseAnimation(outer_layout_2);
        applyPulseAnimation(outer_layout_1);*/

         /*final RippleBackground rippleBackground= findViewById(R.id.content);
        rippleBackground.startRippleAnimation();*/

        if(commonClass.isOnline(MainActivity.this)){
            callVersionStatus();
        }else{
            callIntent();
        }


       /* new Handler().postDelayed(new Runnable() {
            public void run() {


//callIntent();

            }
        }, 5000);*/
    }
    private void callVersionStatus() {
         String versionName = BuildConfig.VERSION_NAME;
        ApiInterface apiInterface  = ApiClient.getTokenWithoutAuth().create(ApiInterface.class);
        Call<ConstructionModal> call = apiInterface.getVersionDetails(versionName);
        Log.d("loginToken"," url "+call.request().url()+" respnse ");
        call.enqueue(new Callback<ConstructionModal>() {
            @Override
            public void onResponse(Call<ConstructionModal> call, Response<ConstructionModal> response) {
                 Log.d("loginToken","response code "+response.code());

                if(response.isSuccessful()){
                    if(response.code()==200){
                        Log.d("getVersionStatud"," status "+response.body().getStatus());
                        if(response.body().getStatus().equals("success")){
                            Log.d("getVersionStatud"," get "+response.body().getCommonPojo().get(0).getApp_version()+
                                    " cons "+response.body().getCommonPojo().get(0).getApp_under_construction());
                            String versionName = BuildConfig.VERSION_NAME;
                            // String versionName ="1";
                            if(!TextUtils.isEmpty(response.body().getCommonPojo().get(0).getApp_version())){
                                if(versionName.equals(response.body().getCommonPojo().get(0).getApp_version())){
                                     callIntent();
                                }else{

                                   /* commonClass.showAppToast(DashboardNewActivity.this,
                                            "App version mismatching. Kindly install latest app from Playstore.");*/
                                    Intent intent = new Intent(getApplicationContext(),ConstrctionClass.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                    intent.putExtra("header","App version mismatching.");
                                    intent.putExtra("sheader","Kindly install latest app from Playstore.");
                                    startActivity(intent);
                                }
                            }
                            //
                            // response.body().getCommonPojo().get(0).setApp_under_construction("1");
                            if(!TextUtils.isEmpty(response.body().getCommonPojo().get(0).getApp_under_construction())){
                                if(response.body().getCommonPojo().get(0).getApp_under_construction().equals("1")){
                                     // commonClass.showAppToast(DashboardNewActivity.this,"App Under Construction!!!");
                                    Intent intent = new Intent(getApplicationContext(),ConstrctionClass.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                    intent.putExtra("header","App Under");
                                    intent.putExtra("sheader","Maintenance");
                                    startActivity(intent);
                                }else{
                                    callIntent();
                                 }
                            }
                        }else{
                             callIntent();
                        }
                    }
                    else if(response.code()==401 || response.code()==403){
                        commonClass.showError(MainActivity.this,"UnAuthendicated");
                        commonClass.clearAlldata(MainActivity.this);
                    }
                }else if(response.code()==401 || response.code()==403){
                    commonClass.showError(MainActivity.this,"UnAuthendicated");
                    commonClass.clearAlldata(MainActivity.this);
                }
            }

            @Override
            public void onFailure(Call<ConstructionModal> call, Throwable t) {
             }
        });



    }

    private void callIntent() {
        if(TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"token"))){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        }else{
                    /*Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                    startActivity(intent);*/
            Intent intent = new Intent(getApplicationContext(), DashboardNewActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);

        }
    }


    private void applyPulseAnimation(View view) {
        ScaleAnimation pulseAnimation = new ScaleAnimation(
                1.0f, 1.01f, // X-axis scaling from 1.0 to 1.2
                1.0f, 1.01f, // Y-axis scaling from 1.0 to 1.2
                Animation.RELATIVE_TO_SELF, 0.1f, // Pivot point X (center)
                Animation.RELATIVE_TO_SELF, 0.1f  // Pivot point Y (center)
        );

        pulseAnimation.setDuration(1000); // Set the duration of one pulse (in milliseconds)
        pulseAnimation.setRepeatCount(Animation.INFINITE); // Infinite repeat

        view.startAnimation(pulseAnimation);
    }
 public void firstFunction() {
        final Animation out = new AlphaAnimation(1.0f, 1.5f);
        out.setDuration(2000);

        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                secondFunction();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        out.setRepeatCount(1);
        out.setRepeatMode(ValueAnimator.REVERSE);
        out.start();
        outer_layout_1.startAnimation(out);
    }
    public void secondFunction() {
        final Animation out = new AlphaAnimation(1.5f, 1.0f);
        out.setDuration(2000);

        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
             // firstFunction();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        out.setRepeatCount(1);
        out.setRepeatMode(ValueAnimator.REVERSE);
        out.start();
        outer_layout_2.startAnimation(out);
    }

}