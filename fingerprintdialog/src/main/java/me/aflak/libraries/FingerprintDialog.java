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
    private FingerprintManager.CryptoObject cryptoObject;
    private KeyStoreHelper keyStoreHelper;

    private LayoutInflater layoutInflater;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private View view;

    private String title, message;

    private boolean cancelOnTouchOutside;
    private int enterAnimation, exitAnimation, successColor, errorColor, delayAfterSuccess;

    public final static int ENTER_FROM_BOTTOM=0, ENTER_FROM_TOP=1, ENTER_FROM_LEFT=2, ENTER_FROM_RIGHT=3;
    public final static int EXIT_TO_BOTTOM=0, EXIT_TO_TOP=1, EXIT_TO_LEFT=2, EXIT_TO_RIGHT=3;
    public final static int NO_ANIMATION=4;

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
        this.successColor = R.color.auth_success;
        this.errorColor = R.color.auth_failed;
        this.delayAfterSuccess = 1200;
        this.cancelOnTouchOutside = false;
        this.enterAnimation = ENTER_FROM_BOTTOM;
        this.exitAnimation = EXIT_TO_BOTTOM;
        this.cryptoObject = null;
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

    public FingerprintDialog cancelOnTouchOutside(boolean cancelOnTouchOutside) {
        this.cancelOnTouchOutside = cancelOnTouchOutside;
        return this;
    }

    public FingerprintDialog show(){
        if(title==null || message==null){
            throw new RuntimeException("Title or message cannot be null.");
        }
        else if(fingerprintCallback!=null){
            show(title, message, fingerprintCallback);
        }
        else if(fingerprintSecureCallback!=null){
            showSecure(title, message, fingerprintSecureCallback);
        }
        return this;
    }

    private void showSecure(String title, String message, final FingerprintSecureCallback fingerprintSecureCallback){
        this.title = title;
        this.message = message;
        this.fingerprintCallback = fingerprintSecureCallback;

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

    private void show(String title, String message, FingerprintCallback fingerprintCallback){
        this.cryptoObject = null;
        this.title = title;
        this.message = message;
        this.fingerprintCallback = fingerprintCallback;

        showDialog();
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
                    fingerprintCallback.onCancelled();
                }
            }
        });
        builder.setView(view);
        dialog = builder.create();
        if(dialog.getWindow() != null && (enterAnimation!=NO_ANIMATION || exitAnimation!=NO_ANIMATION)) {
            int style = getStyle();
            if(style==-1){
                Log.e(TAG, "The animation selected is not available. Default animation will be used.");
            }
            else {
                dialog.getWindow().getAttributes().windowAnimations = style;
            }
        }
        dialog.setCanceledOnTouchOutside(cancelOnTouchOutside);
        dialog.show();

        auth();
    }

    private int getStyle(){
        switch (enterAnimation){
            case ENTER_FROM_BOTTOM:
                switch (exitAnimation){
                    case EXIT_TO_BOTTOM:
                        return R.style.BottomBottomAnimation;
                    case EXIT_TO_TOP:
                        return R.style.BottomTopAnimation;
                    case NO_ANIMATION:
                        return R.style.EnterFromBottomAnimation;
                }
                break;
            case ENTER_FROM_TOP:
                switch (exitAnimation){
                    case EXIT_TO_BOTTOM:
                        return R.style.TopBottomAnimation;
                    case EXIT_TO_TOP:
                        return R.style.TopTopAnimation;
                    case NO_ANIMATION:
                        return R.style.EnterFromTopAnimation;
                }
                break;
            case ENTER_FROM_LEFT:
                switch (exitAnimation){
                    case EXIT_TO_LEFT:
                        return R.style.LeftLeftAnimation;
                    case EXIT_TO_RIGHT:
                        return R.style.LeftRightAnimation;
                    case NO_ANIMATION:
                        return R.style.EnterFromLeftAnimation;
                }
                break;
            case ENTER_FROM_RIGHT:
                switch (exitAnimation){
                    case EXIT_TO_LEFT:
                        return R.style.RightLeftAnimation;
                    case EXIT_TO_RIGHT:
                        return R.style.RightRightAnimation;
                    case NO_ANIMATION:
                        return R.style.EnterFromRightAnimation;
                }
                break;
            case NO_ANIMATION:
                switch (exitAnimation){
                    case EXIT_TO_BOTTOM:
                        return R.style.ExitToBottomAnimation;
                    case EXIT_TO_TOP:
                        return R.style.ExitToTopAnimation;
                    case EXIT_TO_LEFT:
                        return R.style.ExitToLeftAnimation;
                    case EXIT_TO_RIGHT:
                        return R.style.ExitToRightAnimation;
                }
                break;
        }
        return -1;
    }

    private void auth(){
        cancellationSignal = new CancellationSignal();
        if(fingerprintManager.isHardwareDetected()) {
            if (fingerprintManager.hasEnrolledFingerprints()) {
                fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        setStatus(errString.toString(), errorColor, R.drawable.fingerprint_error);
                    }

                    @Override
                    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                        super.onAuthenticationHelp(helpCode, helpString);
                        setStatus(helpString.toString(), errorColor, R.drawable.fingerprint_error);
                    }

                    @Override
                    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        setStatus(R.string.fingerprint_state_success, successColor, R.drawable.fingerprint_success);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.cancel();
                                if (fingerprintCallback != null) {
                                    fingerprintCallback.onAuthenticated();
                                }
                            }
                        }, delayAfterSuccess);
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        setStatus(R.string.fingerprint_state_failure, errorColor, R.drawable.fingerprint_error);
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

    private void setStatus(int textId, int color, int drawable){
        setStatus(context.getResources().getString(textId), color, drawable);
    }

    private void setStatus(final String text, final int color, final int drawable){
        ImageView foreground = view.findViewById(R.id.fingerprint_dialog_icon_foreground);
        View background = view.findViewById(R.id.fingerprint_dialog_icon_background);
        TextView status = view.findViewById(R.id.fingerprint_dialog_status);

        foreground.setImageResource(drawable);
        background.setBackgroundTintList(ColorStateList.valueOf(context.getColor(color)));
        status.setTextColor(ResourcesCompat.getColor(context.getResources(), color, null));
        status.setText(text);
    }
}