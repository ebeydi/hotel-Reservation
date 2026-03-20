package com.hotel.controller;

import com.hotel.dao.ChambreDAO;
import com.hotel.dao.ClientDAO;
import com.hotel.dao.ReservationDAO;
import com.hotel.model.Chambre;
import com.hotel.model.Client;
import com.hotel.model.Reservation;
import com.hotel.model.StatutReservation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class ReservationFormController {

    @FXML
    private TextField txtNomClient;   // ici on suppose : email du client

    @FXML
    private TextField txtChambre;     // numéro de chambre

    @FXML
    private TextField txtNbPersonnes;

    @FXML
    private TextField txtMontantTotal;

    @FXML
    private DatePicker dpArrivee;

    @FXML
    private DatePicker dpDepart;

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final ClientDAO clientDAO = new ClientDAO();
    private final ChambreDAO chambreDAO = new ChambreDAO();

    @FXML
    public void handleEnregistrer(ActionEvent event) {
        String emailClient = txtNomClient.getText();
        String numChambre = txtChambre.getText();
        String nbPersonnesStr = txtNbPersonnes.getText();
        String montantStr = txtMontantTotal.getText();
        LocalDate dateArrivee = dpArrivee.getValue();
        LocalDate dateDepart = dpDepart.getValue();

        StringBuilder errors = new StringBuilder();

        // 1) Validation des champs simples
        if (emailClient == null || emailClient.trim().isEmpty()) {
            errors.append("* L'email du client est obligatoire.\n");
        }
        if (numChambre == null || numChambre.trim().isEmpty()) {
            errors.append("* La chambre est obligatoire.\n");
        }
        if (nbPersonnesStr == null || nbPersonnesStr.trim().isEmpty()) {
            errors.append("* Le nombre de personnes est obligatoire.\n");
        }
        if (montantStr == null || montantStr.trim().isEmpty()) {
            errors.append("* Le montant total est obligatoire.\n");
        }
        if (dateArrivee == null) {
            errors.append("* La date d'arrivée est obligatoire.\n");
        }
        if (dateDepart == null) {
            errors.append("* La date de départ est obligatoire.\n");
        }
        if (dateArrivee != null && dateDepart != null && dateDepart.isBefore(dateArrivee)) {
            errors.append("* La date de départ doit être après la date d'arrivée.\n");
        }

        int nbPersonnes = 0;
        float montantTotal = 0;

        try {
            nbPersonnes = Integer.parseInt(nbPersonnesStr);
            if (nbPersonnes <= 0) {
                errors.append("* Le nombre de personnes doit être positif.\n");
            }
        } catch (NumberFormatException e) {
            errors.append("* Le nombre de personnes doit être un entier.\n");
        }

        try {
            montantTotal = Float.parseFloat(montantStr);
            if (montantTotal < 0) {
                errors.append("* Le montant total ne peut pas être négatif.\n");
            }
        } catch (NumberFormatException e) {
            errors.append("* Le montant total doit être un nombre.\n");
        }

        if (errors.length() > 0) {
            showError("Erreur de saisie", errors.toString());
            return;
        }

        // Conversion LocalDate -> java.util.Date
        Date dateArrive = Date.from(dateArrivee.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date dateDepartDate = Date.from(dateDepart.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // 2) Récupérer le vrai Client via son email
        Client client;
        try {
            client = clientDAO.findByEmail(emailClient.trim());
        } catch (SQLException e) {
            showError("Erreur", "Problème lors de la recherche du client : " + e.getMessage());
            return;
        }
        if (client == null) {
            showError("Erreur", "Aucun client trouvé avec cet email.");
            return;
        }

        // 3) Récupérer la vraie Chambre via son numéro
        Chambre chambre;
        try {
            chambre = chambreDAO.findByNumero(numChambre.trim());
        } catch (SQLException e) {
            showError("Erreur", "Problème lors de la recherche de la chambre : " + e.getMessage());
            return;
        }
        if (chambre == null) {
            showError("Erreur", "Aucune chambre trouvée avec ce numéro.");
            return;
        }

        // 4) Construire la Reservation avec les vrais objets
        String idReservation = reservationDAO.generateReservationId();
        String numReservation = reservationDAO.generateNumReservation();

        Reservation reservation = new Reservation(
                idReservation,
                numReservation,
                dateArrive,
                dateDepartDate,
                nbPersonnes,
                montantTotal,
                StatutReservation.EN_ATTENTE, // ou CONFIRMEE selon ta logique
                client,
                chambre
        );

        // 5) Sauvegarder
        try {
            reservationDAO.save(reservation);
            showInfo("Succès", "La réservation a été enregistrée avec succès.");
            closeWindow();
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Erreur", "Erreur lors de l'enregistrement en base : " + e.getMessage());
        }
    }

    @FXML
    public void handleAnnuler(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtNomClient.getScene().getWindow();
        stage.close();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
