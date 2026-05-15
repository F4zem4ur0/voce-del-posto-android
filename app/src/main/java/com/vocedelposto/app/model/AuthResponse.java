package com.vocedelposto.app.model;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("token")
    private String token;

    @SerializedName("userId")
    private Long userId;

    @SerializedName("username")
    private String username;

    public String getToken() { return token; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
}