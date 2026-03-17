module hotel {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;

    // Autorise JavaFX à lire tes contrôleurs
    opens com.hotel.controller to javafx.fxml; 
    
    exports com.hotel;
}