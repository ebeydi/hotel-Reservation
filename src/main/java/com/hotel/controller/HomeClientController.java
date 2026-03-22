package com.hotel.controller;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class HomeClientController {

    @FXML private VBox sideMenu, viewLocalities, viewHotels, viewReservations, containerHotels, containerReservations;
    @FXML private Label lblCustomerName;

    private static final List<ReservationData> listRes = new ArrayList<>();
    private final Map<String, List<HotelInfo>> atlas = new HashMap<>();
    private boolean menuVisible = false;

    @FXML
    public void initialize() {
        initAtlas();
        if (sideMenu != null) sideMenu.setTranslateX(-300);
        showLocalities();
    }

    public void setUserInfo(String nom, String prenom) {
        if (prenom == null || prenom.trim().isEmpty()) {
            lblCustomerName.setText("BIENVENUE, " + (nom != null ? nom.toUpperCase() : "CLIENT"));
        } else {
            String p = prenom.substring(0, 1).toUpperCase() + prenom.substring(1).toLowerCase();
            lblCustomerName.setText("BIENVENUE, " + p + " " + nom.toUpperCase());
        }
    }

    private void initAtlas() {
        atlas.put("Saly Portudal", Arrays.asList(
                new HotelInfo("THE LAMANTIN BEACH HOTEL", "lamantin.jpg", "Hôtel 5 étoiles de référence avec spa.", 5),
                new HotelInfo("ROYAM HOTEL", "royam.jpg", "Bungalows de charme nichés dans un jardin tropical.", 4),
                new HotelInfo("PALM BEACH RESORT", "palm.jpg", "Un complexe idéal pour les familles.", 4)
        ));

        atlas.put("La Somone", Arrays.asList(
                new HotelInfo("ROYAL HORIZON BAOBAB", "royal.jpg", "Situé entre la lagune et l'océan.", 4),
                new HotelInfo("AFRICA QUEEN", "africa.jpg", "Hôtel convivial en bordure de plage.", 3)
        ));

        atlas.put("Ngaparou", Arrays.asList(
                new HotelInfo("KEUR SALOUM", "keur.jpg", "Un havre de paix haut de gamme.", 4),
                new HotelInfo("VILLA DES PÊCHEURS", "villa.jpg", "Établissement intimiste vue mer.", 3)
        ));

        atlas.put("Palmarin", Arrays.asList(
                new HotelInfo("LES COLLINES DE NIASSAM", "niassam.jpg", "Éco-lodge de charme dans le Sine-Saloum.", 3),
                new HotelInfo("DJIDIACK", "djidiack.jpg", "Immersion totale dans la culture sérère.", 3)
        ));

        atlas.put("Popenguine", Arrays.asList(
                new HotelInfo("L'ÉCHO DES VAGUES", "echo.jpg", "Vue panoramique sur les falaises.", 3),
                new HotelInfo("LA PIERRE DE LISSE", "pierre.jpg", "Tourisme solidaire géré par les femmes.", 2)
        ));

        atlas.put("Toubab Dialao", Arrays.asList(
                new HotelInfo("SOBABADADE", "soba.jpg", "Espace de création artistique sculpté à flanc de falaise.", 3),
                new HotelInfo("IRIS HOTEL DIALAO", "iris.jpg", "Confort moderne surplombant la plage.", 4)
        ));
    }

    @FXML private void clickSaly() { loadHotels("Saly Portudal"); }
    @FXML private void clickSomone() { loadHotels("La Somone"); }
    @FXML private void clickNgaparou() { loadHotels("Ngaparou"); }
    @FXML private void clickPalmarin() { loadHotels("Palmarin"); }
    @FXML private void clickPopenguine() { loadHotels("Popenguine"); }
    @FXML private void clickToubab() { loadHotels("Toubab Dialao"); }

    private void loadHotels(String city) {
        containerHotels.getChildren().clear();
        for (HotelInfo h : atlas.getOrDefault(city, new ArrayList<>())) {
            containerHotels.getChildren().add(createHotelCard(h));
        }
        switchView(viewHotels);
    }

    private VBox createHotelCard(HotelInfo h) {
        VBox card = new VBox(20);
        card.setStyle("-fx-background-color: white; -fx-padding: 25; -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);");

        HBox top = new HBox(20);
        top.setAlignment(Pos.CENTER_LEFT);

        ImageView iv = new ImageView();
        try { iv.setImage(new Image(getClass().getResourceAsStream("/com/hotel/images/" + h.img))); } catch(Exception e) {}
        iv.setFitWidth(250); iv.setFitHeight(150);

        VBox det = new VBox(5);
        Label n = new Label(h.nom); n.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        // --- Ajout visuel des étoiles ---
        HBox starsBox = new HBox(2);
        for (int i = 0; i < 5; i++) {
            Label star = new Label(i < h.etoiles ? "★" : "☆");
            star.setStyle("-fx-font-size: 20; -fx-text-fill: " + (i < h.etoiles ? "#f1c40f" : "#cbd5e1") + ";");
            starsBox.getChildren().add(star);
        }

        Label d = new Label(h.desc); d.setWrapText(true); d.setMaxWidth(400); d.setStyle("-fx-text-fill: #475569; -fx-padding: 10 0 0 0;");
        det.getChildren().addAll(n, starsBox, d);
        top.getChildren().addAll(iv, det);

        VBox rooms = new VBox(12);
        rooms.getChildren().addAll(
                createRoomRow(h.nom, "Chambre Eco", "Ventilé, Wi-Fi", 25000, "#3b82f6"),
                createRoomRow(h.nom, "Chambre Standard", "Clim, Vue Jardin", 45000, "#10b981"),
                createRoomRow(h.nom, "Suite Junior", "Vue Mer, Salon", 85000, "#8b5cf6"),
                createRoomRow(h.nom, "Suite Prestige", "Jacuzzi Privé", 150000, "#f59e0b"),
                createRoomRow(h.nom, "Villa Royale", "Piscine & Majordome", 350000, "#b91c1c")
        );

        card.getChildren().addAll(top, new Separator(), rooms);
        return card;
    }

    private HBox createRoomRow(String hotel, String type, String desc, double prix, String color) {
        HBox row = new HBox(15); row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #f8fafc; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #e2e880;");

        VBox info = new VBox(2);
        Label t = new Label(type); t.setStyle("-fx-font-weight: bold; -fx-text-fill: " + color + ";");
        Label d = new Label(desc); d.setStyle("-fx-font-size: 11; -fx-text-fill: #64748b;");
        info.getChildren().addAll(t, d);
        info.setPrefWidth(160);

        Label p = new Label(prix + " F"); p.setStyle("-fx-font-weight: bold;");
        p.setPrefWidth(80);

        DatePicker d1 = new DatePicker(LocalDate.now());
        DatePicker d2 = new DatePicker(LocalDate.now().plusDays(1));
        d1.setPrefWidth(110); d2.setPrefWidth(110);

        Button btn = new Button("RÉSERVER");
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btn.setOnAction(e -> {
            long days = ChronoUnit.DAYS.between(d1.getValue(), d2.getValue());
            if (days > 0) {
                listRes.add(new ReservationData(hotel, type, desc, d1.getValue(), d2.getValue(), days * prix, color));
                new Alert(Alert.AlertType.INFORMATION, "Réservation enregistrée !").show();
            }
        });

        row.getChildren().addAll(info, p, d1, d2, btn);
        return row;
    }

    @FXML private void showReservations() {
        containerReservations.getChildren().clear();
        if (listRes.isEmpty()) {
            Label noRes = new Label("Aucune réservation effectuée.");
            noRes.setStyle("-fx-font-size: 16; -fx-text-fill: #64748b; -fx-padding: 20;");
            containerReservations.getChildren().add(noRes);
        } else {
            for (ReservationData r : listRes) {
                VBox card = new VBox(10);
                card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 12; " +
                        "-fx-border-left-width: 8; -fx-border-left-color: " + r.color + "; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

                Label lH = new Label("🏨 " + r.h.toUpperCase());
                lH.setStyle("-fx-font-weight: bold; -fx-font-size: 18; -fx-text-fill: #0f172a;");
                Label lT = new Label("🛏 Chambre : " + r.t + " (" + r.rd + ")");
                lT.setStyle("-fx-font-size: 14; -fx-text-fill: #475569;");
                Label lD = new Label("📅 Séjour : Du " + r.s + " au " + r.e);
                lD.setStyle("-fx-font-size: 14; -fx-font-weight: 500;");
                Label lP = new Label("💰 PRIX TOTAL : " + r.tot + " FCFA");
                lP.setStyle("-fx-font-weight: bold; -fx-text-fill: #15803d; -fx-font-size: 16;");

                card.getChildren().addAll(lH, new Separator(), lT, lD, lP);
                containerReservations.getChildren().add(card);
            }
        }
        switchView(viewReservations);
    }

    @FXML private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/hotel/auth.fxml"));
            Stage stage = (Stage) sideMenu.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur de déconnexion").show();
        }
    }

    @FXML private void showLocalities() { switchView(viewLocalities); }

    private void switchView(VBox v) {
        viewLocalities.setVisible(false); viewLocalities.setManaged(false);
        viewHotels.setVisible(false); viewHotels.setManaged(false);
        viewReservations.setVisible(false); viewReservations.setManaged(false);
        v.setVisible(true);
        v.setManaged(true);
        if (menuVisible) toggleMenu();
    }

    @FXML private void toggleMenu() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), sideMenu);
        tt.setToX(menuVisible ? -300 : 0);
        menuVisible = !menuVisible;
        tt.play();
    }
}

class HotelInfo {
    String nom, img, desc;
    int etoiles;
    public HotelInfo(String n, String i, String d, int e) { nom=n; img=i; desc=d; etoiles=e; }
}

class ReservationData {
    String h, t, rd, color; LocalDate s, e; double tot;
    public ReservationData(String h, String t, String rd, LocalDate s, LocalDate e, double tot, String color) {
        this.h=h; this.t=t; this.rd=rd; this.s=s; this.e=e; this.tot=tot; this.color=color;
    }
}