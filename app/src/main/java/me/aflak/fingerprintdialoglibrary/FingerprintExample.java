package me.aflak.fingerprintdialoglibrary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import me.aflak.libraries.FingerprintCallback;
import me.aflak.libraries.FingerprintDialog;

/**
 * Created by Omar on 10/07/2017.
 */

public class FingerprintExample extends AppCompatActivity implements FingerprintCallback{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FingerprintDialog.initialize(FingerprintExample.this, "ArbitraryKey")
                        .enterAnimation(FingerprintDialog.ENTER_FROM_RIGHT)
                        .exitAnimation(FingerprintDialog.EXIT_TO_RIGHT)
                        .callback(FingerprintExample.this) // if you pass a FingerprintCallback object, the CryptoObject won't be used. If you pass a FingerprintSecureCallback object, it will.
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
}
