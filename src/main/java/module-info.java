module hotel {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;
    requires javafx.base; // Indispensable pour les TableView

    // Autorise JavaFX à injecter les composants @FXML dans tes contrôleurs
    opens com.hotel.controller to javafx.fxml; 
    
    // 🔥 AJOUTÉ : Autorise JavaFX (TableView) à lire les propriétés (nom, prenom, etc.) de tes modèles
    opens com.hotel.model to javafx.base, javafx.fxml;

    exports com.hotel;
    exports com.hotel.model;
    exports com.hotel.controller;
}