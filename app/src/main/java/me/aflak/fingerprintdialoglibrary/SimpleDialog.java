package me.aflak.fingerprintdialoglibrary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import me.aflak.libraries.callback.FailAuthCounterDialogCallback;
import me.aflak.libraries.callback.FingerprintDialogCallback;
import me.aflak.libraries.dialog.FingerprintDialog;

/**
 * Created by Omar on 10/07/2017.
 */

public class SimpleDialog extends AppCompatActivity implements View.OnClickListener, FingerprintDialogCallback {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
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
    public void onAuthenticationSucceeded() {
        // Logic when fingerprint is recognized
    }

    @Override
    public void onAuthenticationCancel() {
        // Logic when user canceled operation
    }
}
