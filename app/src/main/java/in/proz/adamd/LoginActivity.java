package in.proz.adamd;

 import android.app.AlertDialog;
 import android.content.Context;
 import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
 import android.os.Handler;
 import android.text.Editable;
 import android.text.TextUtils;
 import android.text.TextWatcher;
 import android.text.method.HideReturnsTransformationMethod;
 import android.text.method.PasswordTransformationMethod;
 import android.util.Log;
 import android.view.LayoutInflater;
 import android.view.View;
 import android.view.inputmethod.InputMethodManager;
 import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
 import android.window.SplashScreen;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

/*import com.shashank.sony.fancytoastlib.FancyToast;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;*/

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tuyenmonkey.mkloader.MKLoader;

import java.io.IOException;

 import in.proz.adamd.Biometric.BiometricActivity;
 import in.proz.adamd.FaceAuth.FaceDetectActivity;
 import in.proz.adamd.ModalClass.ConstructionModal;
 import in.proz.adamd.Retrofit.ApiClient;
import in.proz.adamd.Retrofit.ApiInterface;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;
 import in.proz.adamd.Retrofit.LoginInfo;
 import in.proz.adamd.SQLiteDB.BranchTable;
 import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    TextView submit;
    EditText username,password;
    int construction =0 ;
    CommonClass commonClass;
    int input_type=1;
     LinearLayout touch_id,finger_id;
    ImageView back_arrow,password_vision;
    MKLoader loader;
    TextView versioncode ,header_title,copy_right;
    ImageView notification,profile_imge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_login);
        commonClass = new CommonClass();
        initView();
        Log.d("getLoginDetails"," device id "+commonClass.getDeviceID(LoginActivity.this));
       callVersionStatus();
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //username.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);


            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s=editable.toString();
                if(!s.equals(s.toUpperCase()))
                {
                    s=s.toUpperCase();
                    username.setText(s);
                    username.setSelection(username.length()); //fix reverse texting
                }
            }
        });
    }


    private void initView() {

        notification = findViewById(R.id.notification_n);
        profile_imge = findViewById(R.id.profile_imge);
        notification.setVisibility(View.GONE);
        profile_imge.setVisibility(View.GONE);
        header_title = findViewById(R.id.header_title);
        header_title.setText("Login");
        versioncode = findViewById(R.id.versioncode);
        String versionName = BuildConfig.VERSION_NAME;
        versioncode.setText("Version Code : "+getApplicationContext().getString(R.string.version_Code));


        loader = findViewById(R.id.loader);
        password_vision = findViewById(R.id.password_vision);
        password_vision.setOnClickListener(this);
        /*back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(this);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAndRemoveTask();
                System.exit(0);
                moveTaskToBack(true);
            }
        });*/

        submit = findViewById(R.id.submit);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        touch_id = findViewById(R.id.touch_id);
        finger_id = findViewById(R.id.finger_id);
        touch_id.setOnClickListener(this);
        finger_id.setOnClickListener(this);
        submit.setOnClickListener(this);

    }
    private void callVersionStatus() {
        loader.setVisibility(View.VISIBLE);
        String versionName = BuildConfig.VERSION_NAME;
        ApiInterface apiInterface  = ApiClient.getTokenWithoutAuth( ).create(ApiInterface.class);
        Call<ConstructionModal> call = apiInterface.getVersionDetails(versionName);
        Log.d("loginToken"," url "+call.request().url()+" respnse ");
        call.enqueue(new Callback<ConstructionModal>() {
            @Override
            public void onResponse(Call<ConstructionModal> call, Response<ConstructionModal> response) {
                loader.setVisibility(View.GONE);
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
                        commonClass.showError(LoginActivity.this,"UnAuthendicated");
                        commonClass.clearAlldata(LoginActivity.this);
                    }
                }else if(response.code()==401 || response.code()==403){
                    commonClass.showError(LoginActivity.this,"UnAuthendicated");
                    commonClass.clearAlldata(LoginActivity.this);
                }
            }

            @Override
            public void onFailure(Call<ConstructionModal> call, Throwable t) {
                loader.setVisibility(View.GONE);
                Log.d("loginToken"," error "+t.getMessage()+
                        " err "+t.getCause());
            }
        });



    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.password_vision:
                if(input_type==0){
                    input_type=1;
                    password_vision.setImageDrawable(getApplicationContext().getDrawable(R.drawable.eye_hide));

                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }else{
                    input_type=0;
                    password_vision.setImageDrawable(getApplicationContext().getDrawable(R.drawable.eye_show));

                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                break;
            case R.id.back_arrow:
                Intent intent11 = new Intent(getApplicationContext(), SplashScreen.class);
                startActivity(intent11);
                break;
            case R.id.touch_id:
                if(commonClass.isOnline(LoginActivity.this)){
                    Intent intent1 = new Intent(getApplicationContext(), FaceDetectActivity.class);
                    startActivity(intent1);
                }else{
                    commonClass.showError(LoginActivity.this,"Please Make Sure Internet Enabled");
                }

                break;
            case R.id.finger_id:
                if(commonClass.isOnline(LoginActivity.this)){
                    Intent intent2 = new Intent(getApplicationContext(), BiometricActivity.class);
                    startActivity(intent2);
                }else{
                    commonClass.showError(LoginActivity.this,"Please Make Sure Internet Enabled");
                }

                break;
            case R.id.submit:
                if(commonClass.isOnline(LoginActivity.this)){

                    View view1 = this.getCurrentFocus();
                    if (view1 != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

             if(TextUtils.isEmpty(username.getText().toString())){
                 // commonClass.showAppToast(LoginActivity.this,"Enter Username");
                 commonClass.showWarning(LoginActivity.this,"Enter Username");
                }else if(TextUtils.isEmpty(password.getText().toString())){
                    commonClass.showWarning(LoginActivity.this,"Enter Password");
                }else{
             loader.setVisibility(View.VISIBLE);
                 submit.setEnabled(false);
             String versionName = BuildConfig.VERSION_NAME;

              ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                    Call<CommonPojo> call = apiInterface.callLoginApi(username.getText().toString(),password.getText().toString(),
                            commonClass.getDeviceID(LoginActivity.this),versionName);
                    Log.d("login_url"," url as "+call.request().url()+" device id "+commonClass.getDeviceID(LoginActivity.this));
                    call.enqueue(new Callback<CommonPojo>() {
                        @Override
                        public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                            loader.setVisibility(View.GONE);
                            submit.setEnabled(true);
                            Log.d("login_url"," code "+response.code());

                            if(response.isSuccessful()){
                                if(response.code()==200){

                                    commonClass.putSharedPref(getApplicationContext(),"AdminEmpNo",null);
                                    commonClass.putSharedPref(getApplicationContext(),"AdminName",null);
                                    commonClass.putSharedPref(getApplicationContext(),"AdminRole",null);
                                    commonClass.putSharedPref(getApplicationContext(), "AdminRoleName", null);
                                    commonClass.putSharedPref(LoginActivity.this,"EmppName",null);
                                    commonClass.putSharedPref(LoginActivity.this,"username",null);
                                    commonClass.putSharedPref(LoginActivity.this,"token",null);
                                    commonClass.putSharedPref(LoginActivity.this,"role_no",null);
                                    BranchTable branchTable = new BranchTable(LoginActivity.this);
                                    branchTable.getWritableDatabase();
                                    branchTable.DropTable();



                                    Log.d("role_option"," role "+response.body().getRole());


                                    if(!response.body().getRole().equals("10")) {

                                    if (response.body().getLoginInfo() != null) {
                                        if (response.body().getLoginInfo() != null) {
                                            commonClass.putSharedPref(getApplicationContext(),"role_no",response.body().getRole());
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


                                    if(response.body().getGetBranchDetails()!=null){
                                        if(response.body().getGetBranchDetails().size()!=0){

                                            for (int i=0;i<response.body().getGetBranchDetails().size();i++){
                                                CommonPojo commonPojo = response.body().getGetBranchDetails().get(i);
                                                branchTable.insertData(commonPojo.getBranch_id(),commonPojo.getLatitude(),commonPojo.getLongitude());
                                            }
                                        }
                                    }


                                    commonClass.putSharedPref(LoginActivity.this,"EmppName",response.body().getName());
                                    commonClass.putSharedPref(LoginActivity.this,"username",username.getText().toString());
                                    commonClass.putSharedPref(LoginActivity.this,"token",response.body().getToken_type()+" "+response.body().getBearer_token());
                                    //commonClass.showSuccess(LoginActivity.this,"Logged In Successfully");
                                    if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"first_time"))){
                                        commonClass.showSuccess(LoginActivity.this,"Logged In Successfully");
                                        new Handler().postDelayed(new Runnable() {
                                            public void run() {
                                                 Intent intent = new Intent(getApplicationContext(), DashboardNewActivity.class);
                                                startActivity(intent);
                                            }
                                        }, 2000);
                                    }else{
                                        commonClass.putSharedPref(getApplicationContext(),"first_time","fist");
                                        Log.d("LoginPage"," login name "+response.body().getName());
                                        callAlertDialog(response.body().getName());

                                    }


                                }else{
                                    Log.d("login_url"," response "+response.body().getStatus());

                                    commonClass.showError(LoginActivity.this,response.body().getStatus());
                                }
                            }else{
                                Gson gson = new GsonBuilder().create();
                                CommonPojo mError = new CommonPojo();
                                try {
                                    mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                                    commonClass.showError(LoginActivity.this,mError.getError());
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
                            submit.setEnabled(true);
                         }
                    });
                }
          /*      Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(intent);*/

                }else{
                    commonClass.showError(LoginActivity.this,"Please Make Sure Internet Available");
                }
                break;
        }
    }

    private void callAlertDialog(String username) {
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(this);
        View view= LayoutInflater.from(this).inflate(R.layout.welcome_popup,null);
        TextView userma = view.findViewById(R.id.username);
        userma.setText("Welcome "+username+"!!!");

        builder.setView(view);
        final AlertDialog mDialog = builder.create();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
             finishAndRemoveTask();
            System.exit(0);
            moveTaskToBack(true);

    }
}