package com.hotel.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.hotel.database.connexionDB;
import com.hotel.model.Client;
import com.hotel.model.UsersRole;

public class ClientDAO {

    // On sélectionne tout (*) pour inclure hotel_id
    private static final String FIND_BY_EMAIL_SQL =
            "SELECT * FROM users WHERE email = ? AND role = 'CLIENT'";

    public Client findByEmail(String email) throws SQLException {
        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_EMAIL_SQL)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Extraction des données de la base
                    String id = rs.getString("id");
                    String login = rs.getString("login");
                    String motDePasse = rs.getString("motDePasse");
                    String nom = rs.getString("nom");
                    String prenom = rs.getString("prenom");
                    String telephone = rs.getString("telephone");
                    String adresse = rs.getString("adresse");
                    String nationalite = rs.getString("nationalite");
                    String emailDb = rs.getString("email");
                    String roleStr = rs.getString("role");
                    
                    // 🔥 Extraction de la nouvelle colonne hotel_id
                    String hotelId = rs.getString("hotel_id");

                    UsersRole role = (roleStr != null) ? 
                                     UsersRole.valueOf(roleStr.toUpperCase()) : 
                                     UsersRole.CLIENT;

                    // On retourne le nouveau Client avec TOUS les paramètres 
                    // (Vérifie bien que l'ordre correspond à ton constructeur Client)
                    return new Client(
                            id,
                            login,
                            motDePasse,
                            nom,
                            prenom,
                            telephone,
                            adresse,
                            emailDb,
                            nationalite, // Transmis au parent Users
                            role,        // Transmis au parent Users
                            hotelId      // 🔥 Transmis au parent Users
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur ClientDAO (findByEmail) : " + e.getMessage());
            throw e; 
        }
        return null; // aucun client trouvé
    }
}