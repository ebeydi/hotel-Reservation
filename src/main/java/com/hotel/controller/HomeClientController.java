package com.hotel.controller;

import com.hotel.dao.CHAMBREDAO;
import com.hotel.dao.HOTELDAO;
import com.hotel.model.Chambre;
import com.hotel.model.Hotel;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class HomeClientController {

    @FXML private VBox containerHotels;
    @FXML private VBox viewLocalities;
    @FXML private VBox viewHotels;
    @FXML private VBox viewReservations;
    @FXML private VBox sideMenu;
    @FXML private Label lblCustomerName;

    private boolean menuVisible = false;

    private HOTELDAO hotelDAO = new HOTELDAO();
    private CHAMBREDAO chambreDAO = new CHAMBREDAO();

    /** -------------------- INITIALISATION -------------------- **/
    @FXML
    public void initialize() {
        if (sideMenu != null) sideMenu.setTranslateX(-300);
    }

    /** -------------------- USER INFO -------------------- **/
    public void setUserInfo(String nom, String prenom) {
        if (lblCustomerName != null) {
            String n = (nom != null) ? nom.trim().toUpperCase() : "";
            String p = (prenom != null) ? prenom.substring(0,1).toUpperCase() + prenom.substring(1).toLowerCase() : "";
            lblCustomerName.setText("BIENVENUE, " + p + " " + n);
            animateWelcome();
        }
    }

    private void animateWelcome() {
        if (lblCustomerName == null) return;

        FadeTransition fade = new FadeTransition(Duration.millis(1000), lblCustomerName);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);

        TranslateTransition translate = new TranslateTransition(Duration.millis(800), lblCustomerName);
        translate.setFromY(20);
        translate.setToY(0);

        new ParallelTransition(fade, translate).play();
    }

    /** -------------------- AFFICHAGE HOTELS -------------------- **/
    public void loadHotels(String ville) {
        containerHotels.getChildren().clear();

        try {
            List<Hotel> hotels = hotelDAO.getHotelsByVille(ville);

            if (hotels.isEmpty()) {
                containerHotels.getChildren().add(new Label("Aucun hôtel disponible à " + ville));
                return;
            }

            for (Hotel h : hotels) {
                VBox hotelCard = createHotelCard(h);
                containerHotels.getChildren().add(hotelCard);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        switchView(viewHotels);
    }

    private VBox createHotelCard(Hotel h) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 15;");

        HBox top = new HBox(15);
        top.setStyle("-fx-alignment: center-left;");

        ImageView iv = new ImageView();
        try {
            String path = "/com/hotel/images/" + h.getImage();
            iv.setImage(new Image(getClass().getResourceAsStream(path)));
        } catch (Exception e) {
            System.err.println("Image introuvable : " + h.getImage());
        }
        iv.setFitWidth(200);
        iv.setFitHeight(150);

        VBox details = new VBox(5);
        Label lblNom = new Label(h.getNom());
        lblNom.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        Label lblDesc = new Label(h.getDescription());
        lblDesc.setWrapText(true);

        details.getChildren().addAll(lblNom, lblDesc);
        top.getChildren().addAll(iv, details);
        card.getChildren().add(top);
        card.getChildren().add(new Separator());

        // Liste des chambres
        List<Chambre> chambres = chambreDAO.getAllChambres(h.getId());
        for (Chambre c : chambres) {
            HBox row = new HBox(10);
            row.setStyle("-fx-alignment: center-left;");

            Label lblType = new Label(c.getTypeChambre().getNomType() + " - " + c.getEtat());
            Label lblPrix = new Label("Prix: " + c.getTypeChambre().getTarifNuit() + " FCFA");

            DatePicker dpDebut = new DatePicker(LocalDate.now());
            DatePicker dpFin = new DatePicker(LocalDate.now().plusDays(1));

            Button btnReserver = new Button("Réserver");
            btnReserver.setOnAction(ev -> {
                long jours = ChronoUnit.DAYS.between(dpDebut.getValue(), dpFin.getValue());
                if (jours > 0) {
                    double total = jours * c.getTypeChambre().getTarifNuit();
                    new Alert(Alert.AlertType.INFORMATION, "Réservation OK\nTotal: " + total + " FCFA").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Dates invalides").show();
                }
            });

            row.getChildren().addAll(lblType, lblPrix, dpDebut, dpFin, btnReserver);
            card.getChildren().add(row);
        }

        return card;
    }

    /** -------------------- SWITCH VUES -------------------- **/
    @FXML private void showLocalities() { switchView(viewLocalities); }
    @FXML private void showReservations() { switchView(viewReservations); }
    private void switchView(VBox v) {
        viewLocalities.setVisible(false);
        viewHotels.setVisible(false);
        viewReservations.setVisible(false);
        v.setVisible(true);
    }

    /** -------------------- MENU -------------------- **/
    @FXML private void toggleMenu() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), sideMenu);
        tt.setToX(menuVisible ? -300 : 0);
        menuVisible = !menuVisible;
        tt.play();
    }

    /** -------------------- CLIC VILLES -------------------- **/
    @FXML private void clickSaly() { loadHotels("Saly Portudal"); }
    @FXML private void clickSomone() { loadHotels("La Somone"); }
    @FXML private void clickNgaparou() { loadHotels("Ngaparou"); }
    @FXML private void clickPalmarin() { loadHotels("Palmarin"); }
    @FXML private void clickPopenguine() { loadHotels("Popenguine"); }
    @FXML private void clickToubab() { loadHotels("Toubab Dialao"); }
    @FXML private void clickNianing() { loadHotels("Nianing"); }
    @FXML private void clickMbodjene() { loadHotels("Mbodjène"); }
    @FXML private void clickWarang() { loadHotels("Warang"); }
    @FXML private void clickJoal() { loadHotels("Joal"); }
    @FXML
private void handleLogout() {
    try {
        // Recharge la page de login
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/hotel/auth.fxml"));
        javafx.scene.Parent root = loader.load();
        javafx.stage.Stage stage = (javafx.stage.Stage) sideMenu.getScene().getWindow();
        stage.setScene(new javafx.scene.Scene(root));
        stage.setTitle("Teranga Booking - Login");
        stage.centerOnScreen();
        stage.show();
    } catch (Exception e) {
        e.printStackTrace();
        new Alert(Alert.AlertType.ERROR, "Impossible de se déconnecter.").show();
    }
}
}