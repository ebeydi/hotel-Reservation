package com.hotel.controller;

import com.hotel.dao.RECEPTIONNISTEDAO; // Utilise ton DAO spécialisé
import com.hotel.model.Receptionniste;
import com.hotel.model.Users;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

public class ManageStaffController {

    @FXML private TableView<Receptionniste> staffTable;
    @FXML private TableColumn<Receptionniste, String> colNom;
    @FXML private TableColumn<Receptionniste, String> colPrenom;
    @FXML private TableColumn<Receptionniste, String> colEmail;

    private String hotelIdContext; 
    private RECEPTIONNISTEDAO dao = new RECEPTIONNISTEDAO();

    public void setHotelContext(String hotelId) {
        this.hotelIdContext = hotelId;
        refreshTable();
    }

    @FXML
    public void initialize() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    @FXML
    private void refreshTable() {
        if (hotelIdContext == null) return;
        
        // On récupère la liste via le DAO spécialisé
        ObservableList<Receptionniste> staffList = FXCollections.observableArrayList(
            dao.findRecepsByHotel(hotelIdContext) 
        );
        staffTable.setItems(staffList);
    }

    @FXML
    private void handleAddNewStaff() {
        openStaffDialog(null); // Mode Ajout
    }

    @FXML
    private void handleEditStaff() {
        Receptionniste selected = staffTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openStaffDialog(selected); // Mode Modification
        } else {
            showSimpleAlert("Sélection", "Veuillez sélectionner un réceptionniste à modifier.");
        }
    }

    @FXML
    private void handleDeleteStaff() {
        Receptionniste selected = staffTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Suppression");
            confirm.setHeaderText("Supprimer le compte de " + selected.getPrenom() + " " + selected.getNom() + " ?");
            confirm.setContentText("Cette action est irréversible.");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (dao.delete(selected.getId())) {
                    refreshTable();
                } else {
                    showSimpleAlert("Erreur", "Impossible de supprimer ce réceptionniste.");
                }
            }
        }
    }

    // --- MÉTHODE MOTEUR POUR LE DIALOGUE ---
    private void openStaffDialog(Receptionniste recep) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/hotel/add_staff_dialog.fxml"));
            Parent root = loader.load();

            AddStaffController dialogController = loader.getController();
            dialogController.setHotelId(hotelIdContext);
            
            // Si c'est une modif, on injecte l'objet existant
            if (recep != null) {
                dialogController.setRecepExistante(recep);
            }

            Stage stage = new Stage();
            stage.setTitle(recep == null ? "Ajouter un Réceptionniste" : "Modifier Réceptionniste");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            refreshTable();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showSimpleAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}