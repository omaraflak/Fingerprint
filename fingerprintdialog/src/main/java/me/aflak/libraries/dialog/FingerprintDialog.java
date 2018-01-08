package me.aflak.libraries.dialog;

import android.content.Context;
import android.content.res.ColorStateList;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import me.aflak.libraries.callback.FailAuthCounterCallback;
import me.aflak.libraries.callback.FingerprintCallback;
import me.aflak.libraries.callback.FingerprintSecureCallback;
import me.aflak.libraries.R;

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

    private int fingerprintScanningColor, fingerprintSuccessColor, fingerprintErrorColor;
    private int circleScanningColor, circleSuccessColor, circleErrorColor;
    private int statusScanningColor, statusSuccessColor, statusErrorColor;

    private View.OnClickListener onUsePassword;

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
        this.onUsePassword = null;
        this.cryptoObject = null;
        this.fingerprintScanningColor = R.color.fingerprint_scanning;
        this.fingerprintSuccessColor = R.color.fingerprint_success;
        this.fingerprintErrorColor = R.color.fingerprint_error;
        this.circleScanningColor = R.color.circle_scanning;
        this.circleSuccessColor = R.color.circle_success;
        this.circleErrorColor = R.color.circle_error;
        this.statusScanningColor = R.color.status_scanning;
        this.statusSuccessColor = R.color.status_success;
        this.statusErrorColor = R.color.status_error;
        this.delayAfterSuccess = 1200;
        this.delayAfterError = 1200;
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

    public FingerprintDialog fingerprintScanningColor(int fingerprintScanningColor){
        this.fingerprintScanningColor = fingerprintScanningColor;
        return this;
    }

    public FingerprintDialog fingerprintSuccessColor(int fingerprintSuccessColor){
        this.fingerprintSuccessColor = fingerprintSuccessColor;
        return this;
    }

    public FingerprintDialog fingerprintErrorColor(int fingerprintErrorColor){
        this.fingerprintErrorColor = fingerprintErrorColor;
        return this;
    }

    public FingerprintDialog circleScanningColor(int circleScanningColor){
        this.circleScanningColor = circleScanningColor;
        return this;
    }

    public FingerprintDialog circleSuccessColor(int circleSuccessColor){
        this.circleSuccessColor = circleSuccessColor;
        return this;
    }

    public FingerprintDialog circleErrorColor(int circleErrorColor){
        this.circleErrorColor = circleErrorColor;
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
            cryptoObject = cipherHelper.getEncryptionCryptoObject();
            if(cryptoObject==null) {
                fingerprintSecureCallback.onNewFingerprintEnrolled(new FingerprintToken(cipherHelper));
            }
            else{
                showDialog();
            }
        }
        else if(fingerprintCallback!=null){
            showDialog();
        }
        else{
            throw new RuntimeException("You must specify a callback.");
        }

        return this;
    }

    private void showDialog(){
        view = layoutInflater.inflate(R.layout.fingerprint_dialog, null);
        ((TextView) view.findViewById(R.id.fingerprint_dialog_title)).setText(title);
        ((TextView) view.findViewById(R.id.fingerprint_dialog_message)).setText(message);
        AppCompatButton cancelButton = view.findViewById(R.id.fingerprint_dialog_cancel);
        AppCompatButton usePasswordButton = view.findViewById(R.id.fingerprint_dialog_use_password);
        cancelButton.setText(R.string.fingerprint_cancel);
        usePasswordButton.setText(R.string.fingerprint_use_password);
        setStatus(R.string.fingerprint_state_scanning, statusScanningColor, R.drawable.fingerprint, fingerprintScanningColor, circleScanningColor);

        builder.setView(view);
        dialog = builder.create();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancellationSignal.cancel();
                if(fingerprintSecureCallback!=null){
                    fingerprintSecureCallback.onAuthenticationCancel();
                }
                else{
                    fingerprintCallback.onAuthenticationCancel();
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
                    cancellationSignal.cancel();
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

        auth();
    }

    private void auth(){
        cancellationSignal = new CancellationSignal();
        if(fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints()) {
            fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    setStatus(errString.toString(), statusErrorColor, R.drawable.fingerprint_error, fingerprintErrorColor, circleErrorColor);
                    handler.postDelayed(returnToScanning, delayAfterError);
                }

                @Override
                public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                    super.onAuthenticationHelp(helpCode, helpString);
                    setStatus(helpString.toString(), statusErrorColor, R.drawable.fingerprint_error, fingerprintErrorColor, circleErrorColor);
                    handler.postDelayed(returnToScanning, delayAfterError);
                }

                @Override
                public void onAuthenticationSucceeded(final FingerprintManager.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    handler.removeCallbacks(returnToScanning);
                    setStatus(R.string.fingerprint_state_success, statusSuccessColor, R.drawable.fingerprint_success, fingerprintSuccessColor, circleSuccessColor);

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
                    setStatus(R.string.fingerprint_state_failure, statusErrorColor, R.drawable.fingerprint_error, fingerprintErrorColor, circleErrorColor);
                    handler.postDelayed(returnToScanning, delayAfterError);
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

    private void setStatus(int textId, int textColorId, int drawableId, int drawableColorId, int circleColorId){
        setStatus(context.getResources().getString(textId), textColorId, drawableId, drawableColorId, circleColorId);
    }

    private void setStatus(String text, int textColorId, int drawableId, int drawableColorId, int circleColorId){
        ImageView fingerprint = view.findViewById(R.id.fingerprint_dialog_icon_foreground);
        View circle = view.findViewById(R.id.fingerprint_dialog_icon_background);
        TextView status = view.findViewById(R.id.fingerprint_dialog_status);

        fingerprint.setImageResource(drawableId);
        fingerprint.setColorFilter(ContextCompat.getColor(context, drawableColorId), android.graphics.PorterDuff.Mode.MULTIPLY);
        circle.setBackgroundTintList(ColorStateList.valueOf(context.getColor(circleColorId)));
        status.setTextColor(ResourcesCompat.getColor(context.getResources(), textColorId, context.getTheme()));
        status.setText(text);
    }

    private Runnable returnToScanning = new Runnable() {
        @Override
        public void run() {
            setStatus(R.string.fingerprint_state_scanning, statusScanningColor, R.drawable.fingerprint, fingerprintScanningColor, circleScanningColor);
        }
    };
}