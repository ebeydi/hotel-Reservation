package com.hotel.dao;

import com.hotel.database.connexionDB;
import com.hotel.model.Hotel;
import java.sql.*;

public class HOTELDAO {

    public Hotel getHotelConfiguration() {
        Hotel h = null;
        String sql = "SELECT * FROM hotel LIMIT 1"; // On récupère le seul hôtel configuré
        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                h = new Hotel();
                h.setId(rs.getString("id"));
                h.setNom(rs.getString("nom"));
                h.setVille(rs.getString("ville"));
                h.setAdresse(rs.getString("adresse"));
                h.setCategorie(rs.getString("categorie"));
                h.setDescription(rs.getString("description"));
                h.setTelephone(rs.getString("telephone"));
                h.setEmail(rs.getString("email"));
                // h.setStatut(...) si tu as un Enum Statut
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return h;
    }

    public boolean update(Hotel h) {
        String sql = "UPDATE hotel SET nom=?, ville=?, adresse=?, categorie=?, description=?, telephone=?, email=? WHERE id=?";
        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, h.getNom());
            ps.setString(2, h.getVille());
            ps.setString(3, h.getAdresse());
            ps.setString(4, h.getCategorie());
            ps.setString(5, h.getDescription());
            ps.setString(6, h.getTelephone());
            ps.setString(7, h.getEmail());
            ps.setString(8, h.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
}