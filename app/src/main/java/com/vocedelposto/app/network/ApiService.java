package com.vocedelposto.app.network;

import com.vocedelposto.app.model.AuthRequest;
import com.vocedelposto.app.model.AuthResponse;
import com.vocedelposto.app.model.Place;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

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
}