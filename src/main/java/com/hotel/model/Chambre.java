package com.hotel.model;

import java.util.Objects;

public class Chambre {
    private String id;
    private String numero;
    private EtatChambre etat;
    private TypeChambre typeChambre;
    private Hotel hotel;
    private String hotel_id; // 🔥 Ajouté pour la cohérence avec la BD et le contrôleur

    // --- CONSTRUCTEURS ---
    public Chambre() {}

    public Chambre(String id, String numero, TypeChambre typeChambre, EtatChambre etat, String hotel_id) {
        this.id = id;
        this.numero = numero;
        this.typeChambre = typeChambre;
        this.etat = etat;
        this.hotel_id = hotel_id;
    }

    // --- LOGIQUE MÉTIER ---

    /**
     * Récupère le tarif via le type de chambre.
     */
    public double getTarifNuit() {
        return (typeChambre != null) ? typeChambre.getTarifNuit() : 0.0;
    }

    /**
     * Retourne le nom du type de chambre ou "N/A" si nul.
     * Très utile pour les colonnes de TableView.
     */
    public String getNomTypeAffiche() {
        return (typeChambre != null) ? typeChambre.getNomType() : "N/A";
    }

    // --- GETTERS ET SETTERS ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public EtatChambre getEtat() { return etat; }
    public void setEtat(EtatChambre etat) { this.etat = etat; }

    public TypeChambre getTypeChambre() { return typeChambre; }
    public void setTypeChambre(TypeChambre typeChambre) { this.typeChambre = typeChambre; }

    public Hotel getHotel() { return hotel; }
    public void setHotel(Hotel hotel) { 
        this.hotel = hotel; 
        if (hotel != null) this.hotel_id = hotel.getId(); 
    }

    public String getHotelId() { return hotel_id; }
    public void setHotelId(String hotel_id) { this.hotel_id = hotel_id; }

    // --- MÉTHODES UTILES ---

    @Override
    public String toString() {
        return "Chambre N°" + numero + " (" + getNomTypeAffiche() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chambre chambre = (Chambre) o;
        return Objects.equals(id, chambre.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}