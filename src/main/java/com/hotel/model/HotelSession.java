package com.hotel.model;

/**
 * Cette classe permet de stocker l'hôtel actuellement géré par l'application.
 * Elle est accessible partout dans le code (Contrôleurs, DAO, etc.).
 */
public class HotelSession {

    // L'unique instance de l'hôtel en mémoire
    private static Hotel currentHotel;

    /**
     * Définit l'hôtel actif (généralement appelé au démarrage de l'application)
     * @param hotel L'objet Hotel récupéré depuis la base de données
     */
    public static void setHotel(Hotel hotel) {
        currentHotel = hotel;
    }

    /**
     * Récupère l'hôtel stocké en mémoire
     * @return L'objet Hotel complet
     */
    public static Hotel getHotel() {
        return currentHotel;
    }

    /**
     * Permet de vérifier si une configuration d'hôtel existe
     * @return true si un hôtel est chargé, false sinon
     */
    public static boolean isConfigured() {
        return currentHotel != null;
    }
}
