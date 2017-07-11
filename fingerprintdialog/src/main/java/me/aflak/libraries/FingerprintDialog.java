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
    private KeyStoreHelper keyStoreHelper;

    private LayoutInflater layoutInflater;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private View view;

    private String title, message;

    private boolean cancelOnTouchOutside, cancelOnPressBack, dimBackground;
    private int enterAnimation, exitAnimation, successColor, errorColor, delayAfterSuccess;
    private int limit, tryCounter;

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
        this.keyStoreHelper = new KeyStoreHelper(KEY_NAME);
        this.layoutInflater = LayoutInflater.from(context);
        this.builder = new AlertDialog.Builder(context);
        this.fingerprintCallback = null;
        this.fingerprintSecureCallback = null;
        this.counterCallback = null;
        this.successColor = R.color.fingerprint_auth_success;
        this.errorColor = R.color.fingerprint_auth_failed;
        this.delayAfterSuccess = 1200;
        this.cancelOnTouchOutside = false;
        this.cancelOnPressBack = false;
        this.dimBackground = true;
        this.enterAnimation = DialogAnimation.NO_ANIMATION;
        this.exitAnimation = DialogAnimation.NO_ANIMATION;
        this.cryptoObject = null;
        this.tryCounter = 0;
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
        this.fingerprintSecureCallback = fingerprintSecureCallback;
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

    public FingerprintDialog delayAfterSuccess(int delayAfterSuccess){
        this.delayAfterSuccess = delayAfterSuccess;
        return this;
    }

    public FingerprintDialog enterAnimation(int enterAnimation){
        this.enterAnimation = enterAnimation;
        return this;
    }

    public FingerprintDialog exitAnimation(int exitAnimation){
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

    public FingerprintDialog show(){
        if(title==null || message==null){
            throw new RuntimeException("Title or message cannot be null.");
        }
        else if(fingerprintCallback!=null){
            this.cryptoObject = null;
            showDialog();
        }
        else if(fingerprintSecureCallback!=null){
            showSecure();
        }
        return this;
    }

    private void showSecure(){
        keyStoreHelper.getCryptoObject(new KeyStoreHelperCallback() {
            @Override
            public void onNewFingerprintEnrolled() {
                if(fingerprintSecureCallback!=null){
                    fingerprintSecureCallback.onNewFingerprintEnrolled(new FingerprintToken(keyStoreHelper, FingerprintDialog.this));
                }
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
                if(fingerprintCallback!=null) {
                    fingerprintCallback.onAuthenticationCancel();
                }
            }
        });
        builder.setView(view);
        dialog = builder.create();
        if(dialog.getWindow() != null) {
            if(enterAnimation!=DialogAnimation.NO_ANIMATION || exitAnimation!=DialogAnimation.NO_ANIMATION) {
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
        if(fingerprintManager.isHardwareDetected()) {
            if (fingerprintManager.hasEnrolledFingerprints()) {
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
                    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        setStatus(R.string.fingerprint_state_success, successColor, successColor, R.drawable.fingerprint_success);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.cancel();
                                if (fingerprintCallback != null) {
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
                Log.e(TAG,"No fingerprint enrolled. Use hasEnrolledFingerprints() before showing the fingerprint_dialog.");
            }
        }
        else{
            Log.e(TAG, "No fingerprint scanner detected. Use isHardwareDetected() before showing the fingerprint_dialog.");
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setStatus(R.string.fingerprint_state_scanning, R.color.fingerprint_circle, R.color.fingerprint_auth_scanning, R.drawable.fingerprint);
            }
        }, 1200);
    }
}