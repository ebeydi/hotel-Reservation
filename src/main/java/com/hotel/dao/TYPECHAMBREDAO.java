package com.hotel.dao;

import com.hotel.database.connexionDB;
import com.hotel.model.TypeChambre;
import com.hotel.model.HotelSession;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TYPECHAMBREDAO {

    /**
     * Récupère tous les types de chambres enregistrés pour l'hôtel actuel.
     */
    public List<TypeChambre> getAllTypes() {
        List<TypeChambre> list = new ArrayList<>();
        String sql = "SELECT * FROM type_chambre WHERE id_hotel = ?";

        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // On filtre par l'hôtel en session
            ps.setString(1, HotelSession.getHotel().getId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                TypeChambre t = new TypeChambre();
                t.setId(rs.getString("id"));
                t.setNomType(rs.getString("nom_type"));
                t.setCapacite(rs.getInt("capacite"));
                t.setTarifNuit(rs.getDouble("tarif_nuit"));
                t.setDescription(rs.getString("description"));
                // t.setStatut(...) si tu gères le statut
                
                list.add(t);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur getAllTypes : " + e.getMessage());
        }
        return list;
    }

    /**
     * Enregistre un nouveau type de chambre (ex: "Suite Royale", 150000 FCFA).
     */
    public boolean save(TypeChambre type) {
        String sql = "INSERT INTO type_chambre (id, nom_type, capacite, tarif_nuit, description, id_hotel) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String uniqueID = UUID.randomUUID().toString().substring(0, 8);
            
            ps.setString(1, uniqueID);
            ps.setString(2, type.getNomType());
            ps.setInt(3, type.getCapacite());
            ps.setDouble(4, type.getTarifNuit());
            ps.setString(5, type.getDescription());
            ps.setString(6, HotelSession.getHotel().getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur save TypeChambre : " + e.getMessage());
            return false;
        }
    }
}
