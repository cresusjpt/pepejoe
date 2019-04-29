package com.saltechdigital.dliver.models;

import com.google.gson.annotations.SerializedName;

public class Notifications {

    @SerializedName("notif_id")
    private int id;

    @SerializedName("code_agent")
    private String agent;

    @SerializedName("id_livraison")
    private int livraison;

    @SerializedName("notif_date")
    private String date;

    @SerializedName("id_client")
    private int client;
    @SerializedName("notif_date_echeance")
    private String echeance;
    @SerializedName("notif_titre")
    private String title;
    @SerializedName("notif_short_desc")
    private String shortDesc;
    @SerializedName("notif_desc")
    private String description;
    @SerializedName("notif_icon")
    private String icon;
    @SerializedName("marquee")
    private int marquee;
    @SerializedName("actif")
    private int actif;
    @SerializedName("date_vue")
    private String viewDate;

    public int getClient() {
        return client;
    }

    public void setClient(int client) {
        this.client = client;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public int getLivraison() {
        return livraison;
    }

    public void setLivraison(int livraison) {
        this.livraison = livraison;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEcheance() {
        return echeance;
    }

    public void setEcheance(String echeance) {
        this.echeance = echeance;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getMarquee() {
        return marquee;
    }

    public void setMarquee(int marquee) {
        this.marquee = marquee;
    }

    public int getActif() {
        return actif;
    }

    public void setActif(int actif) {
        this.actif = actif;
    }

    public String getViewDate() {
        return viewDate;
    }

    public void setViewDate(String viewDate) {
        this.viewDate = viewDate;
    }
}
