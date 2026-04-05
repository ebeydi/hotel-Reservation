package com.hotel.controller;

import com.hotel.dao.CHAMBREDAO;
import com.hotel.dao.TYPECHAMBREDAO;
import com.hotel.model.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ManageRoomController {

    @FXML private TextField txtNumero;
    @FXML private ComboBox<TypeChambre> comboType;
    @FXML private ComboBox<EtatChambre> comboEtat;
    @FXML private TableView<Chambre> roomTable;
    @FXML private TableColumn<Chambre, String> colNumero;
    @FXML private TableColumn<Chambre, TypeChambre> colType;
    @FXML private TableColumn<Chambre, EtatChambre> colEtat;

    private CHAMBREDAO chambreDao = new CHAMBREDAO();
    private TYPECHAMBREDAO typeChambreDao = new TYPECHAMBREDAO();

    @FXML
    public void initialize() {
        System.out.println("🔄 Initialisation du contrôleur des chambres...");

        // 1️⃣ Remplissage des ComboBox
        comboEtat.setItems(FXCollections.observableArrayList(EtatChambre.values()));
        loadTypeChambres();

        // 2️⃣ Configuration des colonnes
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colType.setCellValueFactory(new PropertyValueFactory<>("typeChambre"));
        colEtat.setCellValueFactory(new PropertyValueFactory<>("etat"));

        // 3️⃣ Fix resize policy
        roomTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // 4️⃣ Remplissage du tableau avec les chambres de l'hôtel connecté
        refreshTable();
    }

    private void loadTypeChambres() {
        try {
            var types = typeChambreDao.getAllTypes();
            comboType.setItems(FXCollections.observableArrayList(types));
        } catch (Exception e) {
            System.err.println("❌ Erreur chargement types : " + e.getMessage());
        }
    }

    private void refreshTable() {
        if (HotelSession.getHotel() == null) {
            System.err.println("❌ Aucun hôtel en session !");
            return;
        }
        try {
            roomTable.setItems(FXCollections.observableArrayList(
                    chambreDao.getAllChambres(HotelSession.getHotel().getId())
            ));
        } catch (Exception e) {
            System.err.println("❌ Erreur refresh table : " + e.getMessage());
        }
    }

    @FXML
    private void handleAddRoom() {
        try {
            System.out.println("--- CLIC DÉTECTÉ ---");

            // Validation simple
            if (txtNumero.getText().trim().isEmpty() || comboType.getValue() == null || comboEtat.getValue() == null) {
                showAlert("Champs vides", "Veuillez remplir tous les champs avant d'ajouter.");
                return;
            }

            Chambre c = new Chambre();
            c.setNumero(txtNumero.getText().trim());
            c.setTypeChambre(comboType.getValue());
            c.setEtat(comboEtat.getValue());

            System.out.println("💾 Tentative de sauvegarde de la chambre " + c.getNumero());

            if (chambreDao.save(c)) {
                System.out.println("✅ Chambre ajoutée avec succès !");
                txtNumero.clear();
                refreshTable();
            } else {
                System.err.println("❌ Échec de la sauvegarde en base de données.");
            }

        } catch (Exception e) {
            System.err.println("💥 CRASH DANS handleAddRoom :");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}