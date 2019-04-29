package com.saltechdigital.dliver.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Livraison implements Parcelable {

    public static final Creator<Livraison> CREATOR = new Creator<Livraison>() {
        @Override
        public Livraison createFromParcel(Parcel in) {
            return new Livraison(in);
        }

        @Override
        public Livraison[] newArray(int size) {
            return new Livraison[size];
        }
    };
    @SerializedName("id_livraison")
    private int id;
    @SerializedName("code_stat_livraison")
    private int codeStatLivraison;
    @SerializedName("id_point_repere_chargement")
    private int repereCharg;
    @SerializedName("id_point_repere_dechargement")
    private int repereDecharg;
    @SerializedName("id_client")
    private int idClient;
    @SerializedName("id_distance")
    private int idDistance;
    private int internationale;
    @SerializedName("cout_ht")
    private float coutHt;
    @SerializedName("montant_tva")
    private float montantTva;
    @SerializedName("cout_ttc")
    private float coutTTC;
    @SerializedName("date_demande_livraison")
    private String dateDemandeLivraison;
    @SerializedName("distance_reelle")
    private float distanceReelle;
    @SerializedName("nom_expediteur")
    private String nomExpediteur;
    @SerializedName("adresse_expediteur")
    private String adresseExpediteur;
    @SerializedName("tel_expediteur")
    private String telExpediteur;
    @SerializedName("nom_destinataire")
    private String nomDestinataire;
    @SerializedName("adresse_destinataire")
    private String adresseDestinataire;
    @SerializedName("tel_destinataire")
    private String telDestinataire;
    @SerializedName("validate")
    private String validate;

    public Livraison() {
    }

    protected Livraison(Parcel in) {
        id = in.readInt();
        codeStatLivraison = in.readInt();
        repereCharg = in.readInt();
        repereDecharg = in.readInt();
        idClient = in.readInt();
        idDistance = in.readInt();
        internationale = in.readInt();
        coutHt = in.readFloat();
        montantTva = in.readFloat();
        coutTTC = in.readFloat();
        dateDemandeLivraison = in.readString();
        distanceReelle = in.readFloat();
        nomExpediteur = in.readString();
        adresseExpediteur = in.readString();
        telExpediteur = in.readString();
        nomDestinataire = in.readString();
        adresseDestinataire = in.readString();
        telDestinataire = in.readString();
        validate = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCodeStatLivraison() {
        return codeStatLivraison;
    }

    public void setCodeStatLivraison(int codeStatLivraison) {
        this.codeStatLivraison = codeStatLivraison;
    }

    public int getRepereCharg() {
        return repereCharg;
    }

    public void setRepereCharg(int repereCharg) {
        this.repereCharg = repereCharg;
    }

    public int getRepereDecharg() {
        return repereDecharg;
    }

    public void setRepereDecharg(int repereDecharg) {
        this.repereDecharg = repereDecharg;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public int getIdDistance() {
        return idDistance;
    }

    public void setIdDistance(int idDistance) {
        this.idDistance = idDistance;
    }

    public int isInternationale() {
        return internationale;
    }

    public void setInternationale(int internationale) {
        this.internationale = internationale;
    }

    public float getCoutHt() {
        return coutHt;
    }

    public void setCoutHt(float coutHt) {
        this.coutHt = coutHt;
    }

    public float getMontantTva() {
        return montantTva;
    }

    public void setMontantTva(float montantTva) {
        this.montantTva = montantTva;
    }

    public float getCoutTTC() {
        return coutTTC;
    }

    public void setCoutTTC(float coutTTC) {
        this.coutTTC = coutTTC;
    }

    public String getDateDemandeLivraison() {
        return dateDemandeLivraison;
    }

    public void setDateDemandeLivraison(String dateDemandeLivraison) {
        this.dateDemandeLivraison = dateDemandeLivraison;
    }

    public String getValidate() {
        return validate;
    }

    public void setValidate(String validate) {
        this.validate = validate;
    }

    public float getDistanceReelle() {
        return distanceReelle;
    }

    public void setDistanceReelle(float distanceReelle) {
        this.distanceReelle = distanceReelle;
    }

    public String getNomExpediteur() {
        return nomExpediteur;
    }

    public void setNomExpediteur(String nomExpediteur) {
        this.nomExpediteur = nomExpediteur;
    }

    public String getAdresseExpediteur() {
        return adresseExpediteur;
    }

    public void setAdresseExpediteur(String adresseExpediteur) {
        this.adresseExpediteur = adresseExpediteur;
    }

    public String getTelExpediteur() {
        return telExpediteur;
    }

    public void setTelExpediteur(String telExpediteur) {
        this.telExpediteur = telExpediteur;
    }

    public String getNomDestinataire() {
        return nomDestinataire;
    }

    public void setNomDestinataire(String nomDestinataire) {
        this.nomDestinataire = nomDestinataire;
    }

    public String getAdresseDestinataire() {
        return adresseDestinataire;
    }

    public void setAdresseDestinataire(String adresseDestinataire) {
        this.adresseDestinataire = adresseDestinataire;
    }

    public String getTelDestinataire() {
        return telDestinataire;
    }

    public void setTelDestinataire(String telDestinataire) {
        this.telDestinataire = telDestinataire;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(codeStatLivraison);
        dest.writeInt(repereCharg);
        dest.writeInt(repereDecharg);
        dest.writeInt(idClient);
        dest.writeInt(idDistance);
        dest.writeInt(internationale);
        dest.writeFloat(coutHt);
        dest.writeFloat(montantTva);
        dest.writeFloat(coutTTC);
        dest.writeString(dateDemandeLivraison);
        dest.writeFloat(distanceReelle);
        dest.writeString(nomExpediteur);
        dest.writeString(adresseExpediteur);
        dest.writeString(telExpediteur);
        dest.writeString(nomDestinataire);
        dest.writeString(adresseDestinataire);
        dest.writeString(telDestinataire);
        dest.writeString(validate);
    }
}
