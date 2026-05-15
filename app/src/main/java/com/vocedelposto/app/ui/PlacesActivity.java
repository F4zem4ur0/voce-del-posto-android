package com.vocedelposto.app.ui;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.vocedelposto.app.MainActivity;
import com.vocedelposto.app.model.Place;
import com.vocedelposto.app.network.RetrofitClient;
import com.vocedelposto.app.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlacesActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST = 1001;
    private RecyclerView rvPlaces;
    private PlacesAdapter adapter;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        SharedPreferences prefs = getSharedPreferences("voce_del_posto", MODE_PRIVATE);
        String token = prefs.getString("token", null);
        if (token != null) {
            RetrofitClient.getInstance().setAuthToken(token);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        rvPlaces = findViewById(R.id.rvPlaces);
        rvPlaces.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlacesAdapter(this, new ArrayList<>());
        rvPlaces.setAdapter(adapter);

        findViewById(R.id.btnRecommendations).setOnClickListener(v ->
                startActivity(new Intent(this, RecommendationsActivity.class)));

        findViewById(R.id.btnLogout).setOnClickListener(v -> logout());

        requestLocationAndLoad();
    }

    private void requestLocationAndLoad() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        } else {
            getLocationAndLoad();
        }
    }

    private void getLocationAndLoad() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            loadNearbyPlaces(44.8015, 10.3279, 5.0); // fallback Parma
            return;
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        loadNearbyPlaces(location.getLatitude(), location.getLongitude(), 5.0);
                    } else {
                        loadNearbyPlaces(44.8015, 10.3279, 5.0); // fallback Parma
                    }
                })
                .addOnFailureListener(e ->
                        loadNearbyPlaces(44.8015, 10.3279, 5.0)); // fallback Parma
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationAndLoad();
            } else {
                loadNearbyPlaces(44.8015, 10.3279, 5.0); // fallback Parma
            }
        }
    }

    private void loadNearbyPlaces(double lat, double lon, double radius) {
        RetrofitClient.getInstance().getApiService()
                .getPlacesNearby(lat, lon, radius)
                .enqueue(new Callback<List<Place>>() {
                    @Override
                    public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            adapter.updatePlaces(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Place>> call, Throwable t) {
                        // fallback silenzioso
                    }
                });
    }

    private void logout() {
        getSharedPreferences("voce_del_posto", MODE_PRIVATE).edit().clear().apply();
        RetrofitClient.getInstance().setAuthToken(null);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}