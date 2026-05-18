package com.vocedelposto.app.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vocedelposto.app.R;
import com.vocedelposto.app.model.Review;
import com.vocedelposto.app.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyReviewsActivity extends AppCompatActivity {

    private RecyclerView rvReviews;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reviews);

        SharedPreferences prefs = getSharedPreferences("voce_del_posto", MODE_PRIVATE);
        Long userId = prefs.getLong("userId", -1);

        tvEmpty = findViewById(R.id.tvEmpty);
        rvReviews = findViewById(R.id.rvReviews);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));

        RetrofitClient.getInstance().getApiService()
                .getUserReviews(userId)
                .enqueue(new Callback<List<Review>>() {
                    @Override
                    public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isEmpty()) {
                                tvEmpty.setVisibility(View.VISIBLE);
                            } else {
                                rvReviews.setAdapter(new MyReviewsAdapter(response.body()));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Review>> call, Throwable t) {
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                });
    }
}