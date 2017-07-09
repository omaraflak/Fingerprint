package me.aflak.fingerprintdialoglibrary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import me.aflak.libraries.FingerprintDialog;
import me.aflak.libraries.FingerprintSecureCallback;

public class MainActivity extends AppCompatActivity implements FingerprintSecureCallback {
    private FingerprintDialog fingerprintDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fingerprintDialog = new FingerprintDialog(this);
        fingerprintDialog.setAnimation(FingerprintDialog.ENTER_FROM_RIGHT, FingerprintDialog.EXIT_TO_RIGHT);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fingerprintDialog.showSecure(R.string.title, R.string.message, MainActivity.this);
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
    public void onNewFingerprintEnrolled() {
        // A new fingerprint was added
        // should prompt a password
        // if (password correct) {
        //      fingerprintDialog.generateNewKey()
        //      fingerprintDialog.showSecure()
        // }
    }
}
