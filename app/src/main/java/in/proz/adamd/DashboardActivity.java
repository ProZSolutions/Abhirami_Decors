package in.proz.adamd;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

 import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;
import com.tuyenmonkey.mkloader.MKLoader;

import org.aviran.cookiebar2.CookieBar;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import in.proz.adamd.AdminModule.AdminEmployee.AdminEmpActivityNew;
import in.proz.adamd.AdminModule.AdminNewApprovals;
import in.proz.adamd.AdminModule.AdminNewDashboard;
import in.proz.adamd.Attendance.AttendanceActivity;
import in.proz.adamd.AttendanceSQLite.AttendanceListLocation;
import in.proz.adamd.AttendanceSQLite.PunchINOUTDB;
import in.proz.adamd.AttendanceUploadModal.GeoModal;
import in.proz.adamd.AttendanceUploadModal.INTimeOutTimeModal;
import in.proz.adamd.AttendanceUploadModal.UploadGeoModal;
import in.proz.adamd.AttendanceUploadModal.UploadTimeModal;
import in.proz.adamd.BackgroundTask.YourService;
import in.proz.adamd.Calendar.CalendarActivity;
import in.proz.adamd.Claim.ClaimActivity;
import in.proz.adamd.DSR.DSRActivity;
import in.proz.adamd.Leave.LeaveActivity;
import in.proz.adamd.Loan.LoanActivity;
import in.proz.adamd.Meeting.MeetingActivity;
import in.proz.adamd.ModalClass.ConstructionModal;
import in.proz.adamd.ModalClass.PersonalMainModal;
import in.proz.adamd.Profile.ProfileActivity;
import in.proz.adamd.Request.RequestActivity;
import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "BackgroundTask";
    private Handler handler;
    private Runnable runnable;

    LinearLayout data_analysis,attendance,claim,loan,admin_main_layout;
    ImageView back_arrrow;
    CommonClass commonClass = new CommonClass();
    TextView versioncode;
    private final static int ID_HOME = 1;
    private final static int ID_EXPLORE = 2;
    private final static int ID_MESSAGE = 3;
    private final static int ID_NOTIFICATION = 4;
    RelativeLayout leave_layout,attendance_layute,requirements,loan_layout,profileLayout,assetslayout,calendarLayout,
            dsrLayout,meeting,adminsummary;
    ImageView logout;
    CircleImageView profile_img;
    TextView name,designation;
    ProgressDialog progressDialog;



    //notification Class
    public static NotificationManagerCompat manager;
    public static Notification.Builder builder;
    MKLoader picloader,loader1;
    public static  NotificationCompat.Builder builder1;
    LinearLayout online_layout;
    RelativeLayout linear2,linear3;
     ImageView online_icon;
    TextView online_text;
    int construction =0 ;


    @Override
    protected void onDestroy() {
        super.onDestroy();
     }
    public boolean foregroundServiceRunning(){
        Log.d("FuseService"," fore gound called ");
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if(YourService.class.getName().equals(service.service.getClassName())) {
                Log.d("FuseService"," foregrond running ");
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdashboard_activity);
        Log.d("callLogut"," on create new ");
        handler = new Handler(Looper.getMainLooper());

        foregroundServiceRunning();

        // Initialize and start the background task




        // Enqueue the work requeston


        String versionName = BuildConfig.VERSION_NAME;
        versioncode=findViewById(R.id.versioncode);
        versioncode.setText("Version Code : "+versionName);
        progressDialog = new ProgressDialog(DashboardActivity.this);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        picloader = findViewById(R.id.loader);
        loader1 = findViewById(R.id.loader1);

        online_icon = findViewById(R.id.online_icon);
        online_layout = findViewById(R.id.online_layout);
        online_text = findViewById(R.id.online_text);
        commonClass.onlineStatusCheck(DashboardActivity.this,online_layout,online_text,online_icon);


        Log.d("getTokenDetails" ," token "+commonClass.getSharedPref(DashboardActivity.this,"token")
        +" device id "+commonClass.getDeviceID(DashboardActivity.this));

        Dexter.withContext(DashboardActivity.this)
                .withPermissions(
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_DOCUMENTS,
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.USE_BIOMETRIC,
                        Manifest.permission.USE_FINGERPRINT,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.POST_NOTIFICATIONS,
                        Manifest.permission.ACCESS_FINE_LOCATION).withListener(new MultiplePermissionsListener() {
                    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}
                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                }).check();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager = NotificationManagerCompat.from(this);
            callManagerCalls();
        }




        initView();


        Log.d("loginToken"," as "+commonClass.getSharedPref(getApplicationContext(),"token"));

        if(commonClass.isOnline(DashboardActivity.this)){
            syncOfflineToOnline();
        }
/*
        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                Toast.makeText(MainActivity.this, "showing item : " + item.getId(), Toast.LENGTH_SHORT).show();

                String name;
                switch (item.getId()) {
                    case ID_HOME:
                        name = "HOME";
                        break;
                    case ID_EXPLORE:
                        name = "EXPLORE";
                        break;
                    case ID_MESSAGE:
                        name = "MESSAGE";
                        break;
                    case ID_NOTIFICATION:
                        name = "NOTIFICATION";
                        break;
                    case ID_ACCOUNT:
                        name = "ACCOUNT";
                        break;
                    default:
                        name = "";
                }
                tvSelected.setText(getString(R.string.main_page_selected, name));
            }
        });
*/



        if(commonClass.isOnline(DashboardActivity.this)){
            callVersionStatus();
        }
    }

    private void syncOfflineToOnline() {

        AttendanceListLocation attendanceListLocation = new AttendanceListLocation(DashboardActivity.this);
        attendanceListLocation.getWritableDatabase();
        List<GeoModal> geoModals = attendanceListLocation.selectAllData();

        Log.d("GEOTAG"," modal size "+geoModals.size());
        if(geoModals.size()!=0){
            UploadGeoModal uploadGeoModal = new UploadGeoModal();
            uploadGeoModal.setData(geoModals);
            Gson gson = new Gson();
            String jsonArray = gson.toJson(uploadGeoModal);
            Log.d("GEOTAG"," json arr "+jsonArray);
              loader1.setVisibility(View.VISIBLE);
            ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(DashboardActivity.this,"token"),
                    commonClass.getDeviceID(DashboardActivity.this)).create(ApiInterface.class);
            Call<CommonPojo> call = apiInterface.
                    uploadGeoTag(uploadGeoModal);
            Log.d("GEOTAG"," geo url as "+call.request().url());
            call.enqueue(new Callback<CommonPojo>() {
                @Override
                public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                    loader1.setVisibility(View.GONE);
                    Log.d("loginToken"," location response code "+response.code());
                    if(response.isSuccessful()){
                            if(response.code()==200){
                                if (response.body().getStatus().equals("success")){
                                    attendanceListLocation.deleteAll(DashboardActivity.this);
                                }
                            }else if(response.code()==401 || response.code()==403){
                                commonClass.showError(DashboardActivity.this,"UnAuthendicated");
                                commonClass.clearAlldata(DashboardActivity.this);
                            }else{
                                Gson gson = new GsonBuilder().create();
                                CommonPojo mError = new CommonPojo();
                                try {
                                    mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);
                                    Log.d("GEOTAG"," getError "+mError.getError());
                                    //  commonClass.showServerToast(DashboardActivity.this,mError.getError());
                                    //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                                } catch (IOException e) {
                                    // handle failure to read error
                                    Log.d("GEOTAG", " location exp error  " + e.getMessage());
                                }
                            }
                    }else {
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);
                            Log.d("GEOTAG"," location getError "+mError.getError());
                            //  commonClass.showServerToast(DashboardActivity.this,mError.getError());
                            //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            // handle failure to read error
                            Log.d("GEOTAG", " location exp error  " + e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(Call<CommonPojo> call, Throwable t) {
                    Log.d("GEOTAG"," location error "+t.getMessage());
                }
            });
        }

        List<INTimeOutTimeModal>  inTimeOutTimeModals = new ArrayList<>();
        List<INTimeOutTimeModal>  OutTimeModals = new ArrayList<>();
        PunchINOUTDB punchINOUTDB = new PunchINOUTDB(DashboardActivity.this);
        inTimeOutTimeModals =  punchINOUTDB.selectAllData(1);
        OutTimeModals =  punchINOUTDB.selectAllData(2);
        if(inTimeOutTimeModals.size()!=0){

            UploadTimeModal uploadTimeModal = new UploadTimeModal();
            uploadTimeModal.setInTimeOutTimeModalList(inTimeOutTimeModals);
            Gson gson = new Gson();
            String jsonArray = gson.toJson(uploadTimeModal);
            Log.d("GEOTAG"," json arr "+jsonArray);
            loader1.setVisibility(View.VISIBLE);
            ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(DashboardActivity.this,"token"),
                    commonClass.getDeviceID(DashboardActivity.this)).create(ApiInterface.class);
            Call<CommonPojo> call = apiInterface.uploadCheckInTime(uploadTimeModal);
            Log.d("GEOTAG"," intime get intome url "+call.request().url());
            call.enqueue(new Callback<CommonPojo>() {
                @Override
                public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                    loader1.setVisibility(View.GONE);
                    Log.d("loginToken"," outtime cod "+response.code());
                    if(response.isSuccessful()){
                        if(response.code()==200){
                            punchINOUTDB.updateAllRecords();
                        }else if(response.code()==401 || response.code()==403){
                            commonClass.showError(DashboardActivity.this,"UnAuthendicated");
                            commonClass.clearAlldata(DashboardActivity.this);
                        }else{
                            Gson gson = new GsonBuilder().create();
                            CommonPojo mError = new CommonPojo();
                            try {
                                mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);
                                Log.d("GEOTAG"," intime getError "+mError.getError());
                                //  commonClass.showServerToast(DashboardActivity.this,mError.getError());
                                //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                // handle failure to read error
                                Log.d("GEOTAG", "  intime exp error  " + e.getMessage());
                            }
                        }
                    }
                    else if(response.code()==401 || response.code()==403){
                        commonClass.showError(DashboardActivity.this,"UnAuthendicated");
                        commonClass.clearAlldata(DashboardActivity.this);
                    }else{
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);
                            Log.d("GEOTAG"," getError "+mError.getError());
                            //  commonClass.showServerToast(DashboardActivity.this,mError.getError());
                            //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            // handle failure to read error
                            Log.d("GEOTAG", " exp error  " + e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(Call<CommonPojo> call, Throwable t) {
                    loader1.setVisibility(View.GONE);
                    Log.d("GEOTAG"," intime  error "+t.getMessage());
                }
            });
        }


        if(OutTimeModals.size()!=0){

            UploadTimeModal uploadTimeModal = new UploadTimeModal();
            uploadTimeModal.setInTimeOutTimeModalList(OutTimeModals);
            Gson gson = new Gson();
            String jsonArray = gson.toJson(uploadTimeModal);
            Log.d("GEOTAG"," json arr "+jsonArray);
            loader1.setVisibility(View.VISIBLE);
            ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(DashboardActivity.this,"token"),
                    commonClass.getDeviceID(DashboardActivity.this)).create(ApiInterface.class);
            Call<CommonPojo> call = apiInterface.uploadCheckOutTime(uploadTimeModal);
            Log.d("loginToken"," out time get outtime url "+call.request().url());
            call.enqueue(new Callback<CommonPojo>() {
                @Override
                public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                    loader1.setVisibility(View.GONE);
                    Log.d("loginToken"," out time response code "+response.code());
                    if(response.isSuccessful()){
                        if(response.code()==200){
                            punchINOUTDB.updateAllRecords();
                        }else if(response.code()==401 || response.code()==403){
                            commonClass.showError(DashboardActivity.this,"UnAuthendicated");
                            commonClass.clearAlldata(DashboardActivity.this);
                        }else{
                            Gson gson = new GsonBuilder().create();
                            CommonPojo mError = new CommonPojo();
                            try {
                                mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);
                                Log.d("GEOTAG"," outtime getError "+mError.getError());
                                //  commonClass.showServerToast(DashboardActivity.this,mError.getError());
                                //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                // handle failure to read error
                                Log.d("GEOTAG", " exp error  " + e.getMessage());
                            }
                        }
                    }
                    else if(response.code()==401 || response.code()==403){
                        commonClass.showError(DashboardActivity.this,"UnAuthendicated");
                        commonClass.clearAlldata(DashboardActivity.this);
                    }else{
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);
                            Log.d("GEOTAG"," outime  getError "+mError.getError());
                            //  commonClass.showServerToast(DashboardActivity.this,mError.getError());
                            //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            // handle failure to read error
                            Log.d("GEOTAG", " outtim exp error  " + e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(Call<CommonPojo> call, Throwable t) {
                    loader1.setVisibility(View.GONE);
                    Log.d("GEOTAG"," outtime error "+t.getMessage());
                }
            });
        }






    }

    private void callVersionStatus() {
        loader1.setVisibility(View.VISIBLE);
        String versionName = BuildConfig.VERSION_NAME;
        ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(DashboardActivity.this,"token"),
                commonClass.getDeviceID(DashboardActivity.this)).create(ApiInterface.class);
        Call<ConstructionModal> call = apiInterface.getVersionDetails(versionName);
        Log.d("loginToken"," url "+call.request().url()+" respnse ");
        call.enqueue(new Callback<ConstructionModal>() {
            @Override
            public void onResponse(Call<ConstructionModal> call, Response<ConstructionModal> response) {
                loader1.setVisibility(View.GONE);
                Log.d("loginToken","response code "+response.code());

                if(response.isSuccessful()){
                    if(response.code()==200){
                        Log.d("getVersionStatud"," status "+response.body().getStatus());
                        if(response.body().getStatus().equals("success")){
                            Log.d("getVersionStatud"," get "+response.body().getCommonPojo().get(0).getApp_version()+
                                    " cons "+response.body().getCommonPojo().get(0).getApp_under_construction());
                           String versionName = BuildConfig.VERSION_NAME;
                           Log.d("getVersionStatud"," version name "+versionName+" code "+versioncode.getText().toString());
                           // String versionName ="1";
                            if(!TextUtils.isEmpty(response.body().getCommonPojo().get(0).getApp_version())){
                                if(versionName.equals(response.body().getCommonPojo().get(0).getApp_version())){
                                    construction=1;
                                }else{
                                    construction =0;
                                   /* commonClass.showAppToast(DashboardActivity.this,
                                            "App version mismatching. Kindly install latest app from Playstore.");*/
                                    Intent intent = new Intent(getApplicationContext(),ConstrctionClass.class);
                                    intent.putExtra("header","App version mismatching.");
                                    intent.putExtra("sheader","Kindly install latest app from Playstore.");
                                    startActivity(intent);
                                }
                            }
                          //
                             // response.body().getCommonPojo().get(0).setApp_under_construction("1");
                            if(!TextUtils.isEmpty(response.body().getCommonPojo().get(0).getApp_under_construction())){
                                if(response.body().getCommonPojo().get(0).getApp_under_construction().equals("1")){
                                    construction = 0;
                                   // commonClass.showAppToast(DashboardActivity.this,"App Under Construction!!!");
                                    Intent intent = new Intent(getApplicationContext(),ConstrctionClass.class);
                                    intent.putExtra("header","App Under");
                                    intent.putExtra("sheader","Maintenance");
                                    startActivity(intent);
                                }else{
                                    construction=1;
                                }
                            }
                        }else{
                            construction =1;
                        }
                    }
                    else if(response.code()==401 || response.code()==403){
                        commonClass.showError(DashboardActivity.this,"UnAuthendicated");
                        commonClass.clearAlldata(DashboardActivity.this);
                    }
                }else if(response.code()==401 || response.code()==403){
                    commonClass.showError(DashboardActivity.this,"UnAuthendicated");
                    commonClass.clearAlldata(DashboardActivity.this);
                }
            }

            @Override
            public void onFailure(Call<ConstructionModal> call, Throwable t) {
                loader1.setVisibility(View.GONE);
            }
        });



    }

    private void callManagerCalls() {
        // try {
        Log.d("get_notification_error"," punch in value "+commonClass.getSharedPref(getApplicationContext(),"punch_in"));
        if (!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(), "punch_in"))) {
          //  showNotifications();
            // notificationNew();
            int yr, mnth, dy;
            Calendar calendar = Calendar.getInstance();

            yr = calendar.get(Calendar.YEAR);
            mnth = calendar.get(Calendar.MONTH)+1;
            dy = calendar.get(Calendar.DAY_OF_MONTH);
            String today_date = dy+"-"+mnth+"-"+yr;
            Log.d("date_list"," today  "+today_date+" punch in "+commonClass.getSharedPref(getApplicationContext(),"punch_in"));
            if(commonClass.getSharedPref(getApplicationContext(),"punch_in").equals(today_date)){
                int reqCode = 1;
                Intent intent = new Intent(getApplicationContext(), AttendanceActivity.class);
                showNotification(this, "Attendance", "Don't Forget to Check Out!!!", intent, reqCode);
            }else{
                commonClass.putSharedPref(getApplicationContext(),"punch_in",null);
            }

        } else {
            try {
                manager.cancelAll();
            } catch (Exception ex) {
                Log.d("get_notification_error"," inner ex "+ex.getMessage());
                //  is_Attendance_Checking = false;
            }
        }
      /*  }catch (Exception ex){
            Log.d("get_notification_error","error "+ex.getMessage());
            Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_SHORT).show();
        }*/
    }

    public void showNotification(Context context, String title, String message, Intent intent, int reqCode) {

        CookieBar.build(this)
                .setTitle(title)
                .setTitleColor(R.color.white)
                .setBackgroundColor(R.color.black)
                .setIcon(R.drawable.att_clock1)
                 .setMessage(message)
          .setDuration(5000) // 5 seconds
                .show();
    }



    private void initView() {
        linear2 = findViewById(R.id.linear2);
        linear3 = findViewById(R.id.linear3);
        linear3.setOnClickListener(this);
        linear2.setOnClickListener(this);
        admin_main_layout = findViewById(R.id.admin_main_layout);
        Log.d("admincresent"," admin emp no "+commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")+
                " role "+commonClass.getSharedPref(getApplicationContext(),"AdminRole")+" name "+
                commonClass.getSharedPref(getApplicationContext(),"AdminName"));
        if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
        !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
            admin_main_layout.setVisibility(View.VISIBLE);
        }else{
            admin_main_layout.setVisibility(View.GONE);
        }
        adminsummary = findViewById(R.id.adminsummary);
        adminsummary.setOnClickListener(this);
        meeting= findViewById(R.id.meeting);
        meeting.setOnClickListener(this);
        dsrLayout = findViewById(R.id.dsrLayout);
        dsrLayout.setOnClickListener(this);
        name = findViewById(R.id.name);
        assetslayout= findViewById(R.id.assetslayout);
        calendarLayout= findViewById(R.id.calendarLayout);
        calendarLayout.setOnClickListener(this);
        designation = findViewById(R.id.designation);
        profileLayout = findViewById(R.id.profileLayout);
        profileLayout.setOnClickListener(this);
        loan_layout = findViewById(R.id.loan_layout);
        loan_layout.setOnClickListener(this);
        leave_layout = findViewById(R.id.leave_layout);
        leave_layout.setOnClickListener(this);
        attendance_layute = findViewById(R.id.attendance_layute);
        attendance_layute.setOnClickListener(this);
        profile_img = findViewById(R.id.profile_img);
        profile_img.setOnClickListener(this);
        requirements = findViewById(R.id.requirements);
        requirements.setOnClickListener(this);
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(this);
        assetslayout.setOnClickListener(this);
        assetslayout.setOnClickListener(this);

        if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"name"))){
            name.setText(commonClass.getSharedPref(getApplicationContext(),"name"));
                designation.setText(commonClass.getSharedPref(getApplicationContext(),"designation"));
            picloader.setVisibility(View.VISIBLE);
            Picasso.with(DashboardActivity.this).load(commonClass.getSharedPref(getApplicationContext(),"image")).into(profile_img, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    picloader.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    picloader.setVisibility(View.GONE);
                }
            });
            getList(null);

        }else{
            getList(progressDialog);

        }



    }

    @Override
    public void onClick(View view) {
         int id = view.getId();
        switch (id){
            case R.id.linear3:
                if(construction==1){
                    List<Integer> projectListID = new ArrayList<>();
                    //projectListID.add(0);
                    List<String> selectedDeptID = new ArrayList<>();
                    //selectedDeptID.add("ALL");
                    SimpleDateFormat serverDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat usableDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String sd = serverDateFormat.format(new Date());
                    String ud = usableDateFormat.format(new Date());


                    Intent intent = new Intent(getApplicationContext(), AdminNewApprovals.class);
                    intent.putExtra("position","ALL");
                    intent.putExtra("branch","ALL");
                    intent.putExtra("dash","dash");
                    intent.putExtra("serverDate",sd);
                    intent.putExtra("usableDate",ud);
                    intent.putExtra("projectID",(Serializable) projectListID);
                    intent.putExtra("department",(Serializable) selectedDeptID) ;
                    startActivity(intent);
                }
                break;
            case R.id.linear2:
                if(construction==1) {
                    Intent adminemp = new Intent(getApplicationContext(), AdminEmpActivityNew.class);
                    startActivity(adminemp);
                }
                break;
            case R.id.adminsummary:
                if(construction==1) {
                    Intent adminsummary = new Intent(getApplicationContext(), AdminNewDashboard.class);
                    startActivity(adminsummary);
                }
                break;
            case R.id.meeting:
                if(construction==1) {
                    Intent meeting = new Intent(getApplicationContext(), MeetingActivity.class);
                    startActivity(meeting);
                }
                break;
            case R.id.dsrLayout:
                if(construction==1) {
                    Intent dsr = new Intent(getApplicationContext(), DSRActivity.class);
                    startActivity(dsr);
                }
                break;
            case R.id.calendarLayout:
                if(construction==1) {
                    Intent calendar = new Intent(getApplicationContext(), CalendarActivity.class);
                    startActivity(calendar);
                }
                break;
            case R.id.logout:
                if(commonClass.isOnline(DashboardActivity.this)) {
                    if (construction == 1) {
                        AlertDialog.Builder laert = new AlertDialog.Builder(this);
                        laert.setMessage("Would you like to logout?");
                        laert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                commonClass.putSharedPref(getApplicationContext(), "token", null);
                                commonClass.putSharedPref(getApplicationContext(), "AdminEmpNo", null);
                                commonClass.putSharedPref(getApplicationContext(), "AdminName", null);
                                commonClass.putSharedPref(getApplicationContext(), "AdminRole", null);
                                commonClass.putSharedPref(getApplicationContext(), "AdminRoleName", null);

                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            }
                        });
                        laert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        laert.create();
                        laert.show();
                    }
                }else{
                    commonClass.showError(DashboardActivity.this,"Please Make Sure Internet Available");
                }
                break;
            case R.id.loan_layout:
                if(construction==1) {
                    Intent loan = new Intent(getApplicationContext(), LoanActivity.class);
                    startActivity(loan);
                }
                break;
            case R.id.assetslayout:
               /* if(construction==1) {
                    Intent intent123 = new Intent(getApplicationContext(), RequestActivity.class);
                    startActivity(intent123);
                }*/


                if(construction==1) {
                    Dialog dialog = new Dialog(DashboardActivity.this);
                    dialog.setContentView(R.layout.custom_asset_radio);
                    dialog.setTitle("This is my custom dialog box");
                    dialog.setCancelable(true);
                    // there are a lot of settings, for dialog, check them all out!
                    // set up radiobutton
                    RadioButton rd1 = (RadioButton) dialog.findViewById(R.id.rd_1);
                    RadioButton rd2 = (RadioButton) dialog.findViewById(R.id.rd_2);
                    rd1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b) {
                                rd2.setChecked(false);
                            }
                        }
                    });
                    rd2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b) {
                                rd1.setChecked(false);
                            }
                        }
                    });
                    Button btn_continue1 = dialog.findViewById(R.id.btn_continue);
                    // now that the dialog is set up, it's time to show it
                    dialog.show();
                    btn_continue1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (rd1.isChecked() || rd2.isChecked()) {
                                String type = "0";
                                if (rd1.isChecked()) {
                                    type = "asset";
                                }
                                if (rd2.isChecked()) {
                                    type = "ticket";
                                }
                                Intent intent123 = new Intent(getApplicationContext(), RequestActivity.class);
                                intent123.putExtra("request_type", type);
                                startActivity(intent123);
                            } else {
                                commonClass.showWarning(DashboardActivity.this, "Please Select any option");
                            }
                        }
                    });
                }


               /* Dialog dialog_asset = new Dialog(DashboardActivity.this);
                dialog_asset.setContentView(R.layout.custom_asset_radio);
                dialog_asset.setTitle("This is my custom dialog box");
                dialog_asset.setCancelable(true);
                // there are a lot of settings, for dialog, check them all out!
                // set up radiobutton
                RadioButton rd1dialog_asset = (RadioButton) dialog_asset.findViewById(R.id.rd_1);
                RadioButton rd2dialog_asset = (RadioButton) dialog_asset.findViewById(R.id.rd_2);
                rd1dialog_asset.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b){
                            rd2dialog_asset.setChecked(false);
                        }
                    }
                });
                rd2dialog_asset.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b){
                            rd1dialog_asset.setChecked(false);
                        }
                    }
                });
                Button btn_continue = dialog_asset.findViewById(R.id.btn_continue);
                // now that the dialog is set up, it's time to show it
                dialog_asset.show();
                btn_continue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(rd1dialog_asset.isChecked() || rd2dialog_asset.isChecked()){
                             if(rd1dialog_asset.isChecked()){
                                 Intent intent123= new Intent(getApplicationContext(), AssetActivity.class);
                                  startActivity(intent123);
                             }else {
                                 Intent intent123= new Intent(getApplicationContext(), RequestActivity.class);
                                  startActivity(intent123);
                             }

                        }else {
                            commonClass.showWarning(DashboardActivity.this,"Please Select any option");
                        }
                    }
                });*/
                break;
            case R.id.requirements:
                if(construction==1) {
                    Dialog dialog = new Dialog(DashboardActivity.this);
                    dialog.setContentView(R.layout.custom_claim_radio);
                    dialog.setTitle("This is my custom dialog box");
                    dialog.setCancelable(true);
                    // there are a lot of settings, for dialog, check them all out!
                    // set up radiobutton
                    RadioButton rd1 = (RadioButton) dialog.findViewById(R.id.rd_1);
                    RadioButton rd2 = (RadioButton) dialog.findViewById(R.id.rd_2);
                    rd1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b) {
                                rd2.setChecked(false);
                            }
                        }
                    });
                    rd2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b) {
                                rd1.setChecked(false);
                            }
                        }
                    });
                    Button btn_continue1 = dialog.findViewById(R.id.btn_continue);
                    // now that the dialog is set up, it's time to show it
                    dialog.show();
                    btn_continue1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (rd1.isChecked() || rd2.isChecked()) {
                                String type = "0";
                                if (rd1.isChecked()) {
                                    type = "claim";
                                }
                                if (rd2.isChecked()) {
                                    type = "advance_claim";
                                }
                                Intent intent123 = new Intent(getApplicationContext(), ClaimActivity.class);
                                intent123.putExtra("claim_type", type);
                                startActivity(intent123);
                            } else {
                                commonClass.showWarning(DashboardActivity.this, "Please Select any option");
                            }
                        }
                    });
                }
                break;
            case R.id.profileLayout:
                if(construction==1) {
                    Intent inten21 = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(inten21);
                }
                break;
            case R.id.profile_img:

                break;
            case R.id.leave_layout:
                if(construction==1) {
                    Intent inten = new Intent(getApplicationContext(), LeaveActivity.class);
                    startActivity(inten);
                }
                break;
            case R.id.attendance_layute:
              //  if(construction==1) {
                    Intent inten1 = new Intent(getApplicationContext(), AttendanceActivity.class);
                    startActivity(inten1);
              //  }
                break;
        }
    }



    public void getList(ProgressDialog progressDialog){
        if(progressDialog!=null){
            progressDialog.show();
        }
        picloader.setVisibility(View.VISIBLE);

        ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(DashboardActivity.this,"token"),
                commonClass.getDeviceID(DashboardActivity.this)).create(ApiInterface.class);
        Call<PersonalMainModal> call = apiInterface.getUserProfile();
        Log.d("profile_index"," url as "+call.request().url()+commonClass.getDeviceID(DashboardActivity.this)+
                " token "+commonClass.getSharedPref(getApplicationContext(),"token"));
        call.enqueue(new Callback<PersonalMainModal>() {
            @Override
            public void onResponse(Call<PersonalMainModal> call, Response<PersonalMainModal> response) {
                if(progressDialog!=null){
                    progressDialog.dismiss();
                }
                Log.d("loginToken"," ode "+response.code());
                if(response.isSuccessful()){
                    if(response.code()==200){
                        Log.d("profile_index"," name "+response.body().getPersonalModal().getName());
                        commonClass.putSharedPref(getApplicationContext(),"emp_id",response.body().getPersonalModal().getId());
                        commonClass.putSharedPref(getApplicationContext(),"profile_id",response.body().getPersonalModal().getId());
                        commonClass.putSharedPref(getApplicationContext(),"name",response.body().getPersonalModal().getName());
                        commonClass.putSharedPref(getApplicationContext(),"designation",response.body().getPersonalModal().getDesignation());
                        commonClass.putSharedPref(getApplicationContext(),"image",response.body().getPersonalModal().getImage());
                        if(!TextUtils.isEmpty(response.body().getPersonalModal().getImage())){
                            Picasso.with(DashboardActivity.this).load(response.body().getPersonalModal().getImage()).
                                    into(profile_img, new com.squareup.picasso.Callback() {
                                        @Override
                                        public void onSuccess() {
                                            picloader.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onError() {
                                            picloader.setVisibility(View.GONE);
                                        }
                                    });
                        }
                        name.setText(response.body().getPersonalModal().getName());
                        designation.setText(response.body().getPersonalModal().getDesignation());


                    }
                    else if(response.code()==401 || response.code()==403){
                        commonClass.showError(DashboardActivity.this,"UnAuthendicated");
                        commonClass.clearAlldata(DashboardActivity.this);
                    }else{
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                          //  commonClass.showServerToast(DashboardActivity.this,mError.getError());
                            //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            // handle failure to read error
                            Log.d("thumbnail_url", " exp error  " + e.getMessage());
                        }
                    }
                }
                else if(response.code()==401 || response.code()==403){
                    commonClass.showError(DashboardActivity.this,"UnAuthendicated");
                    commonClass.clearAlldata(DashboardActivity.this);
                }else{
                    Gson gson = new GsonBuilder().create();
                    CommonPojo mError = new CommonPojo();
                    try {
                        mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                        //commonClass.showServerToast(DashboardActivity.this,mError.getError());
                        //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // handle failure to read error
                        Log.d("thumbnail_url", " exp error  " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<PersonalMainModal> call, Throwable t) {
                if(progressDialog!=null){
                    progressDialog.show();
                }                //commonClass.showServerToast(DashboardActivity.this,t.getMessage());
            }
        });

    }
}