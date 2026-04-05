package com.hotel.dao;

import com.hotel.database.connexionDB;
import com.hotel.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CHAMBREDAO {

    public Chambre findByNumero(String numero, String hotelId) {

        String sql = "SELECT c.*, tc.nomType, tc.tarifNuit, tc.capacite " +
                     "FROM chambre c " +
                     "JOIN type_chambre tc ON c.id_type_chambre = tc.id " +
                     "WHERE c.numero = ? AND c.id_hotel = ?";

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
                     "JOIN type_chambre tc ON c.id_type_chambre = tc.id " +
                     "WHERE c.id_hotel = ?";

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

        String sql = "INSERT INTO chambre (id, numero, etat, id_type_chambre, id_hotel) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (chambre.getTypeChambre() == null || chambre.getTypeChambre().getId() == null) {
                System.err.println("❌ TypeChambre NULL !");
                return false;
            }

            if (HotelSession.getHotel() == null) {
                System.err.println("❌ Aucun hôtel en session !");
                return false;
            }

            ps.setString(1, UUID.randomUUID().toString());
            ps.setString(2, chambre.getNumero());
            ps.setString(3, chambre.getEtat() != null ? chambre.getEtat().name() : EtatChambre.DISPONIBLE.name());
            ps.setString(4, chambre.getTypeChambre().getId());
            ps.setString(5, HotelSession.getHotel().getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erreur save chambre : " + e.getMessage());
            return false;
        }
    }

    private Chambre mapResultSetToChambre(ResultSet rs) throws SQLException {

        Chambre c = new Chambre();

        c.setId(rs.getString("id"));
        c.setNumero(rs.getString("numero"));

        String etat = rs.getString("etat");
        if (etat != null) {
            c.setEtat(EtatChambre.valueOf(etat));
        }

        TypeChambre tc = new TypeChambre();
        tc.setId(rs.getString("id_type_chambre"));
        tc.setNomType(rs.getString("nomType"));
        tc.setTarifNuit(rs.getDouble("tarifNuit"));
        tc.setCapacite(rs.getInt("capacite"));

        c.setTypeChambre(tc);

        // ✅ IMPORTANT : plus de HotelSession ici
        Hotel h = new Hotel();
        h.setId(rs.getString("id_hotel"));
        c.setHotel(h);

        return c;
    }
}