package com.vocedelposto.app.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vocedelposto.app.R;
import com.vocedelposto.app.model.Review;

import java.util.List;

public class MyReviewsAdapter extends RecyclerView.Adapter<MyReviewsAdapter.ReviewViewHolder> {

    private List<Review> reviews;

    public MyReviewsAdapter(List<Review> reviews) {
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
        // mostra solo la data senza l'ora
        String date = review.getCreatedAt() != null ? review.getCreatedAt().substring(0, 10) : "";
        holder.tvDate.setText(date);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlaceName, tvComment, tvDate;
        RatingBar ratingBar;

        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlaceName = itemView.findViewById(R.id.tvPlaceName);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}