package me.aflak.libraries;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Omar on 02/07/2017.
 */

public class FingerprintDialog extends AnimatedDialog<FingerprintDialog> {
    private FingerprintManager fingerprintManager;
    private CancellationSignal cancellationSignal;
    private FingerprintCallback fingerprintCallback;
    private FingerprintSecureCallback fingerprintSecureCallback;
    private FailAuthCounterCallback counterCallback;
    private FingerprintManager.CryptoObject cryptoObject;
    private CipherHelper cipherHelper;
    private Handler handler;

    private long delayAfterSuccess, delayAfterError;
    private int limit, tryCounter;
    private int successColor, errorColor;

    private final static String TAG = "FingerprintDialog";

    private FingerprintDialog(Context context, FingerprintManager fingerprintManager){
        super(context);
        this.fingerprintManager = fingerprintManager;
        init();
    }

    private FingerprintDialog(Context context){
        super(context);
        this.fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        init();
    }

    private void init(){
        this.cipherHelper = null;
        this.handler = new Handler();
        this.fingerprintCallback = null;
        this.fingerprintSecureCallback = null;
        this.counterCallback = null;
        this.successColor = R.color.fingerprint_auth_success;
        this.errorColor = R.color.fingerprint_auth_failed;
        this.delayAfterSuccess = 1200;
        this.delayAfterError = 1200;
        this.cryptoObject = null;
        this.tryCounter = 0;
    }

    public static boolean isAvailable(Context context){
        FingerprintManager manager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        return (manager!=null && manager.isHardwareDetected() && manager.hasEnrolledFingerprints());
    }

    public static FingerprintDialog initialize(Context context, FingerprintManager fingerprintManager){
        return new FingerprintDialog(context, fingerprintManager);
    }

    public static FingerprintDialog initialize(Context context){
        return new FingerprintDialog(context);
    }

    public FingerprintDialog callback(FingerprintCallback fingerprintCallback){
        this.fingerprintCallback = fingerprintCallback;
        return this;
    }

    public FingerprintDialog callback(FingerprintSecureCallback fingerprintSecureCallback, String KEY_NAME){
        this.fingerprintSecureCallback = fingerprintSecureCallback;
        this.cipherHelper = new CipherHelper(KEY_NAME);
        return this;
    }

    public FingerprintDialog cryptoObject(FingerprintManager.CryptoObject cryptoObject){
        this.cryptoObject = cryptoObject;
        return this;
    }

    public FingerprintDialog successColor(int successColor){
        this.successColor = successColor;
        return this;
    }

    public FingerprintDialog errorColor(int errorColor){
        this.errorColor = errorColor;
        return this;
    }

    public FingerprintDialog delayAfterSuccess(long delayAfterSuccess){
        this.delayAfterSuccess = delayAfterSuccess;
        return this;
    }

    public FingerprintDialog delayAfterError(long delayAfterError){
        this.delayAfterError = delayAfterError;
        return this;
    }

    public FingerprintDialog tryLimit(int limit, FailAuthCounterCallback counterCallback){
        this.limit = limit;
        this.counterCallback = counterCallback;
        return this;
    }

    public FingerprintDialog show(){
        if(title==null || message==null){
            throw new RuntimeException("Title or message cannot be null.");
        }

        if(fingerprintSecureCallback!=null){
            if(cryptoObject!=null){
                throw new RuntimeException("If you specify a CryptoObject you have to use FingerprintCallback");
            }
            showSecure();
        }
        else if(fingerprintCallback!=null){
            showDialog();
        }
        else{
            throw new RuntimeException("You must specify a callback.");
        }

        return this;
    }

    private void showSecure(){
        this.cryptoObject = cipherHelper.getEncryptionCryptoObject();
        if(cryptoObject==null) {
            fingerprintSecureCallback.onNewFingerprintEnrolled(new FingerprintToken(cipherHelper, FingerprintDialog.this));
        }
        else{
            showDialog();
        }
    }

    private void showDialog(){
        view = layoutInflater.inflate(R.layout.fingerprint_dialog, null);
        ((TextView) view.findViewById(R.id.fingerprint_dialog_title)).setText(title);
        ((TextView) view.findViewById(R.id.fingerprint_dialog_message)).setText(message);

        builder.setView(view);
        builder.setPositiveButton(R.string.fingerprint_cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cancellationSignal.cancel();
                if(fingerprintSecureCallback!=null){
                    fingerprintSecureCallback.onAuthenticationCancel();
                }
                else{
                    fingerprintCallback.onAuthenticationCancel();
                }
            }
        });

        dialog = builder.create();
        if(dialog.getWindow() != null) {
            if(enterAnimation!=DialogAnimation.Enter.APPEAR || exitAnimation!=DialogAnimation.Exit.DISAPPEAR) {
                int style = DialogAnimation.getStyle(enterAnimation, exitAnimation);
                if (style != -1) {
                    dialog.getWindow().getAttributes().windowAnimations = style;
                } else {
                    Log.w(TAG, "The animation selected is not available. Default animation will be used.");
                }
            }

            if(!dimBackground){
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }
        }
        dialog.setCanceledOnTouchOutside(cancelOnTouchOutside);
        dialog.setCancelable(cancelOnPressBack);
        dialog.show();

        auth();
    }

    private void auth(){
        cancellationSignal = new CancellationSignal();
        if(fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints()) {
            fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    setStatus(errString.toString(), errorColor, errorColor, R.drawable.fingerprint_error);
                    returnToScanningStatus();
                }

                @Override
                public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                    super.onAuthenticationHelp(helpCode, helpString);
                    setStatus(helpString.toString(), errorColor, errorColor, R.drawable.fingerprint_error);
                    returnToScanningStatus();
                }

                @Override
                public void onAuthenticationSucceeded(final FingerprintManager.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    handler.removeCallbacks(returnToScanning);
                    setStatus(R.string.fingerprint_state_success, successColor, successColor, R.drawable.fingerprint_success);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.cancel();
                            if(fingerprintSecureCallback!=null){
                                fingerprintSecureCallback.onAuthenticationSuccess();
                            }
                            else{
                                fingerprintCallback.onAuthenticationSuccess();
                            }
                        }
                    }, delayAfterSuccess);
                    tryCounter = 0;
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    setStatus(R.string.fingerprint_state_failure, errorColor, errorColor, R.drawable.fingerprint_error);
                    returnToScanningStatus();
                    tryCounter++;
                    if(counterCallback!=null && tryCounter==limit){
                        counterCallback.onTryLimitReached();
                    }
                }
            }, null);
        }
        else{
            Log.e(TAG, "Fingerprint scanner not detected or no fingerprint enrolled. Use FingerprintDialog#isAvailable(Context) before.");
        }
    }

    private void setStatus(int textId, int circleColor, int textColor, int drawable){
        setStatus(context.getResources().getString(textId), circleColor, textColor, drawable);
    }

    private void setStatus(String text, int circleColor, int textColor, int drawable){
        ImageView foreground = view.findViewById(R.id.fingerprint_dialog_icon_foreground);
        View background = view.findViewById(R.id.fingerprint_dialog_icon_background);
        TextView status = view.findViewById(R.id.fingerprint_dialog_status);

        foreground.setImageResource(drawable);
        background.setBackgroundTintList(ColorStateList.valueOf(context.getColor(circleColor)));
        status.setTextColor(ResourcesCompat.getColor(context.getResources(), textColor, null));
        status.setText(text);
    }

    private void returnToScanningStatus() {
        handler.postDelayed(returnToScanning, delayAfterError);
    }

    private Runnable returnToScanning = new Runnable() {
        @Override
        public void run() {
            setStatus(R.string.fingerprint_state_scanning, R.color.fingerprint_circle, R.color.fingerprint_auth_scanning, R.drawable.fingerprint);
        }
    };
}