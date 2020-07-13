package com.google.zomatot.model;

import org.osmdroid.util.GeoPoint;

public class Restaurant {
    private Long id;
    private String name;
    private String address;
    private String rating;
    private String cost;
    private String imageUrl;
    private String currency;
    private double longitude;
    private double latitiude;


    public Restaurant() {
    }

    public Restaurant(Long id, String name, String address, String rating, String cost, String imageUrl, String currency, double longitude, double latitiude) {

        this.id = id;
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.cost = cost;
        this.imageUrl = imageUrl;
        this.currency = currency;
        this.longitude = longitude;
        this.latitiude = latitiude;
    }

    public String getName() {

        return name;
    }

    public void setId(Long id) { this.id = id;}

    public Long getId() { return id; }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitiude() {
        return latitiude;
    }

    public void setLatitiude(double latitiude) {
        this.latitiude = latitiude;
    }

    public GeoPoint getRestaurantGeoPoint(){
        return new GeoPoint(getLatitiude(), getLongitude());
    }
}
