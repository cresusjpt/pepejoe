package com.saltechdigital.pizzeria.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class Categorie {

    @SerializedName("id_categorie")
    @PrimaryKey(autoGenerate = true)
    private int idCategorie;

    @SerializedName("nom_categorie")
    private String nomCategorie;

    @SerializedName("description")
    private String description;

    private String tag;

    public Categorie(int idCategorie, String nomCategorie, String description) {
        this.idCategorie = idCategorie;
        this.nomCategorie = nomCategorie;
        this.description = description;
    }

    public int getIdCategorie() {
        return idCategorie;
    }

    public void setIdCategorie(int idCategorie) {
        this.idCategorie = idCategorie;
    }

    public String getNomCategorie() {
        return nomCategorie;
    }

    public void setNomCategorie(String nomCategorie) {
        this.nomCategorie = nomCategorie;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
