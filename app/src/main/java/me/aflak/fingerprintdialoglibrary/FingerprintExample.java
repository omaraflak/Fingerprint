package me.aflak.fingerprintdialoglibrary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import me.aflak.libraries.callback.FingerprintCallback;
import me.aflak.libraries.dialog.FingerprintDialog;

/**
 * Created by Omar on 10/07/2017.
 */

public class FingerprintExample extends AppCompatActivity implements View.OnClickListener, FingerprintCallback{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(FingerprintDialog.isAvailable(this)) {
            FingerprintDialog.initialize(this)
                    .title(R.string.fingerprint_title)
                    .message(R.string.fingerprint_message)
                    .callback(this)
                    .show();
        }
    }

    @Override
    public void onAuthenticationSuccess() {
        // Fingerprint recognized
    }

    @Override
    public void onAuthenticationCancel() {
        // User pressed cancel button
    }
}
