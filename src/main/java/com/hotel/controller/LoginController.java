package com.hotel.controller;

import com.hotel.dao.USERDAO;
import com.hotel.model.Users;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginController {

    @FXML private VBox vboxLogin, vboxRegister;
    @FXML private Label lblError;

    // Connexion
    @FXML private TextField emailLogin;
    @FXML private PasswordField passwordLogin;

    // Inscription
    @FXML private TextField nomRegister, prenomRegister, emailRegister, adresseRegister, nationaliteRegister;
    @FXML private PasswordField passRegister;

    @FXML
    private void showRegisterForm() {
        lblError.setText(""); // Efface les erreurs
        vboxLogin.setVisible(false);
        vboxLogin.setManaged(false);
        vboxRegister.setVisible(true);
        vboxRegister.setManaged(true);
    }

    @FXML
    private void showLoginForm() {
        lblError.setText("");
        vboxRegister.setVisible(false);
        vboxRegister.setManaged(false);
        vboxLogin.setVisible(true);
        vboxLogin.setManaged(true);
    }

    @FXML
    private void handleLogin() {
        if (emailLogin.getText().isEmpty() || passwordLogin.getText().isEmpty()) {
            lblError.setText("❌ Veuillez remplir tous les champs de connexion");
            return;
        }
        // Logique DAO ici...
    }

    @FXML
    private void handleRegister() {
        // VALIDATION : On vérifie si un seul champ est vide
        if (nomRegister.getText().isEmpty() ||
                prenomRegister.getText().isEmpty() ||
                emailRegister.getText().isEmpty() ||
                passRegister.getText().isEmpty() ||
                adresseRegister.getText().isEmpty() ||
                nationaliteRegister.getText().isEmpty()) {

            lblError.setText("❌ Erreur : Tous les champs sont obligatoires !");
            return;
        }

        // Si tout est rempli :
        lblError.setText("");
        Users newUser = new Users(nomRegister.getText(), prenomRegister.getText(),
                        emailRegister.getText(), passRegister.getText(),
                adresseRegister.getText(), nationaliteRegister.getText(), null);

        // Simuler succès
        System.out.println("Inscription de : " + nomRegister.getText());
        showLoginForm();
        lblError.setText("✅ Compte créé ! Connectez-vous.");
    }
}