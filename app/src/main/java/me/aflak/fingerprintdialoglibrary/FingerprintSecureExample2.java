package me.aflak.fingerprintdialoglibrary;

import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.security.Signature;

import me.aflak.libraries.FingerprintCallback;
import me.aflak.libraries.FingerprintDialog;
import me.aflak.libraries.PasswordCallback;
import me.aflak.libraries.PasswordDialog;
import me.aflak.libraries.SignatureHelper;

public class FingerprintSecureExample2 extends AppCompatActivity implements View.OnClickListener, FingerprintCallback, PasswordCallback {
    private SignatureHelper helper;
    private FingerprintManager.CryptoObject cryptoObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(this);
        helper = new SignatureHelper("KeyName2");
    }

    @Override
    public void onClick(View view) {
        cryptoObject = helper.getSigningCryptoObject();
        if(cryptoObject==null) {
            // /!\ A new fingerprint was added /!\
            //
            // Prompt a password to verify identity, then :
            // if (password correct) {
            //      helper.generateNewKey();
            //      // then recall getSigningCryptoObject()
            // }
            //
            // OR
            //
            // Use PasswordDialog to simplify the process

            PasswordDialog.initialize(this, helper)
                    .title(R.string.password_title)
                    .message(R.string.password_message)
                    .callback(this)
                    .passwordType(PasswordDialog.PASSWORD_TYPE_TEXT)
                    .show();
        }
        else{
            if(FingerprintDialog.isAvailable(this)) {
                FingerprintDialog.initialize(this)
                        .title(R.string.fingerprint_title)
                        .message(R.string.fingerprint_message)
                        .callback(this)
                        .cryptoObject(cryptoObject)
                        .show();
            }
        }
    }

    @Override
    public void onAuthenticationSuccess() {
        // Fingerprint recognized
        Signature signature = cryptoObject.getSignature();
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