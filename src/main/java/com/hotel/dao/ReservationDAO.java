package com.hotel.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.hotel.database.connexionDB;
import com.hotel.model.*;

public class ReservationDAO {

    // Requête de base pour récupérer une réservation avec toutes ses relations (JOIN)
   private static final String BASE_QUERY = 
    "SELECT " +
    "r.id AS res_id, r.numReservation, r.dateArrive, r.dateDepart, r.nbPersonne, r.montantTotal, r.statut AS res_statut, " +
    "u.id AS user_id, u.login, u.nom AS user_nom, u.prenom AS user_prenom, u.telephone AS user_tel, " +
    "u.adresse AS user_adr, u.email AS user_email, u.nationalite, u.role, " +
    "c.id AS chambre_id, c.numero AS chambre_num, c.etat AS chambre_etat, " +
    "tc.id AS type_id, tc.nomType, tc.tarifNuit, tc.capacite, " +
    "h.id AS hotel_id, h.nom AS hotel_nom, h.ville AS hotel_ville, h.adresse AS hotel_adr, " +
    "h.categorie AS hotel_cat, h.telephone AS hotel_tel, h.email AS hotel_email, h.statut AS hotel_statut " +
    "FROM reservation r " +
    "JOIN users u ON r.client_id = u.id " +
    "JOIN chambre c ON r.chambre_id = c.id " +
    "JOIN type_chambre tc ON c.id= tc.id " + // Ajout crucial pour le prix
    "JOIN hotel h ON c.id = h.id";

    /**
     * Sauvegarder une nouvelle réservation
     */
    public void save(Reservation res) throws SQLException {
        String sql = "INSERT INTO reservations (id, num_reservation, date_arrivee, date_depart, nb_personne, montant_total, statut, client_id, chambre_id) VALUES (?,?,?,?,?,?,?,?,?)";
        
        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
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
    }

    /**
     * Récupérer toutes les réservations d'un hôtel spécifique
     */
    public List<Reservation> findAll(String hotelId) throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = BASE_QUERY + " WHERE h.id = ?";
        
        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hotelId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToReservation(rs));
                }
            }
        }
        return list;
    }

    /**
     * Vérifier si une chambre est disponible pour une période donnée (Anti-surréservation)
     */
    public boolean isChambreDisponible(String chambreId, java.util.Date debut, java.util.Date fin) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reservations " +
                     "WHERE chambre_id = ? AND statut NOT IN ('ANNULEE', 'TERMINEE') " +
                     "AND ((date_arrivee < ?) AND (date_depart > ?))";
        
        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, chambreId);
            ps.setDate(2, new java.sql.Date(fin.getTime()));
            ps.setDate(3, new java.sql.Date(debut.getTime()));
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        }
        return false;
    }

    /**
     * CHECK-IN : Passer de CONFIRMEE à OCCUPEE
     */
    public boolean checkIn(String numReservation) throws SQLException {
        String sql = "UPDATE reservations SET statut = 'OCCUPEE' WHERE num_reservation = ? AND statut = 'CONFIRMEE'";
        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numReservation);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * CHECK-OUT : Terminer la réservation et libérer la chambre (Transactionnelle)
     */
    public boolean checkOut(String numReservation) throws SQLException {
        String findSql = "SELECT chambre_id FROM reservations WHERE num_reservation = ? AND statut = 'OCCUPEE'";
        String upRes = "UPDATE reservations SET statut = 'TERMINEE' WHERE num_reservation = ?";
        String upCham = "UPDATE chambres SET etat = 'DISPONIBLE' WHERE id = ?";

        Connection conn = null;
        try {
            conn = connexionDB.getConnection();
            conn.setAutoCommit(false); // Début transaction

            String chambreId = null;
            try (PreparedStatement ps = conn.prepareStatement(findSql)) {
                ps.setString(1, numReservation);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) chambreId = rs.getString("chambre_id");
                else return false;
            }

            // 1. Terminer Réservation
            try (PreparedStatement ps1 = conn.prepareStatement(upRes)) {
                ps1.setString(1, numReservation);
                ps1.executeUpdate();
            }

            // 2. Libérer Chambre
            try (PreparedStatement ps2 = conn.prepareStatement(upCham)) {
                ps2.setString(1, chambreId);
                ps2.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /**
     * Outils de génération d'IDs
     */
    public String generateReservationId() { return "RES-" + System.currentTimeMillis(); }
    public String generateNumReservation() { return "R-" + (System.currentTimeMillis() % 100000); }

    /**
     * MAPPER : Transforme un ResultSet en objet Reservation complet
     */
    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        Hotel h = new Hotel();
        h.setId(rs.getString("hotel_id"));
        h.setNom(rs.getString("hotel_nom"));

        Chambre c = new Chambre();
        c.setId(rs.getString("chambre_id"));
        c.setNumero(rs.getString("chambre_numero"));
        c.setEtat(EtatChambre.valueOf(rs.getString("chambre_etat").toUpperCase()));
        c.setHotel(h);

        Client cl = new Client();
        cl.setId(rs.getString("client_id"));
        cl.setNom(rs.getString("client_nom"));
        cl.setPrenom(rs.getString("client_prenom"));
        cl.setEmail(rs.getString("client_email"));

        Reservation res = new Reservation();
        res.setId(rs.getString("res_id"));
        res.setNumReservation(rs.getString("num_reservation"));
        res.setDateArrive(rs.getDate("date_arrivee"));
        res.setDateDepart(rs.getDate("date_depart"));
        res.setNbPersonne(rs.getInt("nb_personne"));
        res.setMontantTotal(rs.getFloat("montant_total"));
        res.setStatut(StatutReservation.valueOf(rs.getString("res_statut").toUpperCase()));
        res.setClient(cl);
        res.setChambre(c);

        return res;
    }
}