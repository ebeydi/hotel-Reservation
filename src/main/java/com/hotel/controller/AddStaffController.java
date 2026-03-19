
package com.hotel.controller;

import com.hotel.dao.USERDAO;
import com.hotel.model.Users;
import com.hotel.model.UsersRole;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddStaffController {

    @FXML private TextField txtNom, txtPrenom, txtEmail;
    @FXML private PasswordField txtPass;

    @FXML
    private void handleSave() {
        if (txtEmail.getText().isEmpty() || txtPass.getText().isEmpty()) {
            System.err.println("Champs obligatoires vides");
            return;
        }

        // Création du compte avec rôle RECEPTIONNISTE d'office
        Users staff = new Users();
        staff.setNom(txtNom.getText());
        staff.setPrenom(txtPrenom.getText());
        staff.setEmail(txtEmail.getText());
        staff.setPassword(txtPass.getText());
        staff.setRole(UsersRole.RECEPTIONNISTE);
        
        // On remplit les champs secondaires avec des valeurs par défaut
        staff.setTelephone("Non défini");
        staff.setAdresse("Hôtel");
        staff.setNationalite("Sénégalaise");

        if (USERDAO.save(staff)) {
            System.out.println("✅ Réceptionniste ajouté !");
            closeWindow();
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtNom.getScene().getWindow();
        stage.close();
    }
}