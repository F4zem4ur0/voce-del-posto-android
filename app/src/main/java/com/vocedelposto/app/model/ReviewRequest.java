package com.vocedelposto.app.model;

public class ReviewRequest {
    private UserRef user;
    private PlaceRef place;
    private int rating;
    private String comment;

    public ReviewRequest(Long userId, Long placeId, int rating, String comment) {
        this.user = new UserRef(userId);
        this.place = new PlaceRef(placeId);
        this.rating = rating;
        this.comment = comment;
    }

    static class UserRef {
        private Long id;
        UserRef(Long id) { this.id = id; }
    }

    static class PlaceRef {
        private Long id;
        PlaceRef(Long id) { this.id = id; }
    }

    public UserRef getUser() { return user; }
    public PlaceRef getPlace() { return place; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
}