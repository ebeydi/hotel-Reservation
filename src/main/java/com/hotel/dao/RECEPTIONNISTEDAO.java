package com.hotel.dao;

import com.hotel.database.connexionDB;
import com.hotel.model.Chambre;
import com.hotel.model.Receptionniste;
import com.hotel.model.Reservation;
import com.hotel.model.StatutReservation;
import com.hotel.model.Users;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RECEPTIONNISTEDAO {

    /**
     * AJOUTER : Enregistre un nouveau réceptionniste
     */
    public boolean save(Receptionniste recep) {
        String sql = "INSERT INTO users (id, login, motDePasse, nom, prenom, telephone, adresse, nationalite, email, role, hotel_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, recep.getId());
            ps.setString(2, recep.getLogin());
            ps.setString(3, recep.getMotDePasse()); 
            ps.setString(4, recep.getNom());
            ps.setString(5, recep.getPrenom());
            ps.setString(6, recep.getTelephone());
            ps.setString(7, recep.getAdresse());
            ps.setString(8, recep.getNationalite());
            ps.setString(9, recep.getEmail());
            ps.setString(10, "RECEPTIONNISTE");
            ps.setString(11, recep.getHotel_id()); 

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * MODIFIER : Met à jour les informations d'un réceptionniste existant
     */
    public boolean update(Receptionniste recep) {
        String sql = "UPDATE users SET login=?, nom=?, prenom=?, telephone=?, adresse=?, email=?, hotel_id=? WHERE id=?";

        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, recep.getLogin());
            ps.setString(2, recep.getNom());
            ps.setString(3, recep.getPrenom());
            ps.setString(4, recep.getTelephone());
            ps.setString(5, recep.getAdresse());
            ps.setString(6, recep.getEmail());
            ps.setString(7, recep.getHotel_id());
            ps.setString(8, recep.getId()); // Le WHERE id=? pour cibler le bon employé

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification : " + e.getMessage());
            return false;
        }
    }

    /**
     * SUPPRIMER : Supprime un réceptionniste de la base de données
     */
    public boolean delete(String idRecep) {
        String sql = "DELETE FROM users WHERE id = ? AND role = 'RECEPTIONNISTE'";

        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, idRecep);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression : " + e.getMessage());
            return false;
        }
    }

    /**
     * LISTER : Récupère tous les réceptionnistes d'un hôtel
     */
    public List<Receptionniste> findRecepsByHotel(String hotelId) {
        List<Receptionniste> receps = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE hotel_id = ? AND role = 'RECEPTIONNISTE'";

        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, hotelId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Receptionniste r = new Receptionniste();
                r.setId(rs.getString("id"));
                r.setLogin(rs.getString("login"));
                r.setNom(rs.getString("nom"));
                r.setPrenom(rs.getString("prenom"));
                r.setEmail(rs.getString("email"));
                r.setTelephone(rs.getString("telephone"));
                r.setHotel_id(rs.getString("hotel_id"));
                receps.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return receps;
    }

    /**
     * RECHERCHE POUR RECEPTIONNISTE (Tes réservations)
     */
    public List<Reservation> findByHotel(String hotelId) {
        // ... garde ton code actuel ici, il est très bien ...
        List<Reservation> reservations = new ArrayList<>();
        // (Copie de ton code existant)
        return reservations;
    }
}