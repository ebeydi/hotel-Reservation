package com.hotel.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hotel.database.connexionDB;
import com.hotel.model.Chambre;
import com.hotel.model.Client;
import com.hotel.model.EtatChambre;
import com.hotel.model.Hotel;
import com.hotel.model.Reservation;
import com.hotel.model.Statut;
import com.hotel.model.StatutReservation;
import com.hotel.model.UsersRole;

public class ReservationDAO {

    private static final String INSERT_SQL =
            "INSERT INTO reservations (id, num_reservation, date_arrivee, date_depart, " +
            "nb_personne, montant_total, statut, client_id, chambre_id) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * Sauvegarder une réservation.
     */
    public void save(Reservation reservation) throws SQLException {
        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {

            ps.setString(1, reservation.getId());
            ps.setString(2, reservation.getNumReservation());
            ps.setDate(3, new Date(reservation.getDateArrive().getTime()));
            ps.setDate(4, new Date(reservation.getDateDepart().getTime()));
            ps.setInt(5, reservation.getNbPersonne());
            ps.setFloat(6, reservation.getMontantTotal());
            ps.setString(7, reservation.getStatut().name()); // enum -> String

            ps.setString(8, reservation.getClient().getId());
            ps.setString(9, reservation.getChambre().getId());

            ps.executeUpdate();
        }
    }

    /**
     * Récupérer les réservations (ici : du jour courant).
     */
     public List<Reservation> findAll() throws SQLException {
    List<Reservation> list = new ArrayList<>();

    String sql =
            "SELECT " +
            "    r.id              AS res_id, " +
            "    r.num_reservation, " +
            "    r.date_arrivee, " +
            "    r.date_depart, " +
            "    r.nb_personne, " +
            "    r.montant_total, " +
            "    r.statut          AS res_statut, " +
            "    u.id              AS client_id, " +
            "    u.login           AS client_login, " +
            "    u.mot_de_passe    AS client_mdp, " +
            "    u.nom             AS client_nom, " +
            "    u.prenom          AS client_prenom, " +
            "    u.telephone       AS client_telephone, " +
            "    u.adresse         AS client_adresse, " +
            "    u.email           AS client_email, " +
            "    u.nationalite     AS client_nationalite, " +
            "    u.role            AS client_role, " +
            "    c.id              AS chambre_id, " +
            "    c.numero          AS chambre_numero, " +
            "    c.etat            AS chambre_etat, " +
            "    h.id              AS hotel_id, " +
            "    h.nom             AS hotel_nom, " +
            "    h.ville           AS hotel_ville, " +
            "    h.adresse         AS hotel_adresse, " +
            "    h.categorie       AS hotel_categorie, " +
            "    h.description     AS hotel_description, " +
            "    h.telephone       AS hotel_telephone, " +
            "    h.email           AS hotel_email, " +
            "    h.statut          AS hotel_statut " +
            "FROM reservations r " +
            "JOIN users u     ON r.client_id  = u.id " +
            "JOIN chambres c  ON r.chambre_id = c.id " +
            "JOIN hotels h    ON c.hotel_id   = h.id " +
            "WHERE r.date_arrivee = CURDATE()";

    try (Connection conn = connexionDB.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {

            // 1) Hotel
            Hotel hotel = new Hotel(
                    rs.getString("hotel_id"),
                    rs.getString("hotel_nom"),
                    rs.getString("hotel_ville"),
                    rs.getString("hotel_adresse"),
                    rs.getString("hotel_categorie"),
                    rs.getString("hotel_description"),
                    rs.getString("hotel_telephone"),
                    rs.getString("hotel_email"),
                    Statut.valueOf(rs.getString("hotel_statut").toUpperCase()) // enum Statut
            );

            // 2) Chambre
            Chambre chambre = new Chambre(
                    rs.getString("chambre_id"),
                    rs.getString("chambre_numero"),
                    EtatChambre.valueOf(rs.getString("chambre_etat").toUpperCase()),
                    null,     // TypeChambre si tu veux le charger plus tard
                    hotel
            );

            // 3) Client
            Client client = new Client(
                    rs.getString("client_id"),
                    rs.getString("client_login"),
                    rs.getString("client_mdp"),
                    rs.getString("client_nom"),
                    rs.getString("client_prenom"),
                    rs.getString("client_telephone"),
                    rs.getString("client_adresse"),
                    rs.getString("client_email"),
                    UsersRole.valueOf(rs.getString("client_role").toUpperCase()),
                    rs.getString("client_nationalite")
            );

            // 4) Reservation
            Reservation reservation = new Reservation(
                    rs.getString("res_id"),
                    rs.getString("num_reservation"),
                    rs.getDate("date_arrivee"),
                    rs.getDate("date_depart"),
                    rs.getInt("nb_personne"),
                    rs.getFloat("montant_total"),
                    StatutReservation.valueOf(rs.getString("res_statut").toUpperCase()),
                    client,
                    chambre
            );

            list.add(reservation);
        }
    }

    return list;
}


    // Outil pour générer un id de réservation
    public String generateReservationId() {
        return "RES-" + System.currentTimeMillis();
    }

    // Outil pour générer un numéro de réservation
    public String generateNumReservation() {
        return "R-" + System.currentTimeMillis();
    }

    /**
     * Check-in : passer une réservation CONFIRMEE à OCCUPEE.
     */
    public boolean checkInReservation(String numReservation) throws SQLException {
        String selectSql = "SELECT statut FROM reservations WHERE num_reservation = ?";
        String updateSql = "UPDATE reservations SET statut = ? WHERE num_reservation = ?";

        try (Connection conn = connexionDB.getConnection()) {

            String statutActuel = null;

            // 1) Vérifier l'existence et le statut actuel
            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setString(1, numReservation);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statutActuel = rs.getString("statut");
                    } else {
                        return false; // pas de réservation trouvée
                    }
                }
            }

            if (!"CONFIRMEE".equalsIgnoreCase(statutActuel)) {
                return false; // on ne fait le check-in que si la réservation est CONFIRMEE
            }

            // 2) Mettre à jour en OCCUPEE
            try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                psUpdate.setString(1, "OCCUPEE");
                psUpdate.setString(2, numReservation);
                int rows = psUpdate.executeUpdate();
                return rows > 0;
            }
        }
    }

    /**
     * Check-out : terminer une réservation OCCUPEE et rendre la chambre DISPONIBLE.
     */
    public boolean checkOutReservation(String numReservation) throws SQLException {
        String selectSql = "SELECT r.id, r.statut, c.id AS chambre_id " +
                           "FROM reservations r " +
                           "JOIN chambres c ON r.chambre_id = c.id " +
                           "WHERE r.num_reservation = ?";
        String updateReservationSql = "UPDATE reservations SET statut = ? WHERE id = ?";
        String updateChambreSql = "UPDATE chambres SET etat = ? WHERE id = ?";

        try (Connection conn = connexionDB.getConnection()) {

            conn.setAutoCommit(false); // transaction

            String reservationId = null;
            String chambreId = null;
            String statutActuel = null;

            // 1) Récupérer la réservation et la chambre
            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setString(1, numReservation);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        reservationId = rs.getString("id");
                        chambreId = rs.getString("chambre_id");
                        statutActuel = rs.getString("statut");
                    } else {
                        conn.setAutoCommit(true);
                        return false; // pas de réservation trouvée
                    }
                }
            }

            if (!"OCCUPEE".equalsIgnoreCase(statutActuel)) {
                conn.setAutoCommit(true);
                return false; // on ne fait le check-out que si la réservation est OCCUPEE
            }

            // 2) Mettre à jour le statut de la réservation (par ex. TERMINEE)
            try (PreparedStatement psUpdateRes = conn.prepareStatement(updateReservationSql)) {
                psUpdateRes.setString(1, "TERMINEE"); // adapte à ton enum StatutReservation
                psUpdateRes.setString(2, reservationId);
                psUpdateRes.executeUpdate();
            }

            // 3) Mettre la chambre en DISPONIBLE
            try (PreparedStatement psUpdateCh = conn.prepareStatement(updateChambreSql)) {
                psUpdateCh.setString(1, "DISPONIBLE"); // valeur de ton enum EtatChambre
                psUpdateCh.setString(2, chambreId);
                psUpdateCh.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);
            return true;
        }
    }
}
