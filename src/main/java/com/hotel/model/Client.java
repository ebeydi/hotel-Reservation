package com.hotel.model;

public class Client extends Users {

    private String nationalite;

    public Client() {
super( );
    }

    public Client(String id, String login, String motDePasse, String nom,
                  String prenom,String telephone,String adresse, String email, UsersRole role, String nationalite) {

        super(id, login, motDePasse, nom, prenom, telephone, adresse, email, nationalite, role);
        this.nationalite = nationalite;
    }

    // getters et setters
}
