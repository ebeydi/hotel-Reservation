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

    public Reservation(){}
}
