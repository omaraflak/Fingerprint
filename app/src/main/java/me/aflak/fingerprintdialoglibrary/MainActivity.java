package me.aflak.fingerprintdialoglibrary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import me.aflak.libraries.FingerprintDialog;
import me.aflak.libraries.FingerprintCallback;

public class MainActivity extends AppCompatActivity implements FingerprintCallback {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.text);

        FingerprintDialog dialog = new FingerprintDialog(this);
        dialog.show("Sign In", "Confirm fingerprint to continue", this);
    }

    @Override
    public void onFingerprintSuccess() {
        textView.setText("Welcome !");
    }

    @Override
    public void onFingerprintFailure() {
    }
}
