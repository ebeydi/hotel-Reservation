package com.hotel.controller;

import com.hotel.dao.RECEPTIONNISTEDAO;
import com.hotel.model.Receptionniste;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.util.UUID;

public class AddStaffController {

    @FXML private TextField txtNom, txtPrenom, txtEmail, txtLogin;
    @FXML private PasswordField txtPass;

    private String hotelIdContext;
    private RECEPTIONNISTEDAO recepDao = new RECEPTIONNISTEDAO();
    
    // Cet objet sera NULL en mode "Ajout" et REMPLI en mode "Modification"
    private Receptionniste recepExistante = null;

    public void setHotelId(String hotelId) {
        this.hotelIdContext = hotelId;
    }

    /**
     * 🔥 MÉTHODE POUR LA MODIFICATION
     * Appelle cette méthode depuis l'AdminController pour passer les données
     */
    public void setRecepExistante(Receptionniste recep) {
        this.recepExistante = recep;
        
        // On pré-remplit les champs FXML
        txtNom.setText(recep.getNom());
        txtPrenom.setText(recep.getPrenom());
        txtEmail.setText(recep.getEmail());
        txtLogin.setText(recep.getLogin());
        // On laisse le mot de passe vide par sécurité ou on met des astérisques
        txtPass.setPromptText("Laissez vide pour ne pas changer");
    }

    @FXML
    private void handleSave() {
        if (txtEmail.getText().isEmpty() || txtLogin.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Champs obligatoires vides !");
            return;
        }

        boolean success;

        if (recepExistante == null) {
            // --- MODE AJOUT ---
            Receptionniste newStaff = new Receptionniste();
            newStaff.setId(UUID.randomUUID().toString().substring(0, 8));
            fillData(newStaff);
            newStaff.setMotDePasse(txtPass.getText()); // Obligatoire à la création
            success = recepDao.save(newStaff);
        } else {
            // --- MODE MODIFICATION ---
            fillData(recepExistante);
            // Si le champ pass n'est pas vide, on le met à jour
            if (!txtPass.getText().isEmpty()) {
                recepExistante.setMotDePasse(txtPass.getText());
            }
            success = recepDao.update(recepExistante);
        }

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Opération réussie !");
            closeWindow();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "L'opération a échoué en base de données.");
        }
    }

    /**
     * Centralise le remplissage des données communes
     */
    private void fillData(Receptionniste r) {
        r.setNom(txtNom.getText());
        r.setPrenom(txtPrenom.getText());
        r.setEmail(txtEmail.getText());
        r.setLogin(txtLogin.getText());
        r.setHotel_id(this.hotelIdContext);
        r.setTelephone("Non défini"); // Valeurs par défaut si non saisies
        r.setAdresse("Hôtel");
        r.setNationalite("Sénégalaise");
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtNom.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}