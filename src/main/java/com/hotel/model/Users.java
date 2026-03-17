package com.hotel.model;

public class Users {
  private String id;
    private String login;
    private String motDePasse;
    private String nom;
    private String prenom;
    private String email;
    private UsersRole role;

    public Users() {}

    public Users(String id, String login, String motDePasse, String nom,
                 String prenom, String email, UsersRole role) {
        this.id = id;
        this.login = login;
        this.motDePasse = motDePasse;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
    }

    public void setId(String string) {
       
        throw new UnsupportedOperationException("Unimplemented method 'setId'");
    }

    public void setLogin(String string) {

        throw new UnsupportedOperationException("Unimplemented method 'setLogin'");
    }

    public void setNom(String string) {
       
        throw new UnsupportedOperationException("Unimplemented method 'setNom'");
    }

    public void setPrenom(String string) {
       
        throw new UnsupportedOperationException("Unimplemented method 'setPrenom'");
    }

    public void setEmail(String string) {
        
        throw new UnsupportedOperationException("Unimplemented method 'setEmail'");
    }

    public void setPassword(String password) {
    }

    // getters et setters
}

