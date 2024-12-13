package in.proz.adamd.FaceAuth;
import in.proz.adamd.R;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class NewFaceAuth extends AppCompatActivity implements SurfaceHolder.Callback {

    private SurfaceView cameraView;
    private Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_face_auth);

        cameraView = findViewById(R.id.camera_view);
        SurfaceHolder holder = cameraView.getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("NewFaceAuth"," holder called ");
        camera = Camera.open();
        try {
            Log.d("NewFaceAuth"," try block ");
            camera.setPreviewDisplay(holder);
            camera.startPreview();
            camera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    Log.d("NewFaceAuth"," preview fra");
                    detectFaces(data, camera.getParameters().getPreviewSize());
                }
            });
        } catch (IOException e) {
            Log.d("NewFaceAuth"," err1 "+e.getMessage());
            e.printStackTrace();
        }
    }

    private void detectFaces(byte[] data, Camera.Size size) {
        Log.d("NewFaceAuth"," data "+data+" camera size "+size);
        YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, size.width, size.height), 100, baos);
        byte[] jpegData = baos.toByteArray();
        Log.d("NewFaceAuth"," jpeg data" +jpegData);

        // Convert the JPEG byte array to a Bitmap
        Bitmap bitmap = BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length);
        Log.d("NewFaceAuth"," bitmap "+bitmap);


        // Convert byte array to Bitmap here
        // Use FirebaseVisionImage to process the image
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        Log.d("NewFaceAuth"," image "+image);
        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                         .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .build();

        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance().getVisionFaceDetector(options);
        detector.detectInImage(image)
                .addOnSuccessListener(faces -> {
                    for (FirebaseVisionFace face : faces) {
                        // Handle detected faces
                        Log.d("NewFaceAuth"," face deta "+face);
                    }
                })
                .addOnFailureListener(e -> Log.d("NewFaceAuth", "Error: " + e.getMessage()));
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Handle surface changes
        Log.d("NewFaceAuth"," surface changed ");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            Log.d("NewFaceAuth"," surface destroyed ");
        }
    }
}
