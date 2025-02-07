package in.proz.adamd.Map;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.speech.RecognizerIntent;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.Size;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.arsy.maps_library.MapRipple;


import com.codebyashish.googledirectionapi.AbstractRouting;
import com.codebyashish.googledirectionapi.ErrorHandling;
import com.codebyashish.googledirectionapi.RouteDrawing;
import com.codebyashish.googledirectionapi.RouteInfoModel;
import com.codebyashish.googledirectionapi.RouteListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
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
import com.squareup.picasso.Picasso;
import com.tuyenmonkey.mkloader.MKLoader;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;
import in.proz.adamd.AdminModule.AdminNewApprovals;
import in.proz.adamd.Attendance.AttendanceActivity;
import in.proz.adamd.AttendanceSQLite.AttendanceDSR;
import in.proz.adamd.AttendanceSQLite.AttendanceListDB;
import in.proz.adamd.AttendanceSQLite.PunchINOUTDB;
import in.proz.adamd.BackgroundTask.LocationForegroundService;
import in.proz.adamd.BackgroundTask.LocationUpdateService;
import in.proz.adamd.BackgroundTask.YourService;
import in.proz.adamd.DashboardNewActivity;
import in.proz.adamd.Face.FaceModal;
import in.proz.adamd.Face.SQLFaceModal;
import in.proz.adamd.FaceAuth.SimilarityClassifier;
import in.proz.adamd.Map.Modal.DataParser;
import in.proz.adamd.ModalClass.LatLngBranch;
import in.proz.adamd.ModalClass.ProjectListModal;
import in.proz.adamd.ModalClass.PunchModal;
import in.proz.adamd.NotesActivity.NotesActivity;
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

public class MapCurrentLocation extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, RouteListener {
    private MapView mapView;
    EditText edt_planned_work;
    String branch_id = null;
    int distance_val=0;
    double ofz_lat = 11.2391346, ofz_lng = 78.1654629;
    List<String> projectNameList = new ArrayList<>();
    boolean[] selectedProjects;
    String dsr_flag = "0";
    ArrayList<Integer> projectListID = new ArrayList<>();
    ArrayList<Polyline> polylines = null;
    TextView recognize;


    int other_status = 0,geoFenLoc=1,currentDistance=0;

    MapRipple mapRipple;
    int distance_value = 0;

    ProgressDialog progressDialog;
    //RelativeLayout header_relative;

    String workLocation = "office";
    public int yr, mnth, dy;
    public Calendar calendar;
    public String dt, today_date;
    CircleImageView profile_img;
    TextView designation, name;

    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap googleMap;
    double latitude, longitude,nlatitude,nlongitude;
    ProjectDetails projectDetails;
    AttendanceListDB attendanceListDB;
    PunchINOUTDB punchINOUTDB;
    AttendanceDSR attendanceDSR;
    Marker marker, marker1;
    LinearLayout check_in, checkout;
    TextView title;
    ImageView back_arrow;
    DecimalFormat decimalFormat = new DecimalFormat("00");
    MKLoader loader;
    Intent mServiceIntent, lIntent, lUIntent;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;



    LinearLayout nhome_layout, nprofile_layout, nreports_layout, nlocation_layout;
    ImageView iimgnotes;
    TextView txtnote;
    CommonClass commonClass = new CommonClass();
    /*  LinearLayout online_layout;
      ImageView online_icon;
      TextView online_text;*/
    private YourService mYourService;
    LinearLayout office_layout, home_layout, client_layout;
    ImageView ofz_icon, home_icon, client_icon;
    TextView ofz_text, home_text, client_text;
    LocationForegroundService lService;
    LocationUpdateService lUService;
    String admin;


    // face detector
    FaceDetector detector;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView previewView;
    Interpreter tfLite;
    CameraSelector cameraSelector;
    boolean developerMode = false;
    float distance = 1.0f;
    boolean start = true, flipX = false;
    Context context = MapCurrentLocation.this;
    int cam_face = CameraSelector.LENS_FACING_FRONT; //Default Back Camera
    int[] intValues;
    int inputSize = 112;  //Input size for model
    boolean isModelQuantized = false;
    float[][] embeedings;
    float IMAGE_MEAN = 128.0f;
    float IMAGE_STD = 128.0f;
    int OUTPUT_SIZE = 192; //Output size of model
    private static int SELECT_PICTURE = 2;
    ProcessCameraProvider cameraProvider;
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    String modelFile = "mobile_face_net.tflite"; //model name
    private HashMap<String, SimilarityClassifier.Recognition> registered = new HashMap<>(); //saved Faces


    FaceAuthDB faceAuthDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapcurrentlocation);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            SendPushNotification();
        }

        String letme = commonClass.getSharedPref(getApplicationContext(),"distancee");
        if(!TextUtils.isEmpty(letme)){
            distance_val = Integer.parseInt(letme);
        }
        Log.d("AttendanceDistance"," distance "+distance_val);

        //  Toast.makeText(getApplicationContext(), "Flow1", Toast.LENGTH_SHORT).show();
        faceAuthDB = new FaceAuthDB(MapCurrentLocation.this);
        faceAuthDB.getWritableDatabase();
        isLowNetwork(MapCurrentLocation.this);
        ofz_lat = Double.valueOf(commonClass.getSharedPref(getApplicationContext(),"off_lat"));
        ofz_lng = Double.valueOf(commonClass.getSharedPref(getApplicationContext(),"off_lng"));
        Log.d("office_location", " lat " + ofz_lat + " lng " + ofz_lng);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            admin = b.getString("admin");
        }
        CommonClass comm = new CommonClass();
        projectDetails = new ProjectDetails(MapCurrentLocation.this);
        projectDetails.getWritableDatabase();
        attendanceDSR = new AttendanceDSR(MapCurrentLocation.this);
        attendanceDSR.getWritableDatabase();
        attendanceListDB = new AttendanceListDB(MapCurrentLocation.this);
        attendanceListDB.getWritableDatabase();
        punchINOUTDB = new PunchINOUTDB(MapCurrentLocation.this);
        punchINOUTDB.getWritableDatabase();
       /* online_icon = findViewById(R.id.online_icon);
        online_layout = findViewById(R.id.online_layout);
        online_text = findViewById(R.id.online_text);
        comm.onlineStatusCheck(MapCurrentLocation.this,online_layout,online_text,online_icon);*/
        progressDialog = new ProgressDialog(MapCurrentLocation.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        loader = findViewById(R.id.loader);
        title = findViewById(R.id.title);
        title.setText("Current Location");
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                getCurrentLocationChange();
                 //Toast.makeText(getActivity(), "Location Result" + locationResult.getLocations(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLocationAvailability(@NonNull @NotNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        };



        initView();
        getProjectList();

        back_arrow = findViewById(R.id.back_arrow);
        name = findViewById(R.id.name);
        profile_img = findViewById(R.id.profile_img);
        designation = findViewById(R.id.designation);
        name.setText(commonClass.getSharedPref(getApplicationContext(), "name"));
        designation.setText(commonClass.getSharedPref(getApplicationContext(), "designation"));
        Picasso.with(MapCurrentLocation.this).load(commonClass.getSharedPref(getApplicationContext(), "image")).into(profile_img);

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(admin)) {
                    Intent intent = new Intent(getApplicationContext(), AdminNewApprovals.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), DashboardNewActivity.class);
                    startActivity(intent);
                }

            }
        });
        check_in = findViewById(R.id.check_in);
        check_in.setOnClickListener(this);
        checkout = findViewById(R.id.checkout);
        checkout.setOnClickListener(this);
        check_in.setVisibility(View.GONE);
        checkout.setVisibility(View.GONE);


        recognize = findViewById(R.id.button3);

        recognize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start = true;
                recognize.setVisibility(View.GONE);
            }
        });
        registered = readFromSP();


        try {
            Log.d("FaceReg", " try block " + modelFile);
            tfLite = new Interpreter(loadModelFile(MapCurrentLocation.this, modelFile));
        } catch (IOException e) {
            Log.d("FaceReg", " error block " + e.getMessage() + "  " + e.getLocalizedMessage() + " cause " +
                    e.getCause() + " track " + e.getStackTrace());
            e.printStackTrace();
        }
        //Initialize Face Detector
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .build();
        detector = FaceDetection.getClient(highAccuracyOpts);

        cameraBind();
        calendar = Calendar.getInstance();

        yr = calendar.get(Calendar.YEAR);
        mnth = calendar.get(Calendar.MONTH) + 1;
        dy = calendar.get(Calendar.DAY_OF_MONTH);
        dt = decimalFormat.format(Double.valueOf(yr)) + "-" + decimalFormat.format(Double.valueOf(mnth + 1)) + "-" + dy;
        today_date = dy + "-" + mnth + "-" + yr;
        checkout.setEnabled(false);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        Log.d("LocationOnCheckLis","1");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(), "punch_in"))) {
            String date = dy + "-" + mnth + "-" + yr;
            Log.d("attendance_punch", " attendance " + commonClass.getSharedPref(getApplicationContext(), "punch_in") +
                    " date " + date);
            if (date.equals(commonClass.getSharedPref(getApplicationContext(), "punch_in"))) {
                checkout.setEnabled(true);
                check_in.setEnabled(false);
                updateCheckHeader(2);
            } else {
                String splitarr[] = commonClass.getSharedPref(getApplicationContext(), "punch_in").split("-");
                if (!splitarr[0].equals(dy)) {
                    commonClass.putSharedPref(getApplicationContext(), "punch_in", null);
                    check_in.setEnabled(true);
                    checkout.setEnabled(false);
                    updateCheckHeader(1);
                }
            }
        } else {
            check_in.setEnabled(true);
            checkout.setEnabled(false);
        }
        getLastLocation();
        loader.setVisibility(View.VISIBLE);


        if (commonClass.isOnline(MapCurrentLocation.this)) {
            getFaceList();
        } else {
            getFaceListOffline();
        }
        if (commonClass.isOnline(MapCurrentLocation.this)) {
            checkAttendancePunchOrNot();
            getLastLocation();
        } else {
            Log.d("checkinHeader", " already punch " + commonClass.getSharedPref(getApplicationContext(), "sync_id") +
                    " ppun " + commonClass.getSharedPref(getApplicationContext(), "punch_in"));
            if (!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(), "punch_in"))) {
                /// already punch in
                checkout.setEnabled(true);
                check_in.setEnabled(false);
                updateCheckHeader(2);
            } else {
                /// not yet punched
                check_in.setEnabled(true);
                checkout.setEnabled(false);
                updateCheckHeader(1);
            }


        }

        Log.d("gettingWorkLocation", " s " + commonClass.getSharedPref(getApplicationContext(), "work_location"));
        if (!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(), "work_location"))) {
            workLocation = commonClass.getSharedPref(getApplicationContext(), "work_location");
            if (workLocation.equals("client")) {
                updateUIHeader(3);
            } else if (workLocation.equals("home")) {
                updateUIHeader(2);
            } else if (workLocation.equals("office")) {
                updateUIHeader(1);
            }
        } else {
            workLocation = "office";
            updateUIHeader(1);
        }
        getLastLocation();
    }

    public void checkAttendancePunchOrNot() {
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(), "token"),
                commonClass.getDeviceID(getApplicationContext())).create(ApiInterface.class);
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

                            if (!TextUtils.isEmpty(response.body().getCommonPojo().getDsr_flag())) {
                                dsr_flag = response.body().getCommonPojo().getDsr_flag();
                            }

                            Log.d("getAttendance", " punch status " + response.body().getCommonPojo().getPunch_status()
                                    + " syne " + response.body().getCommonPojo().getSync_id() + " dsr flag " + dsr_flag);
                            if (response.body().getCommonPojo().getPunch_status().equals("0")) {
                                commonClass.putSharedPref(getApplicationContext(), "punch_in", null);
                                check_in.setEnabled(true);
                                checkout.setEnabled(false);
                                updateCheckHeader(1);
                            } else {
                                commonClass.putSharedPref(getApplicationContext(), "punch_in", today_date);
                                commonClass.putSharedPref(getApplicationContext(), "sync_id", response.body().getCommonPojo().getSync_id());
                                checkout.setEnabled(true);
                                check_in.setEnabled(false);
                                updateCheckHeader(2);
                            }
                        } else {
                            //commonClass.showError(MapCurrentLocation.this,response.body().getStatus());
                        }
                    } else {
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            //commonClass.showError(MapCurrentLocation.this,mError.getError());
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

                        //  commonClass.showError(MapCurrentLocation.this,mError.getError());
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
                //commonClass.showError(MapCurrentLocation.this,t.getMessage());
            }
        });

    }

    public void initView() {
        iimgnotes = findViewById(R.id.location_icon);
        txtnote = findViewById(R.id.location_text);
        iimgnotes.setImageTintList(getApplicationContext().getColorStateList(R.color.n_org));
        txtnote.setTextColor(getApplicationContext().getColor(R.color.black));
        nhome_layout = findViewById(R.id.nhome_layout);
        nprofile_layout = findViewById(R.id.nprofile_layout);
        nreports_layout = findViewById(R.id.nreports_layout);
        nlocation_layout = findViewById(R.id.nlocation_layout);
        nhome_layout.setOnClickListener(this);
        nprofile_layout.setOnClickListener(this);
        nreports_layout.setOnClickListener(this);
        nlocation_layout.setOnClickListener(this);




        TextView htitle = findViewById(R.id.header_title);
        htitle.setText(commonClass.getSharedPref(getApplicationContext(), "EmppName"));


        lUService = new LocationUpdateService();
        mYourService = new YourService();
        lService = new LocationForegroundService();
        mServiceIntent = new Intent(this, mYourService.getClass());
        lUIntent = new Intent(this, lUService.getClass());
        lIntent = new Intent(this, lService.getClass());
        //  header_relative = findViewById(R.id.header_relative);
        office_layout = findViewById(R.id.office_layout);
        home_layout = findViewById(R.id.home_layout);
        client_layout = findViewById(R.id.client_layout);
        ofz_icon = findViewById(R.id.ofz_icon);
        home_icon = findViewById(R.id.home_icon);
        client_icon = findViewById(R.id.client_icon);
        ofz_text = findViewById(R.id.ofz_text);
        home_text = findViewById(R.id.home_text);
        client_text = findViewById(R.id.client_text);
        office_layout.setOnClickListener(this);
        client_layout.setOnClickListener(this);
        home_layout.setOnClickListener(this);
        if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"work_from_home"))){
            if(commonClass.getSharedPref(getApplicationContext(),"work_from_home").equals("0")){
                home_layout.setVisibility(View.GONE);
                workLocation = "office";
                updateUIHeader(1);
            }else{
                home_layout.setVisibility(View.VISIBLE);
                workLocation = "home";
                updateUIHeader(2);
            }
        }

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
            checkout.setBackgroundTintList(getApplicationContext().getColorStateList(R.color.n_org));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), DashboardNewActivity.class);
        startActivity(intent);
    }
    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private void SendPushNotification() {
        Dexter.withContext(MapCurrentLocation.this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
                    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                        }else{
                         }
                    }
                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                }).check();
    }

    private void getLastLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
         //   Toast.makeText(getApplicationContext(), "Access fine location not given", Toast.LENGTH_SHORT).show();
            Log.d("LocationOnCheckLis","2");

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mapView.getMapAsync(MapCurrentLocation.this);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Log.d("LocationOnCheckLis","3");

                            //     Toast.makeText(getApplicationContext(), "Flow4", Toast.LENGTH_SHORT).show();

                            // Use the location to update the map
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            distance_value = CalculationByDistanceNew(ofz_lat, ofz_lng, latitude, longitude, "10km");
                         //   CalculationByDistance(11.2369154,78.1514318,11.2379334,78.1514359,"10km");
                            Log.d("getActia"," dis "+distance_value);

                            LatLng latLng1 = new LatLng(latitude, longitude);
                            Log.d("mapFunction", " location " + latitude + " lon " + longitude);
                            mapView.getMapAsync(MapCurrentLocation.this);

                            // Add code to update the map with the current location
                        } else {
                          //  Toast.makeText(getApplicationContext(), "Flow5", Toast.LENGTH_SHORT).show();
                            Log.d("LocationOnCheckLis","4");

                        }
                    }
                });
    }

    public boolean isLowNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (nc != null) {
                if (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    int linkSpeed = getWifiLinkSpeed(context); // In Mbps
                    commonClass.showWarning(MapCurrentLocation.this,"Network Slow.Map May take time to load");
                     return linkSpeed < 5; // Example: Low network if speed < 5 Mbps
                } else if (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    int networkType = getMobileNetworkType(context);
                    return isLowMobileNetwork(networkType); // Check based on network type
                }
            }
        }
        return true; // Return true if network information is unavailable
    }

    // Helper to get Wi-Fi link speed
    private int getWifiLinkSpeed(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
          //  Toast.makeText(getApplicationContext(), "WIFI Speed " + wifiInfo.getLinkSpeed(), Toast.LENGTH_SHORT).show();

            return wifiInfo.getLinkSpeed(); // Link speed in Mbps
        }
        return 0;
    }

    // Helper to get mobile network type
    private int getMobileNetworkType(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            return tm.getNetworkType();
        }
        return TelephonyManager.NETWORK_TYPE_UNKNOWN;
    }

    // Helper to check low mobile network types
    private boolean isLowMobileNetwork(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:    // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:    // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:    // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_1xRTT:   // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_IDEN:    // ~ 25 kbps
                return true; // Consider these as low networks
            default:
                return false; // Higher-speed networks
        }
    }

    private void getProjectList() {
        //  progressDialog.show();
       // Toast.makeText(getApplicationContext(), "Flow2", Toast.LENGTH_SHORT).show();

        loader.setVisibility(View.VISIBLE);
        Log.d("projectDetails", " token " + commonClass.getSharedPref(getApplicationContext(), "token") + "  device " +
                commonClass.getDeviceID(getApplicationContext()));
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(), "token"),
                commonClass.getDeviceID(MapCurrentLocation.this)).create(ApiInterface.class);
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

    public void projectAlertDialog(TextView project_name, LinearLayout other_layout) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MapCurrentLocation.this);

        // set title
        builder.setTitle("Select Project Name");
        // set dialog non cancelable
        builder.setCancelable(false);
        String[] stringArray = projectNameList.toArray(new String[0]);

        selectedProjects = new boolean[stringArray.length];


        if (projectListID.size() != 0) {
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
                if (stringBuilder != null) {
                    String str = stringBuilder.toString();
                    if (str.contains("Other")) {
                        other_status = 1;
                        other_layout.setVisibility(View.VISIBLE);
                    } else {
                        other_status = 0;
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


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.n_org));
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.n_org));
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
                edt_planned_work.setText(str_rem);
            }
        }
    }

    private void recordVoiceToText(int i) {
        Intent intent4
                = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent4.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        /*intent4.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());*/
        intent4.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                "ta-IN");
        intent4.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

        try {
            startActivityForResult(intent4, i);
        } catch (Exception e) {
            Toast.makeText(MapCurrentLocation.this, " " + e.getMessage(),
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void callAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dsr_dialog_layout, null);
        TextView skipp_laypput = view.findViewById(R.id.skipp_laypput);
        ImageView back_arrow = view.findViewById(R.id.back_arrow);
        ImageView mike = view.findViewById(R.id.mike);
        mike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordVoiceToText(1);
            }
        });
        MKLoader loader1 = view.findViewById(R.id.loader);
        TextView project_name = view.findViewById(R.id.project_name);
        LinearLayout oth_layout = view.findViewById(R.id.oth_layout);
        EditText edt_others = view.findViewById(R.id.edt_others);
        edt_planned_work = view.findViewById(R.id.edt_planned_work);
        RelativeLayout bottom_request_layout = view.findViewById(R.id.bottom_request_layout);
        project_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                projectAlertDialog(project_name, oth_layout);
            }
        });

        if (!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(), "AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(), "AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(), "AdminName"))) {
            skipp_laypput.setVisibility(View.VISIBLE);
        } else {
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
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                callDashboard();
            }
        });
        skipp_laypput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkInAttendance();
                callDashboard();
            }
        });
        bottom_request_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edt_planned_work.getText().toString())) {
                    edt_planned_work.setText("திட்டமிட்ட பணி எதுவும் இல்லை");
                }
                if (commonClass.isOnline(MapCurrentLocation.this)) {
                    bottom_request_layout.setEnabled(false);
                    check_in.setEnabled(false);
                    mDialog.dismiss();
                    callUpdateMethod(mDialog, project_name.getText().toString(), edt_others.getText().toString(),
                            edt_planned_work.getText().toString(), loader1);
                } else {
                    SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
                    String date_format = date.format(new Date());
                    Log.d("checkinHeader", "insert 3 ");
                    attendanceDSR.insertBulk(date_format, project_name.getText().toString(),
                            edt_planned_work.getText().toString(), edt_others.getText().toString());
                    commonClass.putSharedPref(getApplicationContext(), "dsr_date", date_format);
                    //checkInAttendance();
                }

            }
        });

    }

    private void callDashboard() {
        Intent intent = new Intent(getApplicationContext(), DashboardNewActivity.class);
        startActivity(intent);
    }

    private void callUpdateMethod(AlertDialog mDialog, String projectname, String othername, String plannedwork,
                                  MKLoader loader1) {
        List<String> sProject = new ArrayList<>();
        String str_pro = projectname;
        if (!TextUtils.isEmpty(str_pro)) {
            String[] split = str_pro.split(",");
            for (int i = 0; i < split.length; i++) {
                String projectName = projectDetails.getAllProjectIDList(split[i]);
                Log.d("attendance_list", " split id " + split[i] + " project namt " + projectName);
                if (TextUtils.isEmpty(projectName)) {
                    sProject.add("others");
                } else {
                    sProject.add(projectName);
                }


            }
        }

        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        String date_format = date.format(new Date());
        loader1.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(), "token"),
                commonClass.getDeviceID(MapCurrentLocation.this)).create(ApiInterface.class);
        Call<CommonPojo> call = apiInterface.DSRInsertInAttendance(commonClass.getSharedPref(getApplicationContext(), "emp_id"),
                date_format, plannedwork, sProject, othername);
        Log.d("attendance_list", " call " + call.request().url());
        call.enqueue(new Callback<CommonPojo>() {
            @Override
            public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                loader1.setVisibility(View.GONE);
                Log.d("attendance_list", " code " + response.code());
                mDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.code() == 400) {
                        commonClass.putSharedPref(getApplicationContext(), "dsr_date", date_format);
                    }
                    if (response.code() == 200) {

                        if (response.body().getStatus().contains("success")) {
                            commonClass.putSharedPref(getApplicationContext(), "dsr_date", date_format);
                            // checkInAttendance();
                            commonClass.showSuccess(MapCurrentLocation.this, response.body().getData());
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    callDashboard();
                                }
                            }, 1500);

                        } else {
                            commonClass.showError(MapCurrentLocation.this, response.body().getData());
                        }
                    } else {
                        //  checkInAttendance();
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(MapCurrentLocation.this, mError.getError());
                            //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            // handle failure to read error
                            Log.d("thumbnail_url", " exp error  " + e.getMessage());
                        }
                    }
                } else {
                    // checkInAttendance();
                    Gson gson = new GsonBuilder().create();
                    CommonPojo mError = new CommonPojo();
                    try {
                        mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                        commonClass.showError(MapCurrentLocation.this, mError.getError());
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
                mDialog.dismiss();
                commonClass.showError(MapCurrentLocation.this, t.getMessage());
            }
        });
    }

    private void checkInConditionMethod() {
        if (!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(), "dsr_date"))) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = dateFormat.format(new Date());
            Log.d("getDateFormatValue", " if con " + date);
            if (commonClass.getSharedPref(getApplicationContext(), "dsr_date").equals(date)) {
                if (commonClass.isOnline(MapCurrentLocation.this)) {
                    if (commonClass.isLocationEnabled(MapCurrentLocation.this)) {
                        checkInAttendance();
                    } else {
                        commonClass.showWarning(MapCurrentLocation.this, "Enable Location and then try");
                    }
                } else {
                    checkInAttendance();
                }

            } else {
                Log.d("getDateFormatValue", " com to else ");
                commonClass.putSharedPref(getApplicationContext(), "dsr_date", null);
                // checkInAttendance();
                if (commonClass.isOnline(MapCurrentLocation.this)) {
                    if (commonClass.isLocationEnabled(MapCurrentLocation.this)) {
                        checkInAttendance();
                    } else {
                        commonClass.showWarning(MapCurrentLocation.this, "Enable Location and then try");
                    }
                } else {
                    checkInAttendance();
                }

            }
        } else {
            Log.d("getDateFormatValue", " come to outer else ");
            //callAlertDialog();
            // checkInAttendance();
            if (commonClass.isOnline(MapCurrentLocation.this)) {
                if (commonClass.isLocationEnabled(MapCurrentLocation.this)) {
                    checkInAttendance();
                } else {
                    commonClass.showWarning(MapCurrentLocation.this, "Enable Location and then try");
                }
            } else {
                checkInAttendance();
            }
        }
    }
    public int CalculationByDistanceNew(double lat1,double lon1,double lat2,double lon2,String actual_distance ) {
        Log.d("getActia"," location "+lat1+" "+lon1+" lat "+lat2+" "+lon2);
        double distance;
        Location locationA = new Location("");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lon1);
        Location locationB = new Location("");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lon2);
        distance = locationA.distanceTo(locationB);
        return (int) distance;

    }


    public int CalculationByDistance(double lat1, double lon1, double lat2, double lon2, String actual_distance) {
        Log.d("attendanceLocation"," alt "+lat2+" lng "+lon2+" lat1 "+lat1+" long1 "+lon1+" distance "+distance_val);

        double distance = 0;
        double minDis = 0;

        BranchTable branchTable = new BranchTable(MapCurrentLocation.this);
        branchTable.getWritableDatabase();
        List<in.proz.adamd.ModalClass.LatLng> getBranchDetails = new ArrayList<>();
        getBranchDetails.add(new in.proz.adamd.ModalClass.LatLng("1",String.valueOf(lat1),String.valueOf(lon1)));
        Log.d("getDistance", " get list size " + getBranchDetails.size());
        if (getBranchDetails.size() != 0) {

            for (int i = 0; i < getBranchDetails.size(); i++) {
                Double lat11 = Double.valueOf(getBranchDetails.get(i).getLatitude());
                Double lng11 = Double.valueOf(getBranchDetails.get(i).getLongitude());
                Location locationA = new Location("");
                locationA.setLatitude(lat11);
                locationA.setLongitude(lng11);
                Location locationB = new Location("");
                locationB.setLatitude(lat2);
                locationB.setLongitude(lon2);
                distance = locationA.distanceTo(locationB);
                Log.d("getDistance", " as " + minDis);
                if (i == 0) {
                    minDis = distance;
                    branch_id = getBranchDetails.get(i).getId();
                } else {
                    if (minDis > distance) {
                        minDis = distance;
                        branch_id = getBranchDetails.get(i).getId();
                    }
                }
            }
        }


        Log.d("getDistance", " idea 3 distance " + distance + " branch id " + branch_id);
        return (int) minDis;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("LocationOnCheckLis","9");

        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("LocationOnCheckLis","10");

        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("LocationOnCheckLis","11");

        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d("LocationOnCheckLis","12");

        mapView.onLowMemory();
    }


    public BitmapDescriptor setIcon(Activity context, int drawableID) {
        Drawable drawable = ActivityCompat.getDrawable(context, drawableID);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        //  getLastLocation();
        googleMap = googleMap;
        Log.d("LocationOnCheckLis","8");

        //   Toast.makeText(getApplicationContext(), "Flow7", Toast.LENGTH_SHORT).show();

        if (loader.getVisibility() == View.VISIBLE) {
            loader.setVisibility(View.GONE);
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
         //   Toast.makeText(getApplicationContext(),"Access Fine Location Not Granded ",Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                // Get the current latitude and longitude
                latitude =location.getLatitude();
                longitude = location.getLongitude();

            } else {
               // Toast.makeText(this, "Unable to fetch location", Toast.LENGTH_SHORT).show();
            }
        });

        Log.d("mapFunction"," lat "+latitude+" lng "+longitude);
        LatLng defaultLocation = new LatLng(latitude, longitude); // San Francisco, USA
       // LatLng defaultLocation1 = new LatLng(ofz_lat, ofz_lng); // San Francisco, USA
        Log.d("LocationOnCheckLis","6");


        // Move the camera to the default location and set a zoom level
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 17f));
         mapRipple = new MapRipple(googleMap, defaultLocation, MapCurrentLocation.this);
        mapRipple.withNumberOfRipples(2);
        mapRipple.withFillColor(Color.GRAY);
     //   mapRipple.withStrokeColor(Color.YELLOW);
        mapRipple.withStrokewidth(0);      // 10dp
        mapRipple.withDistance(1);      // 2000 metres radius
        mapRipple.withRippleDuration(12000);    //12000ms
        mapRipple.withTransparency(0.8f);
        mapRipple.startRippleMapAnimation();



        // Add a marker at the default location
   /*     MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(defaultLocation);
        markerOptions.icon(setIcon(MapCurrentLocation.this,R.drawable.location_icon));

        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.position(defaultLocation1);
        markerOptions1.icon(setIcon(MapCurrentLocation.this,R.drawable.active_home_icon));*/

        googleMap.addMarker(new MarkerOptions().position(defaultLocation).title("Current Location"))
                .setIcon(setIcon(MapCurrentLocation.this,R.drawable.placeholder));
       /* googleMap.addMarker(new MarkerOptions().position(defaultLocation1).title("Office Location"))
                .setIcon(setIcon(MapCurrentLocation.this,R.drawable.building));
        getRoutePoints(defaultLocation,defaultLocation1);*/
        /*String url = getDirectionsUrl(marker.getPosition(), marker1.getPosition());
        Log.d("getLocation"," url as "+url);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);*/


        // Example URL: https://maps.googleapis.com/maps/api/directions/json?origin=41.881832,-87.623177&destination=41.874580,-87.630843&key=YOUR_API_KEY


     }
    public void getRoutePoints(LatLng start, LatLng end) {
            Log.d("ROUTELIST"," route "+start+" end "+end );
        //Toast.makeText(getApplicationContext(),"Flow8",Toast.LENGTH_SHORT).show();

        RouteDrawing routeDrawing = new RouteDrawing.Builder()
                    .context(MapCurrentLocation.this)  // pass your activity or fragment's context
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this).alternativeRoutes(true)
                    .waypoints(start, end   )
                    .build();
            routeDrawing.execute();


    }

    @Override
    public void onRouteFailure(ErrorHandling e) {
      //  Toast.makeText(getApplicationContext(),"Flow10",Toast.LENGTH_SHORT).show();

        Log.d("getDetails"," error "+e.getMessage());
        //Toast.makeText(getApplicationContext(),"Route failed "+e.getMessage(),Toast.LENGTH_SHORT).show();
     }

    @Override
    public void onRouteStart() {
        Log.d("ROUTELIST"," on start ");
    }

    @Override
    public void onRouteSuccess(ArrayList<RouteInfoModel> list, int routeIndexing) {
       // Toast.makeText(getApplicationContext(),"Flow9",Toast.LENGTH_SHORT).show();

        if (googleMap == null) {
            Log.e("GoogleMap", "GoogleMap is not initialized. Cannot add polyline.");
            return;
        }

        if (list == null || list.isEmpty() || routeIndexing < 0 || routeIndexing >= list.size()) {
            Log.e("RouteError", "Invalid route data or index.");
            return;
        }
        PolylineOptions polylineOptions = new PolylineOptions();
        ArrayList<Polyline> polylines = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (i == routeIndexing) {
                Log.d("ROUTELIST", "onRoutingSuccess: routeIndexing" + routeIndexing);
                polylineOptions.color(Color.BLACK);
                polylineOptions.width(12);
                polylineOptions.addAll(list.get(routeIndexing).getPoints());
                polylineOptions.startCap(new RoundCap());
                polylineOptions.endCap(new RoundCap());
                Polyline polyline = googleMap.addPolyline(polylineOptions);
                polylines.add(polyline);
            }
        }
    }

    @Override
    public void onRouteCancelled() {
       // Toast.makeText(getApplicationContext(),"Route cancelled ",Toast.LENGTH_SHORT).show();
    }


     private void updateUIHeader(int i) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           // header_relative.setBackground(getApplicationContext().getDrawable(R.drawable.color_shadow));
            office_layout.setBackground(getApplicationContext().getDrawable(R.drawable.rectangle_border));
            office_layout.setBackgroundTintList(getApplicationContext().getColorStateList(R.color.n_blue));
            client_layout.setBackground(getApplicationContext().getDrawable(R.drawable.rectangle_border));
            client_layout.setBackgroundTintList(getApplicationContext().getColorStateList(R.color.n_blue));
            home_layout.setBackground(getApplicationContext().getDrawable(R.drawable.rectangle_border));
            home_layout.setBackgroundTintList(getApplicationContext().getColorStateList(R.color.n_blue));
            /*home_icon.setImageTintList(getApplicationContext().getColorStateList(R.color.black));
            ofz_icon.setImageTintList(getApplicationContext().getColorStateList(R.color.black));
            client_icon.setImageTintList(getApplicationContext().getColorStateList(R.color.black));
            ofz_text.setTextColor(getApplicationContext().getColor(R.color.black));
            home_text.setTextColor(getApplicationContext().getColor(R.color.black));
            client_text.setTextColor(getApplicationContext().getColor(R.color.black));*/
        }

        if (i == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
               // office_layout.setBackground(getApplicationContext().getDrawable(R.drawable.rectangle_border));
                office_layout.setBackgroundTintList(getApplicationContext().getColorStateList(R.color.color_primary));
                ofz_icon.setImageTintList(getApplicationContext().getColorStateList(R.color.white));
                ofz_text.setTextColor(getApplicationContext().getColor(R.color.white));
            }
        } else if (i == 2) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
               // home_layout.setBackground(getApplicationContext().getDrawable(R.drawable.rectangle_border));

                home_layout.setBackgroundTintList(getApplicationContext().getColorStateList(R.color.color_primary));
                home_icon.setImageTintList(getApplicationContext().getColorStateList(R.color.white));
                home_text.setTextColor(getApplicationContext().getColor(R.color.white));
            }

        } else if (i == 3) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //client_layout.setBackground(getApplicationContext().getDrawable(R.drawable.rectangle_border));
                client_layout.setBackgroundTintList(getApplicationContext().getColorStateList(R.color.color_primary));
                client_icon.setImageTintList(getApplicationContext().getColorStateList(R.color.white));
                client_text.setTextColor(getApplicationContext().getColor(R.color.white));
            }

        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.nhome_layout:
                Intent intent1 = new Intent(getApplicationContext(), DashboardNewActivity.class);
                startActivity(intent1);
                break;
            case R.id.nprofile_layout:
                Intent intentabout = new Intent(getApplicationContext(), ProfileActivity.class);
                 startActivity(intentabout);
                break;
            case R.id.nreports_layout:
                Intent notes=new Intent(getApplicationContext(), NotesActivity.class);
                 startActivity(notes);
                break;
            case R.id.nlocation_layout:
                /*Intent intent = new Intent(getApplicationContext(), MapCurrentLocation.class);
                 startActivity(intent);*/
                break;
            case R.id.office_layout:
                workLocation = "office";
                updateUIHeader(1);
                break;
            case R.id.client_layout:
                branch_id =null;
                workLocation = "client";
                updateUIHeader(3);
                break;
            case R.id.home_layout:
                workLocation = "home";
                updateUIHeader(2);
                break;
            case R.id.check_in:
                updateCheckHeader(1);


               /* if(commonClass.isOnline(MapCurrentLocation.this)){
                    if(nlatitude==0){
                        commonClass.showWarning(MapCurrentLocation.this, "Cannot Access Current Location");
                    }
                }else{
                    if (nlatitude == 0) {
                      //  getLastLocation();
                        callLocationCalling();
                    }
                }*/
                callLocationCalling();
                if(nlatitude==0.0){
                    commonClass.showWarning(MapCurrentLocation.this, "Cannot Access Current Location");
                }else{
                    if (nlatitude != 0) {
                        Log.d("commonTag"," get work type "+workLocation);
                        if(workLocation.equals("office")){
                            if(distance_value<=distance_val && distance_value>=0){
                                check_in.setEnabled(false);
                                checkInConditionMethod();
                            }else{
                                commonClass.showWarning(MapCurrentLocation.this, "You should be along to office location  ");
                            }
                        }else{
                            check_in.setEnabled(false);
                            checkInConditionMethod();
                        }

                    } else {
                        commonClass.showWarning(MapCurrentLocation.this, "Cannot access current location.");
                    }
                }


                break;
            case R.id.checkout:
                updateCheckHeader(2);
                if(commonClass.isOnline(MapCurrentLocation.this)){
                    if(nlatitude==0){
                        commonClass.showWarning(MapCurrentLocation.this, "Cannot Access Current Location");
                    }
                }else{
                    if (nlatitude == 0) {
                        getLastLocation();
                    }
                }
                callLocationCalling();
             /*   if(nlatitude!=0){
                    if (workLocation.equals("office")) {
                        if (distance_value <= distance_val && distance_value >= 0) {
                            Log.d("CheckInCondition", " condition 1");

                            if(commonClass.isOnline(MapCurrentLocation.this)){
                                if(commonClass.isLocationEnabled(MapCurrentLocation.this)){
                                    checkOutAttendance();
                                }else{
                                    commonClass.showWarning(MapCurrentLocation.this,"Enable Location and then try");
                                }
                            }else {
                                checkOutAttendance();
                            }


                        } else {
                            commonClass.showWarning(MapCurrentLocation.this, "You should be along to office location  ");
                        }
                    } else {
                        Log.d("CheckInCondition", " condition 2");

                        if(commonClass.isOnline(MapCurrentLocation.this)){
                            if(commonClass.isLocationEnabled(MapCurrentLocation.this)){
                                checkOutAttendance();
                            }else{
                                commonClass.showWarning(MapCurrentLocation.this,"Enable Location and then try");
                            }
                        }else {
                            checkOutAttendance();
                        }
                    }


                }else{
                    commonClass.showWarning(MapCurrentLocation.this,"Cannot access current location.");
                }*/
                break;
        }
    }
 private void checkInAttendance() {
    String id = UUID.randomUUID().toString();
    Log.d("dashboard_id","device "+id);


    commonClass.putSharedPref(getApplicationContext(), "work_location", workLocation);
  if(!check_in.isEnabled()){
    if(commonClass.isOnline(MapCurrentLocation.this)) {
        // progressDialog.show();
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(MapCurrentLocation.this, "token"),
                commonClass.getDeviceID(MapCurrentLocation.this)).create(ApiInterface.class);
        Call<CommonPojo> call = apiInterface.callPunchIn(nlatitude, nlongitude, workLocation,branch_id);
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
                        String getIn = String.valueOf(latitude)+","+String.valueOf(longitude);

                        String week_date = df.format(new Date());
                        SimpleDateFormat df31 = new SimpleDateFormat("yyyy-MM-dd");
                        String strDate = df31.format(new Date());
                        String[] dateArr = strDate.split("-");
                        int monthValue = Integer.parseInt(dateArr[1]);
                        String finalArr=strDate;
                        if(monthValue<=9){
                            finalArr = dateArr[0]+"-0"+dateArr[1]+"-"+dateArr[2];
                        }
                        Log.d("checkinHeader","insert 2");

                        attendanceListDB.insertBulk(finalArr,week_date,"no","0","0","0","online",response.body().getPunchInModal().getSync_id()
                                ,id);
                        punchINOUTDB.insertBulk(finalArr,"0",String.valueOf(df3.format(new Date())),null,"0",workLocation,
                                null,getIn,null,response.body().getPunchInModal().getSync_id(),id);

                        commonClass.putSharedPref(getApplicationContext(), "work_location", workLocation);
                        commonClass.putSharedPref(getApplicationContext(), "punch_in", today_date);
                        commonClass.putSharedPref(getApplicationContext(), "uuid", response.body().getPunchInModal().getMa_uuid());
                        commonClass.putSharedPref(getApplicationContext(), "sync_id", response.body().getPunchInModal().getSync_id());
                        commonClass.showSuccess(MapCurrentLocation.this, response.body().getData());
                        commonClass.putSharedPref(getApplicationContext(),"m_uuid",id);


                        check_in.setEnabled(false);
                        updateCheckHeader(2);
                        sendPushNotification();

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                if(dsr_flag.equals("0")){
                                    callAlertDialog();
                                }else{
                                    callDashboard();
                                }
                            }
                        }, 1500);

                    } else {
                        commonClass.showError(MapCurrentLocation.this, response.body().getData());
                    }
                } else {
                    Gson gson = new GsonBuilder().create();
                    CommonPojo mError = new CommonPojo();
                    try {
                        mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                        commonClass.showError(MapCurrentLocation.this, mError.getError());
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
                commonClass.showError(MapCurrentLocation.this, t.getMessage());
            }
        });
    }else {

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
            commonClass.showError(MapCurrentLocation.this, "Cannot Punch Attendance More than 3 days ");
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
            sendPushNotification();
            commonClass.showSuccess(MapCurrentLocation.this, "Punch In Successfully...");
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

    private void sendPushNotification() {
        Dexter.withContext(MapCurrentLocation.this)
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
                            commonClass.showWarning(MapCurrentLocation.this,"Notification Permission Not Granded");
                        }
                    }
                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                }).check();
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

    private void checkOutAttendance(){
        commonClass.putSharedPref(getApplicationContext(), "work_location", workLocation);
        //progressDialog.show();
        if(isMyServiceRunning(mYourService.getClass())){
            mYourService.onDestroy();
            stopService(mServiceIntent);
        }
        if(isMyServiceRunning(lUService.getClass())){
            lUService.onDestroy();
        }

        if(commonClass.isOnline(MapCurrentLocation.this)){

            loader.setVisibility(View.VISIBLE);
            ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(MapCurrentLocation.this, "token"),
                    commonClass.getDeviceID(MapCurrentLocation.this)).create(ApiInterface.class);
            Call<CommonPojo> call = apiInterface.callPunchOut(
                    commonClass.getSharedPref(getApplicationContext(), "sync_id"),
                    nlatitude, nlongitude, workLocation,branch_id);
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


                            commonClass.showSuccess(MapCurrentLocation.this, response.body().getData());
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
                            commonClass.showError(MapCurrentLocation.this, response.body().getData());
                        }
                    }
                    else if(response.code()==401 || response.code()==403){
                        commonClass.showError(MapCurrentLocation.this,"UnAuthendicated");
                        commonClass.clearAlldata(MapCurrentLocation.this);
                    } else {
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(MapCurrentLocation.this, mError.getError());
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
                    commonClass.showError(MapCurrentLocation.this, t.getMessage());
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
            commonClass.showSuccess(MapCurrentLocation.this,"Punch Out Successfully....");

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
    public void getFaceList(){
        CommonClass commonClass = new CommonClass();
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface  = ApiClient.getTokenWithoutAuth( ).create(ApiInterface.class);
        Call<FaceModal> call = apiInterface.getFaceList(commonClass.getDeviceID(MapCurrentLocation.this));
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
                                        commonClass.getDeviceID(MapCurrentLocation.this), response.body().getCommonPojo().getTitle() , 0.5f);

                                try {
                                    String embeddingsString = response.body().getCommonPojo().getEmbeedings();
                                    faceAuthDB.insertData(embeddingsString,response.body().getCommonPojo().getTitle(),
                                            response.body().getCommonPojo().getUpdated_at());
                                    float[][] value = new Gson().fromJson(embeddingsString, float[][].class);
                                    result.setExtra(value);
                                    registered.put(response.body().getCommonPojo().getTitle(), result);
                                    previewView.setVisibility(View.VISIBLE);
                                } catch (JsonSyntaxException | ClassCastException e) {
                                    // Handle parsing error or class cast error
                                    e.printStackTrace();
                                    commonClass.showError(MapCurrentLocation.this,"No Face Registered");
                                    Log.d("getFaceID"," eror "+e.getMessage());
                                    // You might want to log this or inform the user
                                }


                            }else{
                                previewView.setVisibility(View.GONE);
                                mapView.setVisibility(View.VISIBLE);
                                commonClass.showError(MapCurrentLocation.this,"Register Face for punch attendance");
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
      //  Toast.makeText(getApplicationContext(),"Flow3",Toast.LENGTH_SHORT).show();

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
                                                    //commonClass.showError(MapCurrentLocation.this,"No Face Detected");
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
    private void getFaceListOffline() {
        SQLFaceModal sqlFaceModal=faceAuthDB.selectFaceList();
        if(sqlFaceModal!=null){
            if(sqlFaceModal.getExtra()!=null){
                start=true;
                SimilarityClassifier.Recognition result = new SimilarityClassifier.Recognition(
                        commonClass.getDeviceID(MapCurrentLocation.this),
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
                    commonClass.showError(MapCurrentLocation.this,"No Face Registered");
                    Log.d("getFaceID"," eror "+e.getMessage());
                    // You might want to log this or inform the user
                }

            }else{
                commonClass.showWarning(MapCurrentLocation.this,"Go Online and register Your face first");
            }
        }else{
            commonClass.showWarning(MapCurrentLocation.this,"Go Online and register Your face first");
        }
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




                    if(distance_local<0.95){
                        Log.d("getName"," registerc ");
                        start=false;
                        commonClass.showSuccess(MapCurrentLocation.this,"Face Authentication Success");
                        callLogin();
                    }else {
                        Log.d("getName"," un registered ");
                        start=false;
                         recognize.setVisibility(View.VISIBLE);
                        commonClass.showError(MapCurrentLocation.this,"Face Authentication Failed");
                    }

//                    System.out.println("nearest: " + name + " - distance: " + distance_local);




            }else{
                Log.d("FaceReg"," come to main else part ");
            }
        }else{
            commonClass.showError(MapCurrentLocation.this,"No Face Authentication Detected");
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
        previewView.setVisibility(View.GONE);
        mapView.setVisibility(View.VISIBLE);
        recognize.setVisibility(View.GONE);
        check_in.setVisibility(View.VISIBLE);
        checkout.setVisibility(View.VISIBLE);

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


    private void addGeofence() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        double userLat = location.getLatitude();
                        double userLng = location.getLongitude();
                        if(nlatitude==0.0 && nlongitude==0.0) {
                            nlatitude = location.getLatitude();
                            nlongitude = location.getLongitude();
                        }
                        Log.d("AttendanceLocation", "User Location Lst Known : Lat " + userLat + " Lng " + userLng);

                        getCurrentLocationChange();
                        // checkAttendanceEligibility(nlatitude, nlongitude);

                        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

                    } else {
                        Log.d("AttendanceLocation", "Failed to get location");
                        Toast.makeText(this, "Unable to get location. Try again.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.d("AttendanceLocation", "Location Error: " + e.getMessage()));


        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(this);
        View view= LayoutInflater.from(this).inflate(R.layout.location_check,null);
        ImageView close = view.findViewById(R.id.close);
        CheckBox use_current_location =view.findViewById(R.id.use_current_location);
        EditText edlatitude =view.findViewById(R.id.latitude);
        EditText edlongitude =view.findViewById(R.id.longitude);
        TextView current_distance =view.findViewById(R.id.current_distance);
        TextView submit =view.findViewById(R.id.submit);


        builder.setView(view);
        final AlertDialog mDialog = builder.create();
        mDialog.create();
        mDialog.show();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(edlatitude.getText().toString()) && !use_current_location.isChecked()){
                    Toast.makeText(getApplicationContext(),"Enter latitude",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(edlongitude.getText().toString())  && !use_current_location.isChecked()){
                    Toast.makeText(getApplicationContext(),"Enter Longitude",Toast.LENGTH_SHORT).show();
                }else{
                    int dist =0;
                    String location;
                    if(use_current_location.isChecked()){
                        dist =checkAttendanceEligibility(nlatitude,nlongitude);
                        location =" User Location "+latitude+","+longitude;
                    }else{
                        dist=checkAttendanceEligibility(Double.valueOf(edlatitude.getText().toString()),
                                Double.valueOf(edlongitude.getText().toString()));
                        location =" User Location "+edlatitude.getText().toString()+","+edlongitude.getText().toString();
                    }

                    if(branch_id.equals("-1")){
                        Toast.makeText(getApplicationContext(),"No Branch Present",Toast.LENGTH_SHORT).show();
                    }else{
                        BranchTable  branchTable =new BranchTable(MapCurrentLocation.this);
                        branchTable.getWritableDatabase();
                        LatLngBranch latLngBranch = branchTable.selectRow(branch_id);
                        current_distance.setText("Current Location Distance : "+dist+"\n"+location+"\n Work Location : "+
                                latLngBranch.getLatitude()+","+latLngBranch.getLongitude()+"(Radius : "+latLngBranch.getBranchdistance()+")");
                    }



                }
            }
        });





    }
    private int checkAttendanceEligibility(double userLat, double userLng) {
        Log.d("AttendanceLocation"," lat lng "+userLat+" lng "+userLng);
        float[] results = new float[1];
        BranchTable branchTable =new BranchTable(MapCurrentLocation.this);
        branchTable.getWritableDatabase();
        List<LatLngBranch> getBranch = new ArrayList<>();
        getBranch = branchTable.getAllNameList();
        if(getBranch.size()!=0){
            for(int i=0;i<getBranch.size();i++){
                double officeLat = Double.parseDouble(getBranch.get(i).getLatitude());
                double officeLng = Double.parseDouble(getBranch.get(i).getLongitude());
                int allowedRadius = Integer.parseInt(getBranch.get(i).getBranchdistance());
                Location.distanceBetween(officeLat, officeLng, userLat, userLng, results);
                float distanceInMeters = results[0];
                Log.d("AttendanceLocation"," distance meter "+distanceInMeters);
                int roundedDistance;
                double decimalPart = distanceInMeters - (int) distanceInMeters;

                if (decimalPart > 0.60) {
                    roundedDistance = (int) distanceInMeters + 1; // Round up
                } else {
                    roundedDistance = (int) distanceInMeters; // Normal truncation
                }
                Log.d("AttendanceLocation"," office lat "+officeLat+" officelng "+officeLng+" ofz radius "+
                        allowedRadius);

                Log.d("AttendanceLocation"," round of distance "+roundedDistance+" current distance "+currentDistance);


                if(i==0){
                    currentDistance = roundedDistance;
                    branch_id = getBranch.get(i).getBranchid();
                    if (roundedDistance <= allowedRadius) {
                        geoFenLoc=0;
                    }else{
                        geoFenLoc=1;
                    }
                }else{
                    if(roundedDistance<currentDistance){
                        currentDistance = roundedDistance;
                        branch_id = getBranch.get(i).getBranchid();
                        if (roundedDistance <= allowedRadius) {
                            geoFenLoc=0;
                        }else{
                            geoFenLoc=1;
                        }
                    }
                }

                Log.d("AttendanceLocation"," current distance "+currentDistance+"  branch id "+branch_id+" allowede radius "+
                        geoFenLoc);
            }
        }else{
            commonClass.showWarning(MapCurrentLocation.this,"No Branch List Found");
        }
        return  currentDistance;
    }
    private void callLocationCalling() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED  ) {
            Log.d("AttendanceLocation"," on request");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION }, 2000);
        } else {
            Log.d("AttendanceLocation"," ad geo");
            addGeofence();
        }
    }

    private void getCurrentLocationChange() {
        Log.d("AttendanceLocation"," location change called ");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            Log.d("AttendanceLocation","permission not enabled ");
            return;
        }
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(2000)
                .setFastestInterval(1000)
                .setNumUpdates(1); // Request only one update

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    nlatitude = location.getLatitude();
                    nlongitude = location.getLongitude();
                    Log.d("AttendanceLocation"," on location changed call back "+latitude+","+longitude);
                }
            }

        };

    }



}