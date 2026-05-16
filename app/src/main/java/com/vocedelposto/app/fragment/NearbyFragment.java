package com.vocedelposto.app.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vocedelposto.app.R;
import com.vocedelposto.app.model.Place;
import com.vocedelposto.app.network.RetrofitClient;
import com.vocedelposto.app.ui.PlacesAdapter;
import com.vocedelposto.app.ui.ReviewActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class NearbyFragment extends Fragment {

    private RecyclerView rvPlaces;
    private PlacesAdapter adapter;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nearby, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progressBar);
        rvPlaces = view.findViewById(R.id.rvPlaces);
        rvPlaces.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PlacesAdapter(getContext(), new ArrayList<>());
        rvPlaces.setAdapter(adapter);

        loadNearbyPlaces(44.8015, 10.3279, 5.0);
    }

    private void loadNearbyPlaces(double lat, double lon, double radius) {
        progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.getInstance().getApiService()
                .getPlacesNearby(lat, lon, radius)
                .enqueue(new Callback<List<Place>>() {
                    @Override
                    public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            adapter.updatePlaces(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Place>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}