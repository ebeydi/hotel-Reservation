package com.hotel.controller;

import com.hotel.dao.HOTELDAO;
import com.hotel.model.Hotel;
import com.hotel.model.HotelSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

public class HotelProfilController {

    // Liaison avec le fichier FXML
    @FXML private TextField txtNom;
    @FXML private TextField txtVille;
    @FXML private TextField txtAdresse;
    @FXML private TextField txtCategorie;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelephone;
    @FXML private TextArea txtDescription;
    @FXML private ComboBox<String> comboStatut; // Pour ton attribut Statut

    private HOTELDAO hotelDao = new HOTELDAO();

    /**
     * S'exécute automatiquement au chargement de la page
     */
    @FXML
    public void initialize() {
        // Initialiser le ComboBox des statuts (exemple)
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
                comboStatut.setValue(currentHotel.getStatut().toString());
            }
        }
    }

    /**
     * Action du bouton "Enregistrer les modifications"
     */
    @FXML
    private void handleUpdateHotel() {
        // 1. On récupère l'objet hôtel de la session
        Hotel h = HotelSession.getHotel();
        
        if (h == null) {
            showAlert(AlertType.ERROR, "Erreur", "Aucun hôtel n'est chargé en session.");
            return;
        }

        // 2. On met à jour l'objet avec les saisies de l'utilisateur
        h.setNom(txtNom.getText());
        h.setVille(txtVille.getText());
        h.setAdresse(txtAdresse.getText());
        h.setCategorie(txtCategorie.getText());
        h.setEmail(txtEmail.getText());
        h.setTelephone(txtTelephone.getText());
        h.setDescription(txtDescription.getText());
        
        // Note: Pour le statut, il faudra convertir le String du combo en ton Enum Statut
        // h.setStatut(Statut.valueOf(comboStatut.getValue())); 

        // 3. On enregistre en base de données via le DAO
        if (hotelDao.update(h)) {
            // Mise à jour de la session globale pour que les autres pages voient le changement
            HotelSession.setHotel(h);
            showAlert(AlertType.INFORMATION, "Succès", "Les informations de l'hôtel ont été mises à jour !");
        } else {
            showAlert(AlertType.ERROR, "Échec", "Impossible de mettre à jour la base de données.");
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