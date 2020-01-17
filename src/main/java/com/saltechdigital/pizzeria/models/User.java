package com.saltechdigital.pizzeria.models;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

public class User {

    private String username;

    @SerializedName("is_client")
    private boolean isClient;

    @SerializedName("is_agent")
    private boolean isAgent;

    @SerializedName("id_client")
    private int idClient;

    @SerializedName("code_agent")
    private String codeAgent;

    private String password;

    @SerializedName("rawpassword")
    private String rawPassword;

    private String nom;
    private String email;

    @SerializedName("date_creation")
    private String dateCreation;

    private String contact;

    @SerializedName("auth_key")
    private String authKey;

    @SerializedName("access_token")
    private String accessToken;

    private boolean actif;

    @SerializedName("status")
    private boolean statut;

    @SerializedName("photo_user")
    private String userPhoto;
    private String message;

    public boolean isClient() {
        return isClient;
    }

    public void setClient(boolean client) {
        isClient = client;
    }

    public boolean isAgent() {
        return isAgent;
    }

    public void setAgent(boolean agent) {
        isAgent = agent;
    }

    public String getRawPassword() {
        return rawPassword;
    }

    public void setRawPassword(String rawPassword) {
        this.rawPassword = rawPassword;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    @NotNull
    @Override
    public String toString() {
        return "Username" + username +
                "Name " + nom +
                "Status " + statut +
                "Message " + message +
                "Id client" + idClient;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public String getCodeAgent() {
        return codeAgent;
    }

    public void setCodeAgent(String codeAgent) {
        this.codeAgent = codeAgent;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public boolean isStatut() {
        return statut;
    }

    public void setStatut(boolean statut) {
        this.statut = statut;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
