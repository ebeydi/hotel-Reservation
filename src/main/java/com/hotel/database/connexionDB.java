package com.hotel.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connexionDB {
    private static final String URL = "jdbc:mysql://localhost:3306/hotel_reservation";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Par défaut vide sur XAMPP/WAMP

    public static Connection getConnection() {
        try {
            // Charger le driver (optionnel sur les versions récentes mais plus sûr)
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("❌ Erreur de connexion : " + e.getMessage());
            return null;
        }
    }
}