package com.hotel.controller;

import com.hotel.dao.USERDAO;
import com.hotel.model.UserSession;
import com.hotel.model.Users;
import com.hotel.model.UsersRole;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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

        // 1. On vérifie en base de données
        USERDAO dao = new USERDAO();
        Users user = dao.login(email, pass);

        if (user != null) {
            // 2. On stocke l'utilisateur dans la session
            UserSession.setInstance(user);
            System.out.println("✅ Connexion réussie : " + user.getNom());
            
            // 3. Direction l'accueil !
            navigateToHome();
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
            UsersRole.CLIENT
        );

        if (USERDAO.save(newUser)) {
            showLoginForm();
            lblError.setText("✅ Inscription réussie ! Connectez-vous.");
            lblError.setStyle("-fx-text-fill: #27ae60;");
        } else {
            showError("❌ Erreur lors de l'enregistrement.");
        }
    }

    private void navigateToHome() {
        try {
            Stage stage = (Stage) emailLogin.getScene().getWindow();
            // Assure-tu que le nom du fichier est exact
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/hotel/home_client.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showError("❌ Erreur de chargement de la page d'accueil");
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