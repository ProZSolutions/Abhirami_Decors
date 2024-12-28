package in.proz.adamd;

import static androidx.camera.core.impl.utils.ContextUtil.getApplicationContext;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import in.proz.adamd.CommonJson.DashboardContent;
import in.proz.adamd.CommonJson.DashboardMainParse;
import in.proz.adamd.CommonJson.DashboardParseContent;
import in.proz.adamd.CommonJson.DashboardTagContent;
import in.proz.adamd.DSR.DSRActivity;
import in.proz.adamd.DashboardAdapter.DUIEmpAdapter;
import in.proz.adamd.DashboardAdapter.DUIEssAdapter;
import in.proz.adamd.DashboardAdapter.DUIMainAdapter;
import in.proz.adamd.DashboardAdapter.DUIMidAdapter;
import in.proz.adamd.Face.FaceModal;
import in.proz.adamd.FaceAuth.SimilarityClassifier;
import in.proz.adamd.Leave.LeaveActivity;
import in.proz.adamd.Loan.LoanActivity;
import in.proz.adamd.Map.MapCurrentLocation;
import in.proz.adamd.Meeting.MeetingActivity;
import in.proz.adamd.ModalClass.AttendanceMain;
import in.proz.adamd.ModalClass.ConstructionModal;
import in.proz.adamd.ModalClass.PersonalMainModal;
import in.proz.adamd.NotesActivity.NotesActivity;
import in.proz.adamd.OnDuty.OnDuty;
import in.proz.adamd.OverTime.OverTime;
import in.proz.adamd.Profile.ProfileActivity;
import in.proz.adamd.PushNotification.MyForegroundService;
import in.proz.adamd.Request.RequestActivity;
import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;
import in.proz.adamd.SQLiteDB.BranchTable;
import in.proz.adamd.SQLiteDB.DashboardUIViewTable;
import in.proz.adamd.SQLiteDB.FaceAuthDB;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardNewActivity extends AppCompatActivity implements View.OnClickListener {

    List<String> requestList = new ArrayList<>();
     CommonClass commonClass = new CommonClass();
    CircleImageView profile_img;
    TextView name,designation;
    ProgressDialog progressDialog;
    ImageView profile_imge;
    DashboardUIViewTable dashboardUIViewTable;
    public static NotificationManagerCompat manager;
    public static Notification.Builder builder;
    MKLoader picloader,loader1;

    int[] adminHeaderImg= {R.drawable.summaryyyy,R.drawable.approvalss__1_};
    int[] empListImg= {R.drawable.pfinger,R.drawable.leav,R.drawable.dsr,R.drawable.icons_overtime,R.drawable.meet
            ,R.drawable.loa,R.drawable.reimb,R.drawable.mycalendarrr__1_};

    int[] adminMidImg ={R.drawable.group_emp,R.drawable.dsr,R.drawable.check_out,R.drawable.icons_overtime};
    int[] adminEssentialsImg ={R.drawable.meet,R.drawable.pfinger,R.drawable.mycalendarrr__1_
            ,R.drawable.my_profileee__1_};



    int construction =0 ;
    LinearLayout nhome_layout,nreports_layout,nlocation_layout,nprofile_layout,linear_layout1,linear_layout2,linear_layout3,linear_layout4;
    TextView title_tag1,title_tag2,title_tag3;
    RecyclerView recyclerView1,recyclerView2,recyclerView3,recyclerView4;

    ImageView nhome_icon;
    TextView nhome_text;
    Intent serviceIntent ;
    @Override
    protected void onResume() {
        super.onResume();
        // Register the BootReceiver dynamically at runtime
        IntentFilter filter = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
        Restarter receiver = new Restarter();
        registerReceiver(receiver, filter);


        Log.d("onRegisterCalled"," resu ");
    }


        @Override
    protected void onDestroy() {
        super.onDestroy();
            if(serviceIntent!=null){
                 stopService(serviceIntent);
            }
        }


     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_emp_dashboard);



         serviceIntent = new Intent(this, MyForegroundService.class);
         startService(serviceIntent);

        dashboardUIViewTable = new DashboardUIViewTable(this);
        dashboardUIViewTable.getWritableDatabase();




         Log.d("callLogut"," on crrate ");
         TextView header_title = findViewById(R.id.header_title);
        header_title.setText("Welcome "+commonClass.getSharedPref(getApplicationContext(),"EmppName")+"!");
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




        // Enqueue the work request
         requestList.add("Permission");
         requestList.add("Loan");
        requestList.add("Claim");
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
         linear_layout1= findViewById(R.id.linear_layout1);
        linear_layout2= findViewById(R.id.linear_layout2);
        linear_layout3= findViewById(R.id.linear_layout3);
        linear_layout4= findViewById(R.id.linear_layout4);
        title_tag1= findViewById(R.id.title_tag1);
        title_tag2= findViewById(R.id.title_tag2);
        title_tag3= findViewById(R.id.title_tag3);
        recyclerView1= findViewById(R.id.recyclerView1);
        recyclerView2= findViewById(R.id.recyclerView2);
        recyclerView3= findViewById(R.id.recyclerView3);
        recyclerView4= findViewById(R.id.recyclerView4);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        GridLayoutManager layoutManager2 = new GridLayoutManager(this, 2);
        GridLayoutManager layoutManager3 = new GridLayoutManager(this, 2);
        GridLayoutManager layoutManager1 = new GridLayoutManager(this, 3);
        recyclerView1.setLayoutManager(layoutManager);
        recyclerView2.setLayoutManager(layoutManager1);
        recyclerView3.setLayoutManager(layoutManager2);
        recyclerView4.setLayoutManager(layoutManager3);

        Log.d("dashboardUIViewTable"," offline table count "+dashboardUIViewTable.selectAllCount());
        if(dashboardUIViewTable.selectAllCount()!=0){
            getUIContentOffline();
            if(commonClass.isOnline(DashboardNewActivity.this)){
                getUIContentView();
            }
        }else{
            if(commonClass.isOnline(DashboardNewActivity.this)){
                getUIContentView();
            }else{
                if(dashboardUIViewTable.selectAllCount()!=0){
                    getUIContentOffline();
                }else{
                    commonClass.showWarning(DashboardNewActivity.this,"No Menus Available");
                }
            }
        }





        commonClass.putSharedPref(getApplicationContext(),"department_id",null);
        commonClass.putSharedPref(getApplicationContext(),"branch_id",null);
        commonClass.putSharedPref(getApplicationContext(),"status",null);



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




         Log.d("admincresent"," admin emp no "+commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")+
                " role "+commonClass.getSharedPref(getApplicationContext(),"AdminRole")+" name "+
                commonClass.getSharedPref(getApplicationContext(),"AdminName"));


        name = findViewById(R.id.name);

        designation = findViewById(R.id.designation);



        profile_img = findViewById(R.id.profile_img);
        profile_img.setOnClickListener(this);

       /* logout = findViewById(R.id.logout);
        logout.setOnClickListener(this);*/


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

    public void getUIContentOffline(){
        List<DashboardContent> adminHeaderDashboard = new ArrayList<>();
        adminHeaderDashboard = dashboardUIViewTable.selectAllItems("admin_header_card",adminHeaderImg);
        if(adminHeaderDashboard.size()!=0){
            DUIMainAdapter adapter = new DUIMainAdapter(DashboardNewActivity.this,adminHeaderDashboard);
            recyclerView1.setAdapter(adapter);
        }else{
            linear_layout1.setVisibility(View.GONE);
        }
        List<DashboardContent> employeeList = new ArrayList<>();
        employeeList = dashboardUIViewTable.selectAllItems("employee_card",empListImg);
        if(employeeList.size()!=0){
            title_tag1.setText(dashboardUIViewTable.selectHeaderTagOnly("employee_card"));
            DUIEmpAdapter adapter = new DUIEmpAdapter(DashboardNewActivity.this,employeeList);
            recyclerView2.setAdapter(adapter);
        }else{
            linear_layout2.setVisibility(View.GONE);
        }
        List<DashboardContent> adminMidComp = new ArrayList<>();
        adminMidComp = dashboardUIViewTable.selectAllItems("admin_middle_card",adminMidImg);
        if(adminMidComp.size()!=0){
            title_tag2.setText(dashboardUIViewTable.selectHeaderTagOnly("admin_middle_card"));
            DUIMidAdapter adapter = new DUIMidAdapter(DashboardNewActivity.this,adminMidComp);
            recyclerView3.setAdapter(adapter);
        }else{
            linear_layout3.setVisibility(View.GONE);
        }
        List<DashboardContent> adminessentials = new ArrayList<>();
        adminessentials = dashboardUIViewTable.selectAllItems("admin_essentials",adminEssentialsImg);
        Log.d("dashboardUIViewTable","adminessentials size "+adminessentials.size());
        if(adminessentials.size()!=0){
            title_tag3.setText(dashboardUIViewTable.selectHeaderTagOnly("admin_essentials"));
            DUIEssAdapter adapter = new DUIEssAdapter(DashboardNewActivity.this,adminessentials);
            recyclerView4.setAdapter(adapter);
        }else{
            linear_layout4.setVisibility(View.GONE);
        }



    }


    private void getUIContentView() {
        Log.d("dashboardUIViewTable"," getUI Content Called");
         ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(),"token"),
                commonClass.getDeviceID(DashboardNewActivity.this)).create(ApiInterface.class);
        String type="employee";
         if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
             type="admin";
        }


        Call<DashboardMainParse> call = apiInterface.getDashboardUI();
        Log.d("dashboardUIViewTable"," options as "+call.request().url()+" device id "+
                commonClass.getDeviceID(DashboardNewActivity.this));
        call.enqueue(new Callback<DashboardMainParse>() {
            @Override
            public void onResponse(Call<DashboardMainParse> call, Response<DashboardMainParse> response) {
                Log.d("dashboardUIViewTable"," response code "+response.code());
                if(response.code()==200){
                    if(response.body().getStatus().equals("success")){
                 if(response.body().getDashboardParseContent().getAdmin_header_card()!=null){
                     Log.d("dashboardUIViewTable"," not null1");
                    if(response.body().getDashboardParseContent().getAdmin_header_card().getDashboardContentList()!=null){
                        Log.d("dashboardUIViewTable"," not null2");
                        if(response.body().getDashboardParseContent().getAdmin_header_card().getDashboardContentList().size()!=0){
                            Log.d("dashboardUIViewTable"," not null3");
                            List<DashboardContent> adminHeaderDashboard = new ArrayList<>();
                            for(int i=0;i<response.body().getDashboardParseContent().getAdmin_header_card().getDashboardContentList().size();i++){
                                DashboardContent modal = response.body().getDashboardParseContent().getAdmin_header_card().getDashboardContentList().get(i);
                                dashboardUIViewTable.insertData("admin_header_card",response.body().getDashboardParseContent().getAdmin_header_card().getTag_name(),
                                        modal.getId(),modal.getTag(),modal.getTitle(),modal.isVisible());
                                if(modal.isVisible()){
                                    adminHeaderDashboard.add(new DashboardContent(modal.getId(),modal.getTag(),modal.getTitle(),
                                            modal.isVisible(),adminHeaderImg[i]));
                                }
                            }
                            if(adminHeaderDashboard.size()!=0){
                                DUIMainAdapter adapter = new DUIMainAdapter(DashboardNewActivity.this,adminHeaderDashboard);
                                recyclerView1.setAdapter(adapter);
                            }else{
                                linear_layout1.setVisibility(View.GONE);
                            }
                        }else{
                            linear_layout1.setVisibility(View.GONE);
                        }
                    }else{
                        linear_layout1.setVisibility(View.GONE);

                    }
                }else{
                    linear_layout1.setVisibility(View.GONE);
                }



                if(response.body().getDashboardParseContent().getEmployee_card()!=null){
                    if(response.body().getDashboardParseContent().getEmployee_card().getDashboardContentList()!=null){
                        if(response.body().getDashboardParseContent().getEmployee_card().getDashboardContentList().size()!=0){
                            List<DashboardContent> employeeCard = new ArrayList<>();
                            for(int i=0;i<response.body().getDashboardParseContent().getEmployee_card().getDashboardContentList().size();i++){
                                DashboardContent modal = response.body().getDashboardParseContent().getEmployee_card().getDashboardContentList().get(i);
                                dashboardUIViewTable.insertData("employee_card",response.body().getDashboardParseContent().getEmployee_card().getTag_name(),
                                        modal.getId(),modal.getTag(),modal.getTitle(),modal.isVisible());
                                if(modal.isVisible()){
                                    employeeCard.add(new DashboardContent(modal.getId(),modal.getTag(),modal.getTitle(),
                                            modal.isVisible(),empListImg[i]));
                                }
                            }
                            if(employeeCard.size()!=0){
                                title_tag1.setText(response.body().getDashboardParseContent().getEmployee_card().getTag_name());
                                DUIEmpAdapter adapter = new DUIEmpAdapter(DashboardNewActivity.this,employeeCard);
                                recyclerView2.setAdapter(adapter);
                            }else{
                                linear_layout2.setVisibility(View.GONE);
                            }
                        }else{
                            linear_layout2.setVisibility(View.GONE);
                        }
                    }else{
                        linear_layout2.setVisibility(View.GONE);

                    }
                }else{
                    linear_layout2.setVisibility(View.GONE);
                }


                if(response.body().getDashboardParseContent().getAdmin_middle_card()!=null){
                    if(response.body().getDashboardParseContent().getAdmin_middle_card().getDashboardContentList()!=null){
                        if(response.body().getDashboardParseContent().getAdmin_middle_card().getDashboardContentList().size()!=0){
                            List<DashboardContent> adminHeaderDashboard1 = new ArrayList<>();
                            for(int i=0;i<response.body().getDashboardParseContent().getAdmin_middle_card().getDashboardContentList().size();i++){
                                DashboardContent modal = response.body().getDashboardParseContent().getAdmin_middle_card().getDashboardContentList().get(i);
                                dashboardUIViewTable.insertData("admin_middle_card",response.body().getDashboardParseContent().getEmployee_card().getTag_name(),
                                        modal.getId(),modal.getTag(),modal.getTitle(),modal.isVisible());
                                if(modal.isVisible()){
                                    adminHeaderDashboard1.add(new DashboardContent(modal.getId(),modal.getTag(),modal.getTitle(),
                                            modal.isVisible(),adminMidImg[i]));
                                }
                            }
                            if(adminHeaderDashboard1.size()!=0){
                                title_tag2.setText(response.body().getDashboardParseContent().getAdmin_middle_card().getTag_name());
                                DUIMidAdapter adapter = new DUIMidAdapter(DashboardNewActivity.this,
                                        adminHeaderDashboard1);
                                recyclerView3.setAdapter(adapter);
                            }else{
                                linear_layout3.setVisibility(View.GONE);
                            }
                        }else{
                            linear_layout3.setVisibility(View.GONE);
                        }
                    }else{
                        linear_layout3.setVisibility(View.GONE);

                    }
                }else{
                    linear_layout3.setVisibility(View.GONE);
                }


                if(response.body().getDashboardParseContent().getAdmin_essentials()!=null){
                    if(response.body().getDashboardParseContent().getAdmin_essentials().getDashboardContentList()!=null){
                        if(response.body().getDashboardParseContent().getAdmin_essentials().getDashboardContentList().size()!=0){
                            List<DashboardContent> adminHeaderDashboard2 = new ArrayList<>();
                            for(int i=0;i<response.body().getDashboardParseContent().getAdmin_essentials().getDashboardContentList().size();i++){
                                DashboardContent modal = response.body().getDashboardParseContent().getAdmin_essentials().getDashboardContentList().get(i);
                                dashboardUIViewTable.insertData("admin_essentials",response.body().getDashboardParseContent().getAdmin_essentials().getTag_name(),
                                        modal.getId(),modal.getTag(),modal.getTitle(),modal.isVisible());
                                if(modal.isVisible()){
                                    adminHeaderDashboard2.add(new DashboardContent(modal.getId(),modal.getTag(),modal.getTitle(),
                                            modal.isVisible(),adminEssentialsImg[i]));
                                }
                            }
                            if(adminHeaderDashboard2.size()!=0){
                                title_tag3.setText(response.body().getDashboardParseContent().getAdmin_essentials().getTag_name());
                                DUIEssAdapter adapter = new DUIEssAdapter(DashboardNewActivity.this,
                                        adminHeaderDashboard2);
                                recyclerView4.setAdapter(adapter);
                            }else{
                                linear_layout4.setVisibility(View.GONE);
                            }
                        }else{
                            linear_layout4.setVisibility(View.GONE);
                        }
                    }else{
                        linear_layout4.setVisibility(View.GONE);

                    }
                }else{
                    linear_layout4.setVisibility(View.GONE);
                }

                    }
                }


            }

            @Override
            public void onFailure(Call<DashboardMainParse> call, Throwable t) {
                Log.d("dashboardUIViewTable","error "+t.getMessage());
             }
        });





    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.nprofile_layout:
                Intent intentabout1 = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intentabout1);
                break;
            case R.id.nreports_layout:
                Intent notes=new Intent(getApplicationContext(), NotesActivity.class);
                 startActivity(notes);
                break;
            case R.id.nlocation_layout:
                Intent intent152 = new Intent(getApplicationContext(), MapCurrentLocation.class);
                 startActivity(intent152);
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
                dashboardUIViewTable.DropTable();

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