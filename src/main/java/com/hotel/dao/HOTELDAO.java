package com.hotel.dao;

import com.hotel.database.connexionDB;
import com.hotel.model.Hotel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HOTELDAO {

    /**
     * Récupère un hôtel (le premier de la table)
     */
    public Hotel getHotelConfiguration() {
        Hotel h = null;
        String sql = "SELECT * FROM hotel LIMIT 1";
        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                h = mapResultSetToHotel(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return h;
    }

    /**
     * Récupère tous les hôtels
     */
    public List<Hotel> getAllHotels() {
        List<Hotel> liste = new ArrayList<>();
        String sql = "SELECT * FROM hotel";
        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                liste.add(mapResultSetToHotel(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    /**
     * Récupère les hôtels d'une ville spécifique
     */
    public List<Hotel> getHotelsByVille(String ville) {
        List<Hotel> liste = new ArrayList<>();
        String sql = "SELECT * FROM hotel WHERE ville = ?";
        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ville);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                liste.add(mapResultSetToHotel(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    /**
     * Insert un nouvel hôtel dans la base
     */
    public boolean insert(Hotel h) {
        String sql = "INSERT INTO hotel (id, nom, ville, adresse, categorie, description, telephone, email, statut, image) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        if (h.getId() == null || h.getId().isEmpty()) {
            h.setId(UUID.randomUUID().toString());
        }
        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, h.getId());
            ps.setString(2, h.getNom());
            ps.setString(3, h.getVille());
            ps.setString(4, h.getAdresse());
            ps.setString(5, h.getCategorie());
            ps.setString(6, h.getDescription());
            ps.setString(7, h.getTelephone());
            ps.setString(8, h.getEmail());
            ps.setString(9, h.getStatut());
            ps.setString(10, h.getImage());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Met à jour un hôtel existant
     */
    public boolean update(Hotel h) {
        String sql = "UPDATE hotel SET nom=?, ville=?, adresse=?, categorie=?, description=?, telephone=?, email=?, statut=?, image=? WHERE id=?";
        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, h.getNom());
            ps.setString(2, h.getVille());
            ps.setString(3, h.getAdresse());
            ps.setString(4, h.getCategorie());
            ps.setString(5, h.getDescription());
            ps.setString(6, h.getTelephone());
            ps.setString(7, h.getEmail());
            ps.setString(8, h.getStatut());
            ps.setString(9, h.getImage());
            ps.setString(10, h.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Méthode utilitaire pour convertir un ResultSet en objet Hotel
     */
    private Hotel mapResultSetToHotel(ResultSet rs) throws SQLException {
        Hotel h = new Hotel();
        h.setId(rs.getString("id"));
        h.setNom(rs.getString("nom"));
        h.setVille(rs.getString("ville"));
        h.setAdresse(rs.getString("adresse"));
        h.setCategorie(rs.getString("categorie"));
        h.setDescription(rs.getString("description"));
        h.setTelephone(rs.getString("telephone"));
        h.setEmail(rs.getString("email"));
        h.setStatut(rs.getString("statut"));
        h.setImage(rs.getString("image"));
        return h;
    }
}