package com.hotel.model;

public class Client extends Users {

    private String nationalite;

    public Client() {}

    public Client(String id, String login, String motDePasse, String nom,
                  String prenom, String email, UsersRole role, String nationalite) {

        super(id, login, motDePasse, nom, prenom, email, role);
        this.nationalite = nationalite;
    }

    // getters et setters
}
