package com.hotel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.hotel.dao.USERDAO;
import com.hotel.model.Users;
import com.hotel.model.UsersRole;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Initialisation de l'admin
            //setupAdminAccount();

            // 2. Vérification du chemin FXML (Attention à la minuscule/majuscule !)
            // Remplace "auth.fxml" par le nom EXACT de ton fichier dans ton dossier resources
            URL fxmlLocation = getClass().getResource("/com/hotel/auth.fxml");

            if (fxmlLocation == null) {
                System.err.println("❌ Erreur : Le fichier FXML est introuvable au chemin indiqué !");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Teranga Booking - Connexion");
            primaryStage.centerOnScreen();
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du lancement de l'application :");
            e.printStackTrace();
        }
    }

    private void setupAdminAccount() {
        USERDAO dao = new USERDAO();
        // On vérifie si l'admin existe déjà
        if (dao.login("admin@hotel.com", "admin123") == null) {
            System.out.println("ℹ️ Initialisation du compte Administrateur...");

            // Assure-toi que ton constructeur Users() sans paramètres existe
            Users admin = new Users();
            admin.setNom("SYSTEM");
            admin.setPrenom("Admin");
            admin.setEmail("admin@hotel.com");
            admin.setLogin("admin@hotel.com"); // Important si ton USERDAO utilise le login
            admin.setPassword("admin123");   // Vérifie si c'est setPassword ou setMotDePasse
            admin.setRole(UsersRole.ADMIN);
            admin.setTelephone("00000000");
            admin.setAdresse("Sénégal");
            admin.setNationalite("Sénégalaise");

            if (USERDAO.save(admin)) {
                System.out.println("✅ Succès : Compte admin créé.");
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}