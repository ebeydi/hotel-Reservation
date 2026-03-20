package com.hotel.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.hotel.dao.ReservationDAO;
import com.hotel.model.Chambre;
import com.hotel.model.Client;
import com.hotel.model.Reservation;
import com.hotel.model.StatutReservation;
import com.hotel.model.UserSession;
import com.hotel.model.Users;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class HomeReceptionnisteController {

    @FXML
    private Label lblNbReservationsJour;

    @FXML
    private Label lblNbCheckIn;

    @FXML
    private Label lblNbCheckOut;

    @FXML
    private Label lblWelcome;

    @FXML
    private Label lblEmail;

    @FXML
    private TableView<Reservation> tableReservations;

    @FXML
    private TableColumn<Reservation, String> colNumeroReservation;

    @FXML
    private TableColumn<Reservation, String> colNomClient;

    @FXML
    private TableColumn<Reservation, String> colHotel;

    @FXML
    private TableColumn<Reservation, String> colChambre;

    @FXML
    private TableColumn<Reservation, String> colDateArrivee;

    @FXML
    private TableColumn<Reservation, String> colDateDepart;

    @FXML
    private TableColumn<Reservation, String> colStatut;

    private final ObservableList<Reservation> reservations = FXCollections.observableArrayList();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final ReservationDAO reservationDAO = new ReservationDAO();

    /**
     * Méthode appelée automatiquement quand le FXML est chargé.
     */
    @FXML
    public void initialize() {
        // Infos du réceptionniste connecté
        Users currentUser = UserSession.getInstance();

        if (currentUser != null) {
            lblWelcome.setText("Bienvenue, " + currentUser.getPrenom() + " !");
            lblEmail.setText(currentUser.getEmail());
        } else {
            lblWelcome.setText("Bienvenue, réceptionniste !");
        }

        // Configuration des colonnes du TableView
        configureTableColumns();

        // Lier la liste à la TableView
        tableReservations.setItems(reservations);

        // Charger les réservations depuis la BD
        loadReservations();
    }

    /**
     * Configuration des colonnes du TableView.
     */
    private void configureTableColumns() {
        colNumeroReservation.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getNumReservation())
        );

        colNomClient.setCellValueFactory(
                data -> {
                    Client c = data.getValue().getClient();
                    String nom = (c != null) ? c.getNom() + " " + c.getPrenom() : "";
                    return new SimpleStringProperty(nom);
                }
        );

        colHotel.setCellValueFactory(
                data -> {
                    Chambre ch = data.getValue().getChambre();
                    String hotelNom = "";
                    if (ch != null && ch.getHotel() != null) {
                        hotelNom = ch.getHotel().getNom();
                    }
                    return new SimpleStringProperty(hotelNom);
                }
        );

        colChambre.setCellValueFactory(
                data -> {
                    Chambre ch = data.getValue().getChambre();
                    String numero = (ch != null) ? ch.getNumero() : "";
                    return new SimpleStringProperty(numero);
                }
        );

        colDateArrivee.setCellValueFactory(
                data -> new SimpleStringProperty(formatDate(data.getValue().getDateArrive()))
        );

        colDateDepart.setCellValueFactory(
                data -> new SimpleStringProperty(formatDate(data.getValue().getDateDepart()))
        );

        colStatut.setCellValueFactory(
                data -> {
                    StatutReservation s = data.getValue().getStatut();
                    String txt = (s != null) ? s.name() : "";
                    return new SimpleStringProperty(txt);
                }
        );
    }

    /**
     * Charger les réservations depuis la base et rafraîchir la TableView + stats.
     */
    private void loadReservations() {
        try {
            List<Reservation> list = reservationDAO.findAll(); // ou findForToday()
            reservations.setAll(list);
            tableReservations.refresh();

            // Mise à jour des cartes
            lblNbReservationsJour.setText(String.valueOf(list.size()));

            long nbCheckIn = list.stream()
                    .filter(r -> r.getStatut() == StatutReservation.OCCUPEE)
                    .count();
            lblNbCheckIn.setText(String.valueOf(nbCheckIn));

            long nbCheckOut = list.stream()
                    .filter(r -> r.getStatut() == StatutReservation.ANNULEE) // à adapter si tu as un statut TERMINEE
                    .count();
            lblNbCheckOut.setText(String.valueOf(nbCheckOut));

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur", "Impossible de charger les réservations : " + e.getMessage());
        }
    }

    private String formatDate(Date d) {
        return (d != null) ? dateFormat.format(d) : "";
    }

    /**
     * Passer une réservation : afficher le formulaire à la place de la liste.
     */
    @FXML
    public void handlePasserReservation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/hotel/reservation_form.fxml"));
            Parent formRoot = loader.load();

            // Récupérer le BorderPane racine de la scène actuelle
            Stage stage = (Stage) lblWelcome.getScene().getWindow();
            Scene scene = stage.getScene();
            BorderPane root = (BorderPane) scene.getRoot();

            // Mettre le formulaire au centre à la place du TableView
            root.setCenter(formRoot);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur", "Impossible d'afficher le formulaire de réservation : " + e.getMessage());
        }
    }

    /**
     * Check-in : changer le statut d'une réservation CONFIRMEE en OCCUPEE.
     */
    @FXML
    public void handleCheckIn(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Check-in");
        dialog.setHeaderText("Check-in d'une réservation");
        dialog.setContentText("Numéro de réservation :");

        dialog.showAndWait().ifPresent(numReservation -> {
            if (numReservation.trim().isEmpty()) {
                showError("Erreur", "Le numéro de réservation est obligatoire.");
                return;
            }

            try {
                boolean ok = reservationDAO.checkInReservation(numReservation.trim());

                if (ok) {
                    showInfo("Succès", "Le statut de la réservation a été changé en OCCUPEE.\nVous pouvez remettre la clé au client.");
                    loadReservations(); // rafraîchit la table
                } else {
                    showError("Erreur", "Aucune réservation CONFIRMEE trouvée pour ce numéro.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showError("Erreur", "Problème lors du check-in : " + e.getMessage());
            }
        });
    }

    /**
     * Check-out : terminer une réservation OCCUPEE et rendre la chambre DISPONIBLE.
     */
    @FXML
    public void handleCheckOut(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Check-out");
        dialog.setHeaderText("Check-out d'une réservation");
        dialog.setContentText("Numéro de réservation :");

        dialog.showAndWait().ifPresent(numReservation -> {
            if (numReservation.trim().isEmpty()) {
                showError("Erreur", "Le numéro de réservation est obligatoire.");
                return;
            }

            try {
                boolean ok = reservationDAO.checkOutReservation(numReservation.trim());

                if (ok) {
                    showInfo("Succès", "Le check-out a été effectué.\nLa chambre est maintenant DISPONIBLE.");
                    loadReservations(); // rafraîchit la table
                } else {
                    showError("Erreur", "Aucune réservation OCCUPEE trouvée pour ce numéro.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showError("Erreur", "Problème lors du check-out : " + e.getMessage());
            }
        });
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

    /**
     * Déconnexion : on vide la session et on revient à la page de login.
     */
    @FXML
    private void handleLogout() {
        try {
            UserSession.clean();

            Stage stage = (Stage) lblWelcome.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/hotel/auth.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
