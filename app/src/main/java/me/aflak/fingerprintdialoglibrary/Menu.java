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
        findViewById(R.id.activity_menu_fingerprint_example).setOnClickListener(onSample);
        findViewById(R.id.activity_menu_fingerprint_secure_example1).setOnClickListener(onSecure1);
    }

    private View.OnClickListener onSample = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(Menu.this, FingerprintExample.class));
        }
    };

    private View.OnClickListener onSecure1 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(Menu.this, FingerprintSecureExample1.class));
        }
    };
}
