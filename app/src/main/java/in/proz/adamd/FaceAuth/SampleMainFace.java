package in.proz.adamd.FaceAuth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

import in.proz.adamd.R;
import in.proz.adamd.Retrofit.CommonClass;

public class SampleMainFace extends AppCompatActivity {
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
       @Override
    protected void onCreate(Bundle savedInstanceState) {
           super.onCreate(savedInstanceState);
           setContentView(R.layout.faceauth_sample);

           executor = ContextCompat.getMainExecutor(this);

           // Create BiometricPrompt instance
           biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
               @Override
               public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                   super.onAuthenticationError(errorCode, errString);
                   // Handle authentication error
               }

               @Override
               public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                   super.onAuthenticationSucceeded(result);
                   // Authentication succeeded, unlock your app
                   unlockApp();
               }

               @Override
               public void onAuthenticationFailed() {
                   super.onAuthenticationFailed();
                   // Handle authentication failure
               }
           });

           // Set prompt info
           promptInfo = new BiometricPrompt.PromptInfo.Builder()
                   .setTitle("Face authentication required")
                   .setSubtitle("Please authenticate to unlock the app")
                   .setNegativeButtonText("Cancel")
                   .build();

           // Prompt for biometric authentication
           biometricPrompt.authenticate(promptInfo);
       }
    private void unlockApp() {
        // Add logic to unlock your app after successful authentication
        // For example, navigate to the main activity of your app
        // Optionally finish the authentication activity
        CommonClass commonClass = new CommonClass();
        commonClass.showSuccess(SampleMainFace.this,"Authentication Success");
    }
}
