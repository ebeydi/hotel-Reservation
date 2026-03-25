package com.hotel.controller;

import java.io.File;

import com.hotel.dao.HOTELDAO;
import com.hotel.model.Hotel;
import com.hotel.model.HotelSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;

public class HotelProfilController {

    // Liaison avec le fichier FXML
    @FXML private TextField txtNom;
    @FXML private TextField txtVille;
    @FXML private TextField txtAdresse;
    @FXML private TextField txtCategorie;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelephone;
    @FXML private TextArea txtDescription;
    @FXML private ComboBox<String> comboStatut;

    private HOTELDAO hotelDao = new HOTELDAO();

    /**
     * S'exécute automatiquement au chargement de la page
     */
    @FXML
    public void initialize() {
        // Initialiser le ComboBox des statuts
        if (comboStatut != null) {
            comboStatut.getItems().addAll("OUVERT", "FERME", "EN_TRAVAUX");
        }

        // Charger les informations depuis la session
        loadHotelData();
    }

    /**
     * Remplit les champs du formulaire avec les données de l'hôtel actuel
     */
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
            
            if (currentHotel.getStatut() != null) {
                comboStatut.setValue(currentHotel.getStatut());
            }
        }
    }

    /**
     * Action du bouton "Enregistrer les modifications"
     */
    @FXML
    private void handleUpdateHotel() {

        Hotel h = HotelSession.getHotel();
        boolean isNew = false;

        // 🔥 CAS 1 : création
        if (h == null) {
            h = new Hotel();
            h.setId(java.util.UUID.randomUUID().toString());
            isNew = true;
        }

        // 🔥 remplissage des données
        h.setNom(txtNom.getText());
        h.setVille(txtVille.getText());
        h.setAdresse(txtAdresse.getText());
        h.setCategorie(txtCategorie.getText());
        h.setEmail(txtEmail.getText());
        h.setTelephone(txtTelephone.getText());
        h.setDescription(txtDescription.getText());
        h.setStatut(comboStatut.getValue());
       h.setImage(txtImage.getText() != null ? txtImage.getText() : "default.jpg");

        boolean success;

        // 🔥 décision INSERT ou UPDATE
        if (isNew) {
            success = hotelDao.insert(h);
        } else {
            success = hotelDao.update(h);
        }

        if (success) {
            // mettre à jour la session
            HotelSession.setHotel(h);
            showAlert(AlertType.INFORMATION, "Succès", "Hôtel enregistré !");
        } else {
            showAlert(AlertType.ERROR, "Erreur", "Échec de l'opération.");
        }
    }
@FXML private TextField txtImage; // pour l'image

@FXML
private void handleChooseImage() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Sélectionner une image");
    fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
    );
    File file = fileChooser.showOpenDialog(txtImage.getScene().getWindow());
    if (file != null) {
        txtImage.setText(file.getName()); // on récupère juste le nom pour stocker en DB
        // tu peux copier l'image dans un dossier ressources si besoin
    }
}
    /**
     * Utilitaire pour afficher des messages à l'utilisateur
     */
    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}