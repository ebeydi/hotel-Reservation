package com.hotel.controller;

import com.hotel.dao.RESERVATIONDAO;
import com.hotel.model.Reservation;
import com.hotel.model.UserSession;
import com.hotel.model.Users;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

public class HomeReceptionnisteController {

    @FXML private StackPane contentArea;
    @FXML private Label lblWelcome, lblEmail;

    private final RESERVATIONDAO reservationDAO = new RESERVATIONDAO();

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            Users currentUser = UserSession.getInstance();
            if (currentUser != null) {
                lblWelcome.setText("Bienvenue, " + currentUser.getPrenom() + " !");
                lblEmail.setText(currentUser.getEmail());
            }
            handleAccueil(null); // Charge le Dashboard au démarrage
        });
    }

    /**
     * Charge une vue FXML dans la zone centrale et retourne son FXMLLoader
     */
    private FXMLLoader loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);
            return loader;
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur de navigation", "Impossible de charger la page : " + fxmlPath);
            return null;
        }
    }

    /**
     * Affiche le Dashboard (Tableau des réservations)
     */
    @FXML
    public void handleAccueil(ActionEvent event) {
        FXMLLoader loader = loadView("/com/hotel/dashboardRecep.fxml");
        if (loader != null) {
            // On lie le Dashboard au contrôleur principal pour permettre la navigation
            ReceptionnistController dash = loader.getController();
            dash.setMainController(this);
        }
    }

    /**
     * Ouvre le formulaire pour une nouvelle réservation (vierge)
     */
    @FXML
    public void handlePasserReservation(ActionEvent event) {
        FXMLLoader loader = loadView("/com/hotel/reservation_form.fxml");
        if (loader != null) {
            ReservationFormController controller = loader.getController();
            controller.setMainController(this);
            controller.setReservationExistante(null); // Mode Création
        }
    }

    /**
     * Ouvre le formulaire pré-rempli pour valider/modifier une demande existante
     */
    public void handleModifierReservation(Reservation reservation) {
        if (reservation == null) return;

        FXMLLoader loader = loadView("/com/hotel/reservation_form.fxml");
        if (loader != null) {
            ReservationFormController controller = loader.getController();
            controller.setMainController(this);
            
            // TRANSMISSION CRUCIALE : On injecte l'objet sélectionné
            controller.setReservationExistante(reservation); 
            System.out.println("LOG: Navigation vers formulaire pour la réservation " + reservation.getNumReservation());
        }
    }

    @FXML
    public void handleCheckIn(ActionEvent event) {
        showInputDialog("Check-In", "Arrivée client", "Numéro de réservation :")
            .ifPresent(numRes -> {
                if (reservationDAO.checkIn(numRes)) {
                    showNotify("Succès", "Le client a été enregistré avec succès.");
                    handleAccueil(null);
                } else {
                    showError("Erreur", "Réservation introuvable ou déjà confirmée.");
                }
            });
    }

    @FXML
    public void handleCheckOut(ActionEvent event) {
        showInputDialog("Check-Out", "Départ client", "Numéro de réservation :")
            .ifPresent(numRes -> {
                if (reservationDAO.checkOut(numRes)) {
                    showNotify("Succès", "Chambre libérée et dossier clôturé.");
                    handleAccueil(null);
                } else {
                    showError("Erreur", "Échec du Check-Out (vérifiez le numéro).");
                }
            });
    }

    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/hotel/auth.fxml"));
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            UserSession.clean();
        } catch (IOException e) {
            showError("Erreur", "Déconnexion impossible.");
        }
    }

    // --- Utilitaires d'interface ---

    private Optional<String> showInputDialog(String title, String header, String content) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        return dialog.showAndWait();
    }

    private void showNotify(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}