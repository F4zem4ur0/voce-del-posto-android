package com.vocedelposto.app.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.vocedelposto.app.R;
import com.vocedelposto.app.model.Place;
import com.vocedelposto.app.network.RetrofitClient;
import com.vocedelposto.app.ui.PlacesAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearbyFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST = 1001;
    private RecyclerView rvPlaces;
    private PlacesAdapter adapter;
    private ProgressBar progressBar;
    private FusedLocationProviderClient fusedLocationClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nearby, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        progressBar = view.findViewById(R.id.progressBar);
        rvPlaces = view.findViewById(R.id.rvPlaces);
        rvPlaces.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PlacesAdapter(getContext(), new ArrayList<>());
        rvPlaces.setAdapter(adapter);

        requestLocationAndLoad();
    }

    private void requestLocationAndLoad() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        } else {
            getLocationAndLoad();
        }
    }

    private void getLocationAndLoad() {
        android.content.SharedPreferences prefs = requireContext()
                .getSharedPreferences("voce_del_posto", android.content.Context.MODE_PRIVATE);
        double radius = prefs.getInt("search_radius", 5);

        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            loadNearbyPlaces(44.8015, 10.3279, radius);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        loadNearbyPlaces(location.getLatitude(), location.getLongitude(), radius);
                    } else {
                        loadNearbyPlaces(44.8015, 10.3279, radius);
                    }
                })
                .addOnFailureListener(e -> loadNearbyPlaces(44.8015, 10.3279, radius));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationAndLoad();
            } else {
                android.content.SharedPreferences prefs = requireContext()
                        .getSharedPreferences("voce_del_posto", android.content.Context.MODE_PRIVATE);
                double radius = prefs.getInt("search_radius", 5);
                loadNearbyPlaces(44.8015, 10.3279, radius);
            }
        }
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