package com.saltechdigital.pizzeria.models;

import com.google.gson.annotations.SerializedName;

public class OrderResource {

    @SerializedName("id_resource")
    private int id;

    @SerializedName("id_livraison")
    private int idLLivraison;

    @SerializedName("path")
    private String path;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdLLivraison() {
        return idLLivraison;
    }

    public void setIdLLivraison(int idLLivraison) {
        this.idLLivraison = idLLivraison;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
