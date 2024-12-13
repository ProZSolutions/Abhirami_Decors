package in.proz.adamd.Sample;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import in.proz.adamd.R;
import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SampleImageUpload extends AppCompatActivity {
    TextView choose_image,submit_image,choose_pdf,submit_pdf;
    Bitmap bitmap;
    String imageFileName , imageFileExt;
    ImageView pickImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_upload);
        initView();
    }
    public String getFileNameFromUri(Context context, Uri uri) {
        String fileName = null;
        if (uri.getScheme().equals("content")) {
            ContentResolver contentResolver = context.getContentResolver();
            try {
                // Query the content resolver to get the filename
                Cursor cursor = contentResolver.query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                    fileName = cursor.getString(columnIndex);
                    cursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (uri.getScheme().equals("file")) {
            fileName = new File(uri.getPath()).getName();
        }
        return fileName;
    }
    private void initView() {
        pickImage = findViewById(R.id.pickImage);
        choose_image = findViewById(R.id.choose_image);
        submit_image = findViewById(R.id.submit_image);
        choose_pdf = findViewById(R.id.choose_pdf);
        submit_pdf = findViewById(R.id.submit_pdf);
        choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intent);
            }
        });
        submit_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ByteArrayOutputStream  byteArrayOutputStream = new ByteArrayOutputStream();
                if(bitmap!=null){
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                    byte[] bytes = byteArrayOutputStream.toByteArray();
                    final String base64image= Base64.encodeToString(bytes,Base64.DEFAULT);
                    RequestBody requestBody = RequestBody.create(MediaType.parse(imageFileExt), bytes);
                    MultipartBody.Part part = MultipartBody.Part.createFormData("file", imageFileName, requestBody);
                    uploadImage(part);

                }
            }
        });
        choose_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        submit_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
    private void uploadImage(MultipartBody.Part part) {
        CommonClass commonClass = new CommonClass();

        Log.d("getImageDetails"," image "+part);
        if(part!=null){
             ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(SampleImageUpload.this,"token"),
                    commonClass.getDeviceID(SampleImageUpload.this)).create(ApiInterface.class);
            Call<CommonPojo> call = apiInterface.updateProfile(part,"11");
            Log.d("getImageDetails"," call "+call.request().url());
            call.enqueue(new Callback<CommonPojo>() {
                @Override
                public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                     Log.d("getImageDetails"," respne "+response.code());
                    if(response.isSuccessful()) {
                        if (response.code() == 200) {
                            if(response.body().getStatus().equals("success")){
                                commonClass.showSuccess(SampleImageUpload.this,"Image Updated Successfully...");
                            }else{
                                commonClass.showError(SampleImageUpload.this,"Failed to save image");
                            }
                        } else {
                            Gson gson = new GsonBuilder().create();
                            CommonPojo mError = new CommonPojo();
                            try {
                                mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);
                                commonClass.showError(SampleImageUpload.this, mError.getError());
                                //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                // handle failure to read error
                                Log.d("thumbnail_url", " exp error  " + e.getMessage());
                            }
                        }
                    }else{
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);
                            commonClass.showError(SampleImageUpload.this,mError.getError());
                            //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            // handle failure to read error
                            Log.d("thumbnail_url", " exp error  " + e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(Call<CommonPojo> call, Throwable t) {
                    Log.d("getImageDetails"," err "+t.getMessage());
                     commonClass.showError(SampleImageUpload.this,t.getMessage());
                }
            });
        }else{
            commonClass.showWarning(SampleImageUpload.this,"Image Not Picked");
        }
    }


    public String getFileTypeFromUri(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();

        // If the URI scheme is content
        if ("content".equals(uri.getScheme())) {
            // Use ContentResolver to get the MIME type
            return contentResolver.getType(uri);
        }

        // If the URI scheme is file
        if ("file".equals(uri.getScheme())) {
            // Extract the file extension from the URI path and get the MIME type
            String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        }

        // For other URI schemes, return null or handle as needed
        return null;
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()== Activity.RESULT_OK){
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        imageFileName = getFileNameFromUri(SampleImageUpload.this,uri);
                        imageFileExt = getFileTypeFromUri(SampleImageUpload.this,uri);
                        Log.d("getImageDetails"," file name "+getFileNameFromUri(SampleImageUpload.this,uri)
                        +" file ext "+getFileTypeFromUri(SampleImageUpload.this,uri));

Log.d("getImageDetails"," uro "+uri);
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                            pickImage.setImageBitmap(bitmap);
                        }catch (IOException e){

                        }
                    }
                }
            });

}
