package in.proz.adamd.Profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tuyenmonkey.mkloader.MKLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import in.proz.adamd.AboutActivity;
import in.proz.adamd.BuildConfig;
import in.proz.adamd.DashboardNewActivity;
import in.proz.adamd.LoginActivity;
import in.proz.adamd.Map.MapCurrentLocation;
import in.proz.adamd.ModalClass.PersonalMainModal;
import in.proz.adamd.ModalClass.PersonalModal;
import in.proz.adamd.NotesActivity.NotesActivity;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;
import in.proz.adamd.SQLiteDB.BranchTable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    TextView title;
    CommonClass  commonClass =new CommonClass();
    ImageView back_arrow,edit_icon;
    MKLoader loader;
    String imageFileName , imageFileExt;
    Bitmap bitmap;
    CircleImageView profile_img;
    TextView designation,name,header_title;
    LinearLayout nhome_layout,nreports_layout,nlocation_layout,nprofile_layout,register_face;
    //RelativeLayout personal_info,shift_history,certification,change_password,logout;
    LinearLayout personal_info,shift_history,certification,change_password,logout,aboutLayout;
    ImageView profile_icon;
    TextView profile_text;
  /*    LinearLayout online_layout;
    ImageView online_icon;
    TextView online_text;*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profile_icon = findViewById(R.id.profile_icon);
        profile_text = findViewById(R.id.profile_text);
        loader = findViewById(R.id.loader);
        String versionName = BuildConfig.VERSION_NAME;
        TextView copy_right = findViewById(R.id.copy_right);
        copy_right.setText("Copyright © 2024 ProZ Solutions LLP. All Rights Reserved. Version : "+getApplicationContext().getString(R.string.version_Code));

        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), DashboardNewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY  | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        nhome_layout= findViewById(R.id.nhome_layout);
      //  nabout_layout= findViewById(R.id.nabout_layout);
        nreports_layout= findViewById(R.id.nreports_layout);
        nlocation_layout= findViewById(R.id.nlocation_layout);
        nprofile_layout = findViewById(R.id.nprofile_layout);
        nprofile_layout.setOnClickListener(this);

        nhome_layout.setOnClickListener(this);
       // nabout_layout.setOnClickListener(this);
        nreports_layout.setOnClickListener(this);
        nlocation_layout.setOnClickListener(this);
        header_title = findViewById(R.id.header_title);
        header_title.setText(commonClass.getSharedPref(getApplicationContext(),"EmppName"));

       /* if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
            // header_title.setText("Admin");
            nprofile_layout.setVisibility(View.VISIBLE);
            nlocation_layout.setVisibility(View.GONE);
        }else{
           // header_title.setText("Employee");
            nprofile_layout.setVisibility(View.GONE);
            nlocation_layout.setVisibility(View.VISIBLE);
        }*/
        profile_icon.setImageTintList(getApplicationContext().getColorStateList(R.color.color_primary));
        profile_text.setTextColor(getApplicationContext().getColor(R.color.black));

        title=findViewById(R.id.title);
        title.setText("Profile");
         initView();
         if(commonClass.isOnline(ProfileActivity.this)){
             getList();
         }else{
             commonClass.showInternetWarning(ProfileActivity.this);
         }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(getApplicationContext(), DashboardNewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY  | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void getList() {
        loader.setVisibility(View.VISIBLE);
        ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(ProfileActivity.this,"token"),
                commonClass.getDeviceID(ProfileActivity.this)).create(ApiInterface.class);
        Call<PersonalMainModal> call = apiInterface.getUserProfile();
        Log.d("profile_index"," url as "+call.request().url()+"   "+commonClass.getDeviceID(ProfileActivity.this)+
                " token "+commonClass.getSharedPref(getApplicationContext(),"token"));
        call.enqueue(new retrofit2.Callback<PersonalMainModal>() {
            @Override
            public void onResponse(Call<PersonalMainModal> call, Response<PersonalMainModal> response) {
                //progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                Log.d("profile_index"," ode "+response.code());
                if(response.isSuccessful()){
                    if(response.code()==200){
                        PersonalModal personalModal = response.body().getPersonalModal();
                        Log.d("profile_index"," name "+response.body().getPersonalModal().getName());
                        if(!TextUtils.isEmpty(response.body().getPersonalModal().getName())){
                            Log.d("imagePicker"," response image "+response.body().getPersonalModal().getImage());

                            if(!TextUtils.isEmpty(response.body().getPersonalModal().getImage())){
                                Picasso.with(ProfileActivity.this).
                                        load(response.body().getPersonalModal().getImage()).into(profile_img);
                                commonClass.putSharedPref(getApplicationContext(),"image",null);
                                commonClass.putSharedPref(getApplicationContext(),"image",response.body().getPersonalModal().getImage());
                            }
                            /*
                             */



                        }
                    }else{
                        Gson gson = new GsonBuilder().create();
                        CommonPojo mError = new CommonPojo();
                        try {
                            mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                            commonClass.showError(ProfileActivity.this,mError.getError());
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

                        commonClass.showError(ProfileActivity.this,mError.getError());
                        //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // handle failure to read error
                        Log.d("thumbnail_url", " exp error  " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<PersonalMainModal> call, Throwable t) {
                //progressDialog.dismiss();
                loader.setVisibility(View.GONE);
                commonClass.showError(ProfileActivity.this,t.getMessage());
            }
        });
    }

    private void initView() {
        register_face = findViewById(R.id.register_face);
        register_face.setOnClickListener(this);
         nprofile_layout = findViewById(R.id.nprofile_layout);
        nprofile_layout.setOnClickListener(this);
        logout = findViewById(R.id.logout);
        aboutLayout = findViewById(R.id.aboutLayout);
        logout.setOnClickListener(this);
        aboutLayout.setOnClickListener(this);
        CommonClass comm = new CommonClass();
       // online_icon = findViewById(R.id.online_icon);
        edit_icon = findViewById(R.id.edit_icon);
        change_password = findViewById(R.id.change_password);
       /* online_layout = findViewById(R.id.online_layout);
        online_text = findViewById(R.id.online_text);
        comm.onlineStatusCheck(ProfileActivity.this,online_layout,online_text,online_icon);*/
        shift_history = findViewById(R.id.shift_history);
        name = findViewById(R.id.name);
        profile_img = findViewById(R.id.profile_img);
        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callCustom1();
            }
        });
        edit_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callCustom1();
            }
        });
        designation = findViewById(R.id.designation);
        shift_history.setOnClickListener(this);
        personal_info= findViewById(R.id.personal_info);
       // certification= findViewById(R.id.certification);
        personal_info.setOnClickListener(this);
      //  certification.setOnClickListener(this);
        name.setText(commonClass.getSharedPref(getApplicationContext(),"name"));
        designation.setText(commonClass.getSharedPref(getApplicationContext(),"designation"));
        loader.setVisibility(View.VISIBLE);
        change_password.setOnClickListener(this);
        Picasso.with(ProfileActivity.this).load(commonClass.getSharedPref(getApplicationContext(),"image")).
                into(profile_img, new Callback() {
                    @Override
                    public void onSuccess() {
                        loader.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        loader.setVisibility(View.GONE);
                        profile_img.setImageResource(R.drawable.circle_shape);
                    }
                });

    }
    private void callCustom1() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityResultLauncher.launch(intent);
    }
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()== Activity.RESULT_OK){
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        imageFileName =commonClass.getFileNameFromUri(ProfileActivity.this,uri);
                        imageFileExt = commonClass.getFileTypeFromUri(ProfileActivity.this,uri);


                        Log.d("getImageDetails"," uro "+uri);
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                            profile_img.setImageBitmap(bitmap);
                            if(bitmap!=null) {
                                callAlertDialog(bitmap);
                            }
                        }catch (IOException e){

                        }
                    }
                }
            });

    private void callAlertDialog(Bitmap bitmap) {

        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(this);
        View view= LayoutInflater.from(this).inflate(R.layout.new_warning_dlg,null);
        TextView msg = view.findViewById(R.id.msg);
        TextView cancel_text = view.findViewById(R.id.cancel_text);
        cancel_text.setText("NO");

        TextView btn_title1 = view.findViewById(R.id.btn_title1);
        TextView btn_title = view.findViewById(R.id.btn_title);
        btn_title1.setText("YES");
        btn_title.setText("YES");

        msg.setText("Would You like to Upload Profile Image?");
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
                 uploadImage(bitmap,decline);
            }
        });


     }
    private void uploadImage(Bitmap bitmap,LinearLayout decline) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,10,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        final String base64image= Base64.encodeToString(bytes,Base64.DEFAULT);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), bytes);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", imageFileName, requestBody);
/*        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);*/
        Log.d("imagePicker"," image "+imagePart);
        if(imagePart!=null){
            loader.setVisibility(View.VISIBLE);
            decline.setEnabled(false);
            ApiInterface apiInterface  = ApiClient.getTokenRetrofit(commonClass.getSharedPref(ProfileActivity.this,"token"),
                    commonClass.getDeviceID(ProfileActivity.this)).create(ApiInterface.class);
            Call<CommonPojo> call = apiInterface.updateProfile(imagePart,commonClass.getSharedPref(getApplicationContext(),"profile_id"));
            Log.d("imagePicker"," call "+call.request().url());
            call.enqueue(new retrofit2.Callback<CommonPojo>() {
                @Override
                public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                    loader.setVisibility(View.GONE);
                    decline.setEnabled(true);
                    Log.d("imagePicker"," respne "+response.code());
                    if(response.isSuccessful()) {
                        if (response.code() == 200) {
                            if(response.body().getStatus().equals("success")){
                                getList();
                                commonClass.showSuccess(ProfileActivity.this,"Image Updated Successfully...");
                            }else{
                                commonClass.showError(ProfileActivity.this,"Failed to save image");
                            }
                        } else {
                            Gson gson = new GsonBuilder().create();
                            CommonPojo mError = new CommonPojo();
                            try {
                                mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);
                                commonClass.showError(ProfileActivity.this, mError.getError());
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
                            commonClass.showError(ProfileActivity.this,mError.getError());
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
                    decline.setEnabled(true);
                    commonClass.showError(ProfileActivity.this,t.getMessage());
                }
            });
        }else{
            commonClass.showWarning(ProfileActivity.this,"Image Not Picked");
        }
    }


    @Override
    public void onClick(View view1) {
        int id = view1.getId();
        switch (id){
            case R.id.register_face:
                Intent register_face = new Intent(getApplicationContext(),RegisterFace.class);
                startActivity(register_face);
                break;

            case R.id.aboutLayout:
                Intent aboun = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(aboun);
                break;
            case R.id.nhome_layout:
                Intent intentabout1 = new Intent(getApplicationContext(), DashboardNewActivity.class);
                intentabout1.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY  | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentabout1);
                break;
          /*  case R.id.nabout_layout:
                Intent intentabout = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intentabout);
                break;*/
            case R.id.nreports_layout:
                Intent notes=new Intent(getApplicationContext(), NotesActivity.class);
                startActivity(notes);
                break;
            case R.id.nlocation_layout:
                Intent intent152 = new Intent(getApplicationContext(), MapCurrentLocation.class);
                startActivity(intent152);
                break;
            case R.id.logout:

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
                        BranchTable branchTable = new BranchTable(ProfileActivity.this);
                        branchTable.getWritableDatabase();
                        branchTable.DropTable();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);                    }
                });



                break;
            case R.id.change_password:
                Intent pass = new Intent(getApplicationContext(),CertificationActivity.class);
                startActivity(pass);
                break;
            case R.id.personal_info:
                Intent info = new Intent(getApplicationContext(),PersonalInformation.class);
                startActivity(info);
                break;
            case R.id.shift_history:
                Intent shift = new Intent(getApplicationContext(),ShiftActivity.class);
                startActivity(shift);
                break;
          /*  case R.id.certification:
                Intent cert = new Intent(getApplicationContext(),CertificationActivity.class);
                startActivity(cert);
                break;*/
        }
    }
}
