package me.aflak.fingerprintdialoglibrary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import me.aflak.libraries.FingerprintDialog;
import me.aflak.libraries.FingerprintSecureCallback;
import me.aflak.libraries.KeyStoreHelper;

public class MainActivity extends AppCompatActivity implements FingerprintSecureCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fingerprintAuth();
            }
        });
    }

    void fingerprintAuth(){
        FingerprintDialog.initialize(this, "ArbitraryKey")
                .enterAnimation(FingerprintDialog.ENTER_FROM_RIGHT)
                .exitAnimation(FingerprintDialog.EXIT_TO_RIGHT)
                .callback(this) // if you pass a FingerprintCallback object, the CryptoObject won't be used. If you pass a FingerprintSecureCallback object, it will.
                .title(R.string.title)
                .message(R.string.message)
                .show();
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
    public void onNewFingerprintEnrolled(KeyStoreHelper helper) {
        // A new fingerprint was added
        // should prompt a password to verify identity
        // if (password correct) {
        //      helper.generateNewKey();
        //      fingerprintAuth();
        // }
    }
}
