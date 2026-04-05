package com.hotel.dao;

import com.hotel.database.connexionDB;
import com.hotel.model.Hotel;
import com.hotel.model.Statut;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HOTELDAO {

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
            System.err.println("❌ Erreur getHotelConfiguration : " + e.getMessage());
        }

        return h;
    }

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
            System.err.println("❌ Erreur getAllHotels : " + e.getMessage());
        }

        return liste;
    }

   public List<Hotel> getHotelsByVille(String ville) {

    List<Hotel> liste = new ArrayList<>();

    String sql = "SELECT * FROM hotel WHERE ville = ? AND statut = 'ACTIF'";

    try (Connection conn = connexionDB.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, ville);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            liste.add(mapResultSetToHotel(rs));
        }

    } catch (SQLException e) {
        System.err.println("❌ Erreur getHotelsByVille : " + e.getMessage());
    }

    return liste;
}

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

            // 🔥 FK SAFE
            if (h.getStatut() == null) {
                ps.setString(9, Statut.ACTIF.name());
            } else {
                ps.setString(9, h.getStatut().name());
            }

            ps.setString(10, h.getImage());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erreur insert hotel : " + e.getMessage());
            return false;
        }
    }

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

            // 🔥 FK SAFE
            if (h.getStatut() == null) {
                ps.setString(8, Statut.ACTIF.name());
            } else {
                ps.setString(8, h.getStatut().name());
            }

            ps.setString(9, h.getImage());
            ps.setString(10, h.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erreur update hotel : " + e.getMessage());
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

        // 🔥 Conversion String → Enum
        String statut = rs.getString("statut");
        if (statut != null) {
            h.setStatut(Statut.valueOf(statut));
        } else {
            h.setStatut(Statut.ACTIF);
        }

        h.setImage(rs.getString("image"));

        return h;
    }
}