package com.hotel.dao;

import com.hotel.database.connexionDB;
import com.hotel.model.Hotel;
import com.hotel.model.Statut;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HOTELDAO {

    /**
     * AMÉLIORATION : Récupère un hôtel spécifique par son ID.
     * C'est la méthode indispensable pour ton profil multi-hôtel.
     */
    public Hotel getHotelById(String hotelId) {
        String sql = "SELECT * FROM hotel WHERE id = ?";
        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hotelId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToHotel(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur getHotelById : " + e.getMessage());
        }
        return null;
    }

    /**
     * Récupère l'hôtel associé à un utilisateur spécifique.
     */
    public Hotel getHotelByUserId(String userId) {
        String sql = "SELECT h.* FROM hotel h JOIN users u ON h.id = u.hotel_id WHERE u.id = ?";
        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToHotel(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur getHotelByUserId : " + e.getMessage());
        }
        return null;
    }

    /**
     * Récupère le premier hôtel trouvé (Gardé pour compatibilité)
     */
    public Hotel getHotelConfiguration() {
        String sql = "SELECT * FROM hotel LIMIT 1";
        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return mapResultSetToHotel(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sauvegarde ou met à jour l'hôtel (AMÉLIORÉ pour gérer l'ID spécifique)
     */
    public boolean saveOrUpdate(Hotel h) {
        // Si l'hôtel a déjà un ID, on vérifie s'il existe en BDD
        if (h.getId() != null && getHotelById(h.getId()) != null) {
            return update(h);
        } else {
            return insert(h);
        }
    }

    public List<Hotel> getHotelsByVille(String ville) {
        List<Hotel> liste = new ArrayList<>();
        String sql = "SELECT * FROM hotel WHERE ville = ? AND statut = 'ACTIF'";
        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ville);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(mapResultSetToHotel(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    public boolean insert(Hotel h) {
        String sql = "INSERT INTO hotel (id, nom, ville, adresse, categorie, description, telephone, email, statut, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
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
            ps.setString(9, (h.getStatut() != null) ? h.getStatut().name() : Statut.ACTIF.name());
            ps.setString(10, h.getImage());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur Insert Hotel : " + e.getMessage());
            return false;
        }
    }

    public boolean update(Hotel h) {
        // On s'assure de ne mettre à jour QUE l'hôtel avec le bon ID
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
            ps.setString(8, (h.getStatut() != null) ? h.getStatut().name() : Statut.ACTIF.name());
            ps.setString(9, h.getImage());
            ps.setString(10, h.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur Update Hotel : " + e.getMessage());
            return false;
        }
    }

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
        String st = rs.getString("statut");
        h.setStatut(st != null ? Statut.valueOf(st) : Statut.ACTIF);
        h.setImage(rs.getString("image"));
        return h;
    }
}