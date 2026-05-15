package com.vocedelposto.app.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

public class RecommendationsActivity extends AppCompatActivity {

    private RecyclerView rvRecommendations;
    private PlacesAdapter adapter;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);

        SharedPreferences prefs = getSharedPreferences("voce_del_posto", MODE_PRIVATE);
        Long userId = prefs.getLong("userId", -1);

        tvEmpty = findViewById(R.id.tvEmpty);
        rvRecommendations = findViewById(R.id.rvRecommendations);
        rvRecommendations.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlacesAdapter(this, new ArrayList<>());
        rvRecommendations.setAdapter(adapter);

        loadRecommendations(userId);
    }

    private void loadRecommendations(Long userId) {
        RetrofitClient.getInstance().getApiService()
                .getRecommendations(userId)
                .enqueue(new Callback<List<Place>>() {
                    @Override
                    public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Place> places = response.body();
                            if (places.isEmpty()) {
                                tvEmpty.setVisibility(View.VISIBLE);
                            } else {
                                adapter.updatePlaces(places);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Place>> call, Throwable t) {
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                });
    }
}