package me.aflak.fingerprintdialoglibrary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import me.aflak.libraries.callback.FingerprintSecureCallback;
import me.aflak.libraries.callback.PasswordCallback;
import me.aflak.libraries.dialog.PasswordDialog;
import me.aflak.libraries.utils.FingerprintToken;
import me.aflak.libraries.view.Fingerprint;

/**
 * Created by Omar on 09/01/2018.
 */

public class SecureView extends AppCompatActivity implements FingerprintSecureCallback, PasswordCallback {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        Fingerprint fingerprint = findViewById(R.id.activity_view_example_fingerprint);
        fingerprint.callback(this, "KeyName2")
            .circleScanningColor(android.R.color.black)
            .fingerprintScanningColor(R.color.colorAccent)
            .authenticate();
    }

    @Override
    public void onAuthenticationSucceeded() {
        // Logic when fingerprint is recognized
    }

    @Override
    public void onAuthenticationFailed() {
        // Logic when fingerprint failed to recognize
    }

    @Override
    public void onNewFingerprintEnrolled(FingerprintToken token) {
        // /!\ A new fingerprint was added /!\
        //
        // Prompt a password to verify identity, then :
        // if (password.correct()) {
        //      token.continueAuthentication();
        // }
        //
        // OR
        //
        // Use PasswordDialog to simplify the process

        PasswordDialog.initialize(this, token)
            .title(R.string.password_title)
            .message(R.string.password_message)
            .callback(this)
            .passwordType(PasswordDialog.PASSWORD_TYPE_TEXT)
            .show();
    }

    @Override
    public void onAuthenticationError(int errorCode, String error) {
        // Logic when an error raised while authenticating
        // See Android Doc for errorCode meaning
    }

    @Override
    public boolean onPasswordCheck(String password) {
        return password.equals("password");
    }

    @Override
    public void onPasswordCancel() {
        // Logic when user canceled operation
    }

    @Override
    public void onPasswordSucceeded() {
        // Logic when password is correct (new keys have been generated)
    }
}
