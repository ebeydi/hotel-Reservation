package com.hotel.controller;

import com.hotel.model.UserSession;
import com.hotel.model.Users;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;

public class HomeClientController {

    @FXML private Label lblWelcome;
    @FXML private Label lblEmail;

    /**
     * Cette méthode s'exécute automatiquement quand la page s'affiche.
     */
    @FXML
    public void initialize() {
        Users currentUser = UserSession.getInstance();
        
        if (currentUser != null) {
            // On affiche le vrai prénom et le vrai email du client connecté
            lblWelcome.setText("Bienvenue, " + currentUser.getPrenom() + " !");
            lblEmail.setText(currentUser.getEmail());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            // 1. On vide la session
            UserSession.clean();
            
            // 2. On retourne à la page de connexion
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