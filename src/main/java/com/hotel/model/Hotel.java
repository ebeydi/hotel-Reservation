package com.hotel.model;

public class Hotel {

    private String id;
    private String nom;
    private String ville;
    private String adresse;
    private String categorie;
    private String description;
    private String telephone;
    private String email;
    private Statut statut; // 🔥 enum
    private String image;

    // GETTERS / SETTERS

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}