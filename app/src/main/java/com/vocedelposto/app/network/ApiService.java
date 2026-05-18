package com.vocedelposto.app.network;

import com.vocedelposto.app.model.AuthRequest;
import com.vocedelposto.app.model.AuthResponse;
import com.vocedelposto.app.model.Place;
import com.vocedelposto.app.model.Review;
import com.vocedelposto.app.model.ReviewRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

import com.vocedelposto.app.model.Tag;

public interface ApiService {

    @POST("auth/login")
    Call<AuthResponse> login(@Body AuthRequest request);

    @POST("auth/register")
    Call<AuthResponse> register(@Body AuthRequest request);

    @GET("places/nearby")
    Call<List<Place>> getPlacesNearby(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("radius") double radius
    );

    @POST("reviews")
    Call<Void> createReview(@Body ReviewRequest review);

    @GET("recommendations/{userId}")
    Call<List<Place>> getRecommendations(@Path("userId") Long userId);

    @GET("reviews/exists")
    Call<Boolean> checkReviewExists(
            @Query("userId") Long userId,
            @Query("placeId") Long placeId
    );

    @GET("tags")
    Call<List<Tag>> getAllTags();

    @GET("users/{id}/tags")
    Call<List<Tag>> getUserTags(@Path("id") Long userId);

    @PUT("users/{id}/tags")
    Call<Void> updateUserTags(@Path("id") Long userId, @Body List<Tag> tags);

    @GET("reviews/user/{userId}")
    Call<List<Review>> getUserReviews(@Path("userId") Long userId);

    @PUT("reviews/{id}")
    Call<Void> updateReview(@Path("id") Long reviewId, @Body ReviewRequest review);
}