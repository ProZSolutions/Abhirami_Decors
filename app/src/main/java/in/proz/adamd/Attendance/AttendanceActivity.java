package in.proz.adamd.Attendance;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.Size;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.collection.ArraySet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

 import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.tuyenmonkey.mkloader.MKLoader;

import org.jetbrains.annotations.NotNull;
import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import in.proz.adamd.Adapter.AttendanceAdapter;
import in.proz.adamd.Adapter.TodayAdapter;
import in.proz.adamd.AttendanceSQLite.AttendanceDSR;
import in.proz.adamd.AttendanceSQLite.AttendanceListDB;
import in.proz.adamd.AttendanceSQLite.PunchINOUTDB;
import in.proz.adamd.BackgroundTask.LocationForegroundService;
import in.proz.adamd.BackgroundTask.LocationUpdateService;
import in.proz.adamd.BackgroundTask.Restarter;
import in.proz.adamd.BackgroundTask.YourService;
import in.proz.adamd.DashboardNewActivity;
import in.proz.adamd.Decor.EventDecorator;
import in.proz.adamd.Face.FaceModal;
import in.proz.adamd.Face.SQLFaceModal;
import in.proz.adamd.FaceAuth.SimilarityClassifier;
import in.proz.adamd.Map.MapCurrentLocation;
import in.proz.adamd.ModalClass.AttendanceListSubModal;
import in.proz.adamd.ModalClass.AttendanceMain;
import in.proz.adamd.ModalClass.LatLng;
import in.proz.adamd.ModalClass.ProjectListModal;
import in.proz.adamd.ModalClass.PunchModal;
import in.proz.adamd.ModalClass.TodayModal;
import in.proz.adamd.NotesActivity.NotesActivity;
import in.proz.adamd.OnDuty.OnDuty;
import in.proz.adamd.Profile.ProfileActivity;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;
import in.proz.adamd.SQLiteDB.BranchTable;
import in.proz.adamd.SQLiteDB.FaceAuthDB;
import in.proz.adamd.SQLiteDB.ProjectDetails;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private final static int ID_HOME = 1;
    EditText edt_planned_work;
    TextView   recognize;
    String language ="ta";
    FaceAuthDB faceAuthDB;

    private TextToSpeech textToSpeech;

    String location_not_logged="0";
    List<Integer> colors = new ArrayList<>();
    String dsr_flag="0";
    PunchINOUTDB punchINOUTDB;
    AttendanceDSR attendanceDSR;
    int other_status = 0;
    ProjectDetails projectDetails;
    AttendanceListDB attendanceListDB;
    ArrayList<Integer> projectListID = new ArrayList<>();

    // face detector
    FaceDetector detector;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView previewView;
    Interpreter tfLite;
    CameraSelector cameraSelector;
    boolean developerMode=false;
    float distance= 1.0f;
    boolean start=true,flipX=false;
    Context context= AttendanceActivity.this;
    int cam_face=CameraSelector.LENS_FACING_FRONT; //Default Back Camera
    int[] intValues;
    int inputSize=112;  //Input size for model
    boolean isModelQuantized=false;
    float[][] embeedings;
    float IMAGE_MEAN = 128.0f;
    float IMAGE_STD = 128.0f;
    int OUTPUT_SIZE=192; //Output size of model
    private static int SELECT_PICTURE = 2;
    ProcessCameraProvider cameraProvider;
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    String modelFile="mobile_face_net.tflite"; //model name
    private HashMap<String, SimilarityClassifier.Recognition> registered = new HashMap<>(); //saved Faces



    // face detector






    private final static int ID_EXPLORE = 2;
    Set<CalendarDay> dateNormal = new HashSet<>();
    Set<CalendarDay> dateLeave = new HashSet<>();
    Set<CalendarDay> dateHoliday = new HashSet<>();
    Set<CalendarDay> datePresent= new HashSet<>();
    Set<CalendarDay> dateAbsent = new HashSet<>();
    Set<CalendarDay> dateLAte = new HashSet<>();

    Intent mServiceIntent, lIntent, lUIntent;
    private YourService mYourService;
    LocationForegroundService lService;
    LocationUpdateService lUService;
    TextView tracking,header_title;
    List<AttendanceListSubModal> attendanceListSubModals;
    private final static int ID_MESSAGE = 3;
    LinearLayout nhome_layout,nreports_layout,nlocation_layout,nprofile_layout;

    CommonClass commonClass = new CommonClass();
    private final static int ID_NOTIFICATION = 4;
    ScrollView scrollView;
    public static final String[] MONTHS = {"January", "February", "March", "April", "May", "June", "July",
            "Augest", "September", "October", "November", "December"};

    ImageView back_arrow;
    HashMap<Integer, Object> dateHashmap, detailsHashmap;
    List<String> projectNameList = new ArrayList<>();
    boolean[] selectedProjects;

    TextView title, date_today, time_today, ofz_text, home_text, client_text, change_layout, current_date_event;
    LinearLayout office_layout, home_layout, client_layout, check_in, checkout, listLayout, date_click_event;

    ScrollView applyattendancelayout;
    ImageView ofz_icon, home_icon, client_icon, checkin_icon, checkout_icon;
    String workLocation = "office";
    RelativeLayout header_relative;
    String requestDateFormat;


    double latitude = 0, longitude = 0;

    FusedLocationProviderClient client;

    public int yr, mnth, dy;
    public Calendar calendar;
    public String dt, today_date;
    DecimalFormat decimalFormat = new DecimalFormat("00");
    // ProgressDialog progressDialog;
    RecyclerView todayRV, listRV;
    TextView att_tag;
    String att_tag_status="0";
    Map<CalendarDay, Integer> dateColorMap = new HashMap<>();

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest1;
    MKLoader loader,loader1;
    TextView notpunchin;
    LinearLayout frame_layout;
    ImageView frame_icon;
    TextView frame_tag;
    MaterialCalendarView customCalendar;
    int localtime;


    double ofz_lat = 11.2391346, ofz_lng = 78.1654629;
/*    LinearLayout online_layout;
    ImageView online_icon;*/
    String checkIN = "0";
   // TextView online_text;
    String tokenHeader, deviceHeader;
    String commonTag = "attendanceTag";

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private LocationCallback locationCallback;
    private LocationRequest mLocationRequest;

    int distance_value = 0;
    String branch_id=null;

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    LocationManager mLocationManager;

    public Location getLastKnownLocation() {
        Location lastKnownLocation = null;

        try {
            // Try to get the last known location from the Fused Location Provider
            lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (lastKnownLocation == null) {
                // If GPS_PROVIDER is null, try NETWORK_PROVIDER
                lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return lastKnownLocation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_backup);
        faceAuthDB=new FaceAuthDB(AttendanceActivity.this);
        faceAuthDB.getWritableDatabase();


      /*  ofz_lat = Double.valueOf(commonClass.getSharedPref(getApplicationContext(),"branch_lat"));
        ofz_lng = Double.valueOf(commonClass.getSharedPref(getApplicationContext(),"branch_long"));*/
        Log.d("office_location"," lat "+ofz_lat+" lng "+ofz_lng);


        commonClass.putSharedPref(getApplicationContext(),"location_not_logged","0");
        Log.d("attendance_punch", " oncreate called ");
        attendanceListSubModals = new ArrayList<>();
       // updateBottomNavigation();
        loader = findViewById(R.id.loader);
        loader1 = findViewById(R.id.loader1);
        header_title = findViewById(R.id.header_title);
        header_title.setText(commonClass.getSharedPref(getApplicationContext(),"EmppName"));

        /*if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
             header_title.setText("Admin");
        }else{
            header_title.setText("Employee");
        }*/

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        getLastKnownLocation();
        initView();

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int langResult = textToSpeech.setLanguage(new Locale("ta")); // Set Tamil language for TTS
                if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Tamil language is not supported for TTS", Toast.LENGTH_SHORT).show();
                }
            }
        });

        registered=readFromSP();
        SharedPreferences sharedPref = getSharedPreferences("Distance",Context.MODE_PRIVATE);
        distance = sharedPref.getFloat("distance",1.00f);
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }





        try {
            Log.d("FaceReg"," try block "+modelFile);
            tfLite=new Interpreter(loadModelFile(AttendanceActivity.this,modelFile));
        } catch (IOException e) {
            Log.d("FaceReg"," error block "+e.getMessage()+"  "+e.getLocalizedMessage()+" cause "+
                    e.getCause()+" track "+e.getStackTrace());
            e.printStackTrace();
        }
        //Initialize Face Detector
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .build();
        detector = FaceDetection.getClient(highAccuracyOpts);

        cameraBind();
        
        
        if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
            change_layout.setVisibility(View.GONE);

            if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"role_no"))){
                if(commonClass.getSharedPref(getApplicationContext(),"role_no").equals("70")){
                    frame_layout.setVisibility(View.VISIBLE);
                }else{
                    frame_layout.setVisibility(View.GONE);
                }
            }
        }else{
            frame_layout.setVisibility(View.VISIBLE);
            change_layout.setVisibility(View.GONE);
        }



        Bundle b = getIntent().getExtras();
        if (b != null) {
            String intent = b.getString("intent");
            if (intent != null) {
                Log.d(commonTag, " intent called ");
                frame_tag.setText(" Punch Attendance");
                frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));
                applyattendancelayout.setVisibility(View.GONE);
                listLayout.setVisibility(View.VISIBLE);
                if (commonClass.isOnline(AttendanceActivity.this)) {
                    getList();
                } else {
                    getListFromOffline();
                }
            }
        }
        if(commonClass.isOnline(AttendanceActivity.this)){
            getFaceList();
        }else{
            getFaceListOffline();
        }
        calendar = Calendar.getInstance();

        yr = calendar.get(Calendar.YEAR);
        mnth = calendar.get(Calendar.MONTH) + 1;
        dy = calendar.get(Calendar.DAY_OF_MONTH);
        dt = decimalFormat.format(Double.valueOf(yr)) + "-" + decimalFormat.format(Double.valueOf(mnth + 1)) + "-" + dy;
        today_date = dy + "-" + mnth + "-" + yr;
        //  checkout.setEnabled(false);

        Log.d(commonTag, " punch in " + commonClass.getSharedPref(getApplicationContext(), "punch_in"));
        /*if (!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(), "punch_in"))) {
            String date = dy + "-" + mnth + "-" + yr;
            Log.d(commonTag, " attendance " + commonClass.getSharedPref(getApplicationContext(), "punch_in") +
                    " date " + date);
            if (date.equals(commonClass.getSharedPref(getApplicationContext(), "punch_in"))) {
                checkout.setEnabled(true);
                att_tag.setText("Clock out and Have a Great Day");
                check_in.setEnabled(false);
                updateCheckHeader(2);
            } else {
                String splitarr[] = commonClass.getSharedPref(getApplicationContext(), "punch_in").split("-");
                if (!splitarr[0].equals(dy)) {
                    commonClass.putSharedPref(getApplicationContext(), "punch_in", null);
                    att_tag.setText("Clock in and Get into Work");
                    check_in.setEnabled(true);
                    checkout.setEnabled(false);
                    updateCheckHeader(1);
                }
                }
            }
        } else {
            att_tag.setText("Clock in and Get into Work");
            check_in.setEnabled(true);
            checkout.setEnabled(false);
        }*/

        /// get current location


        getLocationdetails();
    }



    public void callAllTheTodayRecords() {
        if (commonClass.isOnline(AttendanceActivity.this)) {
            todayAttendanceList();
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = simpleDateFormat.format(new Date());
            Log.d(commonTag, " today attendance oddline date as " + date);
            List<String> inTime, outTime, WorkHrs, Pin, Pout;
            inTime = new ArrayList<>();
            outTime = new ArrayList<>();
            WorkHrs = new ArrayList<>();
            Pin = new ArrayList<>();
            Pout = new ArrayList<>();
            inTime = punchINOUTDB.inTimeList(date, 0);
            outTime = punchINOUTDB.inTimeList(date, 1);
            WorkHrs = punchINOUTDB.inTimeList(date, 2);
            Pin = punchINOUTDB.inTimeList(date, 3);
            Pout = punchINOUTDB.inTimeList(date, 4);
            Log.d(commonTag, " intime " + inTime.size());
            if (inTime.size() != 0) {
                TodayAdapter adapter = new TodayAdapter(AttendanceActivity.this, inTime,
                        outTime,
                        WorkHrs, 1, Pin, Pout, loader);
                todayRV.setAdapter(adapter);
                Log.d("ShowTrack"," con 1 "+adapter.getItemCount());
                if(adapter.getItemCount()!=0){
                    tracking.setVisibility(View.VISIBLE);
                }else {
                    tracking.setVisibility(View.GONE);
                }
            }
        }
    }

    public void getLocationdetails() {
     /*   buildGoogleApiClient();
        mGoogleApiClient.connect();*/
        LocationManager service = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        client = LocationServices.getFusedLocationProviderClient(AttendanceActivity.this);
        createLocationRequest();

        getCurrentLocation();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                getCurrentLocation();
                //Toast.makeText(getActivity(), "Location Result" + locationResult.getLocations(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLocationAvailability(@NonNull @NotNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        };
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        super.onResume();
        client.requestLocationUpdates(mLocationRequest, locationCallback, Looper.getMainLooper());
    }

    public void showWarning(List<AttendanceListSubModal> sample, String date) {
        //dialog.setContentView(R.layout.dlg_warning);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.attendance_popup, null);
        ImageView back_arrow = view.findViewById(R.id.back_arrow);
        TextView dateT = view.findViewById(R.id.date);
        dateT.setText(date);
        RecyclerView todayRV1 = view.findViewById(R.id.todayRV);
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
        todayRV1.setLayoutManager(layoutManager);
        AttendanceAdapter adapter = new AttendanceAdapter(AttendanceActivity.this,
                sample, loader);
        todayRV1.setAdapter(adapter);
        builder.setView(view);
        final AlertDialog mDialog = builder.create();
        mDialog.setCancelable(false);
        mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        Window window = mDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);

        mDialog.create();
        mDialog.show();
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

    }

    private void getCurrentLocation() {
        try {
            @SuppressLint("MissingPermission")
            Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Log.d("locationDetails", " lat " + location.getLatitude() + " lng " + location.getLongitude());
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        distance_value = CalculationByDistance(ofz_lat, ofz_lng, latitude, longitude, "10km");
                    }
                }
            });

        } catch (Exception ex) {

        }
    }


    @Override
    public void onStop() {
        super.onStop();
        client.removeLocationUpdates(locationCallback);
    }

    private void initView() {
        recognize = findViewById(R.id.button3);
        
        recognize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start = true;
                att_tag.setText("Face Authenticating");
                recognize.setVisibility(View.GONE);
            }
        });
        
        
         nprofile_layout= findViewById(R.id.nprofile_layout);
        nprofile_layout.setOnClickListener(this);
        nhome_layout= findViewById(R.id.nhome_layout);
      //  nabout_layout= findViewById(R.id.nabout_layout);
        nreports_layout= findViewById(R.id.nreports_layout);
        nlocation_layout= findViewById(R.id.nlocation_layout);
        nhome_layout.setOnClickListener(this);
       // nabout_layout.setOnClickListener(this);
        nreports_layout.setOnClickListener(this);
        nlocation_layout.setOnClickListener(this);
        notpunchin = findViewById(R.id.notpunchin);
        Calendar current_time = Calendar.getInstance();
        localtime = current_time.get(Calendar.HOUR_OF_DAY) + 2;
        Log.d("gettimeA", " localtime " + localtime);

    /*    if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
            nprofile_layout.setVisibility(View.VISIBLE);
            nlocation_layout.setVisibility(View.GONE);
        }else{
            nprofile_layout.setVisibility(View.GONE);
            nlocation_layout.setVisibility(View.VISIBLE);
        }*/

        CommonClass comm = new CommonClass();

        tokenHeader = comm.getSharedPref(getApplicationContext(), "token");
        deviceHeader = comm.getDeviceID(getApplicationContext());

        attendanceListDB = new AttendanceListDB(AttendanceActivity.this);
        attendanceListDB.getWritableDatabase();
        attendanceDSR = new AttendanceDSR(AttendanceActivity.this);
        attendanceDSR.getWritableDatabase();
        punchINOUTDB = new PunchINOUTDB(AttendanceActivity.this);
        punchINOUTDB.getWritableDatabase();


        projectDetails = new ProjectDetails(AttendanceActivity.this);
        projectDetails.getWritableDatabase();
        if (commonClass.isOnline(AttendanceActivity.this)) {
            getProjectList();

        } else {
            updateAdapter();
        }
        lUService = new LocationUpdateService();
        mYourService = new YourService();
        lService = new LocationForegroundService();
        mServiceIntent = new Intent(this, mYourService.getClass());
        lUIntent = new Intent(this, lUService.getClass());
        lIntent = new Intent(this, lService.getClass());
        tracking = findViewById(R.id.tracking);
        tracking.setOnClickListener(this);
        Log.d(commonTag, " init view ");
       /* online_icon = findViewById(R.id.online_icon);
        online_layout = findViewById(R.id.online_layout);
        online_text = findViewById(R.id.online_text);
        comm.onlineStatusCheck(AttendanceActivity.this, online_layout, online_text, online_icon);*/


        dateHashmap = new HashMap<>();
        detailsHashmap = new HashMap<>();
        //   scrollView =findViewById(R.id.scrollView);
        customCalendar = findViewById(R.id.custom_calendar);

        frame_icon = findViewById(R.id.frame_icon);
        frame_tag = findViewById(R.id.frame_tag);
        frame_layout = findViewById(R.id.frame_layout);
        frame_layout.setOnClickListener(this);

        att_tag = findViewById(R.id.att_tag);
     //   att_tag.setText("Clock in and Get into Work");
        todayRV = findViewById(R.id.todayRV);
        listRV = findViewById(R.id.listRV);
        change_layout = findViewById(R.id.change_layout);
        change_layout.setVisibility(View.VISIBLE);
        change_layout.setOnClickListener(this);
        applyattendancelayout = findViewById(R.id.applyattendancelayout);
        listLayout = findViewById(R.id.listLayout);
        date_click_event = findViewById(R.id.date_click_event);
        current_date_event = findViewById(R.id.current_date_event);
        // date_click_event.setOnClickListener(this);

        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
        todayRV.setLayoutManager(layoutManager);
        todayRV.setVisibility(View.GONE);
        GridLayoutManager layoutManager1 = new GridLayoutManager(getApplicationContext(), 1);
        listRV.setLayoutManager(layoutManager1);
        title = findViewById(R.id.title);
        title.setText("Attendance");
        header_relative = findViewById(R.id.header_relative);
        back_arrow = findViewById(R.id.back_arrow);
        date_today = findViewById(R.id.date_today);
        time_today = findViewById(R.id.time_today);
        office_layout = findViewById(R.id.office_layout);
        home_layout = findViewById(R.id.home_layout);
        client_layout = findViewById(R.id.client_layout);
        check_in = findViewById(R.id.check_in);
        checkout = findViewById(R.id.checkout);
         check_in.setVisibility(View.GONE);
        checkout.setVisibility(View.GONE);
        ofz_icon = findViewById(R.id.ofz_icon);
        home_icon = findViewById(R.id.home_icon);
        client_icon = findViewById(R.id.client_icon);
        checkin_icon = findViewById(R.id.checkin_icon);
        checkout_icon = findViewById(R.id.checkout_icon);
        ofz_text = findViewById(R.id.ofz_text);
        home_text = findViewById(R.id.home_text);
        client_text = findViewById(R.id.client_text);


        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("EEEE , MMMM dd - yyyy");
        SimpleDateFormat df1 = new SimpleDateFormat("hh:mm:ss a");
        SimpleDateFormat df2 = new SimpleDateFormat("MMMM yyyy");
        SimpleDateFormat df3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        requestDateFormat = df3.format(new Date());
        Log.d(commonTag, " before " + requestDateFormat);
       // current_date_event.setText(df2.format(new Date()));

        if (comm.isOnline(AttendanceActivity.this)) {
            getList();
        } else {
            getListFromOffline();
        }

        customCalendar.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay newMonth) {
                String sDate = newMonth.getYear()
                        + "-" + newMonth.getMonth()
                        + "-" + newMonth.getDay();
                dateHoliday.clear();
                dateColorMap.clear();
                colors.clear();
                customCalendar.removeDecorators();

               // current_date_event.setText(MONTHS[newMonth.getMonth()-1] + " " + newMonth.getYear());
                String format_date = newMonth.getYear() + "-" + newMonth.getMonth() + "-01";
                String month = String.valueOf(newMonth.getMonth()+ 1);
                String date_str = String.valueOf(newMonth.getMonth());
                int mont = newMonth.getMonth();
                int date = newMonth.getDay();
                String mon = String.valueOf(mont);
                if (mont < 10) {
                    mon = "0" + String.valueOf(mont);
                }
                if (date < 10) {
                    date_str = "0" + String.valueOf(date_str);
                }
                requestDateFormat = newMonth.getYear() + "-" + mon + "-" + 01 + " 10:03";
                Log.d("getDating", "selected as " + requestDateFormat);
                getList();
            }
        });


        date_today.setText("  " + df.format(new Date()));
        Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                time_today.setText("  " + df1.format(new Date()));
                handler.postDelayed(this, 1000); // Update every second
            }
        };
        handler.post(runnable); // Start the Runnable
        back_arrow.setOnClickListener(this);
        office_layout.setOnClickListener(this);
        client_layout.setOnClickListener(this);
        home_layout.setOnClickListener(this);
        checkout.setOnClickListener(this);
        check_in.setOnClickListener(this);





        customCalendar.setOnDateChangedListener(new OnDateSelectedListener() {
          @Override
          public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay newMonth, boolean selected) {

                  String sDate= commonClass.splitYear(newMonth.toString());

              Log.d(commonTag, " sdate as " + sDate);
              String format_date = newMonth.getYear() + "-" + newMonth.getMonth()+ "-01";
               String date_str = String.valueOf(newMonth.getDay());
              int mont = newMonth.getMonth();
              int date = newMonth.getDay();
              String mon = String.valueOf(mont);
              if (mont < 10) {
                  mon = "0" + String.valueOf(mont);
              }
              if (date < 10) {
                  date_str = "0" + String.valueOf(date_str);
              }
              String date_str1 = date_str + "-" + mon + "-" + newMonth.getYear();

              date_str = newMonth.getYear() + "-" + mon + "-" + date_str;
              Log.d(commonTag, " date " + date_str + " ln " + date_str.length());

              List<AttendanceListSubModal> sample = new ArrayList<>();
              if (commonClass.isOnline(AttendanceActivity.this)) {
                  if (attendanceListSubModals.size() != 0) {
                      for (int i = 0; i < attendanceListSubModals.size(); i++) {
                          AttendanceListSubModal modal = attendanceListSubModals.get(i);
                          Log.d(commonTag, " getting " + modal.getDate() + " ln " + modal.getDate().length());
                          if (modal.getDate().equals(date_str)) {
                              sample.add(modal);
                          }
                      }
                  }
              } else {
                  attendanceListSubModals = attendanceListDB.bulkDateList(date_str);
                  if (attendanceListSubModals.size() != 0) {
                      for (int i = 0; i < attendanceListSubModals.size(); i++) {
                          AttendanceListSubModal modal = attendanceListSubModals.get(i);
                          Log.d(commonTag, " getting " + modal.getDate() + " ln " + modal.getDate().length());
                          if (modal.getDate().equals(date_str)) {
                              sample.add(modal);
                          }
                      }
                  }
              }

              if (sample.size() != 0) {
                  showWarning(sample, date_str1);

              }

          }
        });
/*
        customCalendar.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(View view, Calendar newMonth, Object desc) {
                // get string date
                String sDate = newMonth.get(Calendar.YEAR)
                        + "-" + (newMonth.get(Calendar.MONTH) + 1)
                        + "-" + newMonth.get(Calendar.DAY_OF_MONTH);
                Log.d(commonTag, " sdate as " + sDate);
                String format_date = newMonth.get(Calendar.YEAR) + "-" + (newMonth.get(Calendar.MONTH) + 1) + "-01";
                String month = String.valueOf(newMonth.get(Calendar.MONTH)) + 1;
                String date_str = String.valueOf(newMonth.get(Calendar.DAY_OF_MONTH));
                int mont = newMonth.get(Calendar.MONTH) + 1;
                int date = newMonth.get(Calendar.DAY_OF_MONTH);
                String mon = String.valueOf(mont);
                if (mont < 10) {
                    mon = "0" + String.valueOf(mont);
                }
                if (date < 10) {
                    date_str = "0" + String.valueOf(date_str);
                }
                String date_str1 = date_str + "-" + mon + "-" + newMonth.get(Calendar.YEAR);

                date_str = newMonth.get(Calendar.YEAR) + "-" + mon + "-" + date_str;
                Log.d(commonTag, " date " + date_str + " ln " + date_str.length());

                List<AttendanceListSubModal> sample = new ArrayList<>();
                if (commonClass.isOnline(AttendanceActivity.this)) {
                    if (attendanceListSubModals.size() != 0) {
                        for (int i = 0; i < attendanceListSubModals.size(); i++) {
                            AttendanceListSubModal modal = attendanceListSubModals.get(i);
                            Log.d(commonTag, " getting " + modal.getDate() + " ln " + modal.getDate().length());
                            if (modal.getDate().equals(date_str)) {
                                sample.add(modal);
                            }
                        }
                    }
                } else {
                    attendanceListSubModals = attendanceListDB.bulkDateList(date_str);
                    if (attendanceListSubModals.size() != 0) {
                        for (int i = 0; i < attendanceListSubModals.size(); i++) {
                            AttendanceListSubModal modal = attendanceListSubModals.get(i);
                            Log.d(commonTag, " getting " + modal.getDate() + " ln " + modal.getDate().length());
                            if (modal.getDate().equals(date_str)) {
                                sample.add(modal);
                            }
                        }
                    }
                }

                if (sample.size() != 0) {
                    showWarning(sample, date_str1);

                }


            }
        });
*/

        if (commonClass.isOnline(AttendanceActivity.this)) {
            checkAttendancePunchOrNot();
        } else {
            Log.d("checkinHeader", " already punch " + commonClass.getSharedPref(getApplicationContext(), "sync_id") +
                    " ppun " + commonClass.getSharedPref(getApplicationContext(), "punch_in"));
            if (!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(), "punch_in"))) {
                /// already punch in
                checkout.setEnabled(true);
                att_tag_status ="1";
              //  att_tag.setText("Clock out and Have a Great Day");
                check_in.setEnabled(false);
                updateCheckHeader(2);
            } else {
                /// not yet punched
                att_tag_status ="0";
              //  att_tag.setText("Clock in and Get into Work");
                check_in.setEnabled(true);
                checkout.setEnabled(false);
                updateCheckHeader(1);
            }


        }



        Log.d("gettingWorkLocation"," s "+commonClass.getSharedPref(getApplicationContext(),"work_location")
        +" select loc "+commonClass.getSharedPref(getApplicationContext(),"select_location"));

        if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"work_location"))){
            workLocation = commonClass.getSharedPref(getApplicationContext(),"work_location");
            if(workLocation.equals("client")){
                updateUIHeader(3);
            }else if(workLocation.equals("home")){
                updateUIHeader(2);
            }else if(workLocation.equals("office")){
                updateUIHeader(1);
            }
        }else{
            workLocation ="office";
            updateUIHeader(1);
        }
        callAllTheTodayRecords();
    }

    private void getListFromOffline() {
        List<AttendanceListSubModal> modalsTemp = new ArrayList<>();
        modalsTemp = attendanceListDB.bulkDateList(requestDateFormat);
        Log.d(" insertAttendanceList", " formatted date " + requestDateFormat + " size " + modalsTemp.size() + getLastKnownLocation());

        if (modalsTemp.size() != 0) {
            detailsHashmap.clear();
            dateHashmap.clear();
            dateHoliday.clear();
            dateColorMap.clear();
            customCalendar.removeDecorators();
            colors.clear();
            for (int i = 0; i < modalsTemp.size(); i++) {
                AttendanceListSubModal modal = modalsTemp.get(i);
                Log.d("insertAttendanceList", "modals " + modalsTemp.get(i).getDate());
                if (!TextUtils.isEmpty(modalsTemp.get(i).getDate())) {
                    String split[] = modalsTemp.get(i).getDate().split("-");
                    int date = Integer.parseInt(split[2]);
                    String type = "normal";
                    if (modal.getWeek_end().equals("Yes")) {
                        type = "weekend";

                        dateColorMap.put(CalendarDay.from(Integer.parseInt(split[0]), Integer.parseInt(split[1]),Integer.parseInt( split[2])),Color.parseColor(getApplicationContext().getString(R.string.n_org)));

                        colors.add(Color.parseColor(getApplicationContext().getString(R.string.n_org)));
                        dateHoliday.add(CalendarDay.from(Integer.parseInt(split[0]), Integer.parseInt(split[1]),Integer.parseInt( split[2])));

                    } else if (modal.getHoliday().equals("1")) {
                        type = "holiday";
                       // colors.add(R.color.n_org);
                        dateColorMap.put(CalendarDay.from(Integer.parseInt(split[0]), Integer.parseInt(split[1]),Integer.parseInt( split[2])),Color.parseColor(getApplicationContext().getString(R.string.n_org)));

                        colors.add(Color.parseColor(getApplicationContext().getString(R.string.n_org)));
                        dateHoliday.add(CalendarDay.from(Integer.parseInt(split[0]), Integer.parseInt(split[1]),Integer.parseInt( split[2])));

                    } else if (modal.getLeave().equals("1")) {
                        type = "leave";
                        //colors.add(R.color.nabsent);
                        dateColorMap.put(CalendarDay.from(Integer.parseInt(split[0]), Integer.parseInt(split[1]),Integer.parseInt( split[2])),Color.parseColor(getApplicationContext().getString(R.string.nabsent)));

                        colors.add(Color.parseColor(getApplicationContext().getString(R.string.nabsent)));
                        dateHoliday.add(CalendarDay.from(Integer.parseInt(split[0]), Integer.parseInt(split[1]),Integer.parseInt( split[2])));

                    } else if (modal.getPin_work_locationList() != null) {
                        if (modal.getPin_work_locationList().size() == 1) {
                            Log.d("insertAttendanceList", " int time " + modal.getPin_work_locationList().get(0));
                            if (modal.getPin_work_locationList().get(0) == null) {
                                type = "normal";

                            } else {
                                type = "present";
                                colors.add(Color.parseColor(getApplicationContext().getString(R.string.npresnet)));
                             //   colors.add(R.color.npresnet);
                                dateColorMap.put(CalendarDay.from(Integer.parseInt(split[0]), Integer.parseInt(split[1]),Integer.parseInt( split[2])),Color.parseColor(getApplicationContext().getString(R.string.npresnet)));

                                dateHoliday.add(CalendarDay.from(Integer.parseInt(split[0]), Integer.parseInt(split[1]),Integer.parseInt( split[2])));

                            }
                        } else {
                            type = "present";
                          //  colors.add(R.color.npresnet);
                            colors.add(Color.parseColor(getApplicationContext().getString(R.string.npresnet)));
                            dateColorMap.put(CalendarDay.from(Integer.parseInt(split[0]), Integer.parseInt(split[1]),Integer.parseInt( split[2])),Color.parseColor(getApplicationContext().getString(R.string.npresnet)));

                            dateHoliday.add(CalendarDay.from(Integer.parseInt(split[0]), Integer.parseInt(split[1]),Integer.parseInt( split[2])));

                        }

                    }
                    Log.d("insertAttendanceList", " date " + date + " type " + type);
                    dateHashmap.put(date, type);



                }
            }
            Log.d("getHolidayList"," calendar 1 ");
            updateCalendar();
        }

    }

    private void getProjectList() {
        //  progressDialog.show();
        loader.setVisibility(View.VISIBLE);
        Log.d("projectDetails", " token " + commonClass.getSharedPref(getApplicationContext(), "token") + "  device " +
                commonClass.getDeviceID(getApplicationContext()));
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(), "token"),
                commonClass.getDeviceID(AttendanceActivity.this)).create(ApiInterface.class);
        Call<ProjectListModal> call = apiInterface.getProjectList();
        Log.d("getProjectList", " list as " + call.request().url());
        call.enqueue(new Callback<ProjectListModal>() {
            @Override
            public void onResponse(Call<ProjectListModal> call, Response<ProjectListModal> response) {
                // progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                Log.d("getProjectList", " code " + response.code());
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        if (response.body().getGetProjectList().size() != 0) {
                            projectDetails.DropTable();
                            for (int i = 0; i < response.body().getGetProjectList().size(); i++) {
                               /* projectIdList.add(response.body().getGetProjectList().get(i).getProject_id());
                                projectNameList.add(response.body().getGetProjectList().get(i).getProject_name());
                                gitURLList.put(response.body().getGetProjectList().get(i).getProject_id(),
                                        response.body().getGetProjectList().get(i).getGit_url());*/
                                String listString = String.join(",", response.body().getGetProjectList().get(i).getGit_url());
                                Log.d("projectDetails", " insert git " + listString);
                                projectDetails.insertData(response.body().getGetProjectList().get(i).getProject_id(),
                                        response.body().getGetProjectList().get(i).getProject_name(),
                                        listString);
                            }
                            updateAdapter();

                        }
                    } else if (response.code() == 401 || response.code() == 403) {
                        commonClass.showError(AttendanceActivity.this, "UnAuthendicated");
                        commonClass.clearAlldata(AttendanceActivity.this);
                    }
                }
            }

            @Override
            public void onFailure(Call<ProjectListModal> call, Throwable t) {
                // progressDialog.dismiss();
                loader.setVisibility(View.GONE);
            }
        });
    }

    private void updateAdapter() {
        projectNameList.clear();
        projectNameList = projectDetails.getAllProjectList(null);
        projectNameList.add("Others");
        // Collections.sort(projectNameList);
        /*ArrayAdapter<String> adapter = new ArrayAdapter<>(DSRActivity.this, android.R.layout.simple_dropdown_item_1line,
                projectNameList);
        nachoTextView.setAdapter(adapter);*/
    }


    public void checkAttendancePunchOrNot() {
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(tokenHeader,
                deviceHeader).create(ApiInterface.class);
        Call<PunchModal> call = apiInterface.getAttendancePunchInStatus();
        Log.d("getAttendance", " call " + call.request().url());
        call.enqueue(new Callback<PunchModal>() {
            @Override
            public void onResponse(Call<PunchModal> call, Response<PunchModal> response) {
                loader.setVisibility(View.GONE);
                Log.d("getAttendance", " response " + response.code());
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        if (response.body().getStatus().equals("success")) {

                            Log.d("getAttendance", " punch status " + response.body().getCommonPojo().getPunch_status()
                                    + " syne " + response.body().getCommonPojo().getSync_id()
                            +" dsr flag "+response.body().getCommonPojo().getDsr_flag());


                            if(!TextUtils.isEmpty(response.body().getCommonPojo().getDsr_flag())){
                                dsr_flag=response.body().getCommonPojo().getDsr_flag();
                            }



                            if (response.body().getCommonPojo().getPunch_status().equals("0")) {
                                commonClass.putSharedPref(getApplicationContext(), "punch_in", null);
                                att_tag_status="0";
                              //  att_tag.setText("Clock in and Get into Work");
                                check_in.setEnabled(true);
                                checkout.setEnabled(false);
                                updateCheckHeader(1);
                            } else {
                                commonClass.putSharedPref(getApplicationContext(), "punch_in", today_date);
                                commonClass.putSharedPref(getApplicationContext(), "sync_id", response.body().getCommonPojo().getSync_id());
                                checkout.setEnabled(true);
                                att_tag_status="1";
                                //att_tag.setText("Clock out and Have a Great Day");
                                check_in.setEnabled(false);
                                updateCheckHeader(2);
                            }
                        } else {
                            //commonClass.showError(AttendanceActivity.this,response.body().getStatus());
                        }
                    } else if (response.code() == 401 || response.code() == 403) {
                        commonClass.showError(AttendanceActivity.this, "UnAuthendicated");
                        commonClass.clearAlldata(AttendanceActivity.this);
                    } else {
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            //commonClass.showError(AttendanceActivity.this,mError.getError());
                            //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            // handle failure to read error
                            Log.d("thumbnail_url", " exp error  " + e.getMessage());
                        }
                    }
                } else {
                    Gson gson = new GsonBuilder().create();
                    CommonPojo mError = new CommonPojo();
                    try {
                        mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                        //  commonClass.showError(AttendanceActivity.this,mError.getError());
                        //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // handle failure to read error
                        Log.d("thumbnail_url", " exp error  " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<PunchModal> call, Throwable t) {
                loader.setVisibility(View.GONE);
                Log.d("getAttendance", " error " + t.getMessage());
                //commonClass.showError(AttendanceActivity.this,t.getMessage());
            }
        });

    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.nprofile_layout:
                Intent intentabout1 = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intentabout1);
                break;
            case R.id.nhome_layout:
                Intent intent1 = new Intent(getApplicationContext(), DashboardNewActivity.class);
                startActivity(intent1);
                break;
        /*    case R.id.nabout_layout:
                Intent intentabout = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intentabout);
                break;*/
            case R.id.nreports_layout:
                Intent notes=new Intent(getApplicationContext(), NotesActivity.class);
                startActivity(notes);
                break;
            case R.id.nlocation_layout:
                Intent intent = new Intent(getApplicationContext(), MapCurrentLocation.class);
                 startActivity(intent);
                break;
            case R.id.tracking:
                if (todayRV.getVisibility() == View.VISIBLE) {
                    todayRV.setVisibility(View.GONE);
                    tracking.setText(getApplicationContext().getResources().getText(R.string.attendance_show));
                } else {
                    callAllTheTodayRecords();
                    todayRV.setVisibility(View.VISIBLE);
                    tracking.setText(getApplicationContext().getResources().getText(R.string.attendance_hide));
                }
                break;
            case R.id.change_layout:
                Intent intent12 = new Intent(getApplicationContext(), OnDuty.class);
                startActivity(intent12);
                break;
            case R.id.frame_layout:
                if (applyattendancelayout.getVisibility() == View.VISIBLE) {
                    frame_tag.setText(" Punch Attendance");
                    title.setText("Attendance List");
                    frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));
                    applyattendancelayout.setVisibility(View.GONE);
                    listLayout.setVisibility(View.VISIBLE);
                    if (commonClass.isOnline(AttendanceActivity.this)) {
                        getList();
                    } else {
                        getListFromOffline();
                    }

                } else {
                    title.setText("Attendance ");
                    frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.calendar_icon_new));
                    frame_tag.setText("Attendance List");
                    applyattendancelayout.setVisibility(View.VISIBLE);
                    listLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.back_arrow:
                callDashboard();
                break;
            case R.id.office_layout:
                commonClass.putSharedPref(getApplicationContext(),"select_location","office");
                workLocation = "office";
                updateUIHeader(1);
                break;
            case R.id.client_layout:
                branch_id=null;
                commonClass.putSharedPref(getApplicationContext(),"select_location","client");
                workLocation = "client";
                updateUIHeader(3);
                break;
            case R.id.home_layout:
                commonClass.putSharedPref(getApplicationContext(),"select_location","home");
                workLocation = "home";
                updateUIHeader(2);
                break;
            case R.id.check_in:
                updateCheckHeader(1);
                if(commonClass.isOnline(AttendanceActivity.this)){
                    if(latitude==0){
                        commonClass.showWarning(AttendanceActivity.this, "Cannot Access Current Location");
                    }
                }else{
                    if (latitude == 0) {
                        getLastLocation();
                    }
                }

                if(latitude==0){
                   /* latitude =ofz_lat;
                    longitude =ofz_lng;*/
                }
                if (latitude != 0) {
                    commonClass.putSharedPref(getApplicationContext(),"location_not_logged","0");

                    Log.d(commonTag, " get work type " + workLocation);
                    if (workLocation.equals("office")) {
                        if (distance_value <= 20 && distance_value >= 0) {
                            Log.d("CheckInCondition", " condition 1");

                            checkInConditionMethod();
                        } else {
                            commonClass.showWarning(AttendanceActivity.this, "You should be along to office location  ");
                        }
                    } else {
                        Log.d("CheckInCondition", " condition 2");

                        checkInConditionMethod();
                    }

                    //commonClass.putSharedPref(getApplicationContext(),"dsr_date","2024-04-16");


                    // checkInAttendance();

                } else {
                    location_not_logged="1";
                    Log.d("checkoutIn"," con1");
                    commonClass.putSharedPref(getApplicationContext(),"location_not_logged","1");
                    commonClass.showWarning(AttendanceActivity.this, "Cannot access current location.");
                }
                break;
            case R.id.checkout:
                updateCheckHeader(2);
                if(commonClass.isOnline(AttendanceActivity.this)){
                    if(latitude==0){
                        commonClass.showWarning(AttendanceActivity.this, "Cannot Access Current Location");
                    }
                }else{
                    if (latitude == 0) {
                        getLastLocation();
                    }
                }
                if(latitude==0){
                    /*latitude =ofz_lat;
                    longitude =ofz_lng;*/
                }


                if (latitude != 0) {


                    if (workLocation.equals("office")) {
                        if (distance_value <= 20 && distance_value >= 0) {
                            Log.d("CheckInCondition", " condition 1");

                            if(commonClass.isOnline(AttendanceActivity.this)){
                                Log.d("checkoutIn"," loc "+commonClass.getSharedPref(getApplicationContext(),"location_not_logged"));
                                if(commonClass.isLocationEnabled(AttendanceActivity.this)){
                                    Log.d("checkoutIn"," one ");
                                    checkOutAttendance();
                                }else{
                                    commonClass.showWarning(AttendanceActivity.this,"Enable Location and then try");
                                }
                            }else{
                                Log.d("checkoutIn"," teo ");
                                checkOutAttendance();
                            }

                        } else {
                            commonClass.showWarning(AttendanceActivity.this, "You should be along to office location  ");
                        }
                    } else {
                        Log.d("CheckInCondition", " condition 2");

                        if(commonClass.isOnline(AttendanceActivity.this)){
                            Log.d("checkoutIn"," loc "+commonClass.getSharedPref(getApplicationContext(),"location_not_logged"));
                            if(commonClass.isLocationEnabled(AttendanceActivity.this)){
                                Log.d("checkoutIn"," one ");
                                checkOutAttendance();
                            }else{
                                commonClass.showWarning(AttendanceActivity.this,"Enable Location and then try");
                            }
                        }else{
                            Log.d("checkoutIn"," teo ");
                            checkOutAttendance();
                        }
                    }



                } else {
                    location_not_logged="1";
                    Log.d("checkoutIn"," con2");
                    commonClass.putSharedPref(getApplicationContext(),"location_not_logged","1");
                    commonClass.showWarning(AttendanceActivity.this, "Cannot access current location.");
                }
                break;
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            commonClass.showWarning(AttendanceActivity.this,"Enable Location Permission");
            return;
        }
        client.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();
                            // Use the location object to get latitude and longitude
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            commonClass.putSharedPref(getApplicationContext(),"location_not_logged","0");


                        } else {
                            /*latitude = ofz_lat;
                            longitude = ofz_lng;*/
                            location_not_logged="1";
                            Log.d("checkoutIn"," con3");
                            commonClass.putSharedPref(getApplicationContext(),"location_not_logged","1");


                            commonClass.showWarning(AttendanceActivity.this, "Cannot access current location.");

                        }
                    }
                });
    }
    private void checkInConditionMethod() {
        Log.d("getLocation"," details "+commonClass.isLocationEnabled(AttendanceActivity.this));
        if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"dsr_date"))){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = dateFormat.format(new Date());
            Log.d("checkoutIn"," if con "+commonClass.getSharedPref(getApplicationContext(),"location_not_logged"));
            if(commonClass.getSharedPref(getApplicationContext(),"dsr_date").equals(date)){
                Log.d("CheckInCondition"," condition 3");
                  checkIN="1";
                  if(commonClass.isOnline(AttendanceActivity.this)){
                      if(commonClass.isLocationEnabled(AttendanceActivity.this)){
                          checkInAttendance();
                      }else{
                          commonClass.showWarning(AttendanceActivity.this,"Enable Location and then try");
                      }
                  }else{
                      checkInAttendance();
                  }

            }else {
                Log.d("getDateFormatValue", " com to else ");
                Log.d("CheckInCondition"," condition 4");

                commonClass.putSharedPref(getApplicationContext(),"dsr_date",null);
                checkIN="1";
               // checkInAttendance();
                if(commonClass.isOnline(AttendanceActivity.this)){
                    if(commonClass.isLocationEnabled(AttendanceActivity.this)){
                        checkInAttendance();
                    }else{
                        commonClass.showWarning(AttendanceActivity.this,"Enable Location and then try");
                    }
                }else{
                    checkInAttendance();
                }
              /*  if(dsr_flag.equals("0")){
                    callAlertDialog();
                }else{
                    checkIN="1";

                }*/
            }
        }else{
            Log.d("CheckInCondition"," condition 5");

            Log.d("getDateFormatValue"," come to outer else ");
            //callAlertDialog();
            checkIN="1";
            Log.d("checkoutIn"," else part "+commonClass.getSharedPref(getApplicationContext(),"location_not_logged"));


            if(commonClass.isOnline(AttendanceActivity.this)){
                if(commonClass.isLocationEnabled(AttendanceActivity.this)){
                    checkInAttendance();
                }else{
                    commonClass.showWarning(AttendanceActivity.this,"Enable Location and then try");
                }
            }else{
                checkInAttendance();
            }


          //  checkInAttendance();
//            if(dsr_flag.equals("0")){
//                callAlertDialog();
//            }else{
//                checkIN="1";
//                checkInAttendance();
//            }
        }
    }

    public void projectAlertDialog(TextView project_name, LinearLayout other_layout ){
        AlertDialog.Builder builder = new AlertDialog.Builder(AttendanceActivity.this);

        // set title
        builder.setTitle("Select Project Name");
        // set dialog non cancelable
        builder.setCancelable(false);
        String[] stringArray = projectNameList.toArray(new String[0]);

        selectedProjects = new boolean[stringArray.length];


        if(projectListID.size()!=0) {
            for (int i = 0; i < projectListID.size(); i++) {
                selectedProjects[projectListID.get(i)] = true;
            }
        }

        builder.setMultiChoiceItems(stringArray, selectedProjects, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                // check condition
                if (b) {
                    projectListID.add(i);
                    Collections.sort(projectListID);
                } else {
                    projectListID.remove(Integer.valueOf(i));
                }
                //updateGITLISt();
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int j = 0; j < projectListID.size(); j++) {
                    stringBuilder.append(stringArray[projectListID.get(j)]);
                    if (j != projectListID.size() - 1) {
                        stringBuilder.append(",");
                    }
                }
                // set text on textView
                project_name.setText(stringBuilder.toString());
                if(stringBuilder!=null){
                    String str = stringBuilder.toString();
                    if(str.contains("Other")){
                        other_status =1;
                        other_layout.setVisibility(View.VISIBLE);
                    }else{
                        other_status=0;
                        other_layout.setVisibility(View.GONE);
                    }
                }
             }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // dismiss dialog
                dialogInterface.dismiss();
            }
        });

        final AlertDialog mDialog = builder.create();
        mDialog.setCancelable(false);
        mDialog.create();
        mDialog.show();
        mDialog.setCancelable(false);

        mDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.n_org));
        mDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.n_org));
    }

    private void callAlertDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view= LayoutInflater.from(this).inflate(R.layout.dsr_dialog_layout,null);
        TextView  skipp_laypput= view.findViewById(R.id.skipp_laypput);
        ImageView back_arrow = view.findViewById(R.id.back_arrow);
        ImageView mike = view.findViewById(R.id.mike);

        MKLoader loader1 = view.findViewById(R.id.loader);
        TextView project_name = view.findViewById(R.id.project_name);
        LinearLayout oth_layout = view.findViewById(R.id.oth_layout);
        EditText edt_others = view.findViewById(R.id.edt_others);
        edt_planned_work = view.findViewById(R.id.edt_planned_work);
        RelativeLayout bottom_request_layout = view.findViewById(R.id.bottom_request_layout);
        project_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                projectAlertDialog(project_name,oth_layout);
            }
        });
        if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
            skipp_laypput.setVisibility(View.VISIBLE);

        }else{

            skipp_laypput.setVisibility(View.GONE);
        }





        builder.setView(view);
        final AlertDialog mDialog = builder.create();
        mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;

        Window window = mDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);

        mDialog.setCancelable(false);
        mDialog.create();
        mDialog.show();
        mDialog.setCancelable(false);
        mike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordVoiceToText(1);
            }
        });
        skipp_laypput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDashboard();
            }
        });
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                callDashboard();
            }
        });
        bottom_request_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(commonClass.isOnline(AttendanceActivity.this)) {
                    bottom_request_layout.setEnabled(false);
                    check_in.setEnabled(false);
                  //  mDialog.dismiss();

                    if(TextUtils.isEmpty(edt_planned_work.getText().toString())){
                        edt_planned_work.setText("திட்டமிட்ட பணி எதுவும் இல்லை");
                    }
                    callUpdateMethod(mDialog, project_name.getText().toString(), edt_others.getText().toString(),
                            edt_planned_work.getText().toString(), loader1, bottom_request_layout);
                }else{
                    SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
                    String date_format = date.format(new Date());
                    if(TextUtils.isEmpty(edt_planned_work.getText().toString())){
                        edt_planned_work.setText("திட்டமிட்ட பணி எதுவும் இல்லை");
                    }
                    Log.d("checkinHeader"," date format "+date_format);
                    attendanceDSR.insertBulk(date_format,project_name.getText().toString(),
                            edt_planned_work.getText().toString(),edt_others.getText().toString());
                    commonClass.putSharedPref(getApplicationContext(),"dsr_date",date_format);
                    Log.d("CheckInCondition"," condition 7");
                    bottom_request_layout.setEnabled(false);
                    check_in.setEnabled(false);
                    mDialog.dismiss();
                    callDashboard();
                    // checkInAttendance();
                }
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                String str_rem = edt_planned_work.getText().toString();
                if (TextUtils.isEmpty(str_rem)) {
                    str_rem = Objects.requireNonNull(result).get(0);
                } else {
                    str_rem = str_rem + " " + Objects.requireNonNull(result).get(0);
                }
                if(commonClass.containsTamil(str_rem)){
                    Log.d("EnteredLng"," tamil ");
                }else if(commonClass.containsEnglish(str_rem)){
                    Log.d("EnteredLng"," eng ");
                }else{
                    Log.d("EnteredLng"," unno ");
                }
                edt_planned_work.setText(str_rem);
            }
        }else if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                try {
                    InputImage impphoto=InputImage.fromBitmap(getBitmapFromUri(selectedImageUri),0);
                    detector.process(impphoto).addOnSuccessListener(new OnSuccessListener<List<Face>>() {
                        @Override
                        public void onSuccess(List<Face> faces) {

                            if(faces.size()!=0) {
                                Log.d("FaceReg"," reg");
                                recognize.setText("Recognize");
                                Face face = faces.get(0);
//                                System.out.println(face);

                                //write code to recreate bitmap from source
                                //Write code to show bitmap to canvas

                                Bitmap frame_bmp= null;
                                try {
                                    frame_bmp = getBitmapFromUri(selectedImageUri);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Bitmap frame_bmp1 = rotateBitmap(frame_bmp, 0, flipX, false);

                                //face_preview.setImageBitmap(frame_bmp1);


                                RectF boundingBox = new RectF(face.getBoundingBox());


                                Bitmap cropped_face = getCropBitmapByCPU(frame_bmp1, boundingBox);

                                Bitmap scaled = getResizedBitmap(cropped_face, 112, 112);
                                // face_preview.setImageBitmap(scaled);

                                recognizeImage(scaled);
                                //                                System.out.println(boundingBox);
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            start=true;
                            Toast.makeText(context, "Failed to add", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }

    }

    private void recordVoiceToText(int i) {
       /* Intent intent4
                = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent4.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent4.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());
        intent4.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

        try {
            startActivityForResult(intent4, i);
        }
        catch (Exception e) {
            Toast.makeText(AttendanceActivity.this, " " + e.getMessage(),
                            Toast.LENGTH_SHORT)
                    .show();
        }*/



        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // Automatically select the language based on the device's default locale
        Locale currentLocale = Locale.getDefault();
     /* if (currentLocale.getLanguage().equals(new Locale("ta").getLanguage())) {
            // If the device's language is Tamil, set Tamil for speech recognition
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, new Locale("ta"));
        } else {
            // Otherwise, use the default language (likely English)
        }*/
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                "ta-IN");


        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak");

        try {
            startActivityForResult(intent, i);
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void callUpdateMethod(AlertDialog mDialog, String projectname, String othername, String plannedwork,
                                  MKLoader loader1, RelativeLayout bottom_request_layout) {
        loader1.setVisibility(View.VISIBLE);
        List<String> sProject = new ArrayList<>();
        String str_pro = projectname;
        if(!TextUtils.isEmpty(str_pro)){
            String[] split = str_pro.split(",");
            for (int i=0;i<split.length;i++){
                String projectName = projectDetails.getAllProjectIDList(split[i]);
                Log.d("attendance_list"," split id "+split[i]+" project namt "+projectName);
                if(TextUtils.isEmpty(projectName)){
                    sProject.add("others");
                }else {
                    sProject.add(projectName);
                }


            }
        }
        bottom_request_layout.setEnabled(false);
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        String date_format = date.format(new Date());
        loader1.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(tokenHeader,
                deviceHeader).create(ApiInterface.class);
        Call<CommonPojo> call = apiInterface.DSRInsertInAttendance(commonClass.getSharedPref(getApplicationContext(),"emp_id"),
                date_format,plannedwork,sProject,othername);
        Log.d("attendance_list"," call "+call.request().url());
        call.enqueue(new Callback<CommonPojo>() {
            @Override
            public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                loader1.setVisibility(View.GONE);
                Log.d("attendance_list"," code "+response.code());
                if(response.isSuccessful()){
                    if(response.code()==400){
                      //  mDialog.dismiss();

                        commonClass.putSharedPref(getApplicationContext(),"dsr_date",date_format);
                    }
                    mDialog.dismiss();

                    if(response.code()==200){

                        if(response.body().getStatus().contains("success")){
                            commonClass.putSharedPref(getApplicationContext(),"dsr_date",date_format);
                            Log.d("CheckInCondition"," condition 8");
                            check_in.setEnabled(false);
                            checkIN="1";
                            bottom_request_layout.setEnabled(true);
                            //checkInAttendance();
                            commonClass.showSuccess(AttendanceActivity.this,response.body().getData());

                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                 //   mDialog.dismiss();
                                    Intent intent = new Intent(getApplicationContext(), DashboardNewActivity.class);
                                    startActivity(intent);
                                }
                            }, 1500);

                        }else{
                        //    mDialog.dismiss();

                            commonClass.showError(AttendanceActivity.this,response.body().getData());
                        }
                    }
                    else if(response.code()==401 || response.code()==403){
                      //  mDialog.dismiss();

                        bottom_request_layout.setEnabled(true);
                        commonClass.showError(AttendanceActivity.this,"UnAuthendicated");
                        commonClass.clearAlldata(AttendanceActivity.this);
                    } else{
                        Log.d("CheckInCondition"," condition 9");
                    //    mDialog.dismiss();

                       // checkInAttendance();
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(AttendanceActivity.this,mError.getError());
                            //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            // handle failure to read error
                            Log.d("thumbnail_url", " exp error  " + e.getMessage());
                        }
                    }
                }else{
                    Log.d("CheckInCondition"," condition 10");
                   // mDialog.dismiss();

                  //  checkInAttendance();
                    Gson gson = new GsonBuilder().create();
                    CommonPojo mError = new CommonPojo();
                    try {
                        mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                        commonClass.showError(AttendanceActivity.this,mError.getError());
                        //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // handle failure to read error
                        Log.d("thumbnail_url", " exp error  " + e.getMessage());
                    }
                }

            }

            @Override
            public void onFailure(Call<CommonPojo> call, Throwable t) {
                //checkInAttendance();
                loader1.setVisibility(View.GONE);
               // mDialog.dismiss();
                bottom_request_layout.setEnabled(true);
                commonClass.showError(AttendanceActivity.this,t.getMessage());
            }
        });
    }

    private void getList() {
        Log.d(commonTag," get list ");
        Log.d("getDating"," rew "+requestDateFormat);
         //loader1.setVisibility(View.VISIBLE);
         ApiInterface apiInterface = ApiClient.getTokenRetrofit(tokenHeader,
                    deviceHeader).create(ApiInterface.class);
        Call<AttendanceMain> call = apiInterface.getAttenList(requestDateFormat);
        Log.d(commonTag, " url " + call.request().url() + " token " +
               tokenHeader +
                " devic " +deviceHeader + " date " +
                requestDateFormat);
        Log.d("getDating"," url "+tokenHeader+" "+deviceHeader);
        dateHoliday.clear();
        customCalendar.removeDecorators();
        colors.clear();
        call.enqueue(new Callback<AttendanceMain>() {
            @Override
            public void onResponse(Call<AttendanceMain> call, Response<AttendanceMain> response) {
               // loader1.setVisibility(View.GONE);
                Log.d(commonTag," on response ");
                Log.d(commonTag, " code " + response.code());
                if (response.isSuccessful()) {
                    detailsHashmap.clear();
                    dateHashmap.clear();
                    if (response.code() == 200) {
                        if (response.body().getStatus().equals("success")) {

                            attendanceListDB.deleteAll(AttendanceActivity.this);
                            punchINOUTDB.deleteAll(AttendanceActivity.this);
                            customCalendar.setVisibility(View.VISIBLE);
                            customCalendar.removeDecorators();
                            attendanceListSubModals = response.body().getAttendanceListSubModalList();
                            if(attendanceListSubModals.size()!=0){
                                for(int i=0;i<attendanceListSubModals.size();i++) {
                                    AttendanceListSubModal modal = attendanceListSubModals.get(i);
                                        Log.d("AttDate"," date "+attendanceListSubModals.get(i).getDate());
                                        Log.d("getDating"," date "+attendanceListSubModals.get(i).getDate());
                                    if (!TextUtils.isEmpty(attendanceListSubModals.get(i).getDate())){
                                        String str_date = commonClass.splitYear(attendanceListSubModals.get(i).getDate());

                                        String split [] = str_date.split("-");
                                        int date = Integer.parseInt(split[2]);
                                        Log.d("getsizeList"," spl "+split[0]+" "+split[1]+" "+split[2]);
                                        String type = "normal";
                                        Log.d("getDating"," comon "+modal.getWeek_end()+" "+modal.getIs_late());
                                        if(modal.getWeek_end().contains("Yes")){
                                            type="weekend";
                                            Log.d("getDating","con1");
                                        }else if(modal.getHoliday().equals("1")){
                                            type="holiday";
                                           // colors.add(R.color.n_org);
                                            Log.d("getDating","con2");
                                        }else  if(modal.getLeave().equals("1")){
                                            type="leave";
                                            Log.d("getDating","con3");
                                        }else if(modal.getLeave().equals("0") && modal.getPin_work_locationList()==null){
                                            type="absent";
                                            //colors.add(R.color.nabsent);
                                            Log.d("getDating","con4");
                                        }else if(modal.getPin_work_locationList()!=null){
                                            if(modal.getPin_work_locationList().size()==1){
                                                Log.d("getDating"," int time "+modal.getPin_work_locationList().get(0));
                                                if(modal.getPin_work_locationList().get(0)==null){
                                                    if(modal.getIs_late()!=null){
                                                        if(modal.getIs_late().size()>=0){
                                                            if(modal.getIs_late().get(0).equals("1")){
                                                                type="late";
                                                                //   colors.add(R.color.nlate);
                                                                Log.d("getDating","con5 ");
                                                            }else{
                                                                type="normal";

                                                                Log.d("getDating","con8");
                                                            }
                                                        }else{
                                                            Log.d("getDating"," con6 ");
                                                            type="normal";
                                                             }
                                                    }else {
                                                        Log.d("getDating", "con7");
                                                        type = "normal";
                                                        type = "normal";
                                                    }
                                                }else{
                                                    //type ="present";
                                                    if(modal.getIs_late()!=null){
                                                        if(modal.getIs_late().size()>=0){
                                                            if(modal.getIs_late().get(0).equals("1")){
                                                                type="late";
                                                            //    colors.add(R.color.nlate);

                                                                Log.d("getDating","con9");
                                                            }else{
                                                                Log.d("getDating","con10");
                                                                type="present";

                                                            }
                                                        }else{
                                                            type="normal";
                                                        }
                                                    }else {
                                                        Log.d("getDating","con11");
                                                        type = "weekend";
                                                      //  colors.add(R.color.npresnet);

                                                    }
                                                }
                                            }else{
                                              //  type="present";
                                                if(modal.getIs_late()!=null){
                                                    if(modal.getIs_late().size()>=0){
                                                        if(modal.getIs_late().get(0).equals("1")){
                                                            type="late";
                                                            Log.d("getDating"," con 12 ");
                                                            //colors.add(R.color.nlate);

                                                        }
                                                    }else{
                                                        Log.d("getDating"," con 13 ");
                                                        type="normal";
                                                         }
                                                }else {
                                                    Log.d("AttDate"," con 14 ");
                                                   // type = "present";
                                                    type="normal";
                                                       }
                                            }

                                        }
                                        Log.d("getDating"," type as "+type+" date as "+date);
                                        //pin null && leave 0 type= "absent"
                                        dateHashmap.put(date,type);


                                        if(type.contains("weekend") || type.contains("holiday")){
                                            Log.d("getDating"," called weekend/holiday");
                                            setDateCalendar(split,"#f5b24a");
                                        }else if(type.contains("leave")){
                                            Log.d("getDating"," called leave");
                                            setDateCalendar(split,"#F44336");

                                        }else if(type.contains("present")){
                                            Log.d("getDating"," called present");
                                            setDateCalendar(split,"#4CAF50");
                                        }else if(type.contains("absent")){
                                            Log.d("getDating"," called absent");
                                            setDateCalendar(split,"#E91E63");

                                        }else if(type.contains("late")){
                                            Log.d("getDating"," called late");
                                            setDateCalendar(split,"#CDDC39");

                                        }


                                    /*    switch (type){
                                            case "weekend":
                                                setDateCalendar(split,"#f5b24a");
                                                break;
                                            case "holiday":
                                                setDateCalendar(split,"#f5b24a");
                                                break;
                                            case "leave":
                                                setDateCalendar(split,"#F44336");
                                                break;
                                            case "present":
                                                setDateCalendar(split,"#4CAF50");
                                                break;
                                            case "absent":
                                                setDateCalendar(split,"#E91E63");
                                                break;
                                            case "late":
                                                setDateCalendar(split,"#CDDC39");
                                                break;

                                        }

*/

                                    }
                                    if(modal.getIntimeList()!=null){
                                        if(modal.getIntimeList().size()==1){
                                            if(modal.getIntimeList().get(0)==null) {
                                                long id= attendanceListDB.insertBulk(modal.getDate(),modal.getWeek_date(),modal.getWeek_end(),modal.getHoliday(),
                                                        modal.getLeave(),"0","online","0","0");
                                                punchINOUTDB.insertBulk(modal.getDate(),String.valueOf(id),null,null,null,null,null,null,null,"0","0");
                                            }else{
                                                long id=attendanceListDB.insertBulk(modal.getDate(),modal.getWeek_date(),modal.getWeek_end(),modal.getHoliday(),
                                                        modal.getLeave(),"0","online","0","0");
                                                punchINOUTDB.insertBulk(modal.getDate(),String.valueOf(id),modal.getIntimeList().get(0),modal.getOuttimeList().get(0),
                                                        modal.getWorkhoursList().get(0)
                                                        ,modal.getPin_work_locationList().get(0),modal.getPout_work_locationList().get(0),null,null,"0","0");

                                            }
                                        }else{
                                           long id=  attendanceListDB.insertBulk(modal.getDate(),modal.getWeek_date(),modal.getWeek_end(),modal.getHoliday(),
                                                    modal.getLeave(),"0","online","0","0");
                                            for(int i1=0;i1<modal.getIntimeList().size();i1++){
                                                punchINOUTDB.insertBulk(modal.getDate(),String.valueOf(id),modal.getIntimeList().get(i1),
                                                        modal.getOuttimeList().get(i1),modal.getWorkhoursList().get(i1),modal.getPin_work_locationList().get(i1),
                                                        modal.getPout_work_locationList().get(i1),null,null,"0","0");
                                            }
                                        }
                                    }




                                }
                                Log.d("getHolidayList"," call2 ");
                               // updateCalendar();
                            }
                          /*  AttendanceAdapter adapter = new AttendanceAdapter(AttendanceActivity.this,
                                    response.body().getAttendanceListSubModalList(),loader);
                            listRV.setAdapter(adapter);*/
                          /*  frame_tag.setText("Punch Attendance");
                            frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));*/
                          /*  applyattendancelayout.setVisibility(View.GONE);
                            listLayout.setVisibility(View.VISIBLE);*/
                        } else {
                            commonClass.showSuccess(AttendanceActivity.this, response.body().getStatus());
                        }
                    } else if(response.code()==401 || response.code()==403){
                        commonClass.showError(AttendanceActivity.this,"UnAuthendicated");
                        commonClass.clearAlldata(AttendanceActivity.this);
                    } else {
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(AttendanceActivity.this, mError.getError());
                            //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            // handle failure to read error
                            Log.d("thumbnail_url", " exp error  " + e.getMessage());
                        }
                    }
                } else {
                    if(response.code()==401 || response.code()==403){
                        commonClass.showError(AttendanceActivity.this,"UnAuthendicated");
                        commonClass.clearAlldata(AttendanceActivity.this);
                    }else {
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(AttendanceActivity.this, mError.getError());
                            //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            // handle failure to read error
                            Log.d("thumbnail_url", " exp error  " + e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<AttendanceMain> call, Throwable t) {
                // progressDialog.dismiss();
               // loader1.setVisibility(View.GONE);
                Log.d(commonTag," onFail ");
               // commonClass.showServerToast(AttendanceActivity.this, t.getMessage());
            }
        });
    }

    private void setDateCalendar(String[] split, String hashtag) {
        dateColorMap.put(CalendarDay.from(Integer.parseInt(split[0]), Integer.parseInt(split[1]),Integer.parseInt( split[2])),Color.parseColor(hashtag));
        Collection<Integer> colorsNew=new ArrayList<>();
        colorsNew.add(Color.parseColor(hashtag));
        ArraySet<CalendarDay> dateHolidayNew=new ArraySet<>();
        dateHolidayNew.add(CalendarDay.from(Integer.parseInt(split[0]), Integer.parseInt(split[1]),Integer.parseInt( split[2])));
        EventDecorator eventDecorator = new EventDecorator(dateHolidayNew,colorsNew);
        customCalendar.addDecorator(eventDecorator);
    }

    private void updateCalendar() {
        Log.d("dateHashmap"," size sas "+dateHashmap.size());
        customCalendar.setVisibility(View.VISIBLE);
        customCalendar.removeDecorators();
        Log.d("getHolidayList"," hol "+dateHoliday.size()+" color "+colors.size());
        if(dateHoliday.size()!=0 && colors.size()!=0){
            if(dateHoliday.size()==colors.size()) {


                int index = 0;
                for (CalendarDay date : dateHoliday) {
                    Set<CalendarDay> dateHolidaynew = new HashSet<>();
                    Collection<Integer> colornew = new ArrayList<>();
                    Log.d("getColot"," clr "+colors.toArray()[index]+" date "+date);
                     dateHolidaynew.add(date);
                    colornew.add(((Integer) colors.toArray()[index]));
                    EventDecorator eventDecorator = new EventDecorator(dateHolidaynew,colornew);
                    customCalendar.addDecorator(eventDecorator);
                    index++;
                }

            }
        }


/*
        if(dateHashmap.size()!=0){
            Calendar calendar=  Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Log.d("dateHashmap"," foamt "+format);
            try {
                Log.d("dateHashmap"," come to try ");
                calendar.setTime(format.parse(requestDateFormat));
                //customCalendar.setDate(calendar,dateHashmap);


                Log.d("getsizeList"," "+dateNormal.size()+" "+dateLeave.size()+" "+
                        dateAbsent.size()+" "+dateHoliday.size()+" "+datePresent.size()+" "+dateLAte.size());



                //BackgroundColorDecorator decorator1 = new BackgroundColorDecorator(dateNormal, R.color.white);
               */
/* BackgroundColorDecorator decorator2 = new BackgroundColorDecorator(dateLeave, R.color.nleave);
                BackgroundColorDecorator decorator3 = new BackgroundColorDecorator(dateAbsent, R.color.nabsent);
                BackgroundColorDecorator decorator4 = new BackgroundColorDecorator(dateHoliday, R.color.n_org);
                BackgroundColorDecorator decorator5 = new BackgroundColorDecorator(datePresent, R.color.npresnet);
                BackgroundColorDecorator decorator6= new BackgroundColorDecorator(dateLAte, R.color.nlate);
                if(dateLeave.size()!=0){
                    customCalendar.addDecorator(decorator2);
                }
                if(dateAbsent.size()!=0){
                    customCalendar.addDecorator(decorator3);
                }
                if(dateHoliday.size()!=0){
                    customCalendar.addDecorator(decorator4);
                }
                if(datePresent.size()!=0){
                    customCalendar.addDecorator(decorator5);
                }
                if(dateLAte.size()!=0){
                    customCalendar.addDecorator(decorator6);
                }*//*




            } catch (ParseException e) {
                Log.d("dateHashmap"," come to else "+e.getMessage());
                e.printStackTrace();
            }
//                                customCalendar.setDate(calendar,dateHashmap);
        }
*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        callDashboard();
    }

    private void callDashboard() {


        if(listLayout.getVisibility()==View.VISIBLE){
            frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.calendar_icon_new));
            frame_tag.setText("Attendance List");
            title.setText("Attendance");
            applyattendancelayout.setVisibility(View.VISIBLE);
            listLayout.setVisibility(View.GONE);
        }else{
            Intent intent = new Intent(getApplicationContext(), DashboardNewActivity.class);
            startActivity(intent);
        }

    }

    public void todayAttendanceList() {
        Log.d(commonTag," today attendance ");
       // progressDialog.show();
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(AttendanceActivity.this, "token"),
                commonClass.getDeviceID(AttendanceActivity.this)).create(ApiInterface.class);
        Call<TodayModal> call = apiInterface.getDailySummary();
        Log.d(commonTag, " url " + call.request().url() + " token " + commonClass.getDeviceID(AttendanceActivity.this) +
                " device " + commonClass.getSharedPref(getApplicationContext(), "token"));
        call.enqueue(new Callback<TodayModal>() {
            @Override
            public void onResponse(Call<TodayModal> call, Response<TodayModal> response) {
                //progressDialog.dismiss();
                Log.d(commonTag," today response ");
                loader.setVisibility(View.GONE);
                Log.d(commonTag, " code " + response.code()+response.raw().sentRequestAtMillis()+"  "+
                        response.raw().receivedResponseAtMillis());
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        Log.d(commonTag, " status " + response.body().getStatus() + " size as " +
                                response.body().getCommonPojo().getInTimeList().size());
                        if (response.body().getStatus().equals("success")) {
                            if (response.body().getCommonPojo().getInTimeList().size() != 0) {
                                List<String> getList1, getList2;
                                getList1 = new ArrayList<>();
                                getList2 = new ArrayList<>();
                                TodayAdapter adapter = new TodayAdapter(AttendanceActivity.this, response.body().getCommonPojo().getInTimeList(),
                                        response.body().getCommonPojo().getOutTimeList(),
                                        response.body().getCommonPojo().getWorkHours(), 1, getList1, getList2,loader);
                                todayRV.setAdapter(adapter);
                                Log.d("ShowTrack"," con 2 "+adapter.getItemCount());
                                if(adapter.getItemCount()!=0){
                                    tracking.setVisibility(View.VISIBLE);
                                    notpunchin.setVisibility(View.GONE);
                                }else{
                                    notpunchin.setVisibility(View.VISIBLE);
                                    tracking.setVisibility(View.GONE);
                                }
                              /*  scrollView.fullScroll(View.FOCUS_UP);//if you move at the end of the scroll
                                scrollView.pageScroll(View.FOCUS_UP);*/
                            }
                        } else {
                          //  commonClass.showServerToast(AttendanceActivity.this, response.body().getStatus());
                        }

                    }else if(response.code()==401 || response.code()==403){
                            commonClass.showError(AttendanceActivity.this,"UnAuthendicated");
                             commonClass.clearAlldata(AttendanceActivity.this);
                    } else {
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                           // commonClass.showError(AttendanceActivity.this, mError.getError());
                            //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            // handle failure to read error
                            Log.d("thumbnail_url", " exp error  " + e.getMessage());
                        }
                    }
                } else {
                    Gson gson = new GsonBuilder().create();
                    CommonPojo mError = new CommonPojo();
                    try {
                        mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                       // commonClass.showError(AttendanceActivity.this, mError.getError());
                        //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // handle failure to read error
                        Log.d("thumbnail_url", " exp error  " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<TodayModal> call, Throwable t) {
                //progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                Log.d(commonTag," today faile "+t.getMessage());


                // commonClass.showAppToast(AttendanceActivity.this, t.getMessage());
            }
        });
    }

    private void checkOutAttendance() {
        Log.d("checkinHeader"," check out called ");
        commonClass.putSharedPref(getApplicationContext(), "work_location", workLocation);
        //progressDialog.show();
        if(isMyServiceRunning(mYourService.getClass())){
            mYourService.onDestroy();
            stopService(mServiceIntent);
        }
        if(isMyServiceRunning(lUService.getClass())){
            lUService.onDestroy();
         }

        if(commonClass.isOnline(AttendanceActivity.this)){

            loader.setVisibility(View.VISIBLE);
            ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(AttendanceActivity.this, "token"),
                    commonClass.getDeviceID(AttendanceActivity.this)).create(ApiInterface.class);
            Call<CommonPojo> call = apiInterface.callPunchOut(
                    commonClass.getSharedPref(getApplicationContext(), "sync_id"),
                    latitude, longitude, workLocation,branch_id);
            Log.d("work_locatoin","check out "+call.request().url());
            call.enqueue(new Callback<CommonPojo>() {
                @Override
                public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                    // progressDialog.dismiss();
                    loader.setVisibility(View.GONE);
                    Log.d("attendance_punch", " response code " + response.code());
                    if (response.code() == 200) {
                        if (response.body().getStatus().equals("success")) {

                            String getIn = String.valueOf(latitude)+","+String.valueOf(longitude);
                            SimpleDateFormat df3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            SimpleDateFormat df31 = new SimpleDateFormat("yyyy-MM-dd");
                            punchINOUTDB.updateBulk(getIn,df31.format(new Date()),commonClass.getSharedPref(getApplicationContext(),"sync_id")
                                    ,workLocation,String.valueOf(df3.format(new Date())));
                            punchINOUTDB.setWorkingHoures(commonClass.getSharedPref(getApplicationContext(),"sync_id"));


                            commonClass.showSuccess(AttendanceActivity.this, response.body().getData());
                            check_in.setEnabled(true);
                            checkout.setEnabled(false);
                            commonClass.putSharedPref(getApplicationContext(), "punch_in", null);
                            commonClass.putSharedPref(getApplicationContext(), "sync_id", null);
                          //  commonClass.putSharedPref(getApplicationContext(), "work_location", null);
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    Intent intent = new Intent(getApplicationContext(), DashboardNewActivity.class);
                                    startActivity(intent);
                                }
                            }, 1500);
                        } else {
                            commonClass.showError(AttendanceActivity.this, response.body().getData());
                           }
                    }
                    else if(response.code()==401 || response.code()==403){
                        commonClass.showError(AttendanceActivity.this,"UnAuthendicated");
                        commonClass.clearAlldata(AttendanceActivity.this);
                    } else {
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(AttendanceActivity.this, mError.getError());
                            //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            // handle failure to read error
                            Log.d("thumbnail_url", " exp error  " + e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(Call<CommonPojo> call, Throwable t) {
                    //progressDialog.dismiss();
                    loader.setVisibility(View.GONE);
                    commonClass.showError(AttendanceActivity.this, t.getMessage());
                }
            });
        }else{
            //// offline content

            SimpleDateFormat df = new SimpleDateFormat("EEEE , MMMM dd - yyyy");
            SimpleDateFormat df3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

             Location location = getLastKnownLocation();
            String getIn="11.452,76.900";
            if(location!=null) {
                getIn = location.getLatitude() + "," + location.getLongitude();
            }
            SimpleDateFormat df31 = new SimpleDateFormat("yyyy-MM-dd");
            punchINOUTDB.updateBulk(getIn,df31.format(new Date()),commonClass.getSharedPref(getApplicationContext(),"sync_id")
                    ,workLocation,String.valueOf(df3.format(new Date())));
            punchINOUTDB.setWorkingHoures(commonClass.getSharedPref(getApplicationContext(),"sync_id"));



            if(isMyServiceRunning(mYourService.getClass())){
                mYourService.onDestroy();
            }
            if(isMyServiceRunning(lUService.getClass())){
                lUService.onDestroy();
            }
            commonClass.showSuccess(AttendanceActivity.this,"Punch Out Successfully....");

            check_in.setEnabled(true);
            checkout.setEnabled(false);
            commonClass.putSharedPref(getApplicationContext(), "punch_in", null);
            commonClass.putSharedPref(getApplicationContext(), "sync_id", null);
          //  commonClass.putSharedPref(getApplicationContext(), "work_location", null);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), DashboardNewActivity.class);
                    startActivity(intent);
                }
            }, 2500);

        }

    }

    private void checkInAttendance() {
        loader.setVisibility(View.VISIBLE);
        check_in.setEnabled(false);
        Log.d("CheckInCodtion"," is enabled "+check_in.isEnabled());
        if(!check_in.isEnabled()){
        String id = UUID.randomUUID().toString();
        Log.d("dashboard_id", "device " + id);


        commonClass.putSharedPref(getApplicationContext(), "work_location", workLocation);

        if (commonClass.isOnline(AttendanceActivity.this)) {
            Log.d("CheckInCondition"," check in attendance ");
            // progressDialog.show();
            loader.setVisibility(View.VISIBLE);
            ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(AttendanceActivity.this, "token"),
                    commonClass.getDeviceID(AttendanceActivity.this)).create(ApiInterface.class);
            Call<CommonPojo> call = apiInterface.callPunchIn(latitude, longitude, workLocation,branch_id);
            Log.d("work_locatoin", " url " + call.request().url());
            call.enqueue(new Callback<CommonPojo>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                    // progressDialog.dismiss();
                    loader.setVisibility(View.GONE);
                    Log.d("work_locatoin", " response code " + response.code());
                    if (response.code() == 200) {
                        if (response.body().getStatus().equals("success")) {
                            check_in.setEnabled(false);
                            checkout.setEnabled(true);
                            Log.d("checkinHeader", " date " + today_date + " sync id " +
                                    response.body().getPunchInModal().getSync_id());


                            SimpleDateFormat df = new SimpleDateFormat("EEEE , MMMM dd - yyyy");
                            SimpleDateFormat df3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String getIn = String.valueOf(latitude) + "," + String.valueOf(longitude);

                            String week_date = df.format(new Date());
                            SimpleDateFormat df31 = new SimpleDateFormat("yyyy-MM-dd");
                            String strDate = df31.format(new Date());
                            String[] dateArr = strDate.split("-");
                            int monthValue = Integer.parseInt(dateArr[1]);
                            String finalArr = strDate;
                            if (monthValue <= 9) {
                                finalArr = dateArr[0] + "-0" + dateArr[1] + "-" + dateArr[2];
                            }
                            Log.d("checkinHeader", "insert 2");

                            attendanceListDB.insertBulk(finalArr, week_date, "no", "0", "0", "0", "online", response.body().getPunchInModal().getSync_id()
                                    , id);
                            punchINOUTDB.insertBulk(finalArr, "0", String.valueOf(df3.format(new Date())), null, "0", workLocation,
                                    null, getIn, null, response.body().getPunchInModal().getSync_id(), id);

                            commonClass.putSharedPref(getApplicationContext(), "work_location", workLocation);
                            commonClass.putSharedPref(getApplicationContext(), "punch_in", today_date);
                            commonClass.putSharedPref(getApplicationContext(), "uuid", response.body().getPunchInModal().getMa_uuid());
                            commonClass.putSharedPref(getApplicationContext(), "sync_id", response.body().getPunchInModal().getSync_id());
                            commonClass.showSuccess(AttendanceActivity.this, response.body().getData());
                            commonClass.putSharedPref(getApplicationContext(), "m_uuid", id);
                            commonClass.putSharedPref(getApplicationContext(), "punchin_time", String.valueOf(localtime));
                            updateCheckHeader(2);

                            SendPushNotification();
                            check_in.setEnabled(false);
                            checkIN="1";

                             new Handler().postDelayed(new Runnable() {
                                public void run() {
                                   /* Intent intent = new Intent(getApplicationContext(), DashboardNewActivity.class);
                                    startActivity(intent);*/
                                    if(dsr_flag.equals("0")){
                                        callAlertDialog();
                                    }else{
                                        callDashboard();
                                    }
                                }
                            }, 1500);

                        } else {
                            commonClass.showError(AttendanceActivity.this, response.body().getData());
                        }
                    } else {
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(AttendanceActivity.this, mError.getError());
                            //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            // handle failure to read error
                            Log.d("work_locatoin", " exp error  " + e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(Call<CommonPojo> call, Throwable t) {
                    // progressDialog.dismiss();
                    loader.setVisibility(View.GONE);
                    Log.d("work_locatoin", " error " + t.getMessage());
                    commonClass.showError(AttendanceActivity.this, t.getMessage());
                }
            });
        } else {

            String offline_tag = commonClass.getSharedPref(getApplicationContext(), "offline_punch");
            if (!TextUtils.isEmpty(offline_tag)) {
                if (!offline_tag.contains(today_date)) {
                    offline_tag += "," + today_date;
                }
            } else {
                offline_tag = today_date;
            }
            commonClass.putSharedPref(getApplicationContext(), "offline_punch", offline_tag);

            int count = 0;

            for (int i = 0; i < offline_tag.length(); i++) {
                if (offline_tag.charAt(i) == ',')
                    count++;
            }
            if (count > 2) {
                commonClass.showError(AttendanceActivity.this, "Cannot Punch Attendance More than 3 days ");
            } else {


                long sync_id = new Date().getTime();
                SimpleDateFormat df = new SimpleDateFormat("EEEE , MMMM dd - yyyy");
                SimpleDateFormat df3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String week_date = df.format(new Date());
                Location location = getLastKnownLocation();
                String getIn = "11.452,76.900";
                if (location != null) {
                    getIn = location.getLatitude() + "," + location.getLongitude();
                }
                SimpleDateFormat df31 = new SimpleDateFormat("yyyy-MM-dd");
                String strDate = df31.format(new Date());
                String[] dateArr = strDate.split("-");
                int monthValue = Integer.parseInt(dateArr[1]);
                String finalArr = strDate;
                if (monthValue <= 9) {
                    finalArr = dateArr[0] + "-0" + dateArr[1] + "-" + dateArr[2];
                }
                Log.d("checkinHeader", "insert 1 ");

                attendanceListDB.insertBulk(strDate, week_date, "no", "0", "0", "1", "offline", String.valueOf(sync_id), id);
                punchINOUTDB.insertBulk(strDate, "1", String.valueOf(df3.format(new Date())), null, "0", workLocation,
                        null, getIn, null, String.valueOf(sync_id), id);
                commonClass.putSharedPref(getApplicationContext(), "work_location", workLocation);
                commonClass.putSharedPref(getApplicationContext(), "punch_in", today_date);
                commonClass.putSharedPref(getApplicationContext(), "sync_id", String.valueOf(sync_id));
                commonClass.putSharedPref(getApplicationContext(), "m_uuid", id);

                /*if (!isMyServiceRunning(mYourService.getClass())) {
                    Log.d("FuseService", " service not runnning ");
                    startService(mServiceIntent);
                } else {
                    Log.d("FuseService", " service running ");
                }
                if (!isMyServiceRunning(lUService.getClass())) {
                    Log.d("LocationsTrack", " service not runnning ");
                    startService(lUIntent);
                } else {
                    Log.d("LocationsTrack", " service running ");
                }*/
                SendPushNotification();
                commonClass.showSuccess(AttendanceActivity.this, "Punch In Successfully...");
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), DashboardNewActivity.class);
                        startActivity(intent);
                    }
                }, 1500);
            }

        }
    }


    }

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private void SendPushNotification() {
        Dexter.withContext(AttendanceActivity.this)
                .withPermissions(
                        Manifest.permission.FOREGROUND_SERVICE,
                        Manifest.permission.FOREGROUND_SERVICE_LOCATION
                        ).withListener(new MultiplePermissionsListener() {
                    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            if (!isMyServiceRunning(mYourService.getClass())) {
                                Log.d("FuseService", " service not runnning ");
                                startService(mServiceIntent);
                            } else {
                                Log.d("FuseService", " service running ");
                            }
                            if (!isMyServiceRunning(lUService.getClass())) {
                                Log.d("LocationsTrack", " service not runnning ");
                                startService(lUIntent);
                            } else {
                                Log.d("LocationsTrack", " service running ");
                            }
                        }else{
                            commonClass.showWarning(AttendanceActivity.this,"Notification Permission Not Granded");
                        }
                    }
                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                }).check();
    }

    public int CalculationByDistance(double lat1,double lon1,double lat2,double lon2,String actual_distance ) {
        ///idea 3
        double distance=0;
        double minDis=0;

        BranchTable branchTable= new BranchTable(AttendanceActivity.this);
        branchTable.getWritableDatabase();


        List<LatLng> getBranchDetails = new ArrayList<>();
        getBranchDetails = branchTable.getAllNameList();
        Log.d("getDistance"," get list size "+getBranchDetails.size());
        if(getBranchDetails.size()!=0){
            for(int i=0;i<getBranchDetails.size();i++){
                Double lat11 =Double.valueOf(getBranchDetails.get(i).getLatitude());
                Double lng11 =Double.valueOf(getBranchDetails.get(i).getLongitude());
                Location locationA = new Location("");
                locationA.setLatitude(lat11);
                locationA.setLongitude(lng11);
                Location locationB = new Location("");
                locationB.setLatitude(lat2);
                locationB.setLongitude(lon2);
                distance = locationA.distanceTo(locationB);
                Log.d("getDistance"," as "+minDis);
                if(i==0){
                    minDis = distance;
                    branch_id = getBranchDetails.get(i).getId();
                }else{
                    if(minDis>distance){
                        branch_id = getBranchDetails.get(i).getId();
                        minDis=distance;
                    }
                }
            }
        }





        Log.d("getDistance"," idea 3 distance "+distance);
        return (int) minDis;

    }

        @RequiresApi(api = Build.VERSION_CODES.M)
    private void updateCheckHeader(int i) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            check_in.setBackgroundTintList(getApplicationContext().getColorStateList(R.color.n_blue));
            checkout.setBackgroundTintList(getApplicationContext().getColorStateList(R.color.n_blue));
        }
        if (i == 1) {
            check_in.setBackgroundTintList(getApplicationContext().getColorStateList(R.color.n_org));
        } else {
            checkout.setBackgroundTintList(getApplicationContext().getColorStateList(R.color.color_primary));
        }
    }

    private void updateUIHeader(int i) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            office_layout.setBackground(getApplicationContext().getDrawable(R.drawable.rectangle_border));
            office_layout.setBackgroundTintList(getApplicationContext().getColorStateList(R.color.n_blue));
            client_layout.setBackground(getApplicationContext().getDrawable(R.drawable.rectangle_border));
            client_layout.setBackgroundTintList(getApplicationContext().getColorStateList(R.color.n_blue));
            home_layout.setBackground(getApplicationContext().getDrawable(R.drawable.rectangle_border));
            home_layout.setBackgroundTintList(getApplicationContext().getColorStateList(R.color.n_blue));
        }

        if (i == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                 office_layout.setBackgroundTintList(getApplicationContext().getColorStateList(R.color.color_primary));
                ofz_icon.setImageTintList(getApplicationContext().getColorStateList(R.color.white));
                ofz_text.setTextColor(getApplicationContext().getColor(R.color.white));
            }
        } else if (i == 2) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                home_layout.setBackgroundTintList(getApplicationContext().getColorStateList(R.color.color_primary));
                home_icon.setImageTintList(getApplicationContext().getColorStateList(R.color.white));
                home_text.setTextColor(getApplicationContext().getColor(R.color.white));
            }

        } else if (i == 3) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                 client_layout.setBackgroundTintList(getApplicationContext().getColorStateList(R.color.color_primary));
                client_icon.setImageTintList(getApplicationContext().getColorStateList(R.color.white));
                client_text.setTextColor(getApplicationContext().getColor(R.color.white));
            }

        }
    }


    protected synchronized void buildGoogleApiClient() {
        Log.d(commonTag," sync ");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            latitude =mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();

        }

        mLocationRequest1 = new LocationRequest();
        mLocationRequest1.setInterval(10000); //10 seconds
        mLocationRequest1.setFastestInterval(5000); //5 seconds
        mLocationRequest1.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest1.setSmallestDisplacement(1); //1 meter

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest1, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
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
        Log.d("FuseService"," onDestroy "
                +" uuid "+commonClass.getSharedPref(getApplicationContext(),"uuid")+
                " sync id "+commonClass.getSharedPref(getApplicationContext(),"sync_id"));        if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"uuid")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"sync_id"))){
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("restartservice");
            broadcastIntent.setClass(this, Restarter.class);
            this.sendBroadcast(broadcastIntent);
        }
        super.onDestroy();
    }


    //Load Faces from Shared Preferences.Json String to Recognition object
    private HashMap<String, SimilarityClassifier.Recognition>
    readFromSP(){
        Log.d("FaceReg","read from SP");
        SharedPreferences sharedPreferences = getSharedPreferences("HashMap", MODE_PRIVATE);
        String defValue = new Gson().toJson(new HashMap<String, SimilarityClassifier.Recognition>());
        String json=sharedPreferences.getString("map",defValue);
        // System.out.println("Output json"+json.toString());
        TypeToken<HashMap<String,SimilarityClassifier.Recognition>> token = new TypeToken<HashMap<String,SimilarityClassifier.Recognition>>() {};
        HashMap<String,SimilarityClassifier.Recognition> retrievedMap=new Gson().fromJson(json,token.getType());
        // System.out.println("Output map"+retrievedMap.toString());

        //During type conversion and save/load procedure,format changes(eg float converted to double).
        //So embeddings need to be extracted from it in required format(eg.double to float).
        for (Map.Entry<String, SimilarityClassifier.Recognition> entry : retrievedMap.entrySet())
        {
            float[][] output=new float[1][OUTPUT_SIZE];
            ArrayList arrayList= (ArrayList) entry.getValue().getExtra();
            arrayList = (ArrayList) arrayList.get(0);
            for (int counter = 0; counter < arrayList.size(); counter++) {
                output[0][counter]= ((Double) arrayList.get(counter)).floatValue();
            }
            entry.getValue().setExtra(output);

            //System.out.println("Entry output "+entry.getKey()+" "+entry.getValue().getExtra() );

        }
//        System.out.println("OUTPUT"+ Arrays.deepToString(outut));
        //  Toast.makeText(context, "Recognitions Loaded", Toast.LENGTH_SHORT).show();
        return retrievedMap;
    }
    private void getFaceListOffline() {
       SQLFaceModal sqlFaceModal=faceAuthDB.selectFaceList();
       if(sqlFaceModal!=null){
           if(sqlFaceModal.getExtra()!=null){
               start=true;
               SimilarityClassifier.Recognition result = new SimilarityClassifier.Recognition(
                       commonClass.getDeviceID(AttendanceActivity.this),
                       sqlFaceModal.getTitle() , 0.5f);

               try {
                   String embeddingsString = sqlFaceModal.getExtra();
                   float[][] value = new Gson().fromJson(embeddingsString, float[][].class);
                   result.setExtra(value);
                   registered.put(sqlFaceModal.getTitle(), result);
                   previewView.setVisibility(View.VISIBLE);
               } catch (JsonSyntaxException | ClassCastException e) {
                   // Handle parsing error or class cast error
                   e.printStackTrace();
                   commonClass.showError(AttendanceActivity.this,"No Face Registered");
                   Log.d("getFaceID"," eror "+e.getMessage());
                   // You might want to log this or inform the user
               }

           }else{
               commonClass.showWarning(AttendanceActivity.this,"Go Online and register Your face first");
           }
       }else{
           commonClass.showWarning(AttendanceActivity.this,"Go Online and register Your face first");
       }
    }
    public void getFaceList(){
        CommonClass commonClass = new CommonClass();
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface  = ApiClient.getTokenWithoutAuth( ).create(ApiInterface.class);
        Call<FaceModal> call = apiInterface.getFaceList(commonClass.getDeviceID(AttendanceActivity.this));
        Log.d("getFaceID"," details"+call.request().url());
        call.enqueue(new Callback<FaceModal>() {
            @Override
            public void onResponse(Call<FaceModal> call, Response<FaceModal> response) {
                loader.setVisibility(View.GONE);
                Log.d("getFaceID"," face "+response.code());
                if(response.isSuccessful()){
                    if(response.code()==200){
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body());
                        Log.d("getFaceID"," json as "+json);
                        if(response.body().getCommonPojo()!=null){
                            if(response.body().getCommonPojo().getEmbeedings()!=null){
                                start=true;
                                SimilarityClassifier.Recognition result = new SimilarityClassifier.Recognition(
                                        commonClass.getDeviceID(AttendanceActivity.this),
                                        response.body().getCommonPojo().getTitle() , 0.5f);

                                try {
                                    String embeddingsString = response.body().getCommonPojo().getEmbeedings();
                                    float[][] value = new Gson().fromJson(embeddingsString, float[][].class);
                                    result.setExtra(value);
                                    faceAuthDB.insertData(embeddingsString,response.body().getCommonPojo().getTitle(),
                                            response.body().getCommonPojo().getUpdated_at());


                                    registered.put(response.body().getCommonPojo().getTitle(), result);
                                    previewView.setVisibility(View.VISIBLE);
                                } catch (JsonSyntaxException | ClassCastException e) {
                                    // Handle parsing error or class cast error
                                    e.printStackTrace();
                                    commonClass.showError(AttendanceActivity.this,"No Face Registered");
                                    Log.d("getFaceID"," eror "+e.getMessage());
                                    // You might want to log this or inform the user
                                }


                            }else{
                                faceAuthDB.DropTable();
                               commonClass.showError(AttendanceActivity.this,"Register Face for punch attendance");
                            }
                        }else{

                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<FaceModal> call, Throwable t) {
                loader.setVisibility(View.GONE);
                Log.d("getFaceID"," erro "+t.getMessage());
            }
        });



    }
    private MappedByteBuffer loadModelFile(Activity activity, String MODEL_FILE) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
    private void cameraBind()
    {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        previewView=findViewById(R.id.previewView);
        previewView.setVisibility(View.GONE);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();

                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this in Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
    }
    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Log.d("FaceReg"," bind preive ");
        Preview preview = new Preview.Builder()
                .build();

        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(cam_face)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(640, 480))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) //Latest frame is shown
                        .build();

        Executor executor = Executors.newSingleThreadExecutor();
        imageAnalysis.setAnalyzer(executor, new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {
                try {
                    Thread.sleep(0);  //Camera preview refreshed every 10 millisec(adjust as required)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                InputImage image = null;


                @SuppressLint("UnsafeExperimentalUsageError")
                // Camera Feed-->Analyzer-->ImageProxy-->mediaImage-->InputImage(needed for ML kit face detection)

                Image mediaImage = imageProxy.getImage();

                if (mediaImage != null) {
                    image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
//                    System.out.println("Rotation "+imageProxy.getImageInfo().getRotationDegrees());
                }

//                System.out.println("ANALYSIS");

                //Process acquired image to detect faces
                Task<List<Face>> result =
                        detector.process(image)
                                .addOnSuccessListener(
                                        new OnSuccessListener<List<Face>>() {
                                            @Override
                                            public void onSuccess(List<Face> faces) {

                                                if(faces.size()!=0) {

                                                    Face face = faces.get(0); //Get first face from detected faces
//                                                    System.out.println(face);

                                                    //mediaImage to Bitmap
                                                    Bitmap frame_bmp = toBitmap(mediaImage);

                                                    int rot = imageProxy.getImageInfo().getRotationDegrees();

                                                    //Adjust orientation of Face
                                                    Bitmap frame_bmp1 = rotateBitmap(frame_bmp, rot, false, false);



                                                    //Get bounding box of face
                                                    RectF boundingBox = new RectF(face.getBoundingBox());

                                                    //Crop out bounding box from whole Bitmap(image)
                                                    Bitmap cropped_face = getCropBitmapByCPU(frame_bmp1, boundingBox);

                                                    if(flipX)
                                                        cropped_face = rotateBitmap(cropped_face, 0, flipX, false);
                                                    //Scale the acquired Face to 112*112 which is required input for model
                                                    Bitmap scaled = getResizedBitmap(cropped_face, 112, 112);

                                                    if(start)
                                                        recognizeImage(scaled); //Send scaled bitmap to create face embeddings.
//                                                    System.out.println(boundingBox);

                                                }
                                                else
                                                {
                                                    //commonClass.showError(AttendanceActivity.this,"No Face Detected");
                                                }

                                            }
                                        })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Task failed with an exception
                                                // ...
                                            }
                                        })
                                .addOnCompleteListener(new OnCompleteListener<List<Face>>() {
                                    @Override
                                    public void onComplete(@NonNull Task<List<Face>> task) {

                                        imageProxy.close(); //v.important to acquire next frame for analysis
                                    }
                                });


            }
        });


        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imageAnalysis, preview);


    }

    public void recognizeImage(final Bitmap bitmap) {
        Log.d("FaceReg"," recoganize image ");

        // set Face to Preview

        //Create ByteBuffer to store normalized image

        Log.d("FaceReg"," input size "+inputSize);
        ByteBuffer imgData = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4);

        imgData.order(ByteOrder.nativeOrder());

        intValues = new int[inputSize * inputSize];

        //get pixel values from Bitmap to normalize
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        imgData.rewind();

        for (int i = 0; i < inputSize; ++i) {
            for (int j = 0; j < inputSize; ++j) {
                int pixelValue = intValues[i * inputSize + j];
                if (isModelQuantized) {
                    // Quantized model
                    imgData.put((byte) ((pixelValue >> 16) & 0xFF));
                    imgData.put((byte) ((pixelValue >> 8) & 0xFF));
                    imgData.put((byte) (pixelValue & 0xFF));
                } else { // Float model
                    imgData.putFloat((((pixelValue >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                    imgData.putFloat((((pixelValue >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                    imgData.putFloat(((pixelValue & 0xFF) - IMAGE_MEAN) / IMAGE_STD);

                }
            }
        }
        //imgData is input to our model
        Object[] inputArray = {imgData};

        Map<Integer, Object> outputMap = new HashMap<>();


        Log.d("FaceReg"," output size "+OUTPUT_SIZE);
        embeedings = new float[1][OUTPUT_SIZE]; //output of model will be stored in this variable

        Log.d("FaceReg"," eboo "+embeedings);
        outputMap.put(0, embeedings);

        tfLite.runForMultipleInputsOutputs(inputArray, outputMap); //Run model



        float distance_local = Float.MAX_VALUE;
        String id = "0";
        String label = "?";
        Log.d("FaceReg"," registrt size "+registered.size());

        //Compare new face with saved Faces.
        if (registered.size() > 0) {

            final List<Pair<String, Float>> nearest = findNearest(embeedings[0]);//Find 2 closest matching face
            final List<Pair<String, Float>> nearestNew = findNearestNew(embeedings[0]);//Find 2 closest matching face


            if (nearest.get(0) != null) {

                final String name = nearest.get(0).first; //get name and distance of closest matching face\
                Log.d("getName"," name "+name);
                // label = name;
                distance_local = nearest.get(0).second;



                if (developerMode)
                {

                    if(distance_local<distance){
                        Log.d("getName"," registerc ");
                        start=false;
                        commonClass.showSuccess(AttendanceActivity.this,"Face Authentication Success");
                        callLogin();
                    }else {
                        Log.d("getName"," un registered ");
                        start=false;
                        att_tag.setText("Face Id Mismatched . Kindly register first");
                        recognize.setVisibility(View.VISIBLE);
                        commonClass.showError(AttendanceActivity.this,"Face Authentication Failed");
                    }
//                    System.out.println("nearest: " + name + " - distance: " + distance_local);
                }
                else
                {
                    if(distance_local<distance){
                        Log.d("getName"," registerc ");
                        start=false;
                        commonClass.showSuccess(AttendanceActivity.this,"Face Authentication Success");
                        callLogin();
                    }else {
                        Log.d("getName"," un registered ");
                        start=false;
                        att_tag.setText("Face Id Mismatched . Kindly register first");
                        recognize.setVisibility(View.VISIBLE);
                        commonClass.showError(AttendanceActivity.this,"Face Authentication Failed");
                    }

//                    System.out.println("nearest: " + name + " - distance: " + distance_local);
                }



            }else{
                Log.d("FaceReg"," come to main else part ");
            }
        }else{
            commonClass.showError(AttendanceActivity.this,"No Face Authentication Detected");
        }


//            final int numDetectionsOutput = 1;
//            final ArrayList<SimilarityClassifier.Recognition> recognitions = new ArrayList<>(numDetectionsOutput);
//            SimilarityClassifier.Recognition rec = new SimilarityClassifier.Recognition(
//                    id,
//                    label,
//                    distance);
//
//            recognitions.add( rec );

    }

    private void callLogin() {
        recognize.setVisibility(View.GONE);
         check_in.setVisibility(View.VISIBLE);
        checkout.setVisibility(View.VISIBLE);
        if(att_tag_status.equals("0")){
            att_tag.setText("Clock in and Get into Work");
        }else{
            att_tag.setText("Clock out and Have a Great Day");
        }
    }

    private List<Pair<String, Float>> findNearestNew(float[] emb) {
        Log.d("getNameLisr"," find nearedt "+emb);
        List<Pair<String, Float>> neighbour_list = new ArrayList<Pair<String, Float>>();
        Pair<String, Float> ret = null; //to get closest match
        final float threshold =-1f; // Adjust threshold as necessary
        Pair<String, Float> prev_ret = null; //to get second closest match
        for (Map.Entry<String, SimilarityClassifier.Recognition> entry : registered.entrySet()) {
            final String name = entry.getKey();
            final float[] knownEmb = ((float[][]) entry.getValue().getExtra())[0];

            Log.d("getNameLisr", "Comparing with known embedding for: " + name);

            float distance = 0;
            for (int i = 0; i < emb.length; i++) {
                float diff = emb[i] - knownEmb[i];
                distance += diff * diff;
            }
            distance = (float) Math.sqrt(distance);

            if (ret == null || distance < ret.second) {
                prev_ret = ret;
                ret = new Pair<>(name, distance);
            }

            Log.d("getNameLisr", "Current nearest: " + ret + " Previous nearest: " + prev_ret + " Distance: " + distance);
        }

        //10-10-24
        if(prev_ret==null) prev_ret=ret;

        neighbour_list.add(ret);
        neighbour_list.add(prev_ret);



        return neighbour_list;
    }


    private List<Pair<String, Float>> findNearest(float[] emb) {
        Log.d("getName"," find nearedt "+emb);
        List<Pair<String, Float>> neighbour_list = new ArrayList<Pair<String, Float>>();
        Pair<String, Float> ret = null; //to get closest match
        final float threshold =-1f; // Adjust threshold as necessary
        Pair<String, Float> prev_ret = null; //to get second closest match
        for (Map.Entry<String, SimilarityClassifier.Recognition> entry : registered.entrySet())
        {

            final String name = entry.getKey();

            final float[] knownEmb = ((float[][]) entry.getValue().getExtra())[0];
            Log.d("getName"," knownEm  "+knownEmb);

            float distance = 0;
            for (int i = 0; i < emb.length; i++) {
                float diff = emb[i] - knownEmb[i];
                distance += diff*diff;
            }
            distance = (float) Math.sqrt(distance);
            /// 10-10-2024
            if (ret == null || distance < ret.second) {
                prev_ret=ret;
                ret = new Pair<>(name, distance);
            }
            ///10-10-2024
            Log.d("getName"," ret "+ret+"  pre "+prev_ret+" distance "+distance);


        }
        //10-10-24
        if(prev_ret==null) prev_ret=ret;

        neighbour_list.add(ret);
        neighbour_list.add(prev_ret);



        return neighbour_list;

    }
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
    private static Bitmap getCropBitmapByCPU(Bitmap source, RectF cropRectF) {
        Bitmap resultBitmap = Bitmap.createBitmap((int) cropRectF.width(),
                (int) cropRectF.height(), Bitmap.Config.ARGB_8888);
        Canvas cavas = new Canvas(resultBitmap);

        // draw background
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        paint.setColor(Color.WHITE);
        cavas.drawRect(
                new RectF(0, 0, cropRectF.width(), cropRectF.height()),
                paint);

        Matrix matrix = new Matrix();
        matrix.postTranslate(-cropRectF.left, -cropRectF.top);

        cavas.drawBitmap(source, matrix, paint);

        if (source != null && !source.isRecycled()) {
            source.recycle();
        }

        return resultBitmap;
    }

    private static Bitmap rotateBitmap(
            Bitmap bitmap, int rotationDegrees, boolean flipX, boolean flipY) {
        Matrix matrix = new Matrix();

        // Rotate the image back to straight.
        matrix.postRotate(rotationDegrees);

        // Mirror the image along the X or Y axis.
        matrix.postScale(flipX ? -1.0f : 1.0f, flipY ? -1.0f : 1.0f);
        Bitmap rotatedBitmap =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        // Recycle the old bitmap if it has changed.
        if (rotatedBitmap != bitmap) {
            bitmap.recycle();
        }
        return rotatedBitmap;
    }

    //IMPORTANT. If conversion not done ,the toBitmap conversion does not work on some devices.
    private static byte[] YUV_420_888toNV21(Image image) {

        int width = image.getWidth();
        int height = image.getHeight();
        int ySize = width*height;
        int uvSize = width*height/4;

        byte[] nv21 = new byte[ySize + uvSize*2];

        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer(); // Y
        ByteBuffer uBuffer = image.getPlanes()[1].getBuffer(); // U
        ByteBuffer vBuffer = image.getPlanes()[2].getBuffer(); // V

        int rowStride = image.getPlanes()[0].getRowStride();
        assert(image.getPlanes()[0].getPixelStride() == 1);

        int pos = 0;

        if (rowStride == width) { // likely
            yBuffer.get(nv21, 0, ySize);
            pos += ySize;
        }
        else {
            long yBufferPos = -rowStride; // not an actual position
            for (; pos<ySize; pos+=width) {
                yBufferPos += rowStride;
                yBuffer.position((int) yBufferPos);
                yBuffer.get(nv21, pos, width);
            }
        }

        rowStride = image.getPlanes()[2].getRowStride();
        int pixelStride = image.getPlanes()[2].getPixelStride();

        assert(rowStride == image.getPlanes()[1].getRowStride());
        assert(pixelStride == image.getPlanes()[1].getPixelStride());

        if (pixelStride == 2 && rowStride == width && uBuffer.get(0) == vBuffer.get(1)) {
            // maybe V an U planes overlap as per NV21, which means vBuffer[1] is alias of uBuffer[0]
            byte savePixel = vBuffer.get(1);
            try {
                vBuffer.put(1, (byte)~savePixel);
                if (uBuffer.get(0) == (byte)~savePixel) {
                    vBuffer.put(1, savePixel);
                    vBuffer.position(0);
                    uBuffer.position(0);
                    vBuffer.get(nv21, ySize, 1);
                    uBuffer.get(nv21, ySize + 1, uBuffer.remaining());

                    return nv21; // shortcut
                }
            }
            catch (ReadOnlyBufferException ex) {
                // unfortunately, we cannot check if vBuffer and uBuffer overlap
            }

            // unfortunately, the check failed. We must save U and V pixel by pixel
            vBuffer.put(1, savePixel);
        }

        // other optimizations could check if (pixelStride == 1) or (pixelStride == 2),
        // but performance gain would be less significant

        for (int row=0; row<height/2; row++) {
            for (int col=0; col<width/2; col++) {
                int vuPos = col*pixelStride + row*rowStride;
                nv21[pos++] = vBuffer.get(vuPos);
                nv21[pos++] = uBuffer.get(vuPos);
            }
        }

        return nv21;
    }

    private Bitmap toBitmap(Image image) {

        byte[] nv21=YUV_420_888toNV21(image);


        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 75, out);

        byte[] imageBytes = out.toByteArray();
        //System.out.println("bytes"+ Arrays.toString(imageBytes));

        //System.out.println("FORMAT"+image.getFormat());

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }



    //Load Photo from phone storage


    //Similar Analyzing Procedure


    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

}
