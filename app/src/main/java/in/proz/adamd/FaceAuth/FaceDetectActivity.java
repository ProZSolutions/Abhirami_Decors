package in.proz.adamd.FaceAuth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import com.google.common.util.concurrent.ListenableFuture;
import com.tuyenmonkey.mkloader.MKLoader;

import in.proz.adamd.BuildConfig;
 import in.proz.adamd.DashboardNewActivity;
import in.proz.adamd.Face.FaceModal;
import in.proz.adamd.LoginActivity;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;
import in.proz.adamd.Retrofit.LoginInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FaceDetectActivity extends AppCompatActivity {
    FaceDetector detector;
    CommonClass commonClass = new CommonClass();

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView previewView;
    Interpreter tfLite;
    TextView recognize;
    TextView title;
    CameraSelector cameraSelector;
    boolean developerMode=false;
    float distance= 1.0f;
    boolean start=true,flipX=false;
    MKLoader loader;
    Context context=FaceDetectActivity.this;
    int cam_face=CameraSelector.LENS_FACING_FRONT; //Default Back Camera

    int[] intValues;
    int inputSize=112;  //Input size for model
    boolean isModelQuantized=false;
    float[][] embeedings;
    float IMAGE_MEAN = 128.0f;
    float IMAGE_STD = 128.0f;
    int OUTPUT_SIZE=192; //Output size of model
    private static int SELECT_PICTURE = 1;
    ProcessCameraProvider cameraProvider;
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    String modelFile="mobile_face_net.tflite"; //model name

    private HashMap<String, SimilarityClassifier.Recognition> registered = new HashMap<>(); //saved Faces
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FaceReg"," on creaate called ");
        registered=readFromSP(); //Load saved faces from memory when app starts
        setContentView(R.layout.face_auth_new);
        ImageView back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in  = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(in);
            }
        });
        loader = findViewById(R.id.loader);
        SharedPreferences sharedPref = getSharedPreferences("Distance",Context.MODE_PRIVATE);
        distance = sharedPref.getFloat("distance",1.00f);
        title = findViewById(R.id.header_title);
        title.setText("Face Auth");
        recognize=findViewById(R.id.button3);
        recognize.setText("Retry");
        recognize.setVisibility(View.GONE);
        //  camera_switch=findViewById(R.id.button5);
        //        preview_info.setText("        Recognized Face:");
        //Camera Permission
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }



        getList();
        recognize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start = true;
                recognize.setVisibility(View.GONE);
            }
        });

        //Load model
        try {
            Log.d("FaceReg"," try block "+modelFile);
            tfLite=new Interpreter(loadModelFile(FaceDetectActivity.this,modelFile));
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



    }




    public void getList(){
        CommonClass commonClass = new CommonClass();
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface  = ApiClient.getTokenWithoutAuth( ).create(ApiInterface.class);
        Call<FaceModal> call = apiInterface.getFaceList(commonClass.getDeviceID(FaceDetectActivity.this));
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
                                        commonClass.getDeviceID(FaceDetectActivity.this), response.body().getCommonPojo().getTitle() , 0.5f);

                                try {
                                    String embeddingsString = response.body().getCommonPojo().getEmbeedings();
                                    float[][] value = new Gson().fromJson(embeddingsString, float[][].class);
                                    result.setExtra(value);
                                    registered.put(response.body().getCommonPojo().getTitle(), result);
                                    previewView.setVisibility(View.VISIBLE);
                                } catch (JsonSyntaxException | ClassCastException e) {
                                    // Handle parsing error or class cast error
                                    e.printStackTrace();
                                    commonClass.showError(FaceDetectActivity.this,"No Face Registered");
                                    Log.d("getFaceID"," eror "+e.getMessage());
                                    // You might want to log this or inform the user
                                }


                            }else{

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


    private void hyperparameters()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Euclidean Distance");
        builder.setMessage("0.00 -> Perfect Match\n1.00 -> Default\nTurn On Developer Mode to find optimum value\n\nCurrent Value:");
        // Set up the input
        final EditText input = new EditText(context);

        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);
        SharedPreferences sharedPref = getSharedPreferences("Distance",Context.MODE_PRIVATE);
        distance = sharedPref.getFloat("distance",1.00f);
        input.setText(String.valueOf(distance));
        // Set up the buttons
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(context, input.getText().toString(), Toast.LENGTH_SHORT).show();

                distance= Float.parseFloat(input.getText().toString());


                SharedPreferences sharedPref = getSharedPreferences("Distance",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putFloat("distance", distance);
                editor.apply();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        builder.show();
    }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private MappedByteBuffer loadModelFile(Activity activity, String MODEL_FILE) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    //Bind camera and preview view
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
    private void callLogin() {
        loader.setVisibility(View.VISIBLE);
        String versionName = BuildConfig.VERSION_NAME;

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<CommonPojo> call = apiInterface.callFingerAuth(
                commonClass.getDeviceID(FaceDetectActivity.this),versionName);
        Log.d("login_url"," url as "+call.request().url());
        call.enqueue(new Callback<CommonPojo>() {
            @Override
            public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                loader.setVisibility(View.GONE);
                Log.d("login_url"," code "+response.code());

                if(response.isSuccessful()){
                    if(response.code()==200){
                         Log.d("login_url"," response "+response.body().getStatus()+" data "+
                                response.body().getData()+" role "+response.body().getRole().length()+" role id "
                                +response.body().getRole()+response.body().getName());

                        commonClass.putSharedPref(getApplicationContext(),"AdminEmpNo",null);
                        commonClass.putSharedPref(getApplicationContext(),"AdminName",null);
                        commonClass.putSharedPref(getApplicationContext(),"AdminRole",null);
                        commonClass.putSharedPref(getApplicationContext(), "AdminRoleName", null);
                        commonClass.putSharedPref(FaceDetectActivity.this,"EmppName",null);
                        commonClass.putSharedPref(FaceDetectActivity.this,"username",null);
                        commonClass.putSharedPref(FaceDetectActivity.this,"token",null);

                        if(!response.body().getRole().equals("10")) {

                            if (response.body().getLoginInfo() != null) {
                                if (response.body().getLoginInfo() != null) {
                                    LoginInfo info = response.body().getLoginInfo();
                                    if (!TextUtils.isEmpty(info.getEmployee_No())) {
                                        commonClass.putSharedPref(getApplicationContext(), "AdminEmpNo", info.getEmployee_No());
                                    }
                                    if (!TextUtils.isEmpty(info.getName())) {
                                        commonClass.putSharedPref(getApplicationContext(), "AdminName", info.getName());
                                    }
                                    if (info.getRole().size() != 0) {
                                        String listString = String.join("@", info.getRole());
                                        Log.d("login_url", " role " + listString);
                                        commonClass.putSharedPref(getApplicationContext(), "AdminRole", listString);
                                    }
                                    if (info.getRole_Name().size() != 0) {
                                        String listString = String.join("@", info.getRole_Name());
                                        Log.d("login_url", " role name " + listString);
                                        commonClass.putSharedPref(getApplicationContext(), "AdminRoleName", listString);
                                    }
                                }
                            } else {
                                Log.d("login_url", " login info null ");
                            }
                        }
                        Log.d("login_url"," name as "+response.body().getName());
                        commonClass.putSharedPref(FaceDetectActivity.this,"EmppName",response.body().getName());
                         commonClass.putSharedPref(FaceDetectActivity.this,"token",response.body().getToken_type()+" "+response.body().getBearer_token());
                        //commonClass.showSuccess(FaceDetectActivity.this,"Logged In Successfully");
                        if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"first_time"))){
                            commonClass.showSuccess(FaceDetectActivity.this,"Logged In Successfully");
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    Intent intent = new Intent(getApplicationContext(), DashboardNewActivity.class);
                                    startActivity(intent);
                                }
                            }, 2000);
                        }else{
                            commonClass.putSharedPref(getApplicationContext(),"first_time","fist");
                            Log.d("LoginPage"," face  name "+response.body().getName());
                            callAlertDialog(response.body().getName());

                        }


                    }else{
                        Log.d("login_url"," response "+response.body().getStatus());

                        commonClass.showError(FaceDetectActivity.this,response.body().getStatus());
                    }
                }else{
                    Gson gson = new GsonBuilder().create();
                    CommonPojo mError = new CommonPojo();
                    try {
                        mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                        commonClass.showError(FaceDetectActivity.this,mError.getError());
                        //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // handle failure to read error
                        Log.d("thumbnail_url", " exp error  " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CommonPojo> call, Throwable t) {
                loader.setVisibility(View.GONE);
            }
        });
    }
    private void callAlertDialog(String username) {
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(this);
        View view= LayoutInflater.from(this).inflate(R.layout.welcome_popup,null);
        TextView userma = view.findViewById(R.id.username);
        userma.setText("Welcome "+username+"!!!");

        builder.setView(view);
        final android.app.AlertDialog mDialog = builder.create();
        mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        mDialog.create();
        mDialog.show();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                mDialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), DashboardNewActivity.class);
                startActivity(intent);
            }
        }, 2000);

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
                                                    //commonClass.showError(FaceDetectActivity.this,"No Face Detected");
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
                        commonClass.showSuccess(FaceDetectActivity.this,"Face Authentication Success");
                        callLogin();
                    }else {
                        Log.d("getName"," un registered ");
                        start=false;
                        recognize.setVisibility(View.VISIBLE);
                        commonClass.showError(FaceDetectActivity.this,"Face Authentication Failed");
                    }
//                    System.out.println("nearest: " + name + " - distance: " + distance_local);
                }
                else
                {
                    if(distance_local<distance){
                        Log.d("getName"," registerc ");
                        start=false;
                        commonClass.showSuccess(FaceDetectActivity.this,"Face Authentication Success");
                        callLogin();
                    }else {
                        Log.d("getName"," un registered ");
                        start=false;
                        recognize.setVisibility(View.VISIBLE);
                        commonClass.showError(FaceDetectActivity.this,"Face Authentication Failed");
                    }

//                    System.out.println("nearest: " + name + " - distance: " + distance_local);
                }



            }else{
                    Log.d("FaceReg"," come to main else part ");
            }
        }else{
            commonClass.showError(FaceDetectActivity.this,"No Face Authentication Detected");
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
//    public void register(String name, SimilarityClassifier.Recognition rec) {
//        registered.put(name, rec);
//    }

    //Compare Faces by distance between face embeddings


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

    //Save Faces to Shared Preferences.Conversion of Recognition objects to json string


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

    //Load Photo from phone storage


    //Similar Analyzing Procedure
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
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

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

}

