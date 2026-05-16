package com.vocedelposto.app;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vocedelposto.app.fragment.NearbyFragment;
import com.vocedelposto.app.fragment.ProfileFragment;
import com.vocedelposto.app.fragment.RecommendationsFragment;
import com.vocedelposto.app.network.RetrofitClient;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences prefs = getSharedPreferences("voce_del_posto", MODE_PRIVATE);
        String token = prefs.getString("token", null);
        if (token != null) {
            RetrofitClient.getInstance().setAuthToken(token);
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected;
            int id = item.getItemId();
            if (id == R.id.nav_nearby) {
                selected = new NearbyFragment();
            } else if (id == R.id.nav_recommendations) {
                selected = new RecommendationsFragment();
            } else {
                selected = new ProfileFragment();
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, selected)
                    .commit();
            return true;
        });

        // carica il fragment iniziale
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new NearbyFragment())
                .commit();
    }
}