package com.hotel.dao;

import com.hotel.database.connexionDB;
import com.hotel.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CHAMBREDAO {

    // Version complète : Trouve une chambre avec tous ses détails par numéro et hôtel
    public Chambre findByNumero(String numero, String hotelId) {
        String sql = "SELECT c.*, tc.nomType, tc.tarifNuit, tc.capacite " +
                     "FROM chambre c " +
                     "JOIN type_chambre tc ON c.type_chambre_id = tc.id " +
                     "WHERE c.numero = ? AND c.hotel_id = ?"; 

        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, numero);
            ps.setString(2, hotelId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToChambre(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur findByNumero : " + e.getMessage());
        }
        return null;
    }

    public List<Chambre> getAllChambres(String hotelId) {
        List<Chambre> list = new ArrayList<>();
        String sql = "SELECT c.*, tc.nomType, tc.tarifNuit, tc.capacite " +
                     "FROM chambre c " +
                     "JOIN type_chambre tc ON c.type_chambre_id = tc.id " +
                     "WHERE c.hotel_id = ?";

        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, hotelId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToChambre(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur getAllChambres : " + e.getMessage());
        }
        return list;
    }

    public boolean save(Chambre chambre) {
        String sql = "INSERT INTO chambre (id, numero, etat, type_chambre_id, hotel_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (chambre.getTypeChambre() == null) return false;

            String finalId = (chambre.getId() == null || chambre.getId().isEmpty()) 
                             ? UUID.randomUUID().toString() : chambre.getId();

            ps.setString(1, finalId);
            ps.setString(2, chambre.getNumero());
            // Protection contre le null pour l'état à l'insertion
            ps.setString(3, chambre.getEtat() != null ? chambre.getEtat().name() : EtatChambre.DISPONIBLE.name());
            ps.setString(4, chambre.getTypeChambre().getId());
            ps.setString(5, HotelSession.getHotel().getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur save chambre : " + e.getMessage());
            return false;
        }
    }

    /**
     * MÉTHODE DE MISE À JOUR (CORRIGÉE)
     */
    public boolean update(Chambre chambre) {
        String sql = "UPDATE chambre SET numero = ?, etat = ?, type_chambre_id = ? WHERE id = ?";
        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, chambre.getNumero());
            
            // FIX : Protection contre le NullPointerException
            if (chambre.getEtat() != null) {
                ps.setString(2, chambre.getEtat().name());
            } else {
                // On met une valeur par défaut ou on récupère l'ancienne si c'est null
                ps.setString(2, EtatChambre.DISPONIBLE.name());
            }
            
            ps.setString(3, (chambre.getTypeChambre() != null) ? chambre.getTypeChambre().getId() : null);
            ps.setString(4, chambre.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur update chambre : " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String id) {
        String sql = "DELETE FROM chambre WHERE id = ?";
        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur delete chambre : " + e.getMessage());
            return false;
        }
    }

    private Chambre mapResultSetToChambre(ResultSet rs) throws SQLException {
        Chambre c = new Chambre();
        c.setId(rs.getString("id"));
        c.setNumero(rs.getString("numero"));

        String etatStr = rs.getString("etat");
        if (etatStr != null) {
            try {
                c.setEtat(EtatChambre.valueOf(etatStr));
            } catch (IllegalArgumentException e) {
                c.setEtat(EtatChambre.DISPONIBLE); // Valeur par défaut si erreur de texte
            }
        }

        TypeChambre tc = new TypeChambre();
        tc.setId(rs.getString("type_chambre_id"));
        tc.setNomType(rs.getString("nomType"));
        tc.setTarifNuit(rs.getDouble("tarifNuit"));
        tc.setCapacite(rs.getInt("capacite"));
        c.setTypeChambre(tc);

        Hotel h = new Hotel();
        h.setId(rs.getString("hotel_id"));
        c.setHotel(h);

        return c;
    }

    // Version simplifiée (si besoin de chercher sans l'ID de l'hôtel)
    public Chambre findByNumero(String numero) {
        // On réutilise la requête avec JOIN pour avoir les prix/types
        String sql = "SELECT c.*, tc.nomType, tc.tarifNuit, tc.capacite " +
                     "FROM chambre c " +
                     "JOIN type_chambre tc ON c.type_chambre_id = tc.id " +
                     "WHERE c.numero = ?";
        try (Connection conn = connexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, numero);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToChambre(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur findByNumero simple : " + e.getMessage());
        }
        return null;
    }
}