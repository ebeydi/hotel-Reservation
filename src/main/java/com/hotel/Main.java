package com.hotel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.hotel.dao.USERDAO;
import com.hotel.model.Users;
import com.hotel.model.UsersRole;

public class Main extends Application {

    @Override
    public void start(@SuppressWarnings("exports") Stage primaryStage) throws Exception {
        
        // --- ÉTAPE 1 : CRÉATION DE L'ADMIN AU DÉMARRAGE ---
        setupAdminAccount();

        // --- ÉTAPE 2 : CHARGEMENT DE L'INTERFACE ---
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/hotel/Auth.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("La Petite Côte - Connexion");
        primaryStage.show();
    }

    /**
     * Vérifie si l'admin existe, sinon le crée.
     */
    private void setupAdminAccount() {
        USERDAO dao = new USERDAO();
        
        // On tente de voir si l'admin par défaut peut se connecter
        // Note: Assure-toi que ton USERDAO a bien la méthode login(String, String)
        if (dao.login("admin@hotel.com", "admin123") == null) {
            System.out.println("ℹ️ Initialisation du compte Administrateur...");
            
            Users admin = new Users();
            admin.setNom("SYSTEM");
            admin.setPrenom("Admin");
            admin.setEmail("admin@hotel.com");
            admin.setPassword("admin123");
            admin.setRole(UsersRole.ADMIN); // Utilise l'Enum ADMIN
            admin.setTelephone("00000000");
            admin.setAdresse("Hotel");
            admin.setNationalite("Senegal");

            if (USERDAO.save(admin)) {
                System.out.println("✅ Succès : Connectez-vous avec admin@hotel.com / admin123");
            } else {
                System.err.println("❌ Échec de la création automatique de l'Admin.");
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}