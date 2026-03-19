package com.hotel.controller;

import com.hotel.dao.USERDAO;
import com.hotel.model.Users;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class ManageStaffController {

    @FXML private TableView<Users> staffTable;
    @FXML private TableColumn<Users, String> colNom;
    @FXML private TableColumn<Users, String> colPrenom;
    @FXML private TableColumn<Users, String> colEmail;

    private USERDAO dao = new USERDAO();

    @FXML
    public void initialize() {
        // 1. On lie les colonnes aux attributs de la classe Users
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        // 2. On charge les données au démarrage
        refreshTable();
    }

    private void refreshTable() {
        // On récupère la liste via le DAO et on l'affiche
        ObservableList<Users> staffList = FXCollections.observableArrayList(dao.getAllReceptionnists());
        staffTable.setItems(staffList);
    }

    @FXML
    private void handleAddNewStaff() {
        try {
            // Charger la pop-up d'ajout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/hotel/add_staff_dialog.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter un Réceptionniste");
            stage.initModality(Modality.APPLICATION_MODAL); // Bloque la fenêtre principale tant qu'on n'a pas fini
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Attend la fermeture de la pop-up

            // Une fois la pop-up fermée, on rafraîchit le tableau
            refreshTable();

        } catch (IOException e) {
            System.err.println("❌ Erreur ouverture formulaire : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
