package com.hotel.model;

/**
 * La classe Receptionniste hérite de Users.
 * Elle utilise les champs déjà présents dans Users (id, login, hotelId, etc.)
 */
public class Receptionniste extends Users {

    /**
     * Constructeur par défaut
     * Initialise automatiquement le rôle à RECEPTIONNISTE
     */
    public Receptionniste() {
        super();
        this.setRole(UsersRole.RECEPTIONNISTE);
    }

    /**
     * Constructeur complet pour faciliter les créations rapides
     */
    public Receptionniste(String id, String login, String motDePasse, String nom, String prenom,
                          String telephone, String adresse, String email, String nationalite, String hotelId) {
        
        // Appelle le constructeur de la classe parente Users
        super(id, login, motDePasse, nom, prenom, telephone, adresse, email, nationalite, UsersRole.RECEPTIONNISTE, hotelId);
    }

    // Note : Tu n'as pas besoin de rajouter de Getters/Setters pour hotelId 
    // car ils sont déjà hérités de la classe Users.
}