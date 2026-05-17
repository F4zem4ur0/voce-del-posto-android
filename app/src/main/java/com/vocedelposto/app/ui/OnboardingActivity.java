package com.vocedelposto.app.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vocedelposto.app.HomeActivity;
import com.vocedelposto.app.R;
import com.vocedelposto.app.model.Tag;
import com.vocedelposto.app.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OnboardingActivity extends AppCompatActivity {

    private RecyclerView rvTags;
    private TagsAdapter adapter;
    private Long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        SharedPreferences prefs = getSharedPreferences("voce_del_posto", MODE_PRIVATE);
        userId = prefs.getLong("userId", -1);

        rvTags = findViewById(R.id.rvTags);
        rvTags.setLayoutManager(new LinearLayoutManager(this));

        loadTags();

        findViewById(R.id.btnContinue).setOnClickListener(v -> saveTags());
        findViewById(R.id.btnSkip).setOnClickListener(v -> goToHome());
    }

    private void loadTags() {
        RetrofitClient.getInstance().getApiService()
                .getAllTags()
                .enqueue(new Callback<List<Tag>>() {
                    @Override
                    public void onResponse(Call<List<Tag>> call, Response<List<Tag>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            adapter = new TagsAdapter(response.body(), new ArrayList<>());
                            rvTags.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Tag>> call, Throwable t) {
                        goToHome();
                    }
                });
    }

    private void saveTags() {
        if (adapter == null) {
            goToHome();
            return;
        }

        List<Tag> selectedTags = adapter.getSelectedTags();
        if (selectedTags.isEmpty()) {
            goToHome();
            return;
        }

        RetrofitClient.getInstance().getApiService()
                .updateUserTags(userId, selectedTags)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        goToHome();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        goToHome();
                    }
                });
    }

    private void goToHome() {
        getSharedPreferences("voce_del_posto", MODE_PRIVATE)
                .edit()
                .putBoolean("onboarding_completed", true)
                .apply();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}