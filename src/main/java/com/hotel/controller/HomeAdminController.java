package com.hotel.controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class HomeAdminController {

    @FXML
    private StackPane contentArea; // Zone centrale qui change

    /**
     * Cette méthode s'exécute AUTOMATIQUEMENT au chargement du FXML.
     * Elle permet d'afficher le Dashboard par défaut dès l'ouverture.
     */
    @FXML
    public void initialize() {
        showDashboard();
    }

    @FXML
    private void showDashboard() {
        loadView("/com/hotel/dashboard_accueil.fxml");
    }

    @FXML
    private void showStaffManagement() {
        loadView("/com/hotel/manage_staff.fxml");
    }

    @FXML
    private void showRoomManagement() {
        loadView("/com/hotel/manage_rooms.fxml");
    }

    @FXML
    private void showHotelProfil() {
        loadView("/com/hotel/hotel_profil.fxml");
    }

    /**
     * Méthode "Moteur" : elle vide le centre et injecte la nouvelle page.
     * Utiliser .setAll(node) est parfait car cela remplace le contenu précédent.
     */
    private void loadView(String fxmlPath) {
        try {
            Parent node = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(node);
            System.out.println("✅ Vue chargée : " + fxmlPath);
        } catch (IOException e) {
            System.err.println("❌ Erreur de chargement (" + fxmlPath + ") : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        Parent loginView = FXMLLoader.load(getClass().getResource("/com/hotel/auth.fxml"));
        Scene loginScene = new Scene(loginView);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.centerOnScreen();
        window.show();
    }
}