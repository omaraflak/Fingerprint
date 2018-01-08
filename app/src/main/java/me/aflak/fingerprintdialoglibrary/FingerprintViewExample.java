package me.aflak.fingerprintdialoglibrary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import me.aflak.libraries.callback.FingerprintCallback;
import me.aflak.libraries.view.Fingerprint;

/**
 * Created by Omar on 08/01/2018.
 */

public class FingerprintViewExample extends AppCompatActivity implements FingerprintCallback {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_example);

        Fingerprint fingerprint = findViewById(R.id.activity_view_example_fingerprint);
        fingerprint.callback(this)
            .circleScanningColor(android.R.color.black)
            .fingerprintScanningColor(R.color.colorAccent)
            .authenticate();
    }

    @Override
    public void onAuthenticationSucceeded() {
        // Fingerprint recognized
    }

    @Override
    public void onAuthenticationFailed() {
        // Fingerprint failed to recognize
    }

    @Override
    public void onAuthenticationError(int errorCode, String error) {
        // Error while authenticating
        // See Android Doc for errorCode meaning
    }
}
