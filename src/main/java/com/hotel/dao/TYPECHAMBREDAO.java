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
     * Correction de l'erreur : Ajout de la méthode attendue par le Controller.
     * Récupère tous les types de chambres pour un ID d'hôtel spécifique.
     */
    public List<TypeChambre> getAllTypesByHotel(String hotelId) {
        List<TypeChambre> list = new ArrayList<>();
        String sql = "SELECT * FROM type_chambre WHERE hotel_id = ?";

        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, hotelId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                TypeChambre t = new TypeChambre();
                t.setId(rs.getString("id"));
                t.setNomType(rs.getString("nomType"));
                t.setCapacite(rs.getInt("capacite"));
                t.setTarifNuit(rs.getDouble("tarifNuit"));
                t.setDescription(rs.getString("description"));
                // On remplit aussi l'ID de l'hôtel dans l'objet
                t.setHotel_id(rs.getString("hotel_id"));
                
                list.add(t);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur getAllTypesByHotel : " + e.getMessage());
        }
        return list;
    }

    /**
     * Version simplifiée qui utilise la session actuelle.
     */
    public List<TypeChambre> getAllTypes() {
        if (HotelSession.getHotel() == null) return new ArrayList<>();
        return getAllTypesByHotel(HotelSession.getHotel().getId());
    }

    /**
     * Enregistre un nouveau type de chambre.
     */
    public boolean save(TypeChambre type) {
        String sql = "INSERT INTO type_chambre (id, nomType, capacite, tarifNuit, description, hotel_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // Si l'ID n'est pas déjà défini, on en génère un
            String finalID = (type.getId() == null) ? UUID.randomUUID().toString().substring(0, 8) : type.getId();
            
            ps.setString(1, finalID);
            ps.setString(2, type.getNomType());
            ps.setInt(3, type.getCapacite());
            ps.setDouble(4, type.getTarifNuit());
            ps.setString(5, type.getDescription());
            
            // On utilise l'ID de l'hôtel défini dans l'objet type ou celui de la session
            String hotelId = (type.getHotel_id() != null) ? type.getHotel_id() : HotelSession.getHotel().getId();
            ps.setString(6, hotelId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur save TypeChambre : " + e.getMessage());
            return false;
        }
    }
}