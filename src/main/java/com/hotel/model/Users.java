package com.hotel.model;

public class Users {
    private String id;
    private String login;
    private String motDePasse;
    private String nom;
    private String prenom;
    private String telephone;
    private String adresse;
    private String email;
    private String nationalite;
    private UsersRole role;

    // Constructeur vide (Indispensable pour l'instanciation simple dans le Main)
    public Users() {}

    // Constructeur Complet
    public Users(String id, String login, String motDePasse, String nom, String prenom,
                 String telephone, String adresse, String email, String nationalite, UsersRole role) {
        this.id = id;
        this.login = login;
        this.motDePasse = motDePasse;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.adresse = adresse;
        this.email = email;
        this.nationalite = nationalite;
        this.role = role;
    }

    // --- GETTERS ---
    public String getId() { return id; }
    public String getLogin() { return login; }
    public String getMotDePasse() { return motDePasse; } // Corrigé pour correspondre au champ
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getTelephone() { return telephone; }
    public String getAdresse() { return adresse; }
    public String getEmail() { return email; }
    public String getNationalite() { return nationalite; }
    public UsersRole getRole() { return role; }

    // --- SETTERS ---
    public void setId(String id) { this.id = id; }
    public void setLogin(String login) { this.login = login; }

    // On garde setMotDePasse pour être en phase avec ton USERDAO et ton Main
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    // Optionnel : on garde aussi setPassword au cas où tu l'utilises ailleurs
    public void setPassword(String password) { this.motDePasse = password; }

    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setEmail(String email) { this.email = email; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public void setNationalite(String nationalite) { this.nationalite = nationalite; }
    public void setRole(UsersRole role) { this.role = role; }
}