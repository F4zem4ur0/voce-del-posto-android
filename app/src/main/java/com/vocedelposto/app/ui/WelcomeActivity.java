package com.vocedelposto.app.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.vocedelposto.app.MainActivity;
import com.vocedelposto.app.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // se ha già fatto l'onboarding vai direttamente al login
        SharedPreferences prefs = getSharedPreferences("voce_del_posto", MODE_PRIVATE);
        if (prefs.getBoolean("onboarding_completed", false)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_welcome);

        findViewById(R.id.btnGetStarted).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}