package com.hotel.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class AuthController {

    @FXML private VBox vboxLogin, vboxRegister;
    @FXML private Label lblError;
    @FXML private TextField emailLogin, nomRegister, prenomRegister, emailRegister, adresseRegister, nationaliteRegister;
    @FXML private PasswordField passwordLogin, passRegister;

    @FXML
    private void showRegisterForm() {
        vboxLogin.setVisible(false); vboxLogin.setManaged(false);
        vboxRegister.setVisible(true); vboxRegister.setManaged(true);
        lblError.setText("");
    }

    @FXML
    private void showLoginForm() {
        vboxRegister.setVisible(false); vboxRegister.setManaged(false);
        vboxLogin.setVisible(true); vboxLogin.setManaged(true);
        lblError.setText("");
    }

    @FXML
    private void handleLogin() {
        if (emailLogin.getText().isEmpty() || passwordLogin.getText().isEmpty()) {
            lblError.setText("❌ Veuillez saisir vos identifiants.");
            return;
        }

        // Simulation de connexion réussie
        redirectToHome(nomRegister.getText(), prenomRegister.getText());
    }

    @FXML
    private void handleRegister() {
        if (nomRegister.getText().isEmpty() || emailRegister.getText().isEmpty() || passRegister.getText().isEmpty()) {
            lblError.setText("❌ Tous les champs sont obligatoires.");
            return;
        }

        // Logique d'inscription (DAO) à insérer ici
        lblError.setText("✅ Compte créé ! Connectez-vous.");
        lblError.setStyle("-fx-text-fill: green;");
        showLoginForm();
    }

    private void redirectToHome(String nom, String prenom) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/hotel/home_client.fxml"));
            Parent root = loader.load();

            HomeClientController homeController = loader.getController();
            homeController.setUserInfo(nom, prenom);

            Stage stage = (Stage) emailLogin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Teranga Hotels - Accueil");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}