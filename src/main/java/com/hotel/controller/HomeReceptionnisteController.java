package com.hotel.controller;

import com.hotel.dao.RESERVATIONDAO;
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

import java.util.Optional;

public class HomeReceptionnisteController {

    @FXML private StackPane contentArea;
    @FXML private Label lblWelcome, lblEmail;

    private final RESERVATIONDAO reservationDAO = new RESERVATIONDAO();

    @FXML
    public void initialize() {
        // Chargement des infos utilisateur
        Platform.runLater(() -> {
            Users currentUser = UserSession.getInstance();
            if (currentUser != null) {
                lblWelcome.setText("Bienvenue, " + currentUser.getPrenom() + " !");
                lblEmail.setText(currentUser.getEmail());
            } else {
                lblWelcome.setText("Bienvenue, Chargement...");
                lblEmail.setText("");
            }
            // Charger le tableau de bord par défaut
            handleAccueil(null);
        });
    }

    /** Charge une vue FXML dans le contentArea */
    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur chargement", "Impossible de charger : " + fxmlPath + "\n" + e.getMessage());
        }
    }

    /** Tableau de bord */
    @FXML
    public void handleAccueil(ActionEvent event) {
        loadView("/com/hotel/dashboardRecep.fxml");
    }

    /** Nouvelle réservation */
    @FXML
    public void handlePasserReservation(ActionEvent event) {
        loadView("/com/hotel/reservation_form.fxml");
    }

    /** Check-In client */
    @FXML
    public void handleCheckIn(ActionEvent event) {
        Optional<String> result = showInputDialog("Check-In", "Enregistrement Arrivée", "Entrez le numéro de réservation :");
        result.ifPresent(numRes -> {
            try {
                boolean ok = reservationDAO.checkIn(numRes);
                if (ok) {
                    showNotify("Succès", "Check-In validé. Le client est maintenant 'En chambre'.");
                    handleAccueil(null); // Rafraîchit le dashboard
                } else {
                    showError("Erreur", "Numéro de réservation introuvable ou déjà enregistré.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showError("Erreur SQL", ex.getMessage());
            }
        });
    }

    /** Check-Out client */
    @FXML
    public void handleCheckOut(ActionEvent event) {
        Optional<String> result = showInputDialog("Check-Out", "Validation Départ", "Entrez le numéro de réservation :");
        result.ifPresent(numRes -> {
            try {
                boolean ok = reservationDAO.checkOut(numRes);
                if (ok) {
                    showNotify("Succès", "Check-Out validé. La chambre est libérée.");
                    handleAccueil(null);
                } else {
                    showError("Erreur", "Impossible de faire le Check-Out (vérifiez le numéro ou le statut).");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showError("Erreur SQL", ex.getMessage());
            }
        });
    }

    /** Déconnexion / Logout */
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/hotel/auth.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.centerOnScreen();
            stage.show();
            // Supprimer la session actuelle
            UserSession.clean();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur Logout", "Impossible de se déconnecter.");
        }
    }

    /** Dialog pour entrer un texte (numéro de réservation) */
    private Optional<String> showInputDialog(String title, String header, String content) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        return dialog.showAndWait();
    }

    /** Alert info */
    private void showNotify(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(content);
        alert.showAndWait();
    }

    /** Alert erreur */
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(content);
        alert.showAndWait();
    }
}