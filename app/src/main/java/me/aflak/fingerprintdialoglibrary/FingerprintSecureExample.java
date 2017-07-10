package me.aflak.fingerprintdialoglibrary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import me.aflak.libraries.FingerprintCallback;
import me.aflak.libraries.FingerprintDialog;
import me.aflak.libraries.FingerprintSecureCallback;
import me.aflak.libraries.FingerprintToken;
import me.aflak.libraries.PasswordCallback;
import me.aflak.libraries.PasswordDialog;

public class FingerprintSecureExample extends AppCompatActivity implements FingerprintSecureCallback, PasswordCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FingerprintDialog.initialize(FingerprintSecureExample.this, "ArbitraryKey")
                        .enterAnimation(FingerprintDialog.ENTER_FROM_RIGHT)
                        .exitAnimation(FingerprintDialog.EXIT_TO_RIGHT)
                        .callback(FingerprintSecureExample.this) // if you pass a FingerprintCallback object, the CryptoObject won't be used. If you pass a FingerprintSecureCallback object, it will.
                        .title(R.string.fingerprint_title)
                        .message(R.string.fingerprint_message)
                        .show();
            }
        });
    }

    @Override
    public void onAuthenticated() {
        // Fingerprint recognized
    }

    @Override
    public void onCancelled() {
        // User pressed cancel button
    }

    @Override
    public void onNewFingerprintEnrolled(FingerprintToken token) {
        // /!\ A new fingerprint was added /!\
        //
        // Prompt a password to verify identity, then :
        // if (password correct) {
        //      token.continueAuthentication();
        // }
        //
        // OR
        //
        // Use PasswordDialog to simplify the process

        PasswordDialog.initialize(FingerprintSecureExample.this, token)
                .title(R.string.password_title)
                .message(R.string.password_message)
                .callback(FingerprintSecureExample.this)
                .passwordType(PasswordDialog.PASSWORD_TYPE_TEXT)
                .show();
    }

    @Override
    public boolean onPasswordCheck(String password) {
        return password.equals("the correct password");
    }

    @Override
    public void onCancel() {
        // User pressed cancel button
    }
}
