package com.saltechdigital.dliver.models;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

public class Client {

    int id;

    @SerializedName("nom_client")
    private String nomClient;

    @SerializedName("email_client")
    private String emailClient;

    @SerializedName("tel1_client")
    private String phone1;

    @SerializedName("tel2_client")
    private String phone2;


    private String adresse;

    @SerializedName("photo_client")
    private String photo;

    @SerializedName("status")
    private boolean statut;

    public String getNomClient() {
        return nomClient;
    }

    public void setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }

    public String getEmailClient() {
        return emailClient;
    }

    public void setEmailClient(String emailClient) {
        this.emailClient = emailClient;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isStatut() {
        return statut;
    }

    public void setStatut(boolean statut) {
        this.statut = statut;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    @NotNull
    @Override
    public String toString() {
        return "Nom : " + nomClient + " Tel : " + phone1;
    }
}
