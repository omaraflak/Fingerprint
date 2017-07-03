package me.aflak.libraries;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

/**
 * Created by Omar on 02/07/2017.
 */

public class FingerprintDialog {
    private Context context;
    private FingerprintManager fingerprintManager;
    private CancellationSignal cancellationSignal;

    private LayoutInflater layoutInflater;
    private FingerprintCallback fingerprintCallback;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private View view;

    private String title, message;

    private boolean animationEnabled, canceledOnTouchOutside;
    private int successColor, errorColor;

    public FingerprintDialog(Context context, FingerprintManager fingerprintManager){
        this.context = context;
        this.fingerprintManager = fingerprintManager;
        init(context);
    }

    public FingerprintDialog(Context context){
        this.context = context;
        this.fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        init(context);
    }

    private void init(Context context){
        this.layoutInflater = LayoutInflater.from(context);
        this.builder = new AlertDialog.Builder(context);
        this.successColor = R.color.auth_success;
        this.errorColor = R.color.auth_failed;
        this.animationEnabled = true;
        this.canceledOnTouchOutside = false;
    }

    public boolean isHardwareDetected(){
        return fingerprintManager.isHardwareDetected();
    }

    public boolean hasEnrolledFingerprints(){
        return fingerprintManager.hasEnrolledFingerprints();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTitle(int resTitle){
        this.title = context.getResources().getString(resTitle);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessage(int resMessage) {
        this.message = context.getResources().getString(resMessage);
    }

    public void setFingerprintCallback(FingerprintCallback fingerprintCallback) {
        this.fingerprintCallback = fingerprintCallback;
    }


    public void setExitAnimation(boolean enabled){
        this.animationEnabled = enabled;
    }

    public void setSuccessColor(int successColor){
        this.successColor = successColor;
    }

    public void setErrorColor(int errorColor){
        this.errorColor = errorColor;
    }

    public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        this.canceledOnTouchOutside = canceledOnTouchOutside;
    }

    public void show(int resTitle, int resMessage, FingerprintCallback fingerprintCallback){
        show(context.getResources().getString(resTitle), context.getResources().getString(resMessage), fingerprintCallback);
    }

    public void show(String title, String message, FingerprintCallback fingerprintCallback){
        this.title = title;
        this.message = message;
        this.fingerprintCallback = fingerprintCallback;

        show();
    }

    public void show(){
        view = layoutInflater.inflate(R.layout.dialog, null);
        ((TextView) view.findViewById(R.id.dialog_title)).setText(title);
        ((TextView) view.findViewById(R.id.dialog_message)).setText(message);
        builder.setPositiveButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cancellationSignal.cancel();
                if(fingerprintCallback!=null) {
                    fingerprintCallback.onUserCanceled();
                }
            }
        });
        builder.setView(view);
        dialog = builder.create();
        if(dialog.getWindow() != null && animationEnabled) {
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        }
        dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        dialog.show();

        auth();
    }

    private void auth(){
        cancellationSignal = new CancellationSignal();
        if(fingerprintManager.isHardwareDetected()) {
            if (fingerprintManager.hasEnrolledFingerprints()) {
                fingerprintManager.authenticate(null, cancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        setStatus(R.string.state_failure, errorColor, R.drawable.ic_close_white_24dp, null);
                        if (fingerprintCallback != null) {
                            fingerprintCallback.onFingerprintFailure();
                        }
                    }

                    @Override
                    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                        super.onAuthenticationHelp(helpCode, helpString);
                        setStatus(R.string.state_failure, errorColor, R.drawable.ic_close_white_24dp, null);
                        if (fingerprintCallback != null) {
                            fingerprintCallback.onFingerprintFailure();
                        }
                    }

                    @Override
                    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        setStatus(R.string.state_success, successColor, R.drawable.ic_check_white_24dp, new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {
                                dialog.cancel();
                            }
                        });
                        if (fingerprintCallback != null) {
                            fingerprintCallback.onFingerprintSuccess();
                        }
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        setStatus(R.string.state_failure, errorColor, R.drawable.ic_close_white_24dp, null);
                        if (fingerprintCallback != null) {
                            fingerprintCallback.onFingerprintFailure();
                        }
                    }
                }, null);
            }
        }
    }

    private void setStatus(final int textId, final int color, final int drawable, final YoYo.AnimatorCallback callback){
        final RelativeLayout layout = view.findViewById(R.id.dialog_layout_icon);
        final View background = view.findViewById(R.id.dialog_icon_background);
        final ImageView foreground = view.findViewById(R.id.dialog_icon_foreground);
        final TextView status = view.findViewById(R.id.dialog_status);

        YoYo.with(Techniques.FlipOutY)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        status.setText(textId);
                        status.setTextColor(ContextCompat.getColor(context, color));
                        background.setBackgroundTintList(ColorStateList.valueOf(context.getColor(color)));
                        foreground.setImageResource(drawable);

                        if(callback!=null) {
                            YoYo.with(Techniques.FlipInY)
                                    .onEnd(callback)
                                    .duration(350)
                                    .playOn(layout);
                        }
                        else{
                            YoYo.with(Techniques.FlipInY)
                                    .duration(350)
                                    .playOn(layout);
                        }
                    }
                })
                .duration(350)
                .playOn(layout);
    }
}
