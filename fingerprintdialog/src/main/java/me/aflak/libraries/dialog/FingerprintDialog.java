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
import me.aflak.libraries.callback.FingerprintCallback;
import me.aflak.libraries.callback.FingerprintDialogCallback;
import me.aflak.libraries.callback.FingerprintDialogSecureCallback;
import me.aflak.libraries.R;
import me.aflak.libraries.callback.FingerprintSecureCallback;
import me.aflak.libraries.view.FingerprintView;

/**
 * Created by Omar on 02/07/2017.
 */

public class FingerprintDialog extends AnimatedDialog<FingerprintDialog> {
    private FingerprintView fingerprintView;
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
        this.delayAfterError = FingerprintView.DEFAULT_DELAY_AFTER_ERROR;
        this.delayAfterSuccess = FingerprintView.DEFAULT_DELAY_AFTER_ERROR;

        this.statusScanningColor = R.color.status_scanning;
        this.statusSuccessColor = R.color.status_success;
        this.statusErrorColor = R.color.status_error;

        this.dialogView = layoutInflater.inflate(R.layout.fingerprint_dialog, null);
        this.fingerprintView = dialogView.findViewById(R.id.fingerprint_dialog_fp);
        this.dialogTitle = dialogView.findViewById(R.id.fingerprint_dialog_title);
        this.dialogMessage = dialogView.findViewById(R.id.fingerprint_dialog_message);
        this.dialogStatus = dialogView.findViewById(R.id.fingerprint_dialog_status);
        this.cancelButton = dialogView.findViewById(R.id.fingerprint_dialog_cancel);
        this.usePasswordButton = dialogView.findViewById(R.id.fingerprint_dialog_use_password);
    }

    public static boolean isAvailable(Context context){
        FingerprintManager manager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        return (manager!=null && manager.isHardwareDetected() && manager.hasEnrolledFingerprints());
    }

    public static FingerprintDialog initialize(Context context){
        return new FingerprintDialog(context);
    }

    public FingerprintDialog callback(FingerprintDialogCallback fingerprintDialogCallback){
        this.fingerprintDialogCallback = fingerprintDialogCallback;
        this.fingerprintView.callback(fingerprintCallback);
        return this;
    }

    public FingerprintDialog callback(FingerprintDialogSecureCallback fingerprintDialogSecureCallback, String KEY_NAME){
        this.fingerprintDialogSecureCallback = fingerprintDialogSecureCallback;
        this.fingerprintView.callback(fingerprintSecureCallback, KEY_NAME);
        return this;
    }

    public FingerprintDialog cryptoObject(FingerprintManager.CryptoObject cryptoObject){
        this.fingerprintView.cryptoObject(cryptoObject);
        return this;
    }

    public FingerprintDialog fingerprintScanningColor(int fingerprintScanningColor){
        this.fingerprintView.fingerprintScanningColor(fingerprintScanningColor);
        return this;
    }

    public FingerprintDialog fingerprintSuccessColor(int fingerprintSuccessColor){
        this.fingerprintView.fingerprintSuccessColor(fingerprintSuccessColor);
        return this;
    }

    public FingerprintDialog fingerprintErrorColor(int fingerprintErrorColor){
        this.fingerprintView.fingerprintErrorColor(fingerprintErrorColor);
        return this;
    }

    public FingerprintDialog circleScanningColor(int circleScanningColor){
        this.fingerprintView.circleScanningColor(circleScanningColor);
        return this;
    }

    public FingerprintDialog circleSuccessColor(int circleSuccessColor){
        this.fingerprintView.circleSuccessColor(circleSuccessColor);
        return this;
    }

    public FingerprintDialog circleErrorColor(int circleErrorColor){
        this.fingerprintView.circleErrorColor(circleErrorColor);
        return this;
    }

    public FingerprintDialog delayAfterError(int delayAfterError){
        this.delayAfterError = delayAfterError;
        this.fingerprintView.delayAfterError(delayAfterError);
        return this;
    }

    public FingerprintDialog tryLimit(int limit, FailAuthCounterCallback counterCallback){
        this.fingerprintView.tryLimit(limit, counterCallback);
        return this;
    }

    public FingerprintDialog delayAfterSuccess(int delayAfterSuccess){
        this.delayAfterSuccess = delayAfterSuccess;
        return this;
    }

    public FingerprintDialog statusScanningColor(int statusScanningColor){
        this.statusScanningColor = statusScanningColor;
        return this;
    }

    public FingerprintDialog statusSuccessColor(int statusSuccessColor){
        this.statusSuccessColor = statusSuccessColor;
        return this;
    }

    public FingerprintDialog statusErrorColor(int statusErrorColor){
        this.statusErrorColor = statusErrorColor;
        return this;
    }

    public FingerprintDialog usePasswordButton(View.OnClickListener onUsePassword){
        this.onUsePassword = onUsePassword;
        return this;
    }

    public FingerprintDialog show(){
        if(title==null || message==null){
            throw new RuntimeException("Title or message cannot be null.");
        }

        showDialog();
        return this;
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
                fingerprintView.cancel();
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
                    fingerprintView.cancel();
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
        fingerprintView.authenticate();
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