package com.hotel.controller;

import com.hotel.dao.USERDAO;
import com.hotel.dao.HOTELDAO;
import com.hotel.model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class AuthController {

    @FXML private VBox vboxLogin, vboxRegister;
    @FXML private Label lblError;
    @FXML private TextField emailLogin, nomRegister, prenomRegister, emailRegister, adresseRegister, nationaliteRegister;
    @FXML private PasswordField passwordLogin, passRegister;

    private USERDAO userDAO = new USERDAO();
    private HOTELDAO hotelDAO = new HOTELDAO();

    /**
     * Gère la connexion de l'utilisateur (Client, Réceptionniste ou Admin)
     */
    @FXML
    private void handleLogin() {
        String email = emailLogin.getText().trim();
        String pass = passwordLogin.getText().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            showError("❌ Veuillez remplir tous les champs.");
            return;
        }

        try {
            // 1. Authentification via la base de données
            Users user = userDAO.login(email, pass);

            if (user != null) {
                System.out.println("✅ Connexion réussie pour : " + user.getNom());

                // --- CRUCIAL : Initialisation de la Session Utilisateur ---
                // C'est cette ligne qui permet au HomeClientController de ne plus être "null"
                UserSession.setInstance(user); 

                // 2. Initialisation de la session Hôtel pour le staff
                if (user.getRole() == UsersRole.ADMIN || user.getRole() == UsersRole.RECEPTIONNISTE) {
                    Hotel hotel = hotelDAO.getHotelByUserId(user.getId());
                    if (hotel != null) {
                        HotelSession.setHotel(hotel);
                        System.out.println("🏨 Session Hôtel activée : " + hotel.getNom());
                    } else {
                        System.err.println("⚠️ Warning : L'employé n'est lié à aucun hôtel en base.");
                    }
                }

                // 3. Redirection vers le tableau de bord approprié
                navigateToDashboard(user);

            } else {
                showError("❌ Email ou mot de passe incorrect.");
            }
        } catch (Exception e) {
            showError("❌ Erreur de connexion au serveur.");
            e.printStackTrace();
        }
    }

    /**
     * Gère l'inscription d'un nouveau client
     */
    @FXML
    private void handleRegister() {
        if (isAnyFieldEmpty()) {
            showError("❌ Veuillez remplir les champs obligatoires.");
            return;
        }

        // Création de l'objet utilisateur (rôle CLIENT par défaut)
        Users newUser = new Users(
                null,                           // id (généré par le DAO/DB)
                emailRegister.getText(),        // login
                passRegister.getText(),         // motDePasse
                nomRegister.getText(),          // nom
                prenomRegister.getText(),       // prenom
                null,                           // telephone
                adresseRegister.getText(),      // adresse
                emailRegister.getText(),        // email
                nationaliteRegister.getText(),  // nationalite
                UsersRole.CLIENT,               // role
                null                            // hotelId (un client n'est pas lié à un hôtel)
        );

        if (USERDAO.save(newUser)) {
            showLoginForm();
            lblError.setText("✅ Inscription réussie ! Connectez-vous.");
            lblError.setStyle("-fx-text-fill: #27ae60;");
        } else {
            showError("❌ Erreur lors de l'enregistrement.");
        }
    }

    /**
     * Change de scène selon le rôle de l'utilisateur
     */
    private void navigateToDashboard(Users user) {
        try {
            String fxmlFile;
            UsersRole role = user.getRole();

            // Sélection du fichier FXML selon le rôle
            switch (role) {
                case ADMIN: fxmlFile = "/com/hotel/home_admin.fxml"; break;
                case RECEPTIONNISTE: fxmlFile = "/com/hotel/home_receptionniste.fxml"; break;
                default: fxmlFile = "/com/hotel/home_client.fxml"; break;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Si c'est un client, on peut passer les infos au controller via sa méthode spécifique
            if (role == UsersRole.CLIENT) {
                Object controller = loader.getController();
                if (controller instanceof HomeClientController) {
                    ((HomeClientController) controller).setUserInfo(user.getNom(), user.getPrenom());
                }
            }

            // Changement de fenêtre
            Stage stage = (Stage) emailLogin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Teranga Booking - " + role);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("❌ Erreur de chargement de l'interface.");
        }
    }

    private void showError(String message) {
        lblError.setText(message);
        lblError.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
    }

    private boolean isAnyFieldEmpty() {
        return nomRegister.getText().isEmpty() || emailRegister.getText().isEmpty() || passRegister.getText().isEmpty();
    }

    @FXML private void showRegisterForm() { lblError.setText(""); toggleForms(false); }
    @FXML private void showLoginForm() { lblError.setText(""); toggleForms(true); }

    private void toggleForms(boolean showLogin) {
        vboxLogin.setVisible(showLogin);
        vboxLogin.setManaged(showLogin);
        vboxRegister.setVisible(!showLogin);
        vboxRegister.setManaged(!showLogin);
    }
}