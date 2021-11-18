package com.mobiledev.locationfinder.models;

public class Location {

    String address;
    String latitude;
    String longitude;
    String id;

    public Location(String id, String address, String latitude, String longitude) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
    }

    public Location(){}

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
