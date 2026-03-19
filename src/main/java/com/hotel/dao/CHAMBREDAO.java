package com.hotel.dao;

import com.hotel.database.connexionDB;
import com.hotel.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CHAMBREDAO {

    public List<Chambre> getAllChambres() {
        List<Chambre> list = new ArrayList<>();
        // On fait une JOINTURE (JOIN) pour récupérer les détails du type de chambre en même temps
        String sql = "SELECT c.*, tc.nom_type, tc.tarif_nuit, tc.capacite " +
                     "FROM chambres c " +
                     "JOIN type_chambre tc ON c.id_type_chambre = tc.id " +
                     "WHERE c.id_hotel = ?";

        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, HotelSession.getHotel().getId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Chambre c = new Chambre();
                c.setId(rs.getString("id"));
                c.setNumero(rs.getString("numero"));
                c.setEtat(EtatChambre.valueOf(rs.getString("etat"))); // L'état reste un Enum
                
                // --- RECONSTITUTION DE L'OBJET TYPECHAMBRE ---
                TypeChambre tc = new TypeChambre();
                tc.setId(rs.getString("id_type_chambre"));
                tc.setNomType(rs.getString("nom_type"));
                tc.setTarifNuit(rs.getDouble("tarif_nuit"));
                tc.setCapacite(rs.getInt("capacite"));
                
                c.setTypeChambre(tc); // On injecte l'objet complet
                c.setHotel(HotelSession.getHotel());
                
                list.add(c);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur getAllChambres : " + e.getMessage());
        }
        return list;
    }

    public static boolean save(Chambre chambre) {
        // Attention : on enregistre l'ID du type de chambre (Clé étrangère)
        String sql = "INSERT INTO chambres (id, numero, etat, id_type_chambre, id_hotel) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String uniqueID = UUID.randomUUID().toString().substring(0, 8);

            ps.setString(1, uniqueID);
            ps.setString(2, chambre.getNumero());
            ps.setString(3, chambre.getEtat().name());
            
            // --- ICI : On récupère l'ID de l'objet TypeChambre ---
            ps.setString(4, chambre.getTypeChambre().getId()); 
            
            ps.setString(5, HotelSession.getHotel().getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erreur save chambre : " + e.getMessage());
            return false;
        }
    }
}