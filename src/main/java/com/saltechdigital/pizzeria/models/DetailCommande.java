package com.saltechdigital.pizzeria.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity/*(
        //primaryKeys = {"id", "idCommande", "idPlat"},
        foreignKeys = {
                @ForeignKey(
                        entity = Plat.class,
                        parentColumns = "idPlat",
                        childColumns = "idPlat"
                ),
                *//*@ForeignKey(
                        entity = Commande.class,
                        parentColumns = "idCommande",
                        childColumns = "idCommande"
                ),*//*
        },
        indices = {
                @Index("id"),
                @Index(value = "idPlat",unique = true),
                //@Index(value = "idCommande",unique = true)
        }
)*/
public class DetailCommande {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int idCommande;

    private int idPlat;

    private float prixDetail;

    private int isMenu;

    private String infos;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCommande() {
        return idCommande;
    }

    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
    }

    public int getIdPlat() {
        return idPlat;
    }

    public void setIdPlat(int idPlat) {
        this.idPlat = idPlat;
    }

    public float getPrixDetail() {
        return prixDetail;
    }

    public void setPrixDetail(float prixDetail) {
        this.prixDetail = prixDetail;
    }

    public int getIsMenu() {
        return isMenu;
    }

    public void setIsMenu(int isMenu) {
        this.isMenu = isMenu;
    }

    public String getInfos() {
        return infos;
    }

    public void setInfos(String infos) {
        this.infos = infos;
    }

    @NonNull
    @Override
    public String toString() {
        return "id :" + idCommande + " plat :" + idPlat;
    }
}