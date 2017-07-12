package me.aflak.fingerprintdialoglibrary;

import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.security.keystore.KeyProperties;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import me.aflak.libraries.CryptoObjectHelper;
import me.aflak.libraries.CryptoObjectHelperCallback;
import me.aflak.libraries.FingerprintCallback;
import me.aflak.libraries.FingerprintDialog;
import me.aflak.libraries.PasswordCallback;
import me.aflak.libraries.PasswordDialog;

public class FingerprintSecureExample2 extends AppCompatActivity implements View.OnClickListener, FingerprintCallback, PasswordCallback {
    private CryptoObjectHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(FingerprintSecureExample2.this);
        helper = new CryptoObjectHelper("KeyName2");
    }

    @Override
    public void onClick(View view) {
        helper.getCryptoObject(CryptoObjectHelper.Type.SIGNATURE, KeyProperties.PURPOSE_VERIFY, new CryptoObjectHelperCallback() {
            @Override
            public void onNewFingerprintEnrolled() {
                // /!\ A new fingerprint was added /!\
                //
                // Prompt a password to verify identity, then :
                // if (password correct) {
                //      helper.generateNewKey();
                // }
                //
                // OR
                //
                // Use PasswordDialog to simplify the process

                PasswordDialog.initialize(FingerprintSecureExample2.this, helper)
                        .title(R.string.password_title)
                        .message(R.string.password_message)
                        .callback(FingerprintSecureExample2.this)
                        .passwordType(PasswordDialog.PASSWORD_TYPE_TEXT)
                        .show();
            }

            @Override
            public void onCryptoObjectRetrieved(FingerprintManager.CryptoObject cryptoObject) {
                if(FingerprintDialog.isAvailable(FingerprintSecureExample2.this)) {
                    FingerprintDialog.initialize(FingerprintSecureExample2.this)
                            .title(R.string.fingerprint_title)
                            .message(R.string.fingerprint_message)
                            .callback(FingerprintSecureExample2.this)
                            .cryptoObject(cryptoObject)
                            .show();
                }
            }
        });
    }

    @Override
    public void onAuthenticationSuccess() {
        // Fingerprint recognized
    }

    @Override
    public void onAuthenticationCancel() {
        // User pressed cancel button
    }

    @Override
    public boolean onPasswordCheck(String password) {
        return password.equals("password");
    }

    @Override
    public void onPasswordCancel() {
        // User pressed cancel button
    }
}