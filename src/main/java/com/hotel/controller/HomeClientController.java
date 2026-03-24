package com.hotel.controller;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class HomeClientController {

    @FXML private VBox sideMenu, viewLocalities, viewHotels, viewReservations, containerHotels, containerReservations;
    @FXML private Label lblCustomerName;
    @FXML private Label lblSlogan; // Assure-toi que cet ID existe dans ton FXML

    private static final List<ReservationData> listRes = new ArrayList<>();
    private final Map<String, List<HotelInfo>> atlas = new HashMap<>();
    private boolean menuVisible = false;

    @FXML
    public void initialize() {
        initAtlas();

        // Appliquer les couleurs authentiques (Soleil de la Petite Côte)
        if (lblCustomerName != null) {
            lblCustomerName.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 36; -fx-font-weight: bold;");
        }
        if (lblSlogan != null) {
            lblSlogan.setStyle("-fx-text-fill: #FFA500; -fx-font-size: 25;");
        }

        if (sideMenu != null) {
            sideMenu.setTranslateX(-300);
        }
        showLocalities();
    }

    public void setUserInfo(String nom, String prenom) {
        if (lblCustomerName == null) return;

        String n = (nom != null) ? nom.trim().toUpperCase() : "";
        String p = "";
        if (prenom != null && !prenom.trim().isEmpty()) {
            p = prenom.substring(0, 1).toUpperCase() + prenom.substring(1).toLowerCase();
        }

        lblCustomerName.setText("BIENVENUE, " + p + " " + n);

        // Lancer l'animation d'accueil
        animateWelcome();
    }

    private void animateWelcome() {
        if (lblCustomerName == null) return;

        FadeTransition fade = new FadeTransition(Duration.millis(1000), lblCustomerName);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);

        TranslateTransition translate = new TranslateTransition(Duration.millis(800), lblCustomerName);
        translate.setFromY(20);
        translate.setToY(0);

        ParallelTransition parallel = new ParallelTransition(fade, translate);
        parallel.play();
    }

    private void initAtlas() {
        atlas.put("Saly Portudal", Arrays.asList(
                new HotelInfo("THE LAMANTIN BEACH HOTEL", "lamantin.jpg", "Hôtel 5 étoiles avec spa et marina.", 5),
                new HotelInfo("ROYAM HOTEL", "royam.jpg", "Bungalows de charme en jardin tropical.", 4),
                new HotelInfo("PALM BEACH RESORT", "palm.jpg", "Un complexe idéal pour les familles.", 4)
        ));
        atlas.put("La Somone", Arrays.asList(
                new HotelInfo("ROYAL HORIZON BAOBAB", "royal.jpg", "Entre la lagune et l'océan.", 4),
                new HotelInfo("PHENIX", "phenix.jpg", "Petit hôtel calme avec une vue imprenable sur l'océan.", 3),
                new HotelInfo("AFRICA QUEEN", "africa.jpg", "Hôtel convivial en bordure de plage.", 3)
        ));
        atlas.put("Ngaparou", Arrays.asList(
                new HotelInfo("BLUE AFRICA", "blue.jpg", "Un cadre authentique avec des pieds dans l'eau.", 3),
                new HotelInfo("KEUR SALOUM", "keur.jpg", "Un havre de paix haut de gamme.", 4)
        ));
        atlas.put("Palmarin", Arrays.asList(
                new HotelInfo("PALMARIN ECOLODGE", "ecolodge.jpg", "Entre mer et puits de sel, 100% solaire.", 4),
                new HotelInfo("LE LODGE DES COLLINES", "lodge.jpg", "Éco-lodge niché dans les collines.", 3),
                new HotelInfo("DJIDIACK", "djidiack.jpg", "Campement de charme en bord de mer.", 4),
                new HotelInfo("LES COLLINES DE NIASSAM", "niassam.jpg", "Expérience unique dans le Sine-Saloum.", 3)
        ));
        atlas.put("Nianing", Arrays.asList(
                new HotelInfo("DOMAINE DE NIANING", "domaine.jpg", "Ancien club mythique dans une forêt de baobabs.", 3),
                new HotelInfo("BENTENIER", "bentenier.jpg", "Hôtel de charme calme et fleuri en bord de mer.", 3)
        ));
        atlas.put("Mbodjène", Arrays.asList(
                new HotelInfo("LA PARENTHESE", "parenthese.jpg", "Écolodge entre lagune et océan, calme absolu.", 3),
                new HotelInfo("L'OASIS DE MBODJENE", "oasis.jpg", "Un havre de paix pour les amoureux de la nature.", 2)
        ));
        atlas.put("Warang", Arrays.asList(
                new HotelInfo("LES CALANQUES", "calanques.jpg", "Hôtel convivial avec une magnifique piscine.", 3),
                new HotelInfo("HÔTEL DE LA PLAGE", "plage.jpg", "Accès direct à une plage sauvage et paisible.", 3)
        ));
        atlas.put("Joal", Arrays.asList(
                new HotelInfo("LE FINIO", "finio.jpg", "Situé face à l'île aux Coquillages (Fadiouth).", 3),
                new HotelInfo("RELAIS DE JOAL", "relais.jpg", "Hôtel historique au bord du bras de mer.", 3)
        ));
        atlas.put("Popenguine", Arrays.asList(
                new HotelInfo("L'ÉCHO CÔTIER", "cotier.jpg", "Vue imprenable et cuisine raffinée.", 4),
                new HotelInfo("HÔTEL DE LA RÉSIDENCE", "residence.jpg", "Calme et sérénité au village.", 3)
        ));
        atlas.put("Toubab Dialao", Arrays.asList(
                new HotelInfo("SOBO BADE", "soba.jpg", "Espace artistique sculpté en falaise.", 3),
                new HotelInfo("IRIS", "iris.jpg", "Un havre de paix moderne avec une piscine magnifique.", 3),
                new HotelInfo("L'ÉCHO DES VAGUES", "echo.jpg", "Vue panoramique sur les falaises.", 3)
        ));
    }

    @FXML private void clickSaly(MouseEvent e) { loadHotels("Saly Portudal"); }
    @FXML private void clickSomone(MouseEvent e) { loadHotels("La Somone"); }
    @FXML private void clickNgaparou(MouseEvent e) { loadHotels("Ngaparou"); }
    @FXML private void clickPalmarin(MouseEvent e) { loadHotels("Palmarin"); }
    @FXML private void clickPopenguine(MouseEvent e) { loadHotels("Popenguine"); }
    @FXML private void clickToubab(MouseEvent e) { loadHotels("Toubab Dialao"); }
    @FXML private void clickNianing(MouseEvent e) { loadHotels("Nianing"); }
    @FXML private void clickMbodjene(MouseEvent e) { loadHotels("Mbodjène"); }
    @FXML private void clickWarang(MouseEvent e) { loadHotels("Warang"); }
    @FXML private void clickJoal(MouseEvent e) { loadHotels("Joal"); }

    private void loadHotels(String city) {
        containerHotels.getChildren().clear();
        List<HotelInfo> hotels = atlas.get(city);

        if (hotels == null || hotels.isEmpty()) {
            Label msg = new Label("Aucun établissement disponible actuellement pour " + city);
            msg.setStyle("-fx-text-fill: #64748b; -fx-font-size: 16; -fx-padding: 20;");
            containerHotels.getChildren().add(msg);
        } else {
            for (HotelInfo info : hotels) {
                containerHotels.getChildren().add(createHotelCard(info));
            }
        }
        switchView(viewHotels);
    }

    private VBox createHotelCard(HotelInfo h) {
        VBox card = new VBox(20);
        card.setStyle("-fx-background-color: white; -fx-padding: 25; -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);");

        HBox top = new HBox(25);
        top.setAlignment(Pos.CENTER_LEFT);

        ImageView iv = new ImageView();
        try {
            String path = "/com/hotel/images/" + h.img;
            Image img = new Image(getClass().getResourceAsStream(path));
            iv.setImage(img);
        } catch(Exception e) {
            System.err.println("Image introuvable : " + h.img);
        }
        iv.setFitWidth(280); iv.setFitHeight(180);
        iv.setPreserveRatio(false);

        VBox det = new VBox(8);
        Label n = new Label(h.nom);
        n.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        HBox stars = new HBox(2);
        for (int i = 0; i < 5; i++) {
            Label star = new Label(i < h.etoiles ? "★" : "☆");
            star.setStyle("-fx-font-size: 20; -fx-text-fill: " + (i < h.etoiles ? "#f1c40f" : "#cbd5e1") + ";");
            stars.getChildren().add(star);
        }

        Label d = new Label(h.desc);
        d.setWrapText(true); d.setMaxWidth(450);
        d.setStyle("-fx-text-fill: #475569; -fx-font-size: 14;");

        det.getChildren().addAll(n, stars, d);
        top.getChildren().addAll(iv, det);

        VBox rooms = new VBox(12);
        rooms.getChildren().addAll(
                createRoomRow(h.nom, "CHAMBRE ÉCO", "Ventilateur, Petit-déjeuner inclus", 25000, "#3b82f6"),
                createRoomRow(h.nom, "CHAMBRE STANDARD", "Climatisation, Vue sur Mer", 45000, "#10b981"),
                createRoomRow(h.nom, "SUITE LAGUNE", "Salon privé, Mini-bar, Jacuzzi", 85000, "#8b5cf6"),
                createRoomRow(h.nom, "BUNGALOW FAMILIAL", "2 Chambres, Cuisine équipée", 120000, "#f59e0b"),
                createRoomRow(h.nom, "SUITE PRÉSIDENTIELLE", "Vue panoramique, Service majordome", 250000, "#ef4444")
        );

        card.getChildren().addAll(top, new Separator(), rooms);
        return card;
    }

    private HBox createRoomRow(String hotel, String type, String desc, double prix, String color) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #f8fafc; -fx-padding: 15; -fx-background-radius: 12;");

        VBox info = new VBox(2);
        Label t = new Label(type); t.setStyle("-fx-font-weight: bold; -fx-text-fill: " + color + ";");
        Label d = new Label(desc); d.setStyle("-fx-font-size: 12; -fx-text-fill: #64748b;");
        info.getChildren().addAll(t, d);
        info.setPrefWidth(200);

        Label p = new Label(String.format("%,.0f F", prix));
        p.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        p.setPrefWidth(100);

        DatePicker d1 = new DatePicker(LocalDate.now());
        DatePicker d2 = new DatePicker(LocalDate.now().plusDays(1));
        d1.setPrefWidth(120); d2.setPrefWidth(120);

        Button btn = new Button("RÉSERVER");
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15;");

        btn.setOnAction(e -> {
            long days = ChronoUnit.DAYS.between(d1.getValue(), d2.getValue());
            if (days > 0) {
                double total = days * prix;
                listRes.add(new ReservationData(hotel, type, desc, d1.getValue(), d2.getValue(), total, color));
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Réservation enregistrée pour " + hotel + " !\nTotal : " + String.format("%,.0f", total) + " FCFA");
                alert.setTitle("Confirmation");
                alert.setHeaderText(null);
                alert.show();
            } else {
                new Alert(Alert.AlertType.ERROR, "La date de départ doit être après l'arrivée !").show();
            }
        });

        row.getChildren().addAll(info, p, d1, d2, btn);
        return row;
    }

    @FXML private void showReservations() {
        containerReservations.getChildren().clear();
        if (listRes.isEmpty()) {
            Label empty = new Label("Vous n'avez pas encore de réservations.");
            empty.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 16; -fx-padding: 20;");
            containerReservations.getChildren().add(empty);
        } else {
            for (ReservationData r : listRes) {
                VBox card = new VBox(10);
                card.setStyle("-fx-background-color: #C71585; -fx-padding: 20; -fx-background-radius: 12; -fx-border-color: " + r.color + "; -fx-border-width: 0 0 0 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 5);");
                Label h = new Label("🏨 " + r.h + " - " + r.t);
                h.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
                Label d = new Label("🗓 Du " + r.s + " au " + r.e);
                Label t = new Label("💰 TOTAL : " + String.format("%,.0f", r.tot) + " FCFA");
                t.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");
                card.getChildren().addAll(h, d, t);
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
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void showLocalities() { switchView(viewLocalities); }

    private void switchView(VBox v) {
        viewLocalities.setVisible(false); viewLocalities.setManaged(false);
        viewHotels.setVisible(false);     viewHotels.setManaged(false);
        viewReservations.setVisible(false); viewReservations.setManaged(false);
        v.setVisible(true); v.setManaged(true);
        if (menuVisible) toggleMenu();
    }

    @FXML private void toggleMenu() {
        if (sideMenu == null) return;
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), sideMenu);
        tt.setToX(menuVisible ? -300 : 0);
        menuVisible = !menuVisible;
        tt.play();
    }
}

class HotelInfo {
    String nom, img, desc; int etoiles;
    public HotelInfo(String n, String i, String d, int e) { nom=n; img=i; desc=d; etoiles=e; }
}

class ReservationData {
    String h, t, rd, color; LocalDate s, e; double tot;
    public ReservationData(String h, String t, String rd, LocalDate s, LocalDate e, double tot, String color) {
        this.h=h; this.t=t; this.rd=rd; this.s=s; this.e=e; this.tot=tot; this.color=color;
    }
}