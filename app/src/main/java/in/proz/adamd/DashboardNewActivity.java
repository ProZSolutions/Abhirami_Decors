package in.proz.adamd;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;
import com.tuyenmonkey.mkloader.MKLoader;

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
import in.proz.adamd.AdminModule.AdminWSRLayout;
import in.proz.adamd.Attendance.AttendanceActivity;
import in.proz.adamd.AttendanceSQLite.AttendanceListLocation;
import in.proz.adamd.AttendanceSQLite.PunchINOUTDB;
import in.proz.adamd.AttendanceUploadModal.GeoModal;
import in.proz.adamd.AttendanceUploadModal.INTimeOutTimeModal;
import in.proz.adamd.AttendanceUploadModal.UploadGeoModal;
import in.proz.adamd.AttendanceUploadModal.UploadTimeModal;
import in.proz.adamd.BackgroundTask.Restarter;
import in.proz.adamd.Calendar.CalendarActivity;
import in.proz.adamd.Claim.ClaimActivity;
import in.proz.adamd.DSR.DSRActivity;
import in.proz.adamd.Face.FaceModal;
import in.proz.adamd.FaceAuth.SimilarityClassifier;
import in.proz.adamd.Leave.LeaveActivity;
import in.proz.adamd.Loan.LoanActivity;
import in.proz.adamd.Map.MapCurrentLocation;
import in.proz.adamd.Meeting.MeetingActivity;
import in.proz.adamd.ModalClass.ConstructionModal;
import in.proz.adamd.ModalClass.PersonalMainModal;
import in.proz.adamd.NotesActivity.NotesActivity;
import in.proz.adamd.OnDuty.OnDuty;
import in.proz.adamd.OverTime.OverTime;
import in.proz.adamd.Profile.ProfileActivity;
import in.proz.adamd.Request.RequestActivity;
import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;
import in.proz.adamd.SQLiteDB.BranchTable;
import in.proz.adamd.SQLiteDB.FaceAuthDB;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardNewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "BackgroundTask";
    private Handler handler;
    TextView ess_tag;
    LinearLayout ess_lay;
    String SendToID,SendToName;
    LinearLayout overtime;

    TextView tag1,tag2;
    private Runnable runnable;
    List<String> requestList = new ArrayList<>();
    boolean[] selectedNames;

    LinearLayout data_analysis,attendance,claim,loan,admin_main_layout,bottom_tag_lay;
    ImageView back_arrrow;
    CommonClass commonClass = new CommonClass();
    TextView versioncode;
    private final static int ID_HOME = 1;
    private final static int ID_EXPLORE = 2;
    private final static int ID_MESSAGE = 3;
    private final static int ID_NOTIFICATION = 4;
             ;
    LinearLayout ticketsLayout,assetslayout,meeting,adminsummary,linear2,linear3,employee_layout,
            attendance_layute,leave_layout,requirements,loan_layout,dsrLayout,calendarLayout,profileLayout,attendance_layute1;
   // ImageView logout;
    CircleImageView profile_img;
    TextView name,designation;
    ProgressDialog progressDialog;
    ImageView profile_imge;



    //notification Class
    public static NotificationManagerCompat manager;
    public static Notification.Builder builder;
    MKLoader picloader,loader1;
    public static  NotificationCompat.Builder builder1;
    LinearLayout online_layout;

    ImageView online_icon;
    TextView online_text;
    int construction =0 ;
    LinearLayout nhome_layout,nreports_layout,nlocation_layout,wsr_layout,nprofile_layout,employee_management,requestLayout;
    ImageView nhome_icon;
    TextView nhome_text;
    @Override
    protected void onResume() {
        super.onResume();
        // Register the BootReceiver dynamically at runtime
        IntentFilter filter = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
        Restarter receiver = new Restarter();
        registerReceiver(receiver, filter);


        Log.d("onRegisterCalled"," resu ");
    }

    private void sendDeepLinkViaWhatsApp() {
        // Define the deep link URL
        String deepLinkUrl = "yourapp://launch";

        // Create an intent to send the link via WhatsApp
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, deepLinkUrl);

        // Find WhatsApp package and set it as the target app for the intent
        sendIntent.setPackage("com.whatsapp");

        // If WhatsApp is installed, send the deep link; otherwise, show a message
        try {
            startActivity(sendIntent);
        } catch (android.content.ActivityNotFoundException e) {
            // Handle the case where WhatsApp is not installed
            e.printStackTrace();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("onRegisterCalled"," pause ");

        // Unregister the receiver when the activity is paused
    }

        @Override
    protected void onDestroy() {
        super.onDestroy();
    }


     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_emp_dashboard);
         ess_tag = findViewById(R.id.ess_tag);
         ess_lay = findViewById(R.id.ess_lay);
        Log.d("callLogut"," on crrate ");
         TextView header_title = findViewById(R.id.header_title);
        header_title.setText("Welcome "+commonClass.getSharedPref(getApplicationContext(),"EmppName")+"!");
        handler = new Handler(Looper.getMainLooper());



         Intent intent = getIntent();
         Uri data = intent.getData(); // Get the URI from the intent
         Log.d("DeepLink","called");
         if (data != null) {
             String scheme = data.getScheme(); // Should be "yourapp"
             String host = data.getHost(); // Should be "launch"
             Log.d("DeepLink", "Deep link opened: " + data.toString()+" scheme "+scheme+
                     "hose "+host);
             if(!TextUtils.isEmpty(scheme) && !TextUtils.isEmpty(host)){
                 if(scheme.equals("https") && host.equals("adams.proz.in")){
                     if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                             !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                             !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
                         Intent intent1 =new Intent(getApplicationContext(),AdminNewApprovals.class);
                         startActivity(intent1);
                     }
                 }
             }

         }


         //  foregroundServiceRunning();

        // Initialize and start the background task




        // Enqueue the work request
         requestList.add("Permission");
         requestList.add("Loan");
        requestList.add("Claim");
        requestList.add("OnDuty");
        requestList.add("Overtime");

        progressDialog = new ProgressDialog(DashboardNewActivity.this);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        picloader = findViewById(R.id.loader);
        loader1 = findViewById(R.id.loader1);

       /* online_icon = findViewById(R.id.online_icon);
        online_layout = findViewById(R.id.online_layout);
        online_text = findViewById(R.id.online_text);*/
       // commonClass.onlineStatusCheck(DashboardNewActivity.this,online_layout,online_text,online_icon);


        Log.d("getTokenDetails" ," token "+commonClass.getSharedPref(DashboardNewActivity.this,"token")
                +" device id "+commonClass.getDeviceID(DashboardNewActivity.this));

        Dexter.withContext(DashboardNewActivity.this)
                .withPermissions(
                        Manifest.permission.FOREGROUND_SERVICE,
                        Manifest.permission.FOREGROUND_SERVICE_LOCATION,
                        Manifest.permission.CAMERA,
                       Manifest.permission.POST_NOTIFICATIONS,
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

        if(commonClass.isOnline(DashboardNewActivity.this)){
            syncOfflineToOnline();
        }




        if(commonClass.isOnline(DashboardNewActivity.this)){
            callVersionStatus();
        }
    }
    private void callAlertDialog() {
        Log.d("callAlert"," size "+requestList.size());
        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardNewActivity.this);

        // set title
        builder.setTitle("Select Request");
        // set dialog non cancelable
        builder.setCancelable(false);
        String[] stringArray = requestList.toArray(new String[0]);
        //   String[] stringArray = empNameList.toArray(new String[0]);

        selectedNames = new boolean[stringArray.length];
        SendToID ="-1";



        builder.setSingleChoiceItems(stringArray, Integer.parseInt(SendToID),new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                SendToID = String.valueOf(which);
                SendToName = requestList.get(which);
                //dialog.dismiss();
                int po = Integer.parseInt(SendToID);
                Log.d("selectedPOS"," it "+po);
                if(po>=0) {
                    Log.d("selectedPOS", " call if");
                    if (po == 0) {
                        //ticket
                        Intent intent5 = new Intent(getApplicationContext(), LeaveActivity.class);
                        intent5.putExtra("position", 4);
                        startActivity(intent5);

                    }else  if (po == 1) {
                        Intent intent2 = new Intent(getApplicationContext(), LoanActivity.class);
                        intent2.putExtra("position", 0);
                        startActivity(intent2);
                    } else if (po == 2) {
                        Intent intent4 = new Intent(getApplicationContext(), ClaimActivity.class);
                        intent4.putExtra("claim_type", "claim");
                        intent4.putExtra("position", 1);
                        startActivity(intent4);
                    } else if (po == 3) {
                        //asset
                        Intent intent5 = new Intent(getApplicationContext(), OnDuty.class);
                        intent5.putExtra("position", 2);
                        startActivity(intent5);

                    } else if (po == 4) {
                        //ticket
                        Intent intent5 = new Intent(getApplicationContext(), OverTime.class);
                        intent5.putExtra("position", 3);
                        startActivity(intent5);

                    }
                }
            }
        });



        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        AlertDialog mDialog = builder.create();
        mDialog.setCancelable(false);
        mDialog.create();
        mDialog.show();
        mDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.n_org));
        mDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.n_org));
    }





    private void syncOfflineToOnline() {

        AttendanceListLocation attendanceListLocation = new AttendanceListLocation(DashboardNewActivity.this);
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
            ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(DashboardNewActivity.this,"token"),
                    commonClass.getDeviceID(DashboardNewActivity.this)).create(ApiInterface.class);
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
                                attendanceListLocation.deleteAll(DashboardNewActivity.this);
                            }
                        }else if(response.code()==401 || response.code()==403){
                            commonClass.showError(DashboardNewActivity.this,"UnAuthendicated");
                            commonClass.clearAlldata(DashboardNewActivity.this);
                        }else{
                            Gson gson = new GsonBuilder().create();
                            CommonPojo mError = new CommonPojo();
                            try {
                                mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);
                                Log.d("GEOTAG"," getError "+mError.getError());
                                //  commonClass.showServerToast(DashboardNewActivity.this,mError.getError());
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
                            //  commonClass.showServerToast(DashboardNewActivity.this,mError.getError());
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
            getFaceList();
        }

        List<INTimeOutTimeModal>  inTimeOutTimeModals = new ArrayList<>();
        List<INTimeOutTimeModal>  OutTimeModals = new ArrayList<>();
        PunchINOUTDB punchINOUTDB = new PunchINOUTDB(DashboardNewActivity.this);
        inTimeOutTimeModals =  punchINOUTDB.selectAllData(1);
        OutTimeModals =  punchINOUTDB.selectAllData(2);
        if(inTimeOutTimeModals.size()!=0){

            UploadTimeModal uploadTimeModal = new UploadTimeModal();
            uploadTimeModal.setInTimeOutTimeModalList(inTimeOutTimeModals);
            Gson gson = new Gson();
            String jsonArray = gson.toJson(uploadTimeModal);
            Log.d("GEOTAG"," json arr "+jsonArray);
            loader1.setVisibility(View.VISIBLE);
            ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(DashboardNewActivity.this,"token"),
                    commonClass.getDeviceID(DashboardNewActivity.this)).create(ApiInterface.class);
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
                            commonClass.showError(DashboardNewActivity.this,"UnAuthendicated");
                            commonClass.clearAlldata(DashboardNewActivity.this);
                        }else{
                            Gson gson = new GsonBuilder().create();
                            CommonPojo mError = new CommonPojo();
                            try {
                                mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);
                                Log.d("GEOTAG"," intime getError "+mError.getError());
                                //  commonClass.showServerToast(DashboardNewActivity.this,mError.getError());
                                //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                // handle failure to read error
                                Log.d("GEOTAG", "  intime exp error  " + e.getMessage());
                            }
                        }
                    }
                    else if(response.code()==401 || response.code()==403){
                        commonClass.showError(DashboardNewActivity.this,"UnAuthendicated");
                        commonClass.clearAlldata(DashboardNewActivity.this);
                    }else{
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);
                            Log.d("GEOTAG"," getError "+mError.getError());
                            //  commonClass.showServerToast(DashboardNewActivity.this,mError.getError());
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
            ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(DashboardNewActivity.this,"token"),
                    commonClass.getDeviceID(DashboardNewActivity.this)).create(ApiInterface.class);
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
                            commonClass.showError(DashboardNewActivity.this,"UnAuthendicated");
                            commonClass.clearAlldata(DashboardNewActivity.this);
                        }else{
                            Gson gson = new GsonBuilder().create();
                            CommonPojo mError = new CommonPojo();
                            try {
                                mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);
                                Log.d("GEOTAG"," outtime getError "+mError.getError());
                                //  commonClass.showServerToast(DashboardNewActivity.this,mError.getError());
                                //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                // handle failure to read error
                                Log.d("GEOTAG", " exp error  " + e.getMessage());
                            }
                        }
                    }
                    else if(response.code()==401 || response.code()==403){
                        commonClass.showError(DashboardNewActivity.this,"UnAuthendicated");
                        commonClass.clearAlldata(DashboardNewActivity.this);
                    }else{
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);
                            Log.d("GEOTAG"," outime  getError "+mError.getError());
                            //  commonClass.showServerToast(DashboardNewActivity.this,mError.getError());
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

    public void getFaceList(){
        CommonClass commonClass = new CommonClass();
        loader1.setVisibility(View.VISIBLE);
        ApiInterface apiInterface  = ApiClient.getTokenWithoutAuth( ).create(ApiInterface.class);
        Call<FaceModal> call = apiInterface.getFaceList(commonClass.getDeviceID(DashboardNewActivity.this));
        Log.d("getFaceID"," details"+call.request().url());
        FaceAuthDB faceAuthDB = new FaceAuthDB(DashboardNewActivity.this);
        faceAuthDB.getWritableDatabase();
        call.enqueue(new Callback<FaceModal>() {
            @Override
            public void onResponse(Call<FaceModal> call, Response<FaceModal> response) {
                loader1.setVisibility(View.GONE);
                Log.d("getFaceID"," face "+response.code());
                if(response.isSuccessful()){
                    if(response.code()==200){
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body());
                        Log.d("getFaceID"," json as "+json);
                        if(response.body().getCommonPojo()!=null){
                            if(response.body().getCommonPojo().getEmbeedings()!=null){
                                faceAuthDB.insertData(response.body().getCommonPojo().getExtra(),response.body().getCommonPojo().getTitle(),
                                        response.body().getCommonPojo().getUpdated_at());
                             }else{
                                faceAuthDB.DropTable();
                             }
                        }else{
                            faceAuthDB.DropTable();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<FaceModal> call, Throwable t) {
                loader1.setVisibility(View.GONE);
                Log.d("getFaceID"," erro "+t.getMessage());
            }
        });



    }


    private void callVersionStatus() {
        loader1.setVisibility(View.VISIBLE);
        String versionName = BuildConfig.VERSION_NAME;
        ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(DashboardNewActivity.this,"token"),
                commonClass.getDeviceID(DashboardNewActivity.this)).create(ApiInterface.class);
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
                             // String versionName ="1";
                            if(!TextUtils.isEmpty(response.body().getCommonPojo().get(0).getApp_version())){
                                if(versionName.equals(response.body().getCommonPojo().get(0).getApp_version())){
                                    construction=1;
                                }else{
                                    construction =0;
                                   /* commonClass.showAppToast(DashboardNewActivity.this,
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
                                    // commonClass.showAppToast(DashboardNewActivity.this,"App Under Construction!!!");
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
                        commonClass.showError(DashboardNewActivity.this,"UnAuthendicated");
                        commonClass.clearAlldata(DashboardNewActivity.this);
                    }
                }else if(response.code()==401 || response.code()==403){
                    commonClass.showError(DashboardNewActivity.this,"UnAuthendicated");
                    commonClass.clearAlldata(DashboardNewActivity.this);
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
              //  showNotification(this, "Attendance", "Don't Forget to Check Out!!!", intent, reqCode);
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




    private void initView() {
        bottom_tag_lay =findViewById(R.id.bottom_tag_lay);
    //    showPushNotification();
        overtime = findViewById(R.id.overtime);
        overtime.setOnClickListener(this);
        commonClass.putSharedPref(getApplicationContext(),"department_id",null);
        commonClass.putSharedPref(getApplicationContext(),"branch_id",null);
        commonClass.putSharedPref(getApplicationContext(),"status",null);
        tag1=findViewById(R.id.tag1);
        tag2=findViewById(R.id.tag2);
        requestLayout = findViewById(R.id.requestLayout);
        requestLayout.setOnClickListener(this);
        employee_management = findViewById(R.id.employee_management);
         profileLayout = findViewById(R.id.profileLayout);
        profileLayout.setVisibility(View.GONE);
        calendarLayout = findViewById(R.id.calendarLayout);
        attendance_layute = findViewById(R.id.attendance_layute);
        nprofile_layout= findViewById(R.id.nprofile_layout);
        nprofile_layout.setOnClickListener(this);
         profile_imge = findViewById(R.id.profile_imge);
        profile_imge.setVisibility(View.VISIBLE);
        profile_imge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callLogout();
            }
        });
        wsr_layout = findViewById(R.id.wsr_layout);
        wsr_layout.setOnClickListener(this);
        employee_layout = findViewById(R.id.employee_layout);
        attendance_layute1 = findViewById(R.id.attendance_layute1);
        attendance_layute1.setOnClickListener(this);


        nhome_icon= findViewById(R.id.nhome_icon);
        nhome_text= findViewById(R.id.nhome_text);
        nhome_icon.setImageTintList(getApplicationContext().getColorStateList(R.color.color_primary));
        nhome_text.setTextColor(getApplicationContext().getColor(R.color.black));

        nhome_layout= findViewById(R.id.nhome_layout);
       // nabout_layout= findViewById(R.id.nabout_layout);
        nreports_layout= findViewById(R.id.nreports_layout);
        nlocation_layout= findViewById(R.id.nlocation_layout);

        nhome_layout.setOnClickListener(this);
        //nabout_layout.setOnClickListener(this);
        nreports_layout.setOnClickListener(this);
        nlocation_layout.setOnClickListener(this);


        ticketsLayout = findViewById(R.id.ticketsLayout);
        ticketsLayout.setOnClickListener(this);
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
            employee_management.setVisibility(View.VISIBLE);
             employee_layout.setVisibility(View.GONE);
             attendance_layute1.setVisibility(View.VISIBLE);
            attendance_layute.setVisibility(View.GONE);
            calendarLayout.setVisibility(View.GONE);
            ess_tag.setVisibility(View.VISIBLE);
            ess_lay.setVisibility(View.VISIBLE);

             requestLayout.setVisibility(View.VISIBLE);
            bottom_tag_lay.setVisibility(View.VISIBLE);
            tag1.setText("Employee Service & Request");
            tag2.setText("Employee Attendance Management");
        }else{
            tag1.setText("My Service & Request");
            tag2.setText("Attendance Management");
            ess_tag.setVisibility(View.GONE);
            ess_lay.setVisibility(View.GONE);
            bottom_tag_lay.setVisibility(View.GONE);
            requestLayout.setVisibility(View.GONE);
            employee_management.setVisibility(View.GONE);

            admin_main_layout.setVisibility(View.GONE);
             employee_layout.setVisibility(View.VISIBLE);
             calendarLayout.setVisibility(View.VISIBLE);
            attendance_layute1.setVisibility(View.GONE);
            attendance_layute.setVisibility(View.VISIBLE);
         //   profileLayout.setVisibility(View.VISIBLE);
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
       /* logout = findViewById(R.id.logout);
        logout.setOnClickListener(this);*/
        assetslayout.setOnClickListener(this);
        assetslayout.setOnClickListener(this);

        if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"name"))){
            name.setText(commonClass.getSharedPref(getApplicationContext(),"name"));
            designation.setText(commonClass.getSharedPref(getApplicationContext(),"designation"));
            picloader.setVisibility(View.VISIBLE);
            Picasso.with(DashboardNewActivity.this).load(commonClass.getSharedPref(getApplicationContext(),"image")).error(R.drawable.profile_user_icon).into(profile_img, new com.squareup.picasso.Callback() {
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

            case R.id.overtime:
                Intent intentwsr1 = new Intent(getApplicationContext(), OverTime.class);
                startActivity(intentwsr1);
                break;
            case R.id.requestLayout:
                callAlertDialog();
                break;
            case R.id.wsr_layout:
                Intent intentwsr = new Intent(getApplicationContext(), AdminWSRLayout.class);
                startActivity(intentwsr);
                break;
            case R.id.nprofile_layout:
                Intent intentabout1 = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intentabout1);
                break;
            case R.id.nhome_layout:

                break;

            case R.id.nreports_layout:
                Intent notes=new Intent(getApplicationContext(), NotesActivity.class);
                 startActivity(notes);
                break;
            case R.id.nlocation_layout:
                Intent intent152 = new Intent(getApplicationContext(), MapCurrentLocation.class);
                 startActivity(intent152);
                break;
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
                    commonClass.putSharedPref(getApplicationContext(),"dash","dash");


                    Intent intent = new Intent(getApplicationContext(), AdminNewApprovals.class);
                    intent.putExtra("position","0");
                    intent.putExtra("branch","ALL");
                    intent.putExtra("dash","dash");
                    /*intent.putExtra("serverDate",sd);
                    intent.putExtra("usableDate",ud);*/
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
                Log.d("onclickEvenet"," on summar "+construction);
                if(construction==1) {
                    Log.d("onclickEvenet"," called ");
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
          /*  case R.id.logout:
                if(commonClass.isOnline(DashboardNewActivity.this)) {
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
                    commonClass.showError(DashboardNewActivity.this,"Please Make Sure Internet Available");
                }
                break;*/
            case R.id.loan_layout:
                if(construction==1) {
                    Intent loan = new Intent(getApplicationContext(), LoanActivity.class);
                    startActivity(loan);
                }
                break;
            case R.id.ticketsLayout:
                if(construction==1){
                    Intent calendar1 = new Intent(getApplicationContext(), CalendarActivity.class);
                    startActivity(calendar1);
                }
                break;
            case R.id.assetslayout:

            if(construction==1){
                Intent meeting1 = new Intent(getApplicationContext(), MeetingActivity.class);
                startActivity(meeting1);
            }

/*
                if(construction==1) {
                    Dialog dialog = new Dialog(DashboardNewActivity.this);
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
                                commonClass.showWarning(DashboardNewActivity.this, "Please Select any option");
                            }
                        }
                    });
                }
*/



                break;
            case R.id.requirements:
                if(construction==1) {
                    Dialog dialog = new Dialog(DashboardNewActivity.this);
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
                    LinearLayout btn_continue1 = dialog.findViewById(R.id.approve);
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
                                commonClass.showWarning(DashboardNewActivity.this, "Please Select any option");
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
            case R.id.attendance_layute1:
                Intent intent2 = new Intent(getApplicationContext(),AttendanceActivity.class);
                startActivity(intent2);
                break;
        }
    }

    private void callLogout() {
        Log.d("callLogut"," called");
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(this);
        View view= LayoutInflater.from(this).inflate(R.layout.new_warning_dlg,null);
        TextView msg = view.findViewById(R.id.msg);
        TextView cancel_text = view.findViewById(R.id.cancel_text);
        cancel_text.setText("NO");
        TextView btn_title1 = view.findViewById(R.id.btn_title1);
        btn_title1.setText("YES");

        TextView btn_title = view.findViewById(R.id.btn_title);
        btn_title.setText("YES");

        msg.setText("Are you sure you want to logout?");
        LinearLayout approve = view.findViewById(R.id.approve);
        approve.setVisibility(View.GONE);
        LinearLayout decline = view.findViewById(R.id.decline);
        decline.setVisibility(View.VISIBLE);
        LinearLayout cancel = view.findViewById(R.id.cancel);
        builder.setView(view);
        final AlertDialog mDialog = builder.create();
        mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        mDialog.create();
        mDialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                commonClass.putSharedPref(getApplicationContext(), "token", null);
                commonClass.putSharedPref(getApplicationContext(), "AdminEmpNo", null);
                commonClass.putSharedPref(getApplicationContext(), "AdminName", null);
                commonClass.putSharedPref(getApplicationContext(), "AdminRole", null);
                commonClass.putSharedPref(getApplicationContext(), "AdminRoleName", null);

                BranchTable branchTable = new BranchTable(DashboardNewActivity.this);
                branchTable.getWritableDatabase();
                branchTable.DropTable();

                FaceAuthDB authDB = new FaceAuthDB(DashboardNewActivity.this);
                authDB.getWritableDatabase();
                authDB.DropTable();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);                    }
        });

    }


    public void getList(ProgressDialog progressDialog){
        if(progressDialog!=null){
            progressDialog.show();
        }
        picloader.setVisibility(View.VISIBLE);

        ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(DashboardNewActivity.this,"token"),
                commonClass.getDeviceID(DashboardNewActivity.this)).create(ApiInterface.class);
        Call<PersonalMainModal> call = apiInterface.getUserProfile();
        Log.d("profile_index"," url as "+call.request().url()+commonClass.getDeviceID(DashboardNewActivity.this)+
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
                            Picasso.with(DashboardNewActivity.this).load(response.body().getPersonalModal().getImage()).
                                    into(profile_img, new com.squareup.picasso.Callback() {
                                        @Override
                                        public void onSuccess() {
                                            picloader.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onError() {
                                            profile_img.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.profile_user_icon));
                                            picloader.setVisibility(View.GONE);
                                        }
                                    });
                        }
                        name.setText(response.body().getPersonalModal().getName());
                        designation.setText(response.body().getPersonalModal().getDesignation());


                    }
                    else if(response.code()==401 || response.code()==403){
                        commonClass.showError(DashboardNewActivity.this,"UnAuthendicated");
                        commonClass.clearAlldata(DashboardNewActivity.this);
                    }else{
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            //  commonClass.showServerToast(DashboardNewActivity.this,mError.getError());
                            //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            // handle failure to read error
                            Log.d("thumbnail_url", " exp error  " + e.getMessage());
                        }
                    }
                }
                else if(response.code()==401 || response.code()==403){
                    commonClass.showError(DashboardNewActivity.this,"UnAuthendicated");
                    commonClass.clearAlldata(DashboardNewActivity.this);
                }else{
                    Gson gson = new GsonBuilder().create();
                    CommonPojo mError = new CommonPojo();
                    try {
                        mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                        //commonClass.showServerToast(DashboardNewActivity.this,mError.getError());
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
                }                //commonClass.showServerToast(DashboardNewActivity.this,t.getMessage());
            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
         finishAffinity();

    }
}