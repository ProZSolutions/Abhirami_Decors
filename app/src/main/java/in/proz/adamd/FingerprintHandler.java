package in.proz.adamd;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;

    // Constructor
    public FingerprintHandler(Context mContext) {
        context = mContext;
    }

    // Fingerprint authentication starts here..
    public void Authentication(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        Log.d("BiometricData"," Authentication ");
        CancellationSignal cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    // On authentication failed
    @Override
    public void onAuthenticationFailed() {
        this.update("Authentication Failed!!!", false);
    }

    // On successful authentication
    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        this.update("Successfully Authenticated...", true);
        try {
            Log.d("BiometricData"," encoded cipher "+result.getCryptoObject().getCipher().getParameters().getEncoded());
            Log.d("BiometricData"," cipher algm "+result.getCryptoObject().getCipher().getParameters().getAlgorithm());
            Log.d("BiometricData"," cipher provider "+result.getCryptoObject().getCipher().getParameters().getProvider());


            Log.d("BiometricData"," encoded sign "+result.getCryptoObject().getSignature().getParameters().getEncoded());
            Log.d("BiometricData"," sign algm "+result.getCryptoObject().getSignature().getParameters().getAlgorithm());
            Log.d("BiometricData"," sign provider "+result.getCryptoObject().getSignature().getParameters().getProvider());
        }catch (Exception e){

        }
     }

    // This method is used to update the text message
    // depending on the authentication result
    public void update(String e, Boolean success){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            final VibrationEffect vibrationEffect5;
            final Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

            // create vibrator effect with the constant EFFECT_HEAVY_CLICK
            vibrationEffect5 = VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK);

            // it is safe to cancel other vibrations currently taking place
            vibrator.cancel();

            vibrator.vibrate(vibrationEffect5);
        }
        Animation a = AnimationUtils.loadAnimation(context, R.anim.pulse_animation1);
        TextView textView = (TextView) ((Activity)context).findViewById(R.id.errorText);
        textView.startAnimation(a);
        textView.setText(e);
        if(success){
            textView.setTextColor(ContextCompat.getColor(context,R.color.black));
        }
    }
}
