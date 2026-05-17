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
import com.vocedelposto.app.model.Place;
import com.vocedelposto.app.model.ReviewRequest;
import com.vocedelposto.app.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceDetailActivity extends AppCompatActivity {

    private TextView tvName, tvCategory, tvAddress, tvPhone, tvWebsite, tvHours;
    private TextView tvAlreadyReviewed, tvError;
    private RatingBar ratingBar;
    private EditText etComment;
    private Button btnSubmit;

    private Long placeId;
    private Long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        placeId = getIntent().getLongExtra("placeId", -1);
        String name = getIntent().getStringExtra("placeName");
        String category = getIntent().getStringExtra("placeCategory");
        String address = getIntent().getStringExtra("placeAddress");
        String phone = getIntent().getStringExtra("placePhone");
        String website = getIntent().getStringExtra("placeWebsite");
        String hours = getIntent().getStringExtra("placeHours");

        SharedPreferences prefs = getSharedPreferences("voce_del_posto", MODE_PRIVATE);
        userId = prefs.getLong("userId", -1);

        tvName = findViewById(R.id.tvName);
        tvCategory = findViewById(R.id.tvCategory);
        tvAddress = findViewById(R.id.tvAddress);
        tvPhone = findViewById(R.id.tvPhone);
        tvWebsite = findViewById(R.id.tvWebsite);
        tvHours = findViewById(R.id.tvHours);
        tvAlreadyReviewed = findViewById(R.id.tvAlreadyReviewed);
        tvError = findViewById(R.id.tvError);
        ratingBar = findViewById(R.id.ratingBar);
        etComment = findViewById(R.id.etComment);
        btnSubmit = findViewById(R.id.btnSubmit);

        tvName.setText(name);
        tvCategory.setText(category != null ? category : "");
        tvAddress.setText(address != null ? address : "Non disponibile");
        tvPhone.setText(phone != null ? phone : "Non disponibile");
        tvWebsite.setText(website != null ? website : "Non disponibile");
        tvHours.setText(hours != null ? hours : "Non disponibile");

        // nascondi form durante il controllo
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
                            tvAlreadyReviewed.setVisibility(View.VISIBLE);
                        } else {
                            ratingBar.setVisibility(View.VISIBLE);
                            etComment.setVisibility(View.VISIBLE);
                            btnSubmit.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        ratingBar.setVisibility(View.VISIBLE);
                        etComment.setVisibility(View.VISIBLE);
                        btnSubmit.setVisibility(View.VISIBLE);
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
                            ratingBar.setVisibility(View.GONE);
                            etComment.setVisibility(View.GONE);
                            btnSubmit.setVisibility(View.GONE);
                            tvAlreadyReviewed.setVisibility(View.VISIBLE);
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