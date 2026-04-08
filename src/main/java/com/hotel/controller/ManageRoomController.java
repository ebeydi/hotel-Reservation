package com.hotel.controller;

import com.hotel.dao.CHAMBREDAO;
import com.hotel.dao.TYPECHAMBREDAO;
import com.hotel.model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import java.util.UUID;
import java.util.List;

public class ManageRoomController {

    @FXML private TextField txtNumero;
    @FXML private ComboBox<TypeChambre> comboType;
    @FXML private ComboBox<EtatChambre> comboEtat;
    @FXML private TableView<Chambre> roomTable;
    @FXML private TableColumn<Chambre, String> colNumero, colType, colEtat;

    private final CHAMBREDAO chambreDao = new CHAMBREDAO();
    private final TYPECHAMBREDAO typeChambreDao = new TYPECHAMBREDAO();

    @FXML
    public void initialize() {
        if (HotelSession.getHotel() == null) {
            System.err.println("❌ Erreur : Aucune session hôtel active.");
            return;
        }

        setupTableColumns();
        setupTypeComboBoxConverter();
        
        comboEtat.setItems(FXCollections.observableArrayList(EtatChambre.values()));
        loadTypeChambres();
        refreshTable();

        roomTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) fillForm(newVal);
        });
    }

    private void setupTableColumns() {
        colNumero.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNumero()));
        
        // Affiche le type ET la capacité dans la table pour que le manager voit tout
        colType.setCellValueFactory(cellData -> {
            TypeChambre t = cellData.getValue().getTypeChambre();
            String info = (t != null) ? t.getNomType() + " (" + t.getCapacite() + " pers.)" : "N/A";
            return new SimpleStringProperty(info);
        });

        colEtat.setCellValueFactory(cellData -> {
            EtatChambre etat = cellData.getValue().getEtat();
            return new SimpleStringProperty(etat != null ? etat.toString() : "INCONNU");
        });
    }

    @FXML
    private void handleAddNewType() {
        Dialog<TypeChambre> dialog = new Dialog<>();
        dialog.setTitle("Paramétrage du Type de Chambre");
        
        ButtonType saveBtn = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtNom = new TextField(); 
        txtNom.setPromptText("Ex: Suite Royale");
        TextField txtPrix = new TextField();
        txtPrix.setPromptText("Prix en FCFA");
        
        // NOUVEAU : Champ Capacité (C'est ici que le chef décide !)
        Spinner<Integer> spinCapacite = new Spinner<>(1, 10, 2); 
        spinCapacite.setEditable(true);

        grid.add(new Label("Nom du type :"), 0, 0);
        grid.add(txtNom, 1, 0);
        grid.add(new Label("Prix par nuit :"), 0, 1);
        grid.add(txtPrix, 1, 1);
        grid.add(new Label("Capacité (Pers.) :"), 0, 2);
        grid.add(spinCapacite, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                try {
                    TypeChambre t = new TypeChambre();
                    t.setId(UUID.randomUUID().toString().substring(0, 8));
                    t.setNomType(txtNom.getText());
                    t.setTarifNuit(Double.parseDouble(txtPrix.getText()));
                    t.setCapacite(spinCapacite.getValue()); // On fixe la capacité ici
                    t.setHotel_id(HotelSession.getHotel().getId());
                    return t;
                } catch (Exception e) {
                    showAlert("Erreur", "Données invalides. Vérifiez le prix.", Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newType -> {
            if (typeChambreDao.save(newType)) {
                loadTypeChambres();
                showAlert("Succès", "Le type '" + newType.getNomType() + "' est configuré pour " + newType.getCapacite() + " personnes.", Alert.AlertType.INFORMATION);
            }
        });
    }

    // --- LE RESTE DU CODE (ADD, UPDATE, DELETE) RESTE IDENTIQUE ---
    
    @FXML
    private void refreshTable() {
        if (HotelSession.getHotel() != null) {
            List<Chambre> chambres = chambreDao.getAllChambres(HotelSession.getHotel().getId());
            roomTable.setItems(FXCollections.observableArrayList(chambres));
        }
    }

    @FXML
    private void handleAddRoom() {
        if (isInputInvalid()) return;
        Chambre c = new Chambre();
        c.setId(UUID.randomUUID().toString());
        c.setNumero(txtNumero.getText().trim());
        c.setTypeChambre(comboType.getValue());
        c.setEtat(comboEtat.getValue());
        c.setHotel(HotelSession.getHotel());
        if (chambreDao.save(c)) finishAction("Chambre ajoutée !");
    }

  @FXML
private void handleUpdateRoom() {
    Chambre selected = roomTable.getSelectionModel().getSelectedItem();
    
    // 1. Vérifier si une ligne est sélectionnée
    if (selected == null) {
        showAlert("Erreur", "Veuillez sélectionner une chambre dans le tableau.", Alert.AlertType.WARNING);
        return;
    }

    // 2. Vérifier si les champs du formulaire sont remplis
    if (txtNumero.getText().trim().isEmpty() || 
        comboType.getValue() == null || 
        comboEtat.getValue() == null) {
        showAlert("Erreur", "Tous les champs (Numéro, Type, État) sont obligatoires.", Alert.AlertType.ERROR);
        return;
    }

    // 3. Mettre à jour l'objet avec les valeurs du formulaire
    selected.setNumero(txtNumero.getText().trim());
    selected.setTypeChambre(comboType.getValue());
    selected.setEtat(comboEtat.getValue()); // C'est cette valeur qui manquait peut-être

    // 4. Envoyer au DAO
    if (chambreDao.update(selected)) {
        finishAction("Mise à jour réussie !");
    } else {
        showAlert("Erreur", "Échec de la mise à jour en base de données.", Alert.AlertType.ERROR);
    }
}

    @FXML
    private void handleDeleteRoom() {
        Chambre selected = roomTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (chambreDao.delete(selected.getId())) finishAction("Chambre supprimée.");
        }
    }

    private void loadTypeChambres() {
        List<TypeChambre> types = typeChambreDao.getAllTypesByHotel(HotelSession.getHotel().getId());
        comboType.setItems(FXCollections.observableArrayList(types));
    }

    private void setupTypeComboBoxConverter() {
        comboType.setConverter(new StringConverter<TypeChambre>() {
            @Override public String toString(TypeChambre t) { 
                return (t == null) ? "" : t.getNomType() + " (" + t.getCapacite() + " pers.)"; 
            }
            @Override public TypeChambre fromString(String s) { return null; }
        });
    }

    private void fillForm(Chambre c) {
        txtNumero.setText(c.getNumero());
        comboType.setValue(c.getTypeChambre());
        comboEtat.setValue(c.getEtat());
    }

    private void finishAction(String message) {
        showAlert("Succès", message, Alert.AlertType.INFORMATION);
        refreshTable();
        clearFields();
    }

    private boolean isInputInvalid() {
        return txtNumero.getText().isEmpty() || comboType.getValue() == null || comboEtat.getValue() == null;
    }

    private void clearFields() {
        txtNumero.clear();
        comboType.getSelectionModel().clearSelection();
        comboEtat.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert a = new Alert(type); a.setTitle(title); a.setHeaderText(null); a.setContentText(content); a.show();
    }
}