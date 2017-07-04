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
        dialog.setErrorColor(android.R.color.holo_red_dark);
        dialog.setAnimation(FingerprintDialog.ENTER_FROM_RIGHT, FingerprintDialog.EXIT_TO_RIGHT);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show(R.string.title, R.string.message,MainActivity.this);
            }
        });
    }

    @Override
    public void onFingerprintSuccess() {
        // authentication succeeded
    }

    @Override
    public void onFingerprintCancel() {
        // user pressed cancel button
    }
}
