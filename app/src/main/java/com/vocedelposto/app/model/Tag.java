package com.vocedelposto.app.model;

import com.google.gson.annotations.SerializedName;

public class Tag {
    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("category")
    private String category;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public void setId(Long id) { this.id = id; }
}