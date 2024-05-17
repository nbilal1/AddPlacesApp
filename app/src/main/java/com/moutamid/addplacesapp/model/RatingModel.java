package com.moutamid.addplacesapp.model;

public class RatingModel {
    public String  name, rating,feedback;

    public RatingModel() {
    }

    public RatingModel(String name, String rating, String feedback) {
        this.name = name;
        this.rating = rating;
        this.feedback = feedback;
    }
}
