package com.pontonsoft.keepincontact.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "contacts")
public class Contact {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField
    private String prenom;

    @DatabaseField
    private String telephone;

    @DatabaseField(canBeNull = true)
    private String adresse;

    @DatabaseField(canBeNull = true)
    private String nom;

    @DatabaseField(canBeNull = true)
    private Date dateBirth;

    @DatabaseField
    private Date dateCreated;

    public Contact() {}

    public Contact(String prenom, String nom, Date dateBirth, Date dateCreated, String telephone, String adresse) {
        this.prenom = prenom;
        this.nom = nom;
        this.dateBirth = dateBirth;
        this.dateCreated = dateCreated;
        this.telephone = telephone;
        this.adresse = adresse;
    }

    public Long getId() {
        return id;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public Date getDateBirth() {
        return dateBirth;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setDateBirth(Date dateBirth) {
        this.dateBirth = dateBirth;
    }
}