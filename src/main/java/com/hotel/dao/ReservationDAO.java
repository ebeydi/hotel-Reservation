package com.hotel.dao;

import com.hotel.database.connexionDB;
import com.hotel.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RESERVATIONDAO {

    /**
     * FIND BY HOTEL : Récupère les réservations avec détails complets
     */
    public List<Reservation> findByHotel(String hotelId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.*, c.numero as ch_num, u.nom as u_nom, u.prenom as u_prenom, " +
                     "h.nom as h_nom, tc.nomType as tc_nom, tc.capacite as tc_cap " +
                     "FROM reservation r " +
                     "JOIN chambre c ON r.chambre_id = c.id " +
                     "JOIN users u ON r.client_id = u.id " +
                     "JOIN hotel h ON c.hotel_id = h.id " +
                     "JOIN type_chambre tc ON c.type_chambre_id = tc.id " +
                     "WHERE c.hotel_id = ? AND r.statut != 'TERMINEE' " +
                     "ORDER BY r.dateArrive ASC";

        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hotelId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Reservation res = mapResultSetToReservation(rs);
                
                // Client
                Users client = new Users();
                client.setId(rs.getString("client_id"));
                client.setNom(rs.getString("u_nom"));
                client.setPrenom(rs.getString("u_prenom"));
                res.setClient(client);

                // Détails Chambre
                Hotel h = new Hotel();
                h.setNom(rs.getString("h_nom"));

                TypeChambre tc = new TypeChambre();
                tc.setNomType(rs.getString("tc_nom"));
                tc.setCapacite(rs.getInt("tc_cap"));

                Chambre ch = new Chambre();
                ch.setId(rs.getString("chambre_id"));
                ch.setNumero(rs.getString("ch_num"));
                ch.setHotel(h);
                ch.setTypeChambre(tc);
                
                res.setChambre(ch);
                reservations.add(res);
            }
        } catch (SQLException e) { 
            System.err.println("❌ Erreur findByHotel : " + e.getMessage()); 
        }
        return reservations;
    }

    /**
     * SAUVEGARDE : Nouvelle réservation + Passage de la chambre en OCCUPEE
     */
    public boolean save(Reservation res) {
        String sqlRes = "INSERT INTO reservation (id, numReservation, dateArrive, dateDepart, nbPersonne, montantTotal, statut, client_id, chambre_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlCham = "UPDATE chambre SET etat = 'OCCUPEE' WHERE id = ?";

        try (Connection conn = connexionDB.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(sqlRes)) {
                    ps.setString(1, res.getId());
                    ps.setString(2, res.getNumReservation());
                    ps.setDate(3, new java.sql.Date(res.getDateArrive().getTime()));
                    ps.setDate(4, new java.sql.Date(res.getDateDepart().getTime()));
                    ps.setInt(5, res.getNbPersonne()); 
                    ps.setFloat(6, res.getMontantTotal());
                    ps.setString(7, res.getStatut().name());
                    ps.setString(8, res.getClient().getId());
                    ps.setString(9, res.getChambre().getId());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps2 = conn.prepareStatement(sqlCham)) {
                    ps2.setString(1, res.getChambre().getId());
                    ps2.executeUpdate();
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur Save Reservation : " + e.getMessage());
            return false;
        }
    }

    /**
     * UPDATE : Crucial pour la validation par le réceptionniste
     */
    public boolean update(Reservation res) {
        String sql = "UPDATE reservation SET dateArrive=?, dateDepart=?, nbPersonne=?, montantTotal=?, statut=?, chambre_id=? WHERE id=?";
        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, new java.sql.Date(res.getDateArrive().getTime()));
            ps.setDate(2, new java.sql.Date(res.getDateDepart().getTime()));
            ps.setInt(3, res.getNbPersonne());
            ps.setFloat(4, res.getMontantTotal());
            ps.setString(5, res.getStatut().name());
            ps.setString(6, res.getChambre().getId());
            ps.setString(7, res.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur Update Reservation : " + e.getMessage());
            return false;
        }
    }

    /**
     * CHECK-IN : Confirme l'arrivée
     */
    public boolean checkIn(String numReservation) {
        String sql = "UPDATE reservation SET statut = 'CONFIRMEE' WHERE numReservation = ?";
        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numReservation);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur Check-in : " + e.getMessage());
            return false;
        }
    }

    /**
     * CHECK-OUT : Termine la réservation et LIBÈRE la chambre
     */
    public boolean checkOut(String numReservation) {
        String sqlFind = "SELECT chambre_id FROM reservation WHERE numReservation = ?";
        String sqlRes = "UPDATE reservation SET statut = 'TERMINEE' WHERE numReservation = ?";
        String sqlCham = "UPDATE chambre SET etat = 'DISPONIBLE' WHERE id = ?";

        try (Connection conn = connexionDB.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String chambreId = null;
                try (PreparedStatement ps = conn.prepareStatement(sqlFind)) {
                    ps.setString(1, numReservation);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) chambreId = rs.getString("chambre_id");
                }

                if (chambreId != null) {
                    try (PreparedStatement ps1 = conn.prepareStatement(sqlRes)) {
                        ps1.setString(1, numReservation);
                        ps1.executeUpdate();
                    }
                    try (PreparedStatement ps2 = conn.prepareStatement(sqlCham)) {
                        ps2.setString(1, chambreId);
                        ps2.executeUpdate();
                    }
                    conn.commit();
                    return true;
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur CheckOut : " + e.getMessage());
        }
        return false;
    }

    public List<Reservation> findByClient(String clientId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.*, c.numero as ch_num, h.nom as h_nom, tc.nomType as tc_nom " +
                     "FROM reservation r " +
                     "JOIN chambre c ON r.chambre_id = c.id " +
                     "JOIN hotel h ON c.hotel_id = h.id " +
                     "JOIN type_chambre tc ON c.type_chambre_id = tc.id " +
                     "WHERE r.client_id = ? ORDER BY r.dateArrive DESC";

        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, clientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Reservation res = mapResultSetToReservation(rs);
                
                Chambre ch = new Chambre();
                ch.setNumero(rs.getString("ch_num"));
                
                Hotel h = new Hotel();
                h.setNom(rs.getString("h_nom"));
                ch.setHotel(h);

                TypeChambre tc = new TypeChambre();
                tc.setNomType(rs.getString("tc_nom"));
                ch.setTypeChambre(tc);

                res.setChambre(ch);
                reservations.add(res);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return reservations;
    }

    // --- MISE À JOUR : getStatsByHotel Dynamique et Filtrée ---
    public Map<String, Double> getStatsByHotel(String hotelId) {
        Map<String, Double> stats = new HashMap<>();
        String sql = "SELECT " +
                     "(SELECT SUM(r.montantTotal) FROM reservation r JOIN chambre c ON r.chambre_id = c.id WHERE c.hotel_id = ? AND r.statut = 'TERMINEE') as ca, " +
                     "(SELECT COUNT(*) FROM reservation r JOIN chambre c ON r.chambre_id = c.id WHERE c.hotel_id = ? AND r.statut = 'CONFIRMEE') as occup, " +
                     "(SELECT COUNT(*) FROM users WHERE hotel_id = ? AND UPPER(role) = 'RECEPTIONNISTE') as staff";
        
        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hotelId);
            ps.setString(2, hotelId);
            ps.setString(3, hotelId);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                stats.put("CA", rs.getDouble("ca"));
                stats.put("OCCUPATION", rs.getDouble("occup"));
                stats.put("STAFF", rs.getDouble("staff"));
            }
        } catch (SQLException e) { 
            System.err.println("❌ Erreur getStatsByHotel : " + e.getMessage());
        }
        return stats;
    }

    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        Reservation res = new Reservation();
        res.setId(rs.getString("id"));
        res.setNumReservation(rs.getString("numReservation"));
        res.setDateArrive(rs.getDate("dateArrive"));
        res.setDateDepart(rs.getDate("dateDepart"));
        res.setNbPersonne(rs.getInt("nbPersonne"));
        res.setMontantTotal(rs.getFloat("montantTotal"));
        res.setStatut(StatutReservation.valueOf(rs.getString("statut")));
        return res;
    }

    // Gardée pour compatibilité si nécessaire
    public Map<String, Double> getAdminStats() {
        Map<String, Double> stats = new HashMap<>();
        String sql = "SELECT " +
                     "SUM(montantTotal) as total_ca, " +
                     "(SELECT COUNT(*) FROM reservation WHERE statut = 'CONFIRMEE') as total_occup, " +
                     "(SELECT COUNT(*) FROM users WHERE role = 'RECEPTIONNISTE') as total_staff " +
                     "FROM reservation WHERE statut = 'TERMINEE'";
                     
        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                stats.put("CA", rs.getDouble("total_ca"));
                stats.put("OCCUPATION", rs.getDouble("total_occup"));
                stats.put("STAFF", rs.getDouble("total_staff"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return stats;
    }
}