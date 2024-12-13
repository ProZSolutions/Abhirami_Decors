package in.proz.adamd.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tuyenmonkey.mkloader.MKLoader;

import java.io.IOException;

import in.proz.adamd.DashboardNewActivity;
import in.proz.adamd.LoginActivity;
import in.proz.adamd.Map.MapCurrentLocation;
import in.proz.adamd.NotesActivity.NotesActivity;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CertificationActivity extends AppCompatActivity implements View.OnClickListener {
    TextView title;
    ImageView back_arrow;
    EditText edt_old_pass,edt_new_pass,edt_re_pass;
    CommonClass commonClass = new CommonClass();
    MKLoader loader;
   /* LinearLayout online_layout;
    ImageView online_icon;
    TextView online_text;*/
    TextView header_title;
    LinearLayout nhome_layout,nabout_layout,nreports_layout,nlocation_layout,nprofile_layout;
    ImageView profile_icon;
    TextView profile_text;

    RelativeLayout bottom_request_layout;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certification);
       // updateBottomNavigation();
        initView();
    }
    public void initView(){
        profile_icon = findViewById(R.id.profile_icon);
        profile_text = findViewById(R.id.profile_text);
        nhome_layout= findViewById(R.id.nhome_layout);
    //    nabout_layout= findViewById(R.id.nabout_layout);
        profile_icon.setImageTintList(getApplicationContext().getColorStateList(R.color.color_primary));
        profile_text.setTextColor(getApplicationContext().getColor(R.color.black));
        nreports_layout= findViewById(R.id.nreports_layout);
        nlocation_layout= findViewById(R.id.nlocation_layout);
        nprofile_layout = findViewById(R.id.nprofile_layout);
        nprofile_layout.setOnClickListener(this);
        nhome_layout.setOnClickListener(this);
      //  nabout_layout.setOnClickListener(this);
        nreports_layout.setOnClickListener(this);
        nlocation_layout.setOnClickListener(this);
        header_title = findViewById(R.id.header_title);
        header_title.setText(commonClass.getSharedPref(getApplicationContext(),"EmppName"));


      /*  if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminEmpNo")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminRole")) &&
                !TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"AdminName"))){
            //  header_title.setText("Admin");
            nprofile_layout.setVisibility(View.VISIBLE);
            nlocation_layout.setVisibility(View.GONE);
        }else{
            //header_title.setText("Employee");
            nprofile_layout.setVisibility(View.GONE);
            nlocation_layout.setVisibility(View.VISIBLE);
        }*/
        title = findViewById(R.id.title);
        loader = findViewById(R.id.loader);
        title.setText("Change Password");
        back_arrow = findViewById(R.id.back_arrow);
        edt_old_pass = findViewById(R.id.edt_old_pass);
        edt_new_pass = findViewById(R.id.edt_new_pass);
        edt_re_pass = findViewById(R.id.edt_re_pass);
        bottom_request_layout = findViewById(R.id.bottom_request_layout);
        bottom_request_layout.setOnClickListener(this);
        back_arrow.setOnClickListener(this);
       /* online_icon = findViewById(R.id.online_icon);
        online_layout = findViewById(R.id.online_layout);
        online_text = findViewById(R.id.online_text);
        commonClass.onlineStatusCheck(CertificationActivity.this,online_layout,online_text,online_icon);*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backPressCalled();
    }
    public void backPressCalled(){
        Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
        startActivity(intent);
    }
    @Override
    public void onClick(View view) {
        int id=  view.getId();
        switch (id) {
            case R.id.nprofile_layout:
                Intent intentp = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intentp);
                break;
            case R.id.nhome_layout:
                Intent intentabout1 = new Intent(getApplicationContext(), DashboardNewActivity.class);
                startActivity(intentabout1);
                break;
            /*case R.id.nabout_layout:
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
            case R.id.back_arrow:
                backPressCalled();
                break;
            case R.id.bottom_request_layout:
                if (TextUtils.isEmpty(edt_old_pass.getText().toString())) {
                    commonClass.showWarning(CertificationActivity.this, "Enter Old Password");
                } else if (TextUtils.isEmpty(edt_new_pass.getText().toString())) {
                    commonClass.showWarning(CertificationActivity.this, "Enter New Password");
                }
                else if(edt_new_pass.length()<6){
                    commonClass.showWarning(CertificationActivity.this, "Password Length Should be at least 6");

                }else if (TextUtils.isEmpty(edt_re_pass.getText().toString())) {
                    commonClass.showWarning(CertificationActivity.this, "Re-enter the Password");
                } else if (!edt_re_pass.getText().toString().equals(edt_new_pass.getText().toString())) {
                    commonClass.showWarning(CertificationActivity.this, "Re-enter the Password");
                } else if (edt_old_pass.getText().toString().equals(edt_new_pass.getText().toString())) {
                    commonClass.showWarning(CertificationActivity.this, "Old and New Passwords should not be same");
                } else {
                    bottom_request_layout.setEnabled(false);
                    loader.setVisibility(View.VISIBLE);
                    ApiInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(CertificationActivity.this, "token"),
                            commonClass.getDeviceID(CertificationActivity.this)).create(ApiInterface.class);
                    Call<CommonPojo> call = apiInterface.callChangePAssword(edt_old_pass.getText().toString(), edt_new_pass.getText().toString());
                    Log.d("getChange"," url as "+call.request().url());
                    call.enqueue(new Callback<CommonPojo>() {
                        @Override
                        public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                            loader.setVisibility(View.GONE);
                            Log.d("getChange"," code "+response.code());
                            if (response.isSuccessful()) {
                                if (response.code() == 200) {
                                    if(response.body().getStatus().equals("success")){
                                        commonClass.putSharedPref(getApplicationContext(),"token",null);
                                        commonClass.showSuccess(CertificationActivity.this,response.body().getData());
                                        new Handler().postDelayed(new Runnable() {
                                            public void run() {
                                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                startActivity(intent);
                                            }
                                        }, 2000);
                                    }else{
                                        commonClass.showError(CertificationActivity.this,response.body().getData());
                                    }
                                } else {
                                    bottom_request_layout.setEnabled(true);
                                    Gson gson = new GsonBuilder().create();
                                    CommonPojo mError = new CommonPojo();
                                    try {
                                        mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                                        commonClass.showError(CertificationActivity.this, mError.getError());
                                        //    Toast.makeText(getApplicationContext(), mError.getError(), Toast.LENGTH_LONG).show();
                                    } catch (IOException e) {
                                        // handle failure to read error
                                        Log.d("thumbnail_url", " exp error  " + e.getMessage());
                                    }
                                }

                            } else {
                                bottom_request_layout.setEnabled(true);
                                Gson gson = new GsonBuilder().create();
                                CommonPojo mError = new CommonPojo();
                                try {
                                    mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                                    commonClass.showError(CertificationActivity.this, mError.getError());
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
                            commonClass.showError(CertificationActivity.this,t.getMessage());
                        }
                    });
                    break;
                }
        }
    }
}
