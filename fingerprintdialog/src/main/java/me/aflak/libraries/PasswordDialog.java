package me.aflak.libraries;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
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
    private AlertDialog.Builder builder;

    private String title;
    private String message;
    private int passwordType;

    public static final int PASSWORD_TYPE_TEXT = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
    public static final int PASSWORD_TYPE_NUMBER = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD;

    private PasswordDialog(Context context, FingerprintToken token){
        this.context = context;
        this.token = token;
        this.inflater = LayoutInflater.from(context);
        this.builder = new AlertDialog.Builder(context);
        this.passwordType = PASSWORD_TYPE_TEXT;
        this.callback = null;
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

    public PasswordDialog passwordType(int passwordType){
        this.passwordType = passwordType;
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

        builder.setView(view)
                .setPositiveButton(R.string.password_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(callback!=null){
                            String password = input.getText().toString();
                            if (callback.onPasswordCheck(password)){
                                dialogInterface.cancel();
                                token.continueAuthentication();
                            }
                            else{
                                callback.onPasswordWrong();
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.password_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(callback!=null){
                            callback.onCancel();
                        }
                    }
                })
                .setCancelable(false)
                .show();

        return this;
    }
}
