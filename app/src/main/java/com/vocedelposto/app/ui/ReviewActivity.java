package com.vocedelposto.app.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.vocedelposto.app.R;
import com.vocedelposto.app.model.ReviewRequest;
import com.vocedelposto.app.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewActivity extends AppCompatActivity {

    private TextView tvPlaceName, tvError, tvAlreadyReviewed;
    private RatingBar ratingBar;
    private EditText etComment;
    private Button btnSubmit;

    private Long placeId;
    private String placeName;
    private Long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        placeId = getIntent().getLongExtra("placeId", -1);
        placeName = getIntent().getStringExtra("placeName");

        SharedPreferences prefs = getSharedPreferences("voce_del_posto", MODE_PRIVATE);
        userId = prefs.getLong("userId", -1);

        tvPlaceName = findViewById(R.id.tvPlaceName);
        ratingBar = findViewById(R.id.ratingBar);
        etComment = findViewById(R.id.etComment);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvError = findViewById(R.id.tvError);
        tvAlreadyReviewed = findViewById(R.id.tvAlreadyReviewed);

        tvPlaceName.setText(placeName);

        ratingBar.setVisibility(View.GONE);
        etComment.setVisibility(View.GONE);
        btnSubmit.setVisibility(View.GONE);

        checkIfAlreadyReviewed();

        btnSubmit.setOnClickListener(v -> submitReview());
    }

    private void checkIfAlreadyReviewed() {
        RetrofitClient.getInstance().getApiService()
                .checkReviewExists(userId, placeId)
                .enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.isSuccessful() && response.body() != null && response.body()) {
                            // ha già recensito — nascondi il form
                            ratingBar.setVisibility(View.GONE);
                            etComment.setVisibility(View.GONE);
                            btnSubmit.setVisibility(View.GONE);
                            tvAlreadyReviewed.setVisibility(View.VISIBLE);
                        } else {
                            // non ha ancora recensito — mostra il form
                            ratingBar.setVisibility(View.VISIBLE);
                            etComment.setVisibility(View.VISIBLE);
                            btnSubmit.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        // in caso di errore lasciamo il form visibile
                    }
                });
    }

    private void submitReview() {
        int rating = (int) ratingBar.getRating();
        String comment = etComment.getText().toString().trim();

        if (rating == 0) {
            showError("Seleziona una valutazione");
            return;
        }

        btnSubmit.setEnabled(false);
        ReviewRequest request = new ReviewRequest(userId, placeId, rating, comment);

        RetrofitClient.getInstance().getApiService()
                .createReview(request)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        btnSubmit.setEnabled(true);
                        if (response.isSuccessful()) {
                            finish();
                        } else {
                            showError("Errore nell'invio della recensione");
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        btnSubmit.setEnabled(true);
                        showError("Errore di rete: " + t.getMessage());
                    }
                });
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

}