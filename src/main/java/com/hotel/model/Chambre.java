package com.hotel.model;

/**
 * Modèle de données pour une chambre d'hôtel.
 * Gère le numéro, l'état (Enum), le type (Enum) et l'appartenance à un Hôtel.
 */
public class Chambre {
    private String id;
    private String numero;
    private EtatChambre etat;
    private TypeChambre typeChambre;
    private Hotel hotel;

    public Chambre() {}

    // --- GETTERS ET SETTERS ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public EtatChambre getEtat() {
        return etat;
    }

    public void setEtat(EtatChambre etat) {
        this.etat = etat;
    }

    public TypeChambre getTypeChambre() {
        return typeChambre;
    }

    public void setTypeChambre(TypeChambre typeChambre) {
        this.typeChambre = typeChambre;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    /**
     * Utile pour afficher la chambre dans une liste ou un log
     */
    @Override
    public String toString() {
        return "Chambre N°" + numero + " [" + typeChambre + "] - " + etat;
    }
}