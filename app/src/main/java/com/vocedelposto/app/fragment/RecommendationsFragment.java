package com.vocedelposto.app.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vocedelposto.app.R;
import com.vocedelposto.app.model.Place;
import com.vocedelposto.app.network.RetrofitClient;
import com.vocedelposto.app.ui.PlacesAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class RecommendationsFragment extends Fragment {

    private RecyclerView rvRecommendations;
    private PlacesAdapter adapter;
    private TextView tvEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recommendations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvEmpty = view.findViewById(R.id.tvEmpty);
        rvRecommendations = view.findViewById(R.id.rvRecommendations);
        rvRecommendations.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PlacesAdapter(getContext(), new ArrayList<>());
        rvRecommendations.setAdapter(adapter);

        SharedPreferences prefs = requireContext().getSharedPreferences("voce_del_posto", MODE_PRIVATE);
        Long userId = prefs.getLong("userId", -1);
        loadRecommendations(userId);
    }

    private void loadRecommendations(Long userId) {
        RetrofitClient.getInstance().getApiService()
                .getRecommendations(userId)
                .enqueue(new Callback<List<Place>>() {
                    @Override
                    public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isEmpty()) {
                                tvEmpty.setVisibility(View.VISIBLE);
                            } else {
                                adapter.updatePlaces(response.body());
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