package com.hotel.model;

import java.util.Date;

public class Reservation {
    private String id;
    private String numReservation;
    private Date dateArrive;
    private Date dateDepart;
    private int nbPersonne;
    private float montantTotal;
    private StatutReservation statut;
    private Client client;
    private Chambre chambre;

    // Constructeur vide
    public Reservation() {}

    // Constructeur complet (utile pour le DAO)
    public Reservation(String id, String numReservation, Date dateArrive, Date dateDepart, 
                       int nbPersonne, float montantTotal, StatutReservation statut, 
                       Client client, Chambre chambre) {
        this.id = id;
        this.numReservation = numReservation;
        this.dateArrive = dateArrive;
        this.dateDepart = dateDepart;
        this.nbPersonne = nbPersonne;
        this.montantTotal = montantTotal;
        this.statut = statut;
        this.client = client;
        this.chambre = chambre;
    }

    // --- GETTERS ET SETTERS (Les vrais !) ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNumReservation() { return numReservation; }
    public void setNumReservation(String numReservation) { this.numReservation = numReservation; }

    public Date getDateArrive() { return dateArrive; }
    public void setDateArrive(Date dateArrive) { this.dateArrive = dateArrive; }

    public Date getDateDepart() { return dateDepart; }
    public void setDateDepart(Date dateDepart) { this.dateDepart = dateDepart; }

    public int getNbPersonne() { return nbPersonne; }
    public void setNbPersonne(int nbPersonne) { this.nbPersonne = nbPersonne; }

    public float getMontantTotal() { return montantTotal; }
    public void setMontantTotal(float montantTotal) { this.montantTotal = montantTotal; }

    public StatutReservation getStatut() { return statut; }
    public void setStatut(StatutReservation statut) { this.statut = statut; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public Chambre getChambre() { return chambre; }
    public void setChambre(Chambre chambre) { this.chambre = chambre; }
}