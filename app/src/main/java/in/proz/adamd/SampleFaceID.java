package in.proz.adamd;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

 import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class SampleFaceID extends AppCompatActivity   {
    int REQUEST_ONE=100,PICK_ONE=1;
    private static final int PICK_IMAGE = 1000;

    Uri imageUri;
    /*FirebaseVisionFaceDetector detector;
    FirebaseVisionFaceDetectorOptions options;*/
    TextView txtTakePicture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_faceid);
        /*FirebaseApp.initializeApp(SampleFaceID.this);
        txtTakePicture = findViewById(R.id.txtTakePicture);
         options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
                        .build();

         detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(options);*/

// Create a FirebaseVisionImage object from a Bitmap, ByteBuffer, etc.

// Pass the image to the detector
        callImageMethod();

    }

    public  void  callImageMethod(){
        if (Build.VERSION.SDK_INT >= 30) {
            pickImage2(PICK_IMAGE);
        }else {
            if (checkPermission()) {
                pickImage2(PICK_IMAGE);
            } else {
                requestPermission(0,0);
            }
        }
    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(SampleFaceID.this,
                READ_EXTERNAL_STORAGE);
        int result1=ContextCompat.checkSelfPermission(SampleFaceID.this,
                WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED && result1==PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission(int requestCode,int sdk_version) {
        Dexter.withContext(SampleFaceID.this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
                    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            if(!Environment.isExternalStorageManager()){
                                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }else{
                                pickImage2(PICK_ONE);

                            }
                        }
                    }
                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions,
                                                                             PermissionToken token) {
                        Log.d("feedback_request"," permission not granded ");
                        /* ... */}
                }).check();
    }
    private void pickImage2(int i) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"new image");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Fromthe Camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent camintent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camintent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(camintent,PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

         if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (imageUri != null) {

                    final InputStream imageStream;
                    try {
                        imageStream = getContentResolver().openInputStream(imageUri);
                        Log.d("thumbnail_url", " image strm " + imageStream);
                        final Bitmap selectedImagebit = BitmapFactory.decodeStream(imageStream);
                        Log.d("thumbnail_url", "bitmao " + selectedImagebit);
                        Bitmap bitmap = Bitmap.createScaledBitmap(selectedImagebit, 100, 100, true);
                        Log.d("thumbnail_url"," bitmap "+bitmap);
                      /* FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
                        if(image!=null){
                            callTask(image);
                        }*/


                    } catch (FileNotFoundException e) {
                        Log.d("thumbnail_url", " error " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }

    }

   /* private void callTask(FirebaseVisionImage image) {
        Task<List<FirebaseVisionFace>> result =
                detector.detectInImage(image)
                        .addOnSuccessListener(faces -> {
                            // Task completed successfully
                            // Handle the detected faces here
                            for (FirebaseVisionFace face : faces) {
                                // Process each detected face
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Task failed with an exception
                            // Handle any errors here
                        });


    }
*/
}