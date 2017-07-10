package me.aflak.libraries;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Omar on 10/07/2017.
 */

public class PasswordDialog {
    private Context context;
    private FingerprintToken token;
    private LayoutInflater inflater;
    private PasswordCallback callback;
    private FailAuthCounterCallback counterCallback;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    private String title;
    private String message;
    private int passwordType;

    private int enterAnimation;
    private int exitAnimation;
    private int limit, tryCounter;

    private boolean cancelOnTouchOutside, cancelOnPressBack, darkBackground;

    public static final int PASSWORD_TYPE_TEXT = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
    public static final int PASSWORD_TYPE_NUMBER = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD;

    public final static int ENTER_FROM_BOTTOM=0, ENTER_FROM_TOP=1, ENTER_FROM_LEFT=2, ENTER_FROM_RIGHT=3;
    public final static int EXIT_TO_BOTTOM=0, EXIT_TO_TOP=1, EXIT_TO_LEFT=2, EXIT_TO_RIGHT=3;
    public final static int NO_ANIMATION=4;

    private final static String TAG = "PasswordDialog";

    private PasswordDialog(Context context, FingerprintToken token){
        this.context = context;
        this.token = token;
        this.inflater = LayoutInflater.from(context);
        this.builder = new AlertDialog.Builder(context);
        this.passwordType = PASSWORD_TYPE_TEXT;
        this.enterAnimation = NO_ANIMATION;
        this.exitAnimation = NO_ANIMATION;
        this.cancelOnTouchOutside = false;
        this.cancelOnPressBack = false;
        this.darkBackground = true;
        this.callback = null;
        this.counterCallback = null;
        this.tryCounter = 0;
    }

    public static PasswordDialog initialize(Context context, FingerprintToken token){
        return new PasswordDialog(context, token);
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

    public PasswordDialog enterAnimation(int enterAnimation){
        this.enterAnimation = enterAnimation;
        return this;
    }

    public PasswordDialog exitAnimation(int exitAnimation){
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

    public PasswordDialog darkBackground(boolean darkBackground){
        this.darkBackground = darkBackground;
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
            if(enterAnimation!=NO_ANIMATION || exitAnimation!=NO_ANIMATION) {
                int style = FingerprintDialog.getStyle(enterAnimation, exitAnimation);
                if (style == -1) {
                    Log.w(TAG, "The animation selected is not available. Default animation will be used.");
                } else {
                    dialog.getWindow().getAttributes().windowAnimations = style;
                }
            }

            if(!darkBackground){
                dialog.getWindow().setDimAmount(0.0f);
            }
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
                        token.continueAuthentication();
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
