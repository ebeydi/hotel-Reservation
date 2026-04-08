package com.hotel.controller;

import com.hotel.dao.CHAMBREDAO;
import com.hotel.dao.HOTELDAO;
import com.hotel.dao.RESERVATIONDAO;
import com.hotel.model.*;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.util.Duration;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class HomeClientController {

    @FXML private VBox containerHotels, containerReservations;
    @FXML private VBox viewLocalities, viewHotels, viewReservations, sideMenu;
    @FXML private Label lblCustomerName;
    @FXML private ScrollPane mainScrollPane;

    private boolean menuVisible = false;
    private final HOTELDAO hotelDAO = new HOTELDAO();
    private final CHAMBREDAO chambreDAO = new CHAMBREDAO();
    private final RESERVATIONDAO reservationDAO = new RESERVATIONDAO();
    private Image defaultImage;

    @FXML
    public void initialize() {
        // Cacher le menu latéral au démarrage
        if (sideMenu != null) sideMenu.setTranslateX(-300);
        
        // S'assurer qu'on commence sur la vue des localités
        switchView(viewLocalities);

        // Chargement sécurisé de l'image par défaut
        try {
            var res = getClass().getResourceAsStream("/com/hotel/images/default.jpg");
            if (res != null) {
                defaultImage = new Image(res);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Image par défaut introuvable : " + e.getMessage());
        }
    }

    public void setUserInfo(String nom, String prenom) {
        String n = (nom != null) ? nom.toUpperCase() : "";
        String p = (prenom != null && !prenom.isEmpty())
                ? prenom.substring(0, 1).toUpperCase() + prenom.substring(1).toLowerCase()
                : "";
        if (lblCustomerName != null) {
            // Utilisation de Platform.runLater pour éviter les problèmes de thread
            Platform.runLater(() -> lblCustomerName.setText("BIENVENUE, " + p + " " + n));
        }
    }

    private void switchView(VBox viewToShow) {
        VBox[] views = {viewLocalities, viewHotels, viewReservations};
        for (VBox v : views) {
            if (v != null) {
                boolean isTarget = (v == viewToShow);
                v.setVisible(isTarget);
                v.setManaged(isTarget); 
            }
        }
        // Remonter le scroll en haut de page à chaque changement de vue
        if (mainScrollPane != null) mainScrollPane.setVvalue(0.0);
    }

    @FXML
    public void loadHotels(String ville) {
        if (containerHotels == null) return;
        containerHotels.getChildren().clear();
        
        // Récupération des hôtels via le DAO
        List<Hotel> hotels = hotelDAO.getHotelsByVille(ville);

        if (hotels == null || hotels.isEmpty()) {
            Label emptyLabel = new Label("Aucun établissement disponible à " + ville);
            emptyLabel.setStyle("-fx-font-size: 18; -fx-text-fill: #64748b; -fx-padding: 50;");
            containerHotels.getChildren().add(emptyLabel);
        } else {
            for (Hotel h : hotels) {
                containerHotels.getChildren().add(createHotelCard(h));
            }
        }
        switchView(viewHotels);
    }

    private VBox createHotelCard(Hotel h) {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-padding: 25; -fx-background-radius: 20; " +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);");

        HBox header = new HBox(20);
        header.setAlignment(Pos.TOP_LEFT);

        ImageView imgView = new ImageView();
        imgView.setFitWidth(180); 
        imgView.setFitHeight(120);
        imgView.setPreserveRatio(true); // Garder les proportions de l'image
        
        try {
            String imageName = (h.getImage() != null) ? h.getImage() : "default.jpg";
            var imgStream = getClass().getResourceAsStream("/com/hotel/images/" + imageName);
            if (imgStream != null) {
                imgView.setImage(new Image(imgStream));
            } else {
                imgView.setImage(defaultImage);
            }
        } catch (Exception e) { 
            imgView.setImage(defaultImage); 
        }

        VBox infoHotel = new VBox(8);
        Label nom = new Label(h.getNom().toUpperCase());
        nom.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        Label adresse = new Label("📍 " + h.getAdresse());
        adresse.setStyle("-fx-text-fill: #64748b;");
        
        infoHotel.getChildren().addAll(nom, adresse);
        header.getChildren().addAll(imgView, infoHotel);
        card.getChildren().addAll(header, new Separator());

        // Chargement des chambres de cet hôtel
        List<Chambre> chambres = chambreDAO.getAllChambres(h.getId());
        if (chambres != null && !chambres.isEmpty()) {
            for (Chambre c : chambres) {
                // On affiche uniquement les chambres DISPONIBLES
                if (c.getEtat() == EtatChambre.DISPONIBLE) {
                    card.getChildren().add(createRoomRow(c));
                }
            }
        } else {
            Label noRoom = new Label("Aucune chambre disponible actuellement.");
            noRoom.setStyle("-fx-text-fill: #94a3b8; -fx-font-style: italic;");
            card.getChildren().add(noRoom);
        }
        // Dans createHotelCard, juste avant le "if (chambres != null)"
System.out.println("Hôtel : " + h.getNom() + " | Nb chambres trouvées : " + (chambres != null ? chambres.size() : 0));

        return card;
    }

    private HBox createRoomRow(Chambre c) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #f8fafc; -fx-padding: 12; -fx-background-radius: 12; " +
                     "-fx-border-color: #e2e8f0; -fx-border-radius: 12;");

        Label type = new Label(c.getTypeChambre().getNomType());
        type.setPrefWidth(120);
        type.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");

        Label capacite = new Label("👤 " + c.getTypeChambre().getCapacite());
        capacite.setPrefWidth(50);
        capacite.setStyle("-fx-text-fill: #64748b;");

        Label prix = new Label(String.format("%.0f F / nuit", c.getTypeChambre().getTarifNuit()));
        prix.setPrefWidth(100);
        prix.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");

        // Configuration des DatePicker avec des dates cohérentes
        DatePicker d1 = new DatePicker(LocalDate.now());
        DatePicker d2 = new DatePicker(LocalDate.now().plusDays(1));
        d1.setPrefWidth(110); d2.setPrefWidth(110);
        
        // Empêcher de choisir des dates passées
        d1.setDayCellFactory(picker -> new DateCell() {
            @Override public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        Button btn = new Button("RÉSERVER");
        btn.setStyle("-fx-background-color: #0f172a; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 8;");
        btn.setOnAction(e -> handleReservation(c, d1.getValue(), d2.getValue()));

        row.getChildren().addAll(type, capacite, prix, new Label("Du"), d1, new Label("au"), d2, btn);
        return row;
    }

    private void handleReservation(Chambre c, LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner vos dates de séjour.");
            return;
        }
        long jours = ChronoUnit.DAYS.between(start, end);
        if (jours <= 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La date de départ doit être au moins 1 nuit après l'arrivée.");
            return;
        }

        double total = jours * c.getTypeChambre().getTarifNuit();
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de réservation");
        confirm.setHeaderText("Réserver la chambre " + c.getNumero());
        confirm.setContentText("Hôtel : " + c.getHotel().getNom() + 
                             "\nType : " + c.getTypeChambre().getNomType() +
                             "\nDurée : " + jours + " nuit(s)" +
                             "\nMontant total : " + String.format("%.0f FCFA", total));

        confirm.showAndWait().ifPresent(res -> {
            if (res == ButtonType.OK) saveReservation(c, start, end, total);
        });
    }

    private void saveReservation(Chambre c, LocalDate start, LocalDate end, double total) {
        Users currentUser = UserSession.getInstance();
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Session expirée", "Veuillez vous reconnecter.");
            return;
        }

        Reservation res = new Reservation();
        res.setId(UUID.randomUUID().toString().substring(0, 8));
        res.setNumReservation("RES-" + (System.currentTimeMillis() % 1000000));
        res.setClient(currentUser);
        res.setChambre(c);
        res.setDateArrive(Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        res.setDateDepart(Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        res.setNbPersonne(c.getTypeChambre().getCapacite()); 
        res.setMontantTotal((float) total);
        res.setStatut(StatutReservation.EN_ATTENTE); 

        if (reservationDAO.save(res)) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", 
                "Votre demande a été enregistrée. Statut actuel : EN ATTENTE.");
            // Rafraîchir la vue pour voir le changement d'état si nécessaire
            loadHotels(c.getHotel().getVille()); 
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'enregistrer la réservation.");
        }
    }

    @FXML
    public void showReservations() {
        if (containerReservations == null) return;
        containerReservations.getChildren().clear();
        
        Users current = UserSession.getInstance();
        if (current == null) return;

        List<Reservation> mesRes = reservationDAO.findByClient(current.getId());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        if (mesRes == null || mesRes.isEmpty()) {
            Label noRes = new Label("Vous n'avez aucune réservation pour le moment.");
            noRes.setStyle("-fx-padding: 30; -fx-text-fill: #94a3b8; -fx-font-size: 16;");
            containerReservations.getChildren().add(noRes);
        } else {
            for (Reservation r : mesRes) {
                VBox card = new VBox(10);
                card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 15; " +
                              "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5);");

                HBox top = new HBox();
                Label hName = new Label("🏨 " + r.getChambre().getHotel().getNom());
                hName.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: #0f172a;");
                
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                
                Label statusBadge = new Label(r.getStatut().toString());
                statusBadge.setStyle("-fx-padding: 5 12; -fx-background-radius: 20; -fx-font-size: 11; " +
                                     "-fx-font-weight: bold; -fx-text-fill: white;");
                
                // Gestion dynamique de la couleur du badge de statut
                if(r.getStatut() == StatutReservation.CONFIRMEE) 
                    statusBadge.setStyle(statusBadge.getStyle() + "-fx-background-color: #16a34a;");
                else if(r.getStatut() == StatutReservation.EN_ATTENTE) 
                    statusBadge.setStyle(statusBadge.getStyle() + "-fx-background-color: #f59e0b;");
                else 
                    statusBadge.setStyle(statusBadge.getStyle() + "-fx-background-color: #ef4444;");

                top.getChildren().addAll(hName, spacer, statusBadge);

                Label details = new Label("🛏️ " + r.getChambre().getTypeChambre().getNomType() + 
                                        " (Chambre " + r.getChambre().getNumero() + ")");
                details.setStyle("-fx-text-fill: #64748b;");

                Label dates = new Label("📅 Du " + sdf.format(r.getDateArrive()) + " au " + sdf.format(r.getDateDepart()));
                Label totalLbl = new Label("Total : " + String.format("%.0f FCFA", r.getMontantTotal()));
                totalLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b;");

                card.getChildren().addAll(top, details, dates, totalLbl);
                containerReservations.getChildren().add(card);
            }
        }
        switchView(viewReservations);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // --- Navigation Villes ---
    @FXML private void clickSaly() { loadHotels("Saly Portudal"); }
    @FXML private void clickSomone() { loadHotels("La Somone"); }
    @FXML private void clickNgaparou() { loadHotels("Ngaparou"); }
    @FXML private void clickPalmarin() { loadHotels("Palmarin"); }
    @FXML private void clickPopenguine() { loadHotels("Popenguine"); }
    @FXML private void clickToubab() { loadHotels("Toubab Dialaw"); }
    @FXML private void clickNianing() { loadHotels("Nianing"); }
    @FXML private void clickMbodjene() { loadHotels("Mbodjène"); }
    @FXML private void clickWarang() { loadHotels("Warang"); }
    @FXML private void clickJoal() { loadHotels("Joal Fadiouth"); }

    @FXML private void showLocalities() { switchView(viewLocalities); }

    @FXML 
    private void handleLogout() {
        try {
            UserSession.clean();
            Stage stage = (Stage) sideMenu.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/com/hotel/auth.fxml"));
            stage.setScene(new Scene(root));
        } catch (Exception e) { 
            System.err.println("Erreur de déconnexion : " + e.getMessage());
        }
    }

    @FXML
    private void toggleMenu() {
        if (sideMenu == null) return;
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), sideMenu);
        tt.setToX(menuVisible ? -300 : 0);
        menuVisible = !menuVisible;
        tt.play();
    }
}