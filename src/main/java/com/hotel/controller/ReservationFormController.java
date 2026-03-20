package com.hotel.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

public class ReservationFormController {

    @FXML private TextField txtNomClient, txtChambre, txtNbPersonnes, txtMontantTotal;
    @FXML private DatePicker dpArrivee, dpDepart;

    private HomeReceptionnisteController mainController;

    public void setMainController(HomeReceptionnisteController controller) {
        this.mainController = controller;
    }

    @FXML
    private void handleEnregistrer(ActionEvent e) {
        System.out.println("✅ Réservation enregistrée");

        if (mainController != null) {
            mainController.handleAccueil(null);
        }
    }

    @FXML
    private void handleAnnuler(ActionEvent e) {
        if (mainController != null) {
            mainController.handleAccueil(null);
        }
    }
}