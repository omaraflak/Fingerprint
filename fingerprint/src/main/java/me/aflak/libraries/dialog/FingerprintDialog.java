package me.aflak.libraries.dialog;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import me.aflak.libraries.callback.FailAuthCounterCallback;
import me.aflak.libraries.callback.FailAuthCounterDialogCallback;
import me.aflak.libraries.callback.FingerprintCallback;
import me.aflak.libraries.callback.FingerprintDialogCallback;
import me.aflak.libraries.callback.FingerprintDialogSecureCallback;
import me.aflak.libraries.R;
import me.aflak.libraries.callback.FingerprintSecureCallback;
import me.aflak.libraries.view.Fingerprint;
import me.aflak.libraries.utils.FingerprintToken;

/**
 * Created by Omar on 02/07/2017.
 */

public class FingerprintDialog extends AnimatedDialog<FingerprintDialog> {
    private Fingerprint fingerprint;
    private TextView dialogTitle, dialogMessage, dialogStatus;
    private AppCompatButton cancelButton, usePasswordButton;

    private FingerprintDialogCallback fingerprintDialogCallback;
    private FingerprintDialogSecureCallback fingerprintDialogSecureCallback;

    private int statusScanningColor, statusSuccessColor, statusErrorColor;
    private View.OnClickListener onUsePassword;
    private Handler handler;

    private int delayAfterError, delayAfterSuccess;

    private final static String TAG = "FingerprintDialog";

    private FingerprintDialog(Context context){
        super(context);
        init();
    }

    private void init(){
        this.handler = new Handler();
        this.onUsePassword = null;
        this.delayAfterError = Fingerprint.DEFAULT_DELAY_AFTER_ERROR;
        this.delayAfterSuccess = Fingerprint.DEFAULT_DELAY_AFTER_ERROR;

        this.statusScanningColor = R.color.status_scanning;
        this.statusSuccessColor = R.color.status_success;
        this.statusErrorColor = R.color.status_error;

        this.dialogView = layoutInflater.inflate(R.layout.fingerprint_dialog, null);
        this.fingerprint = dialogView.findViewById(R.id.fingerprint_dialog_fp);
        this.dialogTitle = dialogView.findViewById(R.id.fingerprint_dialog_title);
        this.dialogMessage = dialogView.findViewById(R.id.fingerprint_dialog_message);
        this.dialogStatus = dialogView.findViewById(R.id.fingerprint_dialog_status);
        this.cancelButton = dialogView.findViewById(R.id.fingerprint_dialog_cancel);
        this.usePasswordButton = dialogView.findViewById(R.id.fingerprint_dialog_use_password);
    }

    /**
     * Check if a fingerprint scanner is available and if at least one finger is enrolled in the phone.
     * @param context A context
     * @return True is authentication is available, False otherwise
     */
    public static boolean isAvailable(Context context){
        FingerprintManager manager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        return (manager!=null && manager.isHardwareDetected() && manager.hasEnrolledFingerprints());
    }

    /**
     * Create a FingerprintDialog instance.
     * @param context Activity Context
     * @return FingerprintDialog instance
     */
    public static FingerprintDialog initialize(Context context){
        return new FingerprintDialog(context);
    }

    /**
     * Set an authentication callback.
     * @param fingerprintDialogCallback The callback
     * @return FingerprintDialog object
     */
    public FingerprintDialog callback(FingerprintDialogCallback fingerprintDialogCallback){
        this.fingerprintDialogCallback = fingerprintDialogCallback;
        this.fingerprint.callback(fingerprintCallback);
        return this;
    }

    /**
     * Set a callback for secured authentication.
     * @param fingerprintDialogSecureCallback The callback
     * @param KEY_NAME An arbitrary string used to create a cipher pair in the Android KeyStore
     * @return FingerprintDialog object
     */
    public FingerprintDialog callback(FingerprintDialogSecureCallback fingerprintDialogSecureCallback, String KEY_NAME){
        this.fingerprintDialogSecureCallback = fingerprintDialogSecureCallback;
        this.fingerprint.callback(fingerprintSecureCallback, KEY_NAME);
        return this;
    }

    /**
     * Perform a secured authentication using that particular CryptoObject.
     * @param cryptoObject CryptoObject to use
     * @return FingerprintDialog object
     */
    public FingerprintDialog cryptoObject(FingerprintManager.CryptoObject cryptoObject){
        this.fingerprint.cryptoObject(cryptoObject);
        return this;
    }

    /**
     * Set color of the fingerprint scanning status.
     * @param fingerprintScanningColor resource color
     * @return FingerprintDialog object
     */
    public FingerprintDialog fingerprintScanningColor(int fingerprintScanningColor){
        this.fingerprint.fingerprintScanningColor(fingerprintScanningColor);
        return this;
    }

    /**
     * Set color of the fingerprint success status.
     * @param fingerprintSuccessColor resource color
     * @return FingerprintDialog object
     */
    public FingerprintDialog fingerprintSuccessColor(int fingerprintSuccessColor){
        this.fingerprint.fingerprintSuccessColor(fingerprintSuccessColor);
        return this;
    }

    /**
     * Set color of the fingerprint error status.
     * @param fingerprintErrorColor resource color
     * @return FingerprintDialog object
     */
    public FingerprintDialog fingerprintErrorColor(int fingerprintErrorColor){
        this.fingerprint.fingerprintErrorColor(fingerprintErrorColor);
        return this;
    }

    /**
     * Set color of the circle scanning status.
     * @param circleScanningColor resource color
     * @return FingerprintDialog object
     */
    public FingerprintDialog circleScanningColor(int circleScanningColor){
        this.fingerprint.circleScanningColor(circleScanningColor);
        return this;
    }

    /**
     * Set color of the circle success status.
     * @param circleSuccessColor resource color
     * @return FingerprintDialog object
     */
    public FingerprintDialog circleSuccessColor(int circleSuccessColor){
        this.fingerprint.circleSuccessColor(circleSuccessColor);
        return this;
    }

    /**
     * Set color of the circle error status.
     * @param circleErrorColor resource color
     * @return FingerprintDialog object
     */
    public FingerprintDialog circleErrorColor(int circleErrorColor){
        this.fingerprint.circleErrorColor(circleErrorColor);
        return this;
    }

    /**
     * Set color of the text scanning status.
     * @param statusScanningColor resource color
     * @return FingerprintDialog object
     */
    public FingerprintDialog statusScanningColor(int statusScanningColor){
        this.statusScanningColor = statusScanningColor;
        return this;
    }

    /**
     * Set color of the text success status.
     * @param statusSuccessColor resource color
     * @return FingerprintDialog object
     */
    public FingerprintDialog statusSuccessColor(int statusSuccessColor){
        this.statusSuccessColor = statusSuccessColor;
        return this;
    }

    /**
     * Set color of the text error status.
     * @param statusErrorColor resource color
     * @return FingerprintDialog object
     */
    public FingerprintDialog statusErrorColor(int statusErrorColor){
        this.statusErrorColor = statusErrorColor;
        return this;
    }

    /**
     * Set delay before triggering callback after a failed attempt to authenticate.
     * @param delayAfterError delay in milliseconds
     * @return FingerprintDialog object
     */
    public FingerprintDialog delayAfterError(int delayAfterError){
        this.delayAfterError = delayAfterError;
        this.fingerprint.delayAfterError(delayAfterError);
        return this;
    }

    /**
     * Set delay before triggering callback after a successful authentication.
     * @param delayAfterSuccess delay in milliseconds
     * @return FingerprintDialog object
     */
    public FingerprintDialog delayAfterSuccess(int delayAfterSuccess){
        this.delayAfterSuccess = delayAfterSuccess;
        return this;
    }

    /**
     * Set a fail limit. Android blocks automatically when 5 attempts failed.
     * @param limit number of tries
     * @param counterCallback callback to be triggered when limit is reached
     * @return FingerprintDialog object
     */
    public FingerprintDialog tryLimit(int limit, final FailAuthCounterDialogCallback counterCallback){
        this.fingerprint.tryLimit(limit, new FailAuthCounterCallback() {
            @Override
            public void onTryLimitReached(Fingerprint fingerprint) {
                counterCallback.onTryLimitReached(FingerprintDialog.this);
            }
        });
        return this;
    }

    /**
     * Display a "use password" button on the dialog.
     * @param onUsePassword OnClickListener triggered when button is clicked
     * @return FingerprintDialog object
     */
    public FingerprintDialog usePasswordButton(View.OnClickListener onUsePassword){
        this.onUsePassword = onUsePassword;
        return this;
    }

    /**
     * Show the dialog.
     */
    public void show(){
        if(title==null || message==null){
            throw new RuntimeException("Title or message cannot be null.");
        }

        showDialog();
    }

    /**
     * Dismiss the dialog.
     */
    public void dismiss(){
        fingerprint.cancel();
        if(dialog.isShowing()){
            dialog.dismiss();
        }
    }

    private void showDialog(){
        dialogTitle.setText(title);
        dialogMessage.setText(message);
        cancelButton.setText(R.string.fingerprint_cancel);
        usePasswordButton.setText(R.string.fingerprint_use_password);
        setStatus(R.string.fingerprint_state_scanning, statusScanningColor);

        builder.setView(dialogView);
        dialog = builder.create();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fingerprint.cancel();
                if(fingerprintDialogSecureCallback!=null){
                    fingerprintDialogSecureCallback.onAuthenticationCancel();
                }
                else{
                    fingerprintDialogCallback.onAuthenticationCancel();
                }
                dialog.cancel();
            }
        });

        if(onUsePassword==null){
            usePasswordButton.setVisibility(View.GONE);
        }
        else{
            usePasswordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fingerprint.cancel();
                    dialog.cancel();
                    onUsePassword.onClick(view);
                }
            });
        }

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

        authenticate();
    }

    private void authenticate(){
        fingerprint.authenticate();
    }

    private void setStatus(int textId, int textColorId){
        setStatus(context.getResources().getString(textId), textColorId);
    }

    private void setStatus(String text, int textColorId){
        dialogStatus.setTextColor(ResourcesCompat.getColor(context.getResources(), textColorId, context.getTheme()));
        dialogStatus.setText(text);
    }

    private Runnable returnToScanning = new Runnable() {
        @Override
        public void run() {
            setStatus(R.string.fingerprint_state_scanning, statusScanningColor);
        }
    };

    private FingerprintCallback fingerprintCallback = new FingerprintCallback() {
        @Override
        public void onAuthenticationSucceeded() {
            handler.removeCallbacks(returnToScanning);
            setStatus(R.string.fingerprint_state_success, statusSuccessColor);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.cancel();
                    if(fingerprintDialogCallback!=null){
                        fingerprintDialogCallback.onAuthenticationSucceeded();
                    }
                }
            }, delayAfterSuccess);
        }

        @Override
        public void onAuthenticationFailed() {
            setStatus(R.string.fingerprint_state_failure, statusErrorColor);
            handler.postDelayed(returnToScanning, delayAfterError);
        }

        @Override
        public void onAuthenticationError(int errorCode, String error) {
            setStatus(error, statusErrorColor);
            handler.postDelayed(returnToScanning, delayAfterError);
        }
    };

    private FingerprintSecureCallback fingerprintSecureCallback = new FingerprintSecureCallback() {
        @Override
        public void onAuthenticationSucceeded() {
            handler.removeCallbacks(returnToScanning);
            setStatus(R.string.fingerprint_state_success, statusSuccessColor);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.cancel();
                    if(fingerprintDialogSecureCallback!=null){
                        fingerprintDialogSecureCallback.onAuthenticationSucceeded();
                    }
                }
            }, delayAfterSuccess);
        }

        @Override
        public void onAuthenticationFailed() {
            setStatus(R.string.fingerprint_state_failure, statusErrorColor);
            handler.postDelayed(returnToScanning, delayAfterError);
        }

        @Override
        public void onNewFingerprintEnrolled(FingerprintToken token) {
            dialog.cancel();
            if(fingerprintDialogSecureCallback!=null){
                fingerprintDialogSecureCallback.onNewFingerprintEnrolled(token);
            }
        }

        @Override
        public void onAuthenticationError(int errorCode, String error) {
            setStatus(error, statusErrorColor);
            handler.postDelayed(returnToScanning, delayAfterError);
        }
    };
}