package com.hotel.controller;

import com.hotel.dao.USERDAO;
import com.hotel.model.Users;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        Users user = new Users(email, password, password, password, password, password, null);
        boolean success = USERDAO.login(user);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (success) {
            alert.setContentText("✅ Connexion réussie !");
        } else {
            alert.setContentText("❌ Email ou mot de passe incorrect");
        }
        alert.showAndWait();
    }
}
