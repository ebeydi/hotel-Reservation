package com.hotel.model;

/**
 * Définit une catégorie de chambre (ex: Suite Junior, Chambre Double Eco).
 * Permet de centraliser le tarif et la capacité pour toutes les chambres de ce type.
 */
public class TypeChambre {
    private String id;
    private String nomType;    // ex: "Suite Royale"
    private int capacite;      // Nombre de personnes (ex: 2, 4)
    private double tarifNuit;  // Utilisation de double pour les prix (ex: 45000.0)
    private String description;
    private Statut statut;     // Pour savoir si ce type est encore proposé
    private Hotel hotel;       // Liaison avec l'hôtel propriétaire

    public TypeChambre() {}

    // --- GETTERS ET SETTERS ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomType() {
        return nomType;
    }

    public void setNomType(String nomType) {
        this.nomType = nomType;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public double getTarifNuit() {
        return tarifNuit;
    }

    public void setTarifNuit(double tarifNuit) {
        this.tarifNuit = tarifNuit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    /**
     * Très utile pour remplir les ComboBox dans l'interface JavaFX
     */
    @Override
    public String toString() {
        return nomType + " (" + tarifNuit + " FCFA)";
    }
}