package com.saltechdigital.pizzeria.models;

import com.google.gson.annotations.SerializedName;

public class Address {

    @SerializedName("id_adreesse")
    private int id;

    @SerializedName("id_client")
    private int idClient;

    @SerializedName("id_point_repere")
    private int idRepere;

    @SerializedName("libelle_adresse")
    private String libAddress;

    private double latitude;

    private double longitude;

    //private boolean defaut;
    private int defaut;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public int getIdRepere() {
        return idRepere;
    }

    public void setIdRepere(int idRepere) {
        this.idRepere = idRepere;
    }

    public String getLibAddress() {
        return libAddress;
    }

    public void setLibAddress(String libAddress) {
        this.libAddress = libAddress;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int isDefaut() {
        return defaut;
    }

    public void setDefaut(int defaut) {
        this.defaut = defaut;
    }
}
