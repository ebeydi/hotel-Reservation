package com.hotel.controller;

import com.hotel.dao.USERDAO;
import com.hotel.model.UserSession;
import com.hotel.model.Users;
import com.hotel.model.UsersRole;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class AuthController {

    @FXML private VBox vboxLogin, vboxRegister;
    @FXML private Label lblError;
    @FXML private TextField emailLogin, nomRegister, prenomRegister, emailRegister, phoneRegister, adresseRegister, nationaliteRegister;
    @FXML private PasswordField passwordLogin, passRegister;

    @FXML
    private void handleLogin() {
        lblError.setText("");
        String email = emailLogin.getText();
        String pass = passwordLogin.getText();

        if (email.isEmpty() || pass.isEmpty()) {
            showError("❌ Veuillez remplir tous les champs");
            return;
        }

        USERDAO dao = new USERDAO();
        Users user = dao.login(email, pass);

        if (user != null) {
            UserSession.setInstance(user);
            System.out.println("✅ Connexion réussie : " + user.getNom() + " | Rôle : " + user.getRole());
            
            // On appelle la nouvelle méthode de navigation intelligente
            navigateToDashboard(user.getRole());
        } else {
            showError("❌ Email ou mot de passe incorrect");
        }
    }

    @FXML
    private void handleRegister() {
        if (isAnyFieldEmpty()) {
            showError("❌ Veuillez remplir les champs obligatoires");
            return;
        }

        Users newUser = new Users(
            null, emailRegister.getText(), passRegister.getText(), 
            nomRegister.getText(), prenomRegister.getText(), 
            phoneRegister.getText(), adresseRegister.getText(), 
            emailRegister.getText(), nationaliteRegister.getText(), 
            UsersRole.CLIENT // Inscription par défaut en tant que CLIENT
        );

        if (USERDAO.save(newUser)) {
            showLoginForm();
            lblError.setText("✅ Inscription réussie ! Connectez-vous.");
            lblError.setStyle("-fx-text-fill: #27ae60;");
        } else {
            showError("❌ Erreur lors de l'enregistrement.");
        }
    }

    // --- LA MÉTHODE QUI GÈRE LA REDIRECTION SELON LE RÔLE ---
    private void navigateToDashboard(UsersRole role) {
        try {
            String fxmlFile = "";
            
            // Sélection du fichier selon le rôle
            if (role == UsersRole.ADMIN) {
                fxmlFile = "/com/hotel/home_admin.fxml";
            } else {
                fxmlFile = "/com/hotel/home_client.fxml";
            }

            Stage stage = (Stage) emailLogin.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            
        } catch (IOException e) {
            e.printStackTrace();
            showError("❌ Erreur de chargement de l'interface : " + role);
        }
    }

    private void showError(String message) {
        lblError.setText(message);
        lblError.setStyle("-fx-text-fill: #e74c3c;");
    }

    private boolean isAnyFieldEmpty() {
        return nomRegister.getText().isEmpty() || emailRegister.getText().isEmpty() || passRegister.getText().isEmpty();
    }

    @FXML
    private void showRegisterForm() {
        lblError.setText("");
        toggleForms(false);
    }

    @FXML
    private void showLoginForm() {
        lblError.setText("");
        toggleForms(true);
    }

    private void toggleForms(boolean showLogin) {
        vboxLogin.setVisible(showLogin);
        vboxLogin.setManaged(showLogin);
        vboxRegister.setVisible(!showLogin);
        vboxRegister.setManaged(!showLogin);
        if (showLogin) vboxLogin.toFront(); else vboxRegister.toFront();
    }
}