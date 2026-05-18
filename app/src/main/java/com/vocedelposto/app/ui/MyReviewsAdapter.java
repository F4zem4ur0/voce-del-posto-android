package com.vocedelposto.app.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vocedelposto.app.R;
import com.vocedelposto.app.model.Review;
import com.vocedelposto.app.model.ReviewRequest;
import com.vocedelposto.app.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyReviewsAdapter extends RecyclerView.Adapter<MyReviewsAdapter.ReviewViewHolder> {

    private List<Review> reviews;
    private Context context;

    public MyReviewsAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.tvPlaceName.setText(review.getPlaceName());
        holder.ratingBar.setRating(review.getRating());
        holder.tvComment.setText(review.getComment() != null ? review.getComment() : "");
        String date = review.getCreatedAt() != null ? review.getCreatedAt().substring(0, 10) : "";
        holder.tvDate.setText(date);

        holder.btnEdit.setOnClickListener(v -> showEditDialog(review, position));
    }

    private void showEditDialog(Review review, int position) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_review, null);
        RatingBar ratingBar = dialogView.findViewById(R.id.dialogRatingBar);
        EditText etComment = dialogView.findViewById(R.id.dialogEtComment);

        ratingBar.setRating(review.getRating());
        etComment.setText(review.getComment());

        new AlertDialog.Builder(context)
                .setTitle("Modifica recensione")
                .setView(dialogView)
                .setPositiveButton("Salva", (dialog, which) -> {
                    int newRating = (int) ratingBar.getRating();
                    String newComment = etComment.getText().toString().trim();

                    if (newRating == 0) {
                        Toast.makeText(context, "Seleziona una valutazione", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ReviewRequest request = new ReviewRequest(
                            review.getUserId(), review.getPlaceId(), newRating, newComment);

                    RetrofitClient.getInstance().getApiService()
                            .updateReview(review.getId(), request)
                            .enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        reviews.get(position).setRating(newRating);
                                        reviews.get(position).setComment(newComment);
                                        notifyItemChanged(position);
                                        Toast.makeText(context, "Recensione aggiornata!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(context, "Errore di rete", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Annulla", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlaceName, tvComment, tvDate;
        RatingBar ratingBar;
        Button btnEdit;

        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlaceName = itemView.findViewById(R.id.tvPlaceName);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}