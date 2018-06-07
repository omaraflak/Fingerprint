package me.aflak.libraries.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import me.aflak.libraries.callback.FailAuthCounterCallback;
import me.aflak.libraries.callback.PasswordCallback;
import me.aflak.libraries.R;
import me.aflak.libraries.utils.FingerprintToken;

/**
 * Created by Omar on 10/07/2017.
 */

public class PasswordDialog extends AnimatedDialog<PasswordDialog> {
    private FingerprintToken token;
    private PasswordCallback callback;

    private int passwordType;

    public static final int PASSWORD_TYPE_TEXT = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
    public static final int PASSWORD_TYPE_NUMBER = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD;

    private final static String TAG = "PasswordDialog";

    private PasswordDialog(Context context, FingerprintToken token){
        super(context);
        this.token = token;
        this.passwordType = PASSWORD_TYPE_TEXT;
        this.callback = null;
    }

    /**
     * Create a PasswordDialog instance.
     * @param context Activity Context
     * @param token Token got with FingerprintDialogSecureCallback
     * @return PasswordDialog instance
     */
    public static PasswordDialog initialize(Context context, FingerprintToken token){
        return new PasswordDialog(context, token);
    }

    /**
     * Create a PasswordDialog instance.
     * @param context Activity Context
     * @return PasswordDialog instance
     */
    public static PasswordDialog initialize(Context context){
        return new PasswordDialog(context, null);
    }

    /**
     * Set callback triggered when Password is entered.
     * @param callback The callback
     * @return PasswordDialog object
     */
    public PasswordDialog callback(PasswordCallback callback){
        this.callback = callback;
        return this;
    }

    /**
     * Set the password type (text or numbers)
     * @param passwordType PASSWORD_TYPE_TEXT or PASSWORD_TYPE_NUMBER
     * @return PasswordDialog object
     */
    public PasswordDialog passwordType(int passwordType){
        this.passwordType = passwordType;
        return this;
    }

    /**
     * Show the password dialog
     */
    public void show(){
        if(title==null || message==null) {
            throw new RuntimeException("Title or message cannot be null.");
        }

        dialogView = layoutInflater.inflate(R.layout.password_dialog, null);
        ((TextView) dialogView.findViewById(R.id.password_dialog_title)).setText(title);
        ((TextView) dialogView.findViewById(R.id.password_dialog_message)).setText(message);
        final EditText input = dialogView.findViewById(R.id.password_dialog_input);

        input.setInputType(passwordType);

        dialog = builder.setView(dialogView)
                .setPositiveButton(R.string.password_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // have to override this listener, otherwise it will close the dialog
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
                    if(callback.onPasswordCheck(password)){
                        dialog.dismiss();
                        if(token!=null){
                            token.validate();
                        }
                        callback.onPasswordSucceeded();
                    }
                    else{
                        input.setText("");
                        input.setError(context.getResources().getString(R.string.password_incorrect));
                    }
                }
            }
        });
    }
}
