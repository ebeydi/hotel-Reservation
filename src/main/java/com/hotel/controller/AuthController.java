package com.hotel.controller;

import com.hotel.dao.USERDAO;
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
    // CORRECTION : phoneRegister a été supprimé ici
    @FXML private TextField emailLogin, nomRegister, prenomRegister, emailRegister, adresseRegister, nationaliteRegister;
    @FXML private PasswordField passwordLogin, passRegister;

    @FXML
    private void handleLogin() {
        String email = emailLogin.getText();
        String pass = passwordLogin.getText();

        if (email.isEmpty() || pass.isEmpty()) {
            showError("❌ Veuillez remplir tous les champs.");
            return;
        }

        try {
            USERDAO dao = new USERDAO();
            Users user = dao.login(email, pass);

            if (user != null) {
                System.out.println("✅ Connexion réussie pour : " + user.getNom());
                navigateToDashboard(user);
            } else {
                showError("❌ Email ou mot de passe incorrect.");
            }
        } catch (Exception e) {
            showError("❌ Erreur de base de données. Vérifiez XAMPP.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegister() {
        if (isAnyFieldEmpty()) {
            showError("❌ Veuillez remplir les champs obligatoires.");
            return;
        }

        // Création du nouvel utilisateur
        Users newUser = new Users(
                null,
                emailRegister.getText(),
                passRegister.getText(),
                nomRegister.getText(),
                prenomRegister.getText(),
                null, // CORRECTION : Le champ téléphone est passé à null
                adresseRegister.getText(),
                emailRegister.getText(),
                nationaliteRegister.getText(),
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

    private void navigateToDashboard(Users user) {
        try {
            String fxmlFile;
            UsersRole role = user.getRole();

            switch (role) {
                case ADMIN: fxmlFile = "/com/hotel/home_admin.fxml"; break;
                case RECEPTIONNISTE: fxmlFile = "/com/hotel/home_receptionniste.fxml"; break;
                default: fxmlFile = "/com/hotel/home_client.fxml"; break;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            if (role == UsersRole.CLIENT) {
                HomeClientController controller = loader.getController();
                controller.setUserInfo(user.getNom(), user.getPrenom());
            }

            Stage stage = (Stage) emailLogin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Teranga Booking - " + role);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("❌ Erreur de chargement de l'interface.");
        }
    }

    private void showError(String message) {
        lblError.setText(message);
        lblError.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
    }

    private boolean isAnyFieldEmpty() {
        return nomRegister.getText().isEmpty() || emailRegister.getText().isEmpty() || passRegister.getText().isEmpty();
    }

    @FXML private void showRegisterForm() { lblError.setText(""); toggleForms(false); }
    @FXML private void showLoginForm() { lblError.setText(""); toggleForms(true); }

    private void toggleForms(boolean showLogin) {
        vboxLogin.setVisible(showLogin);
        vboxLogin.setManaged(showLogin);
        vboxRegister.setVisible(!showLogin);
        vboxRegister.setManaged(!showLogin);
        if (showLogin) vboxLogin.toFront(); else vboxRegister.toFront();
    }
}