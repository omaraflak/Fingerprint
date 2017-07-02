package me.aflak.fingerprintdialoglibrary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import me.aflak.libraries.FingerprintDialog;
import me.aflak.libraries.FingerprintCallback;

public class MainActivity extends AppCompatActivity implements FingerprintCallback {
    private FingerprintDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new FingerprintDialog(this);
        dialog.setSuccessColor(R.color.colorPrimary);
        dialog.setErrorColor(R.color.colorAccent);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show("Sign In", "Confirm fingerprint to continue", MainActivity.this);
            }
        });
    }

    @Override
    public void onFingerprintSuccess() {
        // authentication succeeded
    }

    @Override
    public void onFingerprintFailure() {
        // authentication failed
    }
}
