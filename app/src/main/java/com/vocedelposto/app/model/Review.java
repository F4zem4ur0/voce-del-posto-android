package com.vocedelposto.app.model;

import com.google.gson.annotations.SerializedName;

public class Review {
    @SerializedName("id")
    private Long id;

    @SerializedName("placeId")
    private Long placeId;

    @SerializedName("placeName")
    private String placeName;

    @SerializedName("rating")
    private int rating;

    @SerializedName("comment")
    private String comment;

    @SerializedName("createdAt")
    private String createdAt;

    public Long getId() { return id; }
    public Long getPlaceId() { return placeId; }
    public String getPlaceName() { return placeName; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public String getCreatedAt() { return createdAt; }
}