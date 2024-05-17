package com.moutamid.addplacesapp.model;


public class LocationModel {
    private String name;
    private String category;
    private String details;
    private String image;
    private String lat;
    private String lng;
    private String key;

    public LocationModel() {
    }

    public LocationModel(String name,  String details, String image, String category,String lat, String lng, String key) {
        this.name = name;
        this.category = category;
        this.details = details;
        this.image = image;
        this.lat = lat;
        this.lng = lng;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

