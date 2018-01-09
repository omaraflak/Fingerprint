package me.aflak.fingerprintdialoglibrary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Omar on 07/12/2017.
 */

public class Menu extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        findViewById(R.id.activity_menu_fingerprint_view).setOnClickListener(onView);
        findViewById(R.id.activity_menu_fingerprint_secure_view).setOnClickListener(onSecureView);
        findViewById(R.id.activity_menu_fingerprint_dialog).setOnClickListener(onDialog);
        findViewById(R.id.activity_menu_fingerprint_secure_dialog).setOnClickListener(onSecureDialog);
    }

    private View.OnClickListener onView = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(Menu.this, FingerprintView.class));
        }
    };

    private View.OnClickListener onSecureView = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(Menu.this, FingerprintSecureView.class));
        }
    };

    private View.OnClickListener onDialog = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(Menu.this, FingerprintDialog.class));
        }
    };

    private View.OnClickListener onSecureDialog = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(Menu.this, FingerprintSecureDialog.class));
        }
    };
}
