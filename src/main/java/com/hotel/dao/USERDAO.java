package com.hotel.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import com.hotel.database.connexionDB;
import com.hotel.model.Users;
import com.hotel.model.UsersRole;

public class USERDAO {

    // --- MÉTHODE LOGIN (Celle qui manquait ou était mal définie) ---
    public Users login(String email, String password) {
        Users user = null;
        // Rappel : Dans ton tableau SQL, la colonne s'appelle 'motDePasse'
        String sql = "SELECT * FROM users WHERE email=? AND motDePasse=?";

        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new Users();
                    user.setId(rs.getString("id"));
                    user.setNom(rs.getString("nom"));
                    user.setPrenom(rs.getString("prenom"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("motDePasse"));
                    user.setTelephone(rs.getString("telephone"));
                    user.setAdresse(rs.getString("adresse"));
                    user.setNationalite(rs.getString("nationalite"));
                    
                    // Conversion du String de la BD vers l'Enum Java
                    String roleBD = rs.getString("role");
                    if (roleBD != null) {
                        user.setRole(UsersRole.valueOf(roleBD.toUpperCase()));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du login : " + e.getMessage());
        }
        return user;
    }

    // --- MÉTHODE SAVE ---
    public static boolean save(Users user) {
        String sql = "INSERT INTO users (id, login, motDePasse, nom, prenom, telephone, adresse, nationalite, email, role) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String uniqueID = UUID.randomUUID().toString().substring(0, 8); 

            ps.setString(1, uniqueID);
            ps.setString(2, user.getEmail()); // login
            ps.setString(3, user.getPassword()); // motDePasse
            ps.setString(4, user.getNom());
            ps.setString(5, user.getPrenom());
            ps.setString(6, user.getTelephone());
            ps.setString(7, user.getAdresse());
            ps.setString(8, user.getNationalite());
            ps.setString(9, user.getEmail());
            ps.setString(10, user.getRole().toString());

            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'enregistrement : " + e.getMessage());
            return false;
        }
    }
}