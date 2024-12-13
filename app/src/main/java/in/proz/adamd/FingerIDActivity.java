package in.proz.adamd;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class FingerIDActivity extends AppCompatActivity implements View.OnClickListener {
    RelativeLayout outer_layout_1;
    ImageView back_arrow;
    private static final String KEY_NAME = "KeyName";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String FORWARD_SLASH = "/";
    private TextView submit;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_id);
        outer_layout_1 = findViewById(R.id.outer_layout_1);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom);
        outer_layout_1.startAnimation(animation);
        animation.setRepeatCount(Animation.INFINITE);
        initView();
        submit = findViewById(R.id.submit);
         outer_layout_1.setOnClickListener((view) -> onTouchIdClick());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            displayBiometricButton();
        }

    }
    private void onTouchIdClick() {
        getBiometricPromptHandler().authenticate(getBiometricPrompt(), new BiometricPrompt.CryptoObject(getCipher()));
        // Please see the below mentioned note section.
         getBiometricPromptHandler().authenticate(getBiometricPrompt());
    }
  /*  private boolean isBiometricCompatibleDevice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (getBiometricManager().canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
                return true;
            } else {
                return false;
            }
        }
    }*/
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void displayBiometricButton() {
        /*if (isBiometricCompatibleDevice()) {
            submit.setEnabled(false);
        } else {*/
            submit.setEnabled(true);
            generateSecretKey();
        //}
    }
  /*  private BiometricManager getBiometricManager() {
        return BiometricManager.BIOMETRIC_SUCCESS;
    }*/
    private void initView() {
        submit = findViewById(R.id.submit);
          back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.back_arrow:
                callLogin();
                break;
        }
    }


    private void callLogin() {
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        callLogin();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void generateSecretKey() {
        KeyGenerator keyGenerator = null;
        KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(
                KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(true)
                .setInvalidatedByBiometricEnrollment(false)
                .build();
        try {
            keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        if (keyGenerator != null) {
            try {
                keyGenerator.init(keyGenParameterSpec);
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }
            keyGenerator.generateKey();
        }
    }

    private SecretKey getSecretKey() {
        KeyStore keyStore = null;
        Key secretKey = null;
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        if (keyStore != null) {
            try {
                keyStore.load(null);
            } catch (CertificateException | IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            try {
                secretKey = keyStore.getKey(KEY_NAME, null);
            } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
                e.printStackTrace();
            }
        }
        return (SecretKey) secretKey;
    }

    private Cipher getCipher() {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + FORWARD_SLASH
                    + KeyProperties.BLOCK_MODE_CBC + FORWARD_SLASH
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            try {
                cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return cipher;
    }

    private BiometricPrompt.PromptInfo getBiometricPrompt() {
        return new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Login with your biometric credential")
                .setNegativeButtonText("cancel")
                .setConfirmationRequired(false)
                .build();
    }

    private void onBiometricSuccess() {
        //Call the respective API on biometric success
        //callLoginApi("userName", "password");
        submit.setText("BioMetric Success");
    }

    private BiometricPrompt getBiometricPromptHandler() {
        return new BiometricPrompt(this, ContextCompat.getMainExecutor(this),
                new BiometricPrompt.AuthenticationCallback() {

                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        submit.setText("BioMetric Failure");
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        onBiometricSuccess();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                    }
                }
        );
    }

}
