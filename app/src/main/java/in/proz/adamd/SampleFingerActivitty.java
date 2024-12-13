package in.proz.adamd;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
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

public class SampleFingerActivitty extends AppCompatActivity {
    private KeyStore keyStore;
    // Defining variable for storing
    // key in android keystore container
    private static final String KEY_NAME = "PROZAPMS";
    String TAG="BiometricData";
    private Cipher cipher;
    private TextView errorText;
    ImageView finderImage ;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_id);
        Log.d(TAG,"oncreate");
        finderImage= findViewById(R.id.finderImage);


        // Initializing KeyguardManager and FingerprintManager
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        // Initializing our error text
        errorText = (TextView) findViewById(R.id.errorText);

        // Here, we are using various security checks
        // Checking device is inbuilt with fingerprint sensor or not
        if(!fingerprintManager.isHardwareDetected()){
            Log.d(TAG,"hardware not detected");
            // Setting error message if device
            // doesn't have fingerprint sensor
            errorText.setText("Device does not support fingerprint sensor");
        }else {
            // Checking fingerprint permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG,"fingerprint Authentication not enabled");
                errorText.setText("Fingerprint authentication is not enabled");
            }else{
                // Check for at least one registered finger
                if (!fingerprintManager.hasEnrolledFingerprints()) {
                    Log.d(TAG,"no finger registered before ");
                    errorText.setText("Register at least one finger");
                }else{
                    // Checking for screen lock security
                    if (!keyguardManager.isKeyguardSecure()) {
                        Log.d(TAG,"screen lock security not ");
                        errorText.setText("Screen lock security not enabled");
                    }else{

                        // if everything is enabled and correct then we will generate
                        // the encryption key which will be stored on the device
                        finderImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                generateKey();
                                if (cipherInit()) {
                                    Log.d(TAG,"cipher init ");
                                    FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                                    FingerprintHandler helper = new FingerprintHandler(SampleFingerActivitty.this);
                                    helper.Authentication(fingerprintManager, cryptoObject);
                                }
                            }
                        });

                    }
                }
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            Log.d(TAG,"generate key  "+keyStore);
        } catch (Exception e) {
            Log.d(TAG,"generate error "+e.getMessage());
            e.printStackTrace();
        }


        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            Log.d(TAG,"key generator ");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            Log.d(TAG,"no provider "+e.getMessage());
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
            Log.d(TAG,"keystore generator ");
        } catch (NoSuchAlgorithmException |
                 InvalidAlgorithmParameterException
                 | CertificateException | IOException e) {
            Log.d(TAG,"keystore error "+e.getMessage());
            throw new RuntimeException(e);
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            Log.d(TAG," cioher init "+cipher);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            Log.d(TAG," no such algm "+e.getMessage());
            throw new RuntimeException("Cipher failed", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            Log.d(TAG,"cipher  key "+key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            Log.d(TAG," error1 "+e.getMessage());
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException |
                 InvalidKeyException e) {
            Log.d(TAG," error2 "+e.getMessage());
            throw new RuntimeException("Cipher initialization failed", e);
        }
    }
}