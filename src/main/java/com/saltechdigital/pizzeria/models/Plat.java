package com.saltechdigital.pizzeria.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(foreignKeys = @ForeignKey(entity = Categorie.class,
parentColumns = "idCategorie",childColumns = "idCategorie"),indices ={@Index("idCategorie")})

public class Plat implements Parcelable {
    @SerializedName("id_plat")
    @PrimaryKey
    private int idPlat;

    @SerializedName("id_categorie")
    private int idCategorie;

    @SerializedName("nom_plat")
    private String nomPlat;

    @SerializedName("description_plat")
    private String description;

    @SerializedName("photo")
    private String photo;

    @SerializedName("prix_p")
    private float prixPetite;

    @SerializedName("prix_m")
    private float prixMoyen;

    @SerializedName("prix_g")
    private float prix_geante;

    public Plat() {}

    protected Plat(Parcel in) {
        idPlat = in.readInt();
        idCategorie = in.readInt();
        nomPlat = in.readString();
        description = in.readString();
        prixPetite = in.readFloat();
        prixMoyen = in.readFloat();
        prix_geante = in.readFloat();
    }

    public static final Creator<Plat> CREATOR = new Creator<Plat>() {
        @Override
        public Plat createFromParcel(Parcel in) {
            return new Plat(in);
        }

        @Override
        public Plat[] newArray(int size) {
            return new Plat[size];
        }
    };

    public int getIdPlat() {
        return idPlat;
    }

    public void setIdPlat(int idPlat) {
        this.idPlat = idPlat;
    }

    public int getIdCategorie() {
        return idCategorie;
    }

    public void setIdCategorie(int idCategorie) {
        this.idCategorie = idCategorie;
    }

    public String getNomPlat() {
        return nomPlat;
    }

    public void setNomPlat(String nomPlat) {
        this.nomPlat = nomPlat;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrixPetite() {
        return prixPetite;
    }

    public void setPrixPetite(float prixPetite) {
        this.prixPetite = prixPetite;
    }

    public float getPrixMoyen() {
        return prixMoyen;
    }

    public void setPrixMoyen(float prixMoyen) {
        this.prixMoyen = prixMoyen;
    }

    public float getPrix_geante() {
        return prix_geante;
    }

    public void setPrix_geante(float prix_geante) {
        this.prix_geante = prix_geante;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idPlat);
        dest.writeInt(idCategorie);
        dest.writeString(nomPlat);
        dest.writeString(description);
        dest.writeFloat(prixPetite);
        dest.writeFloat(prixMoyen);
        dest.writeFloat(prix_geante);
    }
}
