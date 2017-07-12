package me.aflak.libraries;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import javax.crypto.Cipher;

/**
 * Created by Omar on 02/07/2017.
 */

public class FingerprintDialog {
    private Context context;
    private FingerprintManager fingerprintManager;
    private CancellationSignal cancellationSignal;
    private FingerprintCallback fingerprintCallback;
    private FingerprintSecureCallback fingerprintSecureCallback;
    private FailAuthCounterCallback counterCallback;
    private FingerprintManager.CryptoObject cryptoObject;
    private CryptoObjectHelper cryptoObjectHelper;
    private Handler handler;

    private LayoutInflater layoutInflater;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private View view;

    private String title, message;
    private long delayAfterSuccess, delayAfterError;
    private CryptoObjectHelper.Type cryptoObjectType;
    private DialogAnimation.Enter enterAnimation;
    private DialogAnimation.Exit exitAnimation;
    private int cryptoObjectMode;
    private int limit, tryCounter;
    private int successColor, errorColor;
    private boolean cancelOnTouchOutside, cancelOnPressBack, dimBackground;

    private final static String TAG = "FingerprintDialog";

    private FingerprintDialog(Context context, FingerprintManager fingerprintManager, String KEY_NAME){
        this.context = context;
        this.fingerprintManager = fingerprintManager;
        init(KEY_NAME);
    }

    private FingerprintDialog(Context context, String KEY_NAME){
        this.context = context;
        this.fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        init(KEY_NAME);
    }

    private void init(String KEY_NAME){
        this.cryptoObjectHelper = new CryptoObjectHelper(KEY_NAME);
        this.layoutInflater = LayoutInflater.from(context);
        this.builder = new AlertDialog.Builder(context);
        this.handler = new Handler();
        this.fingerprintCallback = null;
        this.fingerprintSecureCallback = null;
        this.counterCallback = null;
        this.successColor = R.color.fingerprint_auth_success;
        this.errorColor = R.color.fingerprint_auth_failed;
        this.delayAfterSuccess = 1200;
        this.delayAfterError = 1200;
        this.cancelOnTouchOutside = false;
        this.cancelOnPressBack = false;
        this.dimBackground = true;
        this.enterAnimation = DialogAnimation.Enter.APPEAR;
        this.exitAnimation = DialogAnimation.Exit.DISAPPEAR;
        this.cryptoObject = null;
        this.tryCounter = 0;
    }

    public static boolean isAvailable(Context context){
        FingerprintManager manager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        return (manager.isHardwareDetected() && manager.hasEnrolledFingerprints());
    }

    public static FingerprintDialog initialize(Context context, FingerprintManager fingerprintManager, String KEY_NAME){
        return new FingerprintDialog(context, fingerprintManager, KEY_NAME);
    }

    public static FingerprintDialog initialize(Context context, String KEY_NAME){
        return new FingerprintDialog(context, KEY_NAME);
    }

    public FingerprintDialog title(String title){
        this.title = title;
        return this;
    }

    public FingerprintDialog message(String message){
        this.message = message;
        return this;
    }

    public FingerprintDialog title(int resTitle){
        this.title = context.getResources().getString(resTitle);
        return this;
    }

    public FingerprintDialog message(int resMessage){
        this.message = context.getResources().getString(resMessage);
        return this;
    }

    public FingerprintDialog callback(FingerprintCallback fingerprintCallback){
        this.fingerprintCallback = fingerprintCallback;
        return this;
    }

    public FingerprintDialog callback(FingerprintSecureCallback fingerprintSecureCallback){
        return callback(fingerprintSecureCallback, CryptoObjectHelper.Type.CIPHER, Cipher.ENCRYPT_MODE);
    }

    public FingerprintDialog callback(FingerprintSecureCallback fingerprintSecureCallback, CryptoObjectHelper.Type cryptoObjectType, int cryptoObjectMode){
        this.fingerprintSecureCallback = fingerprintSecureCallback;
        this.cryptoObjectType = cryptoObjectType;
        this.cryptoObjectMode = cryptoObjectMode;
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

    public FingerprintDialog enterAnimation(DialogAnimation.Enter enterAnimation){
        this.enterAnimation = enterAnimation;
        return this;
    }

    public FingerprintDialog exitAnimation(DialogAnimation.Exit exitAnimation){
        this.exitAnimation = exitAnimation;
        return this;
    }

    public FingerprintDialog tryLimit(int limit, FailAuthCounterCallback counterCallback){
        this.limit = limit;
        this.counterCallback = counterCallback;
        return this;
    }

    public FingerprintDialog cancelOnTouchOutside(boolean cancelOnTouchOutside) {
        this.cancelOnTouchOutside = cancelOnTouchOutside;
        return this;
    }

    public FingerprintDialog cancelOnPressBack(boolean cancelOnPressBack){
        this.cancelOnPressBack = cancelOnPressBack;
        return this;
    }

    public FingerprintDialog dimBackground(boolean dimBackground){
        this.dimBackground = dimBackground;
        return this;
    }

    public FingerprintDialog cryptoObject(FingerprintManager.CryptoObject cryptoObject){
        this.cryptoObject = cryptoObject;
        return this;
    }

    public FingerprintDialog show(){
        if(title==null || message==null){
            throw new RuntimeException("Title or message cannot be null.");
        }

        if(fingerprintSecureCallback!=null){
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
        cryptoObjectHelper.getCryptoObject(cryptoObjectType, cryptoObjectMode, new CryptoObjectHelperCallback() {
            @Override
            public void onNewFingerprintEnrolled() {
                fingerprintSecureCallback.onNewFingerprintEnrolled(new FingerprintToken(cryptoObjectHelper, FingerprintDialog.this));
            }

            @Override
            public void onCryptoObjectRetrieved(FingerprintManager.CryptoObject cryptoObject) {
                FingerprintDialog.this.cryptoObject = cryptoObject;
                showDialog();
            }
        });
    }

    private void showDialog(){
        view = layoutInflater.inflate(R.layout.fingerprint_dialog, null);
        ((TextView) view.findViewById(R.id.fingerprint_dialog_title)).setText(title);
        ((TextView) view.findViewById(R.id.fingerprint_dialog_message)).setText(message);
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
        builder.setView(view);
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
        else{
            Log.w(TAG, "Could not get window from dialog");
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
                                fingerprintSecureCallback.onAuthenticationSuccess(result.getCryptoObject());
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