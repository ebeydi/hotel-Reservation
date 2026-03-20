package com.hotel.model;

public class Chambre {
    private String id;
    private String numero;
    private EtatChambre etat;
    private TypeChambre typeChambre;
    private Hotel hotel;

    public Chambre() {}

    // --- MÉTHODE PRATIQUE POUR LE CALCUL DU MONTANT ---
    public double getTarifNuit() {
        if (this.typeChambre != null) {
            return this.typeChambre.getTarifNuit();
        }
        return 0.0;
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
    public void setHotel(Hotel hotel) { this.hotel = hotel; }

    @Override
    public String toString() {
        return "Chambre N°" + numero + " (" + (typeChambre != null ? typeChambre.getNomType() : "N/A") + ")";
    }
}