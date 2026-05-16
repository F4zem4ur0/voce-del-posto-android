package com.vocedelposto.app.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vocedelposto.app.R;
import com.vocedelposto.app.model.Tag;
import com.vocedelposto.app.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TagsActivity extends AppCompatActivity {

    private RecyclerView rvTags;
    private TagsAdapter adapter;
    private Button btnSaveTags;
    private Long userId;
    private List<Tag> allTags = new ArrayList<>();
    private List<Tag> userTags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);

        SharedPreferences prefs = getSharedPreferences("voce_del_posto", MODE_PRIVATE);
        userId = prefs.getLong("userId", -1);

        rvTags = findViewById(R.id.rvTags);
        rvTags.setLayoutManager(new LinearLayoutManager(this));
        btnSaveTags = findViewById(R.id.btnSaveTags);

        btnSaveTags.setOnClickListener(v -> saveTags());

        loadTags();
    }

    private void loadTags() {
        // prima carica i tag dell'utente
        RetrofitClient.getInstance().getApiService()
                .getUserTags(userId)
                .enqueue(new Callback<List<Tag>>() {
                    @Override
                    public void onResponse(Call<List<Tag>> call, Response<List<Tag>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            userTags = response.body();
                        }
                        loadAllTags(); // poi carica tutti i tag
                    }

                    @Override
                    public void onFailure(Call<List<Tag>> call, Throwable t) {
                        loadAllTags();
                    }
                });
    }

    private void loadAllTags() {
        RetrofitClient.getInstance().getApiService()
                .getAllTags()
                .enqueue(new Callback<List<Tag>>() {
                    @Override
                    public void onResponse(Call<List<Tag>> call, Response<List<Tag>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            allTags = response.body();
                            adapter = new TagsAdapter(allTags, userTags);
                            rvTags.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Tag>> call, Throwable t) {
                        Toast.makeText(TagsActivity.this,
                                "Errore di rete", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveTags() {
        List<Tag> selectedTags = adapter.getSelectedTags();
        btnSaveTags.setEnabled(false);

        RetrofitClient.getInstance().getApiService()
                .updateUserTags(userId, selectedTags)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        btnSaveTags.setEnabled(true);
                        if (response.isSuccessful()) {
                            Toast.makeText(TagsActivity.this,
                                    "Preferenze salvate!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(TagsActivity.this,
                                    "Errore nel salvataggio: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        btnSaveTags.setEnabled(true);
                        Toast.makeText(TagsActivity.this,
                                "Errore di rete", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}