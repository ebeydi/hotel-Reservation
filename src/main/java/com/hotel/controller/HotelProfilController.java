package com.hotel.controller;

import com.hotel.dao.HOTELDAO;
import com.hotel.model.Hotel;
import com.hotel.model.Statut;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HotelProfilController {

    @FXML private TextField txtNom, txtVille, txtAdresse, txtCategorie, txtTelephone, txtEmail;
    @FXML private TextArea txtDescription;
    @FXML private ComboBox<String> comboImages;
    @FXML private ComboBox<Statut> comboStatut;
    @FXML private ImageView imgPreview;

    private final HOTELDAO hotelDao = new HOTELDAO();
    private Hotel hotelActuel;

    @FXML
    public void initialize() {
        // 1. Charger les enums dans le combo Statut
        comboStatut.setItems(FXCollections.observableArrayList(Statut.values()));

        // 2. Charger la galerie d'images
        chargerGalerieImages();

        // --- CHANGEMENT ICI ---
        // On ne charge plus les données automatiquement au démarrage
        // On attend que le HomeAdminController appelle setHotelContext()

        // 3. Ecouteur sur la ComboBox pour changer l'aperçu de l'image
        comboImages.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                afficherAperçu(newVal);
            }
        });
    }

    /**
     * NOUVELLE MÉTHODE : Appelée par le HomeAdminController pour injecter l'ID de l'hôtel
     */
    public void setHotelContext(String hotelId) {
        System.out.println("🏨 Chargement du profil pour l'hôtel ID : " + hotelId);
        
        // On récupère l'hôtel spécifique via le DAO amélioré
        hotelActuel = hotelDao.getHotelById(hotelId);

        if (hotelActuel != null) {
            remplirChamps();
        } else {
            // Si l'hôtel n'existe pas encore en BDD, on crée un nouvel objet avec cet ID
            hotelActuel = new Hotel();
            hotelActuel.setId(hotelId);
            System.out.println("ℹ️ Aucun profil trouvé, prêt pour une nouvelle création.");
        }
    }

    /**
     * Utilitaire pour remplir les champs texte avec les données de l'objet hotelActuel
     */
    private void remplirChamps() {
        txtNom.setText(hotelActuel.getNom());
        txtVille.setText(hotelActuel.getVille());
        txtAdresse.setText(hotelActuel.getAdresse());
        txtCategorie.setText(hotelActuel.getCategorie());
        txtTelephone.setText(hotelActuel.getTelephone());
        txtEmail.setText(hotelActuel.getEmail());
        txtDescription.setText(hotelActuel.getDescription());
        comboStatut.setValue(hotelActuel.getStatut());
        comboImages.setValue(hotelActuel.getImage());
        
        if (hotelActuel.getImage() != null && !hotelActuel.getImage().isEmpty()) {
            afficherAperçu(hotelActuel.getImage());
        }
    }

    // Gardé tel quel
    private void chargerDonneesHotel() {
        // Cette méthode est maintenant remplacée par setHotelContext pour le multi-hôtel
        // Mais on la laisse vide ou on la supprime si elle n'est plus appelée
    }

    private void chargerGalerieImages() {
        List<String> images = new ArrayList<>();
        try {
            URL url = getClass().getResource("/com/hotel/images/");
            if (url != null) {
                File dossier = new File(url.toURI());
                File[] fichiers = dossier.listFiles((dir, name) -> 
                    name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpeg")
                );

                if (fichiers != null) {
                    for (File f : fichiers) images.add(f.getName());
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement galerie : " + e.getMessage());
        }
        comboImages.setItems(FXCollections.observableArrayList(images));
    }

    private void afficherAperçu(String nomImage) {
        try {
            String path = "/com/hotel/images/" + nomImage;
            imgPreview.setImage(new Image(getClass().getResourceAsStream(path)));
        } catch (Exception e) {
            try {
                imgPreview.setImage(new Image(getClass().getResourceAsStream("/com/hotel/images/default.jpg")));
            } catch (Exception ignored) {}
        }
    }

    @FXML
    private void handleUpdateHotel() {
        if (txtNom.getText().isEmpty() || txtEmail.getText().isEmpty()) {
            showAlert(AlertType.WARNING, "Champs obligatoires", "Veuillez remplir au moins le nom et l'email.");
            return;
        }

        // Si hotelActuel est nul (cas improbable avec setHotelContext), on le sécurise
        if (hotelActuel == null) {
            hotelActuel = new Hotel();
        }

        // Mise à jour de l'objet (l'ID reste celui envoyé par setHotelContext)
        hotelActuel.setNom(txtNom.getText());
        hotelActuel.setVille(txtVille.getText());
        hotelActuel.setAdresse(txtAdresse.getText());
        hotelActuel.setCategorie(txtCategorie.getText());
        hotelActuel.setTelephone(txtTelephone.getText());
        hotelActuel.setEmail(txtEmail.getText());
        hotelActuel.setDescription(txtDescription.getText());
        hotelActuel.setStatut(comboStatut.getValue());
        hotelActuel.setImage(comboImages.getValue());

        // On utilise saveOrUpdate du DAO qui gère intelligemment INSERT ou UPDATE
        boolean succes = hotelDao.saveOrUpdate(hotelActuel);

        if (succes) {
            showAlert(AlertType.INFORMATION, "Succès", "Les informations de l'établissement ont été enregistrées.");
        } else {
            showAlert(AlertType.ERROR, "Erreur", "La sauvegarde a échoué dans la base de données.");
        }
    }

    private void showAlert(AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}