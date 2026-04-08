package com.hotel.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.hotel.database.connexionDB;
import com.hotel.model.Users;
import com.hotel.model.UsersRole;

public class USERDAO {

    public Users login(String email, String password) {
        Users user = null;
        // On sélectionne tout, y compris hotel_id
        String sql = "SELECT * FROM users WHERE email=? AND motDePasse=?";
        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) user = mapResultSetToUser(rs);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return user;
    }

    public List<Users> getReceptionnistsByHotel(String hotelId) {
    List<Users> list = new ArrayList<>();
    String sql = "SELECT * FROM users WHERE role = 'RECEPTIONNISTE' AND hotel_id = ?";
    
    try (Connection conn = connexionDB.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, hotelId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Users u = new Users();
            u.setNom(rs.getString("nom"));
            u.setPrenom(rs.getString("prenom"));
            u.setEmail(rs.getString("email"));
            list.add(u);
        }
    } catch (SQLException e) { e.printStackTrace(); }
    return list;
}

    public static boolean save(Users user) {
        if (user == null) return false;
        
        // Ajout de 'hotel_id' dans la requête (11 colonnes désormais)
        String sql = "INSERT INTO users (id, login, motDePasse, nom, prenom, telephone, adresse, nationalite, email, role, hotel_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String finalID = (user.getId() == null || user.getId().isEmpty())
                    ? UUID.randomUUID().toString().substring(0, 8) : user.getId();

            ps.setString(1, finalID);
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getMotDePasse());
            ps.setString(4, user.getNom());
            ps.setString(5, user.getPrenom());
            ps.setString(6, user.getTelephone());
            ps.setString(7, user.getAdresse());
            ps.setString(8, user.getNationalite());
            ps.setString(9, user.getEmail());
            ps.setString(10, user.getRole().toString());
            ps.setString(11, user.getHotel_id()); // Enregistre l'ID de l'hôtel

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL Save User : " + e.getMessage());
            return false;
        }
    }

    private Users mapResultSetToUser(ResultSet rs) throws SQLException {
        Users user = new Users();
        user.setId(rs.getString("id"));
        user.setNom(rs.getString("nom"));
        user.setPrenom(rs.getString("prenom"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("motDePasse"));
        user.setTelephone(rs.getString("telephone"));
        user.setAdresse(rs.getString("adresse"));
        user.setNationalite(rs.getString("nationalite"));
        
        // 🔥 CRUCIAL : On récupère l'hotel_id depuis la base
        user.setHotel_id(rs.getString("hotel_id")); 
        
        String roleBD = rs.getString("role");
        if (roleBD != null) {
            try {
                user.setRole(UsersRole.valueOf(roleBD.toUpperCase()));
            } catch (IllegalArgumentException e) {
                user.setRole(UsersRole.CLIENT); // Role par défaut en cas d'erreur
            }
        }
        return user;
    }
}