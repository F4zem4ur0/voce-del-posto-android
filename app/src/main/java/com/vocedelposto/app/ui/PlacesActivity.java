package com.vocedelposto.app.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vocedelposto.app.R;
import com.vocedelposto.app.model.Place;
import com.vocedelposto.app.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlacesActivity extends AppCompatActivity {

    private RecyclerView rvPlaces;
    private PlacesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        // Recupera il token salvato e lo imposta nel client
        SharedPreferences prefs = getSharedPreferences("voce_del_posto", MODE_PRIVATE);
        String token = prefs.getString("token", null);
        if (token != null) {
            RetrofitClient.getInstance().setAuthToken(token);
        }

        rvPlaces = findViewById(R.id.rvPlaces);
        rvPlaces.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlacesAdapter(new ArrayList<>());
        rvPlaces.setAdapter(adapter);

        // Coordinate di Parma centro come default
        loadNearbyPlaces(44.8015, 10.3279, 5.0);
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
                        // gestiremo gli errori dopo
                    }
                });
    }
}