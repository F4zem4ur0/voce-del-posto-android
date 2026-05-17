package com.vocedelposto.app.model;

import com.google.gson.annotations.SerializedName;

public class Place {
    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("address")
    private String address;

    @SerializedName("category")
    private String category;

    @SerializedName("latitude")
    private Double latitude;

    @SerializedName("longitude")
    private Double longitude;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getCategory() { return category; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }

    @SerializedName("phone")
    private String phone;

    @SerializedName("website")
    private String website;

    @SerializedName("openingHours")
    private String openingHours;

    @SerializedName("description")
    private String description;

    public String getPhone() { return phone; }
    public String getWebsite() { return website; }
    public String getOpeningHours() { return openingHours; }
    public String getDescription() { return description; }
}