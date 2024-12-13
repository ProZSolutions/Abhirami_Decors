package in.proz.adamd.Biometric;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
 import com.tuyenmonkey.mkloader.MKLoader;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.Executor;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


import in.proz.adamd.BuildConfig;
import in.proz.adamd.DashboardNewActivity;
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

public class BiometricActivity  extends AppCompatActivity {
    ImageView finderImage,back_arrow ;
    TextView submit;
    String message=null;

    private KeyStore keyStore;
    MKLoader loader;
    CommonClass commonClass= new CommonClass();
    // Defining variable for storing
    // key in android keystore container
    private static final String KEY_NAME = "PROZAPMS";
    private Cipher cipher;

    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        BackArrowPress();
    }
    public  void  BackArrowPress(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_id);
        back_arrow= findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackArrowPress();
            }
        });
        loader = findViewById(R.id.loader);
        finderImage = findViewById(R.id.finderImage);
        submit= findViewById(R.id.submit);

            BiometricManager biometricManager = BiometricManager.from(this);
            switch (biometricManager.canAuthenticate()) {
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                    message = "No Biometric Hardware in your device";
                    commonClass.showError(BiometricActivity.this,"No Biometric Hardware in your device");
                   // Toast.makeText(getApplicationContext(), " No bio hardware ", Toast.LENGTH_SHORT).show();
                    break;
                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                    message = "Biometric Not Working";
                    commonClass.showError(BiometricActivity.this,"Biometric Not Working");

                   // Toast.makeText(getApplicationContext(), " Not working ", Toast.LENGTH_SHORT).show();
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                    message = "Fingerprint No Enrolled in Your Device.Please Add One Fingerprint and Login";
                    commonClass.showError(BiometricActivity.this,"Fingerprint No Enrolled in Your Device.Please Add One Fingerprint and Login");
                     break;



            }

        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(BiometricActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                commonClass.showError(BiometricActivity.this,"Failed to Authendicate");
                //Toast.makeText(getApplicationContext(),"Failed to authendicate",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                commonClass.showSuccess(BiometricActivity.this,"Authentication Success");
                //Toast.makeText(getApplicationContext(), " Authentication success  ", Toast.LENGTH_SHORT).show();
                callLogin();
                try {
                    // Access crypto object from the authentication result
                    BiometricPrompt.CryptoObject cryptoObject = result.getCryptoObject();
                    Log.d("promtinfo"," object as "+cryptoObject);
                    if (cryptoObject != null) {
                        // Handle crypto object, perform cryptographic operations, or access biometric data
                        Cipher cipher = cryptoObject.getCipher();
                        Log.d("promtinfo"," cipher "+cipher);
                        // Use the cipher to perform cryptographic operations or access biometric data
                    }
                } catch (Exception e) {
                    Log.d("promtinfo","exception "+e.getMessage());
                    // Handle exception
                }


            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });
        promptInfo= new BiometricPrompt.PromptInfo.Builder().setTitle("Login ")
                .setDescription("Use Fingerpint to login ").setDeviceCredentialAllowed(true).build();
       /* finderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(message)){
                    generateKey();
                    if (cipherInit()) {
                        biometricPrompt.authenticate(promptInfo);
                    }
                }else{
                    commonClass.showError(BiometricActivity.this,message);
                }
            }
        });
*/
        if(TextUtils.isEmpty(message)){
            generateKey();
            if (cipherInit()) {
                biometricPrompt.authenticate(promptInfo);
            }
        }else{
            commonClass.showError(BiometricActivity.this,message);
        }


    }

    private void callLogin() {
        loader.setVisibility(View.VISIBLE);
        String versionName = BuildConfig.VERSION_NAME;

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<CommonPojo> call = apiInterface.callFingerAuth(
                commonClass.getDeviceID(BiometricActivity.this),versionName);
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
                                +response.body().getRole());

                        commonClass.putSharedPref(getApplicationContext(),"AdminEmpNo",null);
                        commonClass.putSharedPref(getApplicationContext(),"AdminName",null);
                        commonClass.putSharedPref(getApplicationContext(),"AdminRole",null);
                        commonClass.putSharedPref(getApplicationContext(), "AdminRoleName", null);
                        commonClass.putSharedPref(BiometricActivity.this,"EmppName",null);
                        commonClass.putSharedPref(BiometricActivity.this,"username",null);
                        commonClass.putSharedPref(BiometricActivity.this,"token",null);

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
                        commonClass.putSharedPref(BiometricActivity.this,"EmppName",response.body().getName());
                         commonClass.putSharedPref(BiometricActivity.this,"token",response.body().getToken_type()+" "+response.body().getBearer_token());
                        //commonClass.showSuccess(LoginActivity.this,"Logged In Successfully");
                        if(!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(),"first_time"))){
                            commonClass.showSuccess(BiometricActivity.this,"Logged In Successfully");
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    Intent intent = new Intent(getApplicationContext(), DashboardNewActivity.class);
                                    startActivity(intent);
                                }
                            }, 2000);
                        }else{
                            commonClass.putSharedPref(getApplicationContext(),"first_time","fist");
                            Log.d("LoginPage"," biometric name "+response.body().getName());
                            callAlertDialog(response.body().getName());

                        }


                    }else{
                        Log.d("login_url"," response "+response.body().getStatus());

                        commonClass.showError(BiometricActivity.this,response.body().getStatus());
                    }
                }else{
                    Gson gson = new GsonBuilder().create();
                    CommonPojo mError = new CommonPojo();
                    try {
                        mError = gson.fromJson(response.errorBody().string(), CommonPojo.class);

                        commonClass.showError(BiometricActivity.this,mError.getError());
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


    @TargetApi(Build.VERSION_CODES.M)
    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            Log.d("prombtInfo","generate key  "+keyStore);
        } catch (Exception e) {
            Log.d("prombtInfo","generate error "+e.getMessage());
            e.printStackTrace();
        }


        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            Log.d("prombtInfo","key generator ");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            Log.d("prombtInfo","no provider "+e.getMessage());
            throw new RuntimeException("KeyGenerator instance failed", e);
        }

        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
            Log.d("prombtInfo","keystore generator ");
        } catch (NoSuchAlgorithmException |
                 InvalidAlgorithmParameterException
                 | CertificateException | IOException e) {
            Log.d("prombtInfo","keystore error "+e.getMessage());
            throw new RuntimeException(e);
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            Log.d("prombtInfo"," cioher init "+cipher);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            Log.d("prombtInfo"," no such algm "+e.getMessage());
            throw new RuntimeException("Cipher failed", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            Log.d("prombtInfo","cipher  key "+key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            Log.d("prombtInfo"," error1 "+e.getMessage());
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException |
                 InvalidKeyException e) {
            Log.d("prombtInfo"," error2 "+e.getMessage());
            throw new RuntimeException("Cipher initialization failed", e);
        }
    }
}
