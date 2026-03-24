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
        String sql = "SELECT * FROM users WHERE email=? AND motDePasse=?";
        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) user = mapResultSetToUser(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return user;
    }

    // Dans le fichier USERDAO.java
    public List<Users> getAllReceptionnists() {
        List<Users> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'RECEPTIONNISTE'";
        try (Connection conn = connexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Users(
                        rs.getString("id"),
                        rs.getString("login"),
                        rs.getString("motDePasse"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("telephone"),
                        rs.getString("adresse"),
                        rs.getString("email"),
                        rs.getString("nationalite"),
                        UsersRole.valueOf(rs.getString("role"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public static boolean save(Users user) {
        if (user == null) return false;
        // Correction : Ajout de 'telephone' dans la liste des colonnes (10 colonnes totales)
        String sql = "INSERT INTO users (id, login, motDePasse, nom, prenom, telephone, adresse, nationalite, email, role) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String finalID = (user.getId() == null || user.getId().isEmpty())
                    ? UUID.randomUUID().toString().substring(0, 8) : user.getId();

            ps.setString(1, finalID);
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getMotDePasse());
            ps.setString(4, user.getNom());
            ps.setString(5, user.getPrenom());
            ps.setString(6, user.getTelephone()); // Correction de l'index 6
            ps.setString(7, user.getAdresse());
            ps.setString(8, user.getNationalite());
            ps.setString(9, user.getEmail());
            ps.setString(10, user.getRole().toString());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL Save : " + e.getMessage());
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
        String roleBD = rs.getString("role");
        if (roleBD != null) user.setRole(UsersRole.valueOf(roleBD.toUpperCase()));
        return user;
    }
}