package com.hotel.model;

/**
 * Définit une catégorie de chambre (ex: Suite Junior, Chambre Double Eco).
 */
public class TypeChambre {
    private String id;
    private String nomType;
    private int capacite;
    private double tarifNuit;
    private String description;
    private Statut statut;
    private Hotel hotel;       // L'objet complet (pour la logique Java)
    private String hotel_id;    // 🔥 L'ID seul (pour faciliter les échanges avec la BDD)

    public TypeChambre() {}

    // --- GETTERS ET SETTERS ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNomType() { return nomType; }
    public void setNomType(String nomType) { this.nomType = nomType; }

    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }

    public double getTarifNuit() { return tarifNuit; }
    public void setTarifNuit(double tarifNuit) { this.tarifNuit = tarifNuit; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }

    // --- LIAISON HÔTEL ---

    public Hotel getHotel() { return hotel; }
    
    public void setHotel(Hotel hotel) { 
        this.hotel = hotel; 
        if (hotel != null) {
            this.hotel_id = hotel.getId(); // Synchronise l'ID si on passe l'objet
        }
    }

    // 🔥 AJOUT DE CETTE MÉTHODE POUR RÉPARER L'ERREUR
    public String getHotel_id() { 
        return hotel_id; 
    }

    public void setHotel_id(String hotel_id) { 
        this.hotel_id = hotel_id; 
    }

    @Override
    public String toString() {
        return nomType + " (" + tarifNuit + " FCFA)";
    }
}