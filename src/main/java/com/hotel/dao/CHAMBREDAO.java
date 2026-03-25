package com.hotel.dao;

import com.hotel.database.connexionDB;
import com.hotel.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CHAMBREDAO {

    /**
     * Récupère une chambre par son numéro et son hôtel
     */
    public Chambre findByNumero(String numero) {
        String sql = "SELECT c.*, tc.nom_type, tc.tarif_nuit, tc.capacite " +
                     "FROM chambre c " +
                     "JOIN type_chambre tc ON c.id_type_chambre = tc.id " +
                     "WHERE c.numero = ? AND c.id_hotel = ?";

        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, numero);
            ps.setString(2, HotelSession.getHotel().getId());
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToChambre(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur findByNumero : " + e.getMessage());
        }
        return null;
    }

    /**
     * Récupère toutes les chambres d'un hôtel donné
     */
    public List<Chambre> getAllChambres(String hotelId) {
        List<Chambre> list = new ArrayList<>();
        String sql = "SELECT c.*, tc.nom_type, tc.tarif_nuit, tc.capacite " +
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

    /**
     * Sauvegarde une nouvelle chambre
     */
    public boolean save(Chambre chambre) {
        String sql = "INSERT INTO chambre (id, numero, etat, id_type_chambre, id_hotel) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String uniqueID = UUID.randomUUID().toString();
            ps.setString(1, uniqueID);
            ps.setString(2, chambre.getNumero());
            ps.setString(3, chambre.getEtat().name());
            ps.setString(4, chambre.getTypeChambre().getId());
            ps.setString(5, HotelSession.getHotel().getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur save chambre : " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper privé pour éviter la répétition de code (Mapping SQL -> Objet)
     */
    private Chambre mapResultSetToChambre(ResultSet rs) throws SQLException {
        Chambre c = new Chambre();
        c.setId(rs.getString("id"));
        c.setNumero(rs.getString("numero"));
        c.setEtat(EtatChambre.valueOf(rs.getString("etat")));

        TypeChambre tc = new TypeChambre();
        tc.setId(rs.getString("id_type_chambre"));
        tc.setNomType(rs.getString("nom_type"));
        tc.setTarifNuit(rs.getDouble("tarif_nuit"));
        tc.setCapacite(rs.getInt("capacite"));

        c.setTypeChambre(tc);
        c.setHotel(HotelSession.getHotel());
        return c;
    }
}