package com.saltechdigital.pizzeria.models;

import com.google.gson.annotations.SerializedName;

public class Distance {

    @SerializedName("id_distance")
    private int id;

    @SerializedName("plage_debut_distance")
    private int begin;

    @SerializedName("plage_fin_distance")
    private int end;

    @SerializedName("prix_ht_distance")
    private float price;

    @SerializedName("lib_distance")
    private String libDistance;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getLibDistance() {
        return libDistance;
    }

    public void setLibDistance(String libDistance) {
        this.libDistance = libDistance;
    }
}
