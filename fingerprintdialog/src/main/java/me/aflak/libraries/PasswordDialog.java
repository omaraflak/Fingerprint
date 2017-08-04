package me.aflak.libraries;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Omar on 10/07/2017.
 */

public class PasswordDialog {
    private Context context;
    private FingerprintToken token;
    private CipherHelper cipherHelper;
    private SignatureHelper signatureHelper;
    private LayoutInflater inflater;
    private PasswordCallback callback;
    private FailAuthCounterCallback counterCallback;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    private String title;
    private String message;
    private int passwordType;

    private DialogAnimation.Enter enterAnimation;
    private DialogAnimation.Exit exitAnimation;
    private int limit, tryCounter;

    private boolean cancelOnTouchOutside, cancelOnPressBack, dimBackground;
    private boolean manualMode, isUsingCipher;

    public static final int PASSWORD_TYPE_TEXT = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
    public static final int PASSWORD_TYPE_NUMBER = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD;

    private final static String TAG = "PasswordDialog";

    private PasswordDialog(Context context, FingerprintToken token){
        this.context = context;
        this.token = token;
        this.manualMode = false;
        init();
    }

    private PasswordDialog(Context context, CipherHelper cipherHelper){
        this.context = context;
        this.cipherHelper = cipherHelper;
        this.manualMode = true;
        this.isUsingCipher = true;
        init();
    }

    private PasswordDialog(Context context, SignatureHelper signatureHelper){
        this.context = context;
        this.signatureHelper = signatureHelper;
        this.manualMode = true;
        this.isUsingCipher = false;
        init();
    }

    private void init(){
        this.inflater = LayoutInflater.from(context);
        this.builder = new AlertDialog.Builder(context);
        this.passwordType = PASSWORD_TYPE_TEXT;
        this.enterAnimation = DialogAnimation.Enter.APPEAR;
        this.exitAnimation = DialogAnimation.Exit.DISAPPEAR;
        this.cancelOnTouchOutside = false;
        this.cancelOnPressBack = false;
        this.dimBackground = true;
        this.callback = null;
        this.counterCallback = null;
        this.tryCounter = 0;
    }

    public static PasswordDialog initialize(Context context, FingerprintToken token){
        return new PasswordDialog(context, token);
    }

    public static PasswordDialog initialize(Context context, CipherHelper helper){
        return new PasswordDialog(context, helper);
    }

    public static PasswordDialog initialize(Context context, SignatureHelper helper){
        return new PasswordDialog(context, helper);
    }

    public PasswordDialog title(String title){
        this.title = title;
        return this;
    }

    public PasswordDialog title(int resTitle){
        this.title = context.getResources().getString(resTitle);
        return this;
    }

    public PasswordDialog message(String message){
        this.message = message;
        return this;
    }

    public PasswordDialog message(int resMessage){
        this.message = context.getResources().getString(resMessage);
        return this;
    }

    public PasswordDialog callback(PasswordCallback callback){
        this.callback = callback;
        return this;
    }

    public PasswordDialog tryLimit(int limit, FailAuthCounterCallback counterCallback){
        this.limit = limit;
        this.counterCallback = counterCallback;
        return this;
    }

    public PasswordDialog passwordType(int passwordType){
        this.passwordType = passwordType;
        return this;
    }

    public PasswordDialog enterAnimation(DialogAnimation.Enter enterAnimation){
        this.enterAnimation = enterAnimation;
        return this;
    }

    public PasswordDialog exitAnimation(DialogAnimation.Exit exitAnimation){
        this.exitAnimation = exitAnimation;
        return this;
    }

    public PasswordDialog cancelOnTouchOutside(boolean cancelOnTouchOutside){
        this.cancelOnTouchOutside = cancelOnTouchOutside;
        return this;
    }

    public PasswordDialog cancelOnPressBack(boolean cancelOnPressBack){
        this.cancelOnPressBack = cancelOnPressBack;
        return this;
    }

    public PasswordDialog dimBackground(boolean dimBackground){
        this.dimBackground = dimBackground;
        return this;
    }

    public PasswordDialog show(){
        if(title==null || message==null) {
            throw new RuntimeException("Title or message cannot be null.");
        }

        View view = inflater.inflate(R.layout.password_dialog, null);
        ((TextView) view.findViewById(R.id.password_dialog_title)).setText(title);
        ((TextView) view.findViewById(R.id.password_dialog_message)).setText(message);
        final EditText input = view.findViewById(R.id.password_dialog_input);

        input.setInputType(passwordType);

        dialog = builder.setView(view)
                .setPositiveButton(R.string.password_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // got to use another listener because this one will close the dialog.
                    }
                })
                .setNegativeButton(R.string.password_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(callback!=null){
                            callback.onPasswordCancel();
                        }
                    }
                })
                .create();

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

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(callback!=null){
                    String password = input.getText().toString();
                    if (callback.onPasswordCheck(password)){
                        dialog.dismiss();
                        if(manualMode){
                            if(isUsingCipher){
                                cipherHelper.generateNewKey();
                                cipherHelper.recall();
                            }
                            else{
                                signatureHelper.generateNewKey();
                                signatureHelper.recall();
                            }
                        }
                        else {
                            token.continueAuthentication();
                        }
                        tryCounter = 0;
                    }
                    else{
                        input.setError(context.getResources().getString(R.string.password_incorrect));
                        tryCounter++;
                        if(counterCallback!=null && tryCounter==limit){
                            counterCallback.onTryLimitReached();
                        }
                    }
                }
            }
        });

        return this;
    }
}
