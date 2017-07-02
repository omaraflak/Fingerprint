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
    private DialogInterface.OnClickListener listener;

    public FingerprintDialog(Context context){
        this.context = context;
        this.fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        this.layoutInflater = LayoutInflater.from(context);
        this.builder = new AlertDialog.Builder(context);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Callback to know when user hit the Cancel button
     * @param listener
     */
    public void setCancelListener(DialogInterface.OnClickListener listener){
        this.listener = listener;
    }

    /**
     * Callback to know whether the authentication succeeded or not
     * @param fingerprintCallback
     */
    public void setFingerprintCallback(FingerprintCallback fingerprintCallback) {
        this.fingerprintCallback = fingerprintCallback;
    }

    /**
     * Show the dialog setting automatically Cancel listener to null
     * @param title
     * @param message
     * @param callback
     */
    public void show(String title, String message, FingerprintCallback callback){
        show(title, message, null, callback);
    }

    /**
     * Show the dialog
     * @param title
     * @param message
     * @param fingerprintCallback
     */
    public void show(String title, String message, DialogInterface.OnClickListener listener, FingerprintCallback fingerprintCallback){
        this.title = title;
        this.message = message;
        this.listener = listener;
        this.fingerprintCallback = fingerprintCallback;

        show();
    }

    /**
     * Show the dialog
     */
    public void show(){
        view = layoutInflater.inflate(R.layout.dialog, null);
        ((TextView) view.findViewById(R.id.dialog_title)).setText(title);
        ((TextView) view.findViewById(R.id.dialog_message)).setText(message);
        builder.setPositiveButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cancellationSignal.cancel();
                if(listener!=null) {
                    listener.onClick(dialogInterface, i);
                }
            }
        });
        builder.setView(view);
        dialog = builder.create();
        if(dialog.getWindow() != null) {
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        }
        dialog.show();

        auth();
    }

    public void hide(){
        if(dialog.isShowing()){
            dialog.cancel();
        }
    }

    private void auth(){
        this.cancellationSignal = new CancellationSignal();
        if(fingerprintManager.isHardwareDetected()) {
            if (fingerprintManager.hasEnrolledFingerprints()) {
                fingerprintManager.authenticate(null, cancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        setStatus(R.string.state_failure, R.color.auth_failed, R.drawable.ic_close_white_24dp, null);
                        if (fingerprintCallback != null) {
                            fingerprintCallback.onFingerprintFailure();
                        }
                    }

                    @Override
                    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                        super.onAuthenticationHelp(helpCode, helpString);
                        setStatus(R.string.state_failure, R.color.auth_failed, R.drawable.ic_close_white_24dp, null);
                        if (fingerprintCallback != null) {
                            fingerprintCallback.onFingerprintFailure();
                        }
                    }

                    @Override
                    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        setStatus(R.string.state_success, R.color.auth_success, R.drawable.ic_check_white_24dp, new YoYo.AnimatorCallback() {
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
                        setStatus(R.string.state_failure, R.color.auth_failed, R.drawable.ic_close_white_24dp, null);
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
