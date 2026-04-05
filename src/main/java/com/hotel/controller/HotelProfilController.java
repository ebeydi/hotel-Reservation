package com.hotel.controller;

import java.io.File;

import com.hotel.dao.HOTELDAO;
import com.hotel.model.Hotel;
import com.hotel.model.HotelSession;
import com.hotel.model.Statut;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;

public class HotelProfilController {

    @FXML private TextField txtNom;
    @FXML private TextField txtVille;
    @FXML private TextField txtAdresse;
    @FXML private TextField txtCategorie;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelephone;
    @FXML private TextArea txtDescription;
    @FXML private ComboBox<Statut> comboStatut; // 🔥 ENUM
    @FXML private TextField txtImage;

    private HOTELDAO hotelDao = new HOTELDAO();

    @FXML
    public void initialize() {

        // 🔥 ComboBox enum
        comboStatut.getItems().setAll(Statut.values());

        loadHotelData();
    }

    private void loadHotelData() {

        Hotel currentHotel = HotelSession.getHotel();

        if (currentHotel != null) {
            txtNom.setText(currentHotel.getNom());
            txtVille.setText(currentHotel.getVille());
            txtAdresse.setText(currentHotel.getAdresse());
            txtCategorie.setText(currentHotel.getCategorie());
            txtEmail.setText(currentHotel.getEmail());
            txtTelephone.setText(currentHotel.getTelephone());
            txtDescription.setText(currentHotel.getDescription());

            // 🔥 correction enum
            if (currentHotel.getStatut() != null) {
                comboStatut.setValue(currentHotel.getStatut());
            }
        }
    }

    @FXML
    private void handleUpdateHotel() {

        Hotel h = HotelSession.getHotel();
        boolean isNew = false;

        // 🔥 création si null
        if (h == null) {
            h = new Hotel();
            h.setId(java.util.UUID.randomUUID().toString());
            isNew = true;
        }

        // 🔥 remplissage
        h.setNom(txtNom.getText());
        h.setVille(txtVille.getText());
        h.setAdresse(txtAdresse.getText());
        h.setCategorie(txtCategorie.getText());
        h.setEmail(txtEmail.getText());
        h.setTelephone(txtTelephone.getText());
        h.setDescription(txtDescription.getText());

        // 🔥 CORRECTION CRITIQUE FK
        if (comboStatut.getValue() == null) {
            h.setStatut(Statut.ACTIF); // valeur par défaut
        } else {
            h.setStatut(comboStatut.getValue());
        }

        // 🔥 image safe
        if (txtImage.getText() == null || txtImage.getText().isEmpty()) {
            h.setImage("default.jpg");
        } else {
            h.setImage(txtImage.getText());
        }

        boolean success;

        if (isNew) {
            success = hotelDao.insert(h);
        } else {
            success = hotelDao.update(h);
        }

        if (success) {
            HotelSession.setHotel(h);
            showAlert(AlertType.INFORMATION, "Succès", "Hôtel enregistré !");
        } else {
            showAlert(AlertType.ERROR, "Erreur", "Échec de l'opération.");
        }
    }

    @FXML
    private void handleChooseImage() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");

        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(txtImage.getScene().getWindow());

        if (file != null) {
            txtImage.setText(file.getName());
        }
    }

    private void showAlert(AlertType type, String title, String content) {

        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}