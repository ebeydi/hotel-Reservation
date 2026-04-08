package com.hotel.model;

public class Client extends Users {


    public Client() {
        super();
    }

    public Client(String id, String login, String motDePasse, String nom, String prenom, 
                  String telephone, String adresse, String email, String nationalite, 
                  UsersRole role, String hotelId) {
        
        // On appelle le constructeur de Users avec TOUS les paramètres, y compris hotelId
        super(id, login, motDePasse, nom, prenom, telephone, adresse, email, nationalite, role, hotelId);
    }

    // Si tu veux des méthodes spécifiques aux clients, tu les ajoutes ici.
    // La nationalité est déjà accessible via getNationalite() du parent.
}