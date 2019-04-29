package com.saltechdigital.dliver.models;

import com.google.gson.annotations.SerializedName;

public class Repere {

    @SerializedName("id_point_repere")
    private int id;

    @SerializedName("lib_point_repere")
    private String libRepere;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("latitude")
    private double latitude;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLibRepere() {
        return libRepere;
    }

    public void setLibRepere(String libRepere) {
        this.libRepere = libRepere;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
