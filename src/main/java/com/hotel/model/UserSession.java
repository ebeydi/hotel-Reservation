package com.hotel.model;

/**
 * Cette classe permet de garder en mémoire l'utilisateur connecté 
 * pendant toute la durée d'utilisation de l'application.
 */
public class UserSession {
    
    // L'unique instance de l'utilisateur connecté
    private static Users loggedUser;

    // Appelé lors du login réussi
    public static void setInstance(Users user) {
        loggedUser = user;
    }

    // Utilisé par les autres pages (Accueil, Profil, etc.) pour savoir qui est connecté
    public static Users getInstance() {
        return loggedUser;
    }

    // Appelé lors de la déconnexion
    public static void clean() {
        loggedUser = null;
    }
}