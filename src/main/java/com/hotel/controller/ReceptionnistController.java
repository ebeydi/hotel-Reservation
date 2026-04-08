package com.hotel.controller;

import com.hotel.dao.RESERVATIONDAO;
import com.hotel.model.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class ReceptionnistController {

    // --- UI COMPONENTS ---
    @FXML private TableView<Reservation> tableReservations;
    @FXML private TableColumn<Reservation, String> colNumeroReservation;
    @FXML private TableColumn<Reservation, String> colNomClient;
    @FXML private TableColumn<Reservation, Integer> colNbPersonnes;
    @FXML private TableColumn<Reservation, String> colHotel;
    @FXML private TableColumn<Reservation, String> colChambre;
    @FXML private TableColumn<Reservation, String> colDateArrivee;
    @FXML private TableColumn<Reservation, String> colDateDepart;
    @FXML private TableColumn<Reservation, String> colStatut;

    @FXML private Label lblNbReservationsJour, lblNbCheckIn, lblNbCheckOut, lblTotalPersonnes, lblHotelName;

    // --- LOGIC PROPERTIES ---
    private HomeReceptionnisteController mainController;
    private final RESERVATIONDAO reservationDAO = new RESERVATIONDAO();
    private String currentHotelId;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @FXML
    public void initialize() {
        // 1. Identification de l'hôtel de la session
        Users currentUser = UserSession.getInstance();
        if (currentUser != null) {
            this.currentHotelId = currentUser.getHotel_id();
            if (lblHotelName != null) lblHotelName.setText("Hôtel ID: " + currentHotelId);
        }

        // 2. Configuration graphique
        setupColumns();
        refreshTable();
        setupContextMenu();

        // 3. Événement : Double-clic pour valider/modifier
        tableReservations.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tableReservations.getSelectionModel().getSelectedItem() != null) {
                handleOuvrirValidation();
            }
        });
    }

    /**
     * Reçoit l'instance du contrôleur parent pour la navigation
     */
    public void setMainController(HomeReceptionnisteController mainController) {
        this.mainController = mainController;
    }

    /**
     * Action : Envoie la réservation sélectionnée au formulaire de validation
     */
    @FXML
    private void handleOuvrirValidation() {
        Reservation selected = tableReservations.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (mainController != null) {
                // On délègue l'ouverture au Home avec l'objet complet (incluant le client d'origine)
                mainController.handleModifierReservation(selected);
            } else {
                System.err.println("ERREUR : mainController non lié. Vérifiez handleAccueil() dans le Home.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Sélection", "Veuillez sélectionner une réservation dans le tableau.");
        }
    }

    private void setupColumns() {
        colNumeroReservation.setCellValueFactory(new PropertyValueFactory<>("numReservation"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Extraction des données imbriquées pour les colonnes
        colNbPersonnes.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getNbPersonne()).asObject());

        colDateArrivee.setCellValueFactory(cellData -> 
            new SimpleStringProperty(dateFormat.format(cellData.getValue().getDateArrive())));
        
        colDateDepart.setCellValueFactory(cellData -> 
            new SimpleStringProperty(dateFormat.format(cellData.getValue().getDateDepart())));

        colNomClient.setCellValueFactory(cellData -> {
            Users c = cellData.getValue().getClient();
            return new SimpleStringProperty(c != null ? c.getNom().toUpperCase() + " " + c.getPrenom() : "Client Inconnu");
        });

        colChambre.setCellValueFactory(cellData -> {
            Chambre ch = cellData.getValue().getChambre();
            return new SimpleStringProperty(ch != null ? ch.getNumero() : "Non assignée");
        });

        colHotel.setCellValueFactory(cellData -> {
            Chambre ch = cellData.getValue().getChambre();
            return new SimpleStringProperty(ch != null && ch.getHotel() != null ? ch.getHotel().getNom() : "Hôtel");
        });
    }

    @FXML
    public void refreshTable() {
        if (currentHotelId == null) return;
        
        // Chargement des données
        List<Reservation> list = reservationDAO.findByHotel(currentHotelId);
        tableReservations.setItems(FXCollections.observableArrayList(list));

        // Mise à jour des cartes statistiques
        Map<String, Double> stats = reservationDAO.getStatsByHotel(currentHotelId);
        lblNbReservationsJour.setText(String.valueOf(stats.getOrDefault("EN_ATTENTE", 0.0)));
        lblNbCheckIn.setText(String.valueOf(stats.getOrDefault("CONFIRMEE", 0.0)));
        lblNbCheckOut.setText(String.valueOf(stats.getOrDefault("TERMINEE", 0.0)));

        // Calcul du total des clients attendus
        int totalPers = list.stream()
                .filter(r -> r.getStatut() != StatutReservation.TERMINEE)
                .mapToInt(Reservation::getNbPersonne)
                .sum();
        
        if (lblTotalPersonnes != null) lblTotalPersonnes.setText(String.valueOf(totalPers));
    }

    @FXML
    private void handleCheckIn() {
        Reservation selected = tableReservations.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getStatut() == StatutReservation.EN_ATTENTE) {
            if (reservationDAO.checkIn(selected.getNumReservation())) {
                refreshTable();
                showAlert(Alert.AlertType.INFORMATION, "Check-In", "Arrivée enregistrée avec succès.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Action impossible", "Sélectionnez une réservation 'EN ATTENTE'.");
        }
    }

    @FXML
    private void handleCheckOut() {
        Reservation selected = tableReservations.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getStatut() == StatutReservation.CONFIRMEE) {
            if (reservationDAO.checkOut(selected.getNumReservation())) {
                refreshTable();
                showAlert(Alert.AlertType.INFORMATION, "Check-Out", "Départ validé et chambre libérée.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Action impossible", "Le client doit être 'CONFIRMÉE' pour sortir.");
        }
    }

    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        
        MenuItem modifierItem = new MenuItem("📝 Valider / Modifier la demande");
        MenuItem checkInItem = new MenuItem("🔑 Confirmer l'Arrivée (Check-In)");
        MenuItem checkOutItem = new MenuItem("🚪 Confirmer le Départ (Check-Out)");

        modifierItem.setOnAction(e -> handleOuvrirValidation());
        checkInItem.setOnAction(e -> handleCheckIn());
        checkOutItem.setOnAction(e -> handleCheckOut());

        contextMenu.getItems().addAll(modifierItem, new SeparatorMenuItem(), checkInItem, checkOutItem);
        tableReservations.setContextMenu(contextMenu);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
}