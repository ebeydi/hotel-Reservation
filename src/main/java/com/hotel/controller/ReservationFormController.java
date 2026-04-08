package com.hotel.controller;

import com.hotel.dao.CHAMBREDAO;
import com.hotel.dao.RESERVATIONDAO;
import com.hotel.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

public class ReservationFormController {

    @FXML private TextField txtNomClient, txtChambre, txtNbPersonnes, txtMontantTotal;
    @FXML private DatePicker dpArrivee, dpDepart;

    private HomeReceptionnisteController mainController;
    private Chambre chambreSelectionnee; 
    private Reservation reservationExistante; 
    
    private final RESERVATIONDAO reservationDao = new RESERVATIONDAO();
    private final CHAMBREDAO chambreDao = new CHAMBREDAO();

    @FXML
    public void initialize() {
        // 1. Désactiver les dates passées
        dpArrivee.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        // 2. Listeners pour recalcul automatique (Dates)
        dpArrivee.valueProperty().addListener((o, oldV, newV) -> {
            updateDepartDateConstraints();
            calculerPrix();
        });
        dpDepart.valueProperty().addListener((o, oldV, newV) -> calculerPrix());

        // 3. RECHERCHE DYNAMIQUE DU PRIX (Quand on tape le numéro de chambre)
        txtChambre.textProperty().addListener((o, oldV, newV) -> {
            if (newV != null && !newV.trim().isEmpty()) {
                // On cherche la chambre en BDD pour récupérer son TYPE et son TARIF
                Chambre c = chambreDao.findByNumero(newV.trim());
                if (c != null) {
                    this.chambreSelectionnee = c;
                    txtNbPersonnes.setPromptText("Max: " + c.getTypeChambre().getCapacite());
                } else {
                    this.chambreSelectionnee = null;
                }
                calculerPrix();
            }
        });

        // 4. Forcer la saisie numérique
        txtNbPersonnes.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtNbPersonnes.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    /**
     * Mode "Validation" : Appelé depuis le Dashboard
     */
    public void setReservationExistante(Reservation res) {
        this.reservationExistante = res;
        if (res != null) {
            this.chambreSelectionnee = res.getChambre();
            txtNomClient.setText(res.getClient().getNom() + " " + res.getClient().getPrenom());
            txtChambre.setText(res.getChambre().getNumero());
            txtNbPersonnes.setText(String.valueOf(res.getNbPersonne()));
            dpArrivee.setValue(convertToLocalDate(res.getDateArrive()));
            dpDepart.setValue(convertToLocalDate(res.getDateDepart()));
            
            // On bloque le nom pour ne pas changer le client
            txtNomClient.setEditable(false);
            calculerPrix(); 
        }
    }

    private void calculerPrix() {
        if (dpArrivee.getValue() != null && dpDepart.getValue() != null) {
            long nuits = ChronoUnit.DAYS.between(dpArrivee.getValue(), dpDepart.getValue());
            
            if (nuits > 0) {
                // On récupère le prix REEL du type de chambre
                if (chambreSelectionnee != null && chambreSelectionnee.getTypeChambre() != null) {
                    double prixNuit = chambreSelectionnee.getTypeChambre().getTarifNuit();
                    double total = nuits * prixNuit;
                    txtMontantTotal.setText(String.format("%.2f", total).replace(",", "."));
                } else {
                    // Si numéro de chambre invalide, on ne peut pas calculer
                    txtMontantTotal.setText("Chambre introuvable");
                }
            } else {
                txtMontantTotal.setText("0.00");
            }
        }
    }

  @FXML
private void handleEnregistrer() {
    System.out.println("DEBUG: reservationExistante est " + (reservationExistante == null ? "NULL" : "PRÉSENTE"));
    
    if (reservationExistante != null) {
        System.out.println("DEBUG: ID du client original: " + reservationExistante.getClient().getId());
    }
    // 1. Validation des champs (omise ici pour la clarté)

    try {
        // Récupération de la chambre
        chambreSelectionnee = chambreDao.findByNumero(txtChambre.getText().trim());
        if (chambreSelectionnee == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Chambre introuvable.");
            return;
        }

        boolean success;

        if (reservationExistante != null) {
            // --- CAS 1 : VALIDATION D'UNE DEMANDE CLIENT ---
            // On NE touche PAS à reservationExistante.getClient() !
            // On met juste à jour les informations de séjour
            reservationExistante.setChambre(chambreSelectionnee);
            reservationExistante.setDateArrive(convert(dpArrivee.getValue()));
            reservationExistante.setDateDepart(convert(dpDepart.getValue()));
            reservationExistante.setNbPersonne(Integer.parseInt(txtNbPersonnes.getText().trim()));
            reservationExistante.setMontantTotal(Float.parseFloat(txtMontantTotal.getText()));
            reservationExistante.setStatut(StatutReservation.CONFIRMEE); 

            // ON APPELLE UPDATE (Pour changer le statut en base sans recréer de ligne)
            success = reservationDao.update(reservationExistante);
            
        } else {
            // --- CAS 2 : CRÉATION MANUELLE PAR LE RÉCEPTIONNISTE ---
            Reservation res = new Reservation();
            res.setId(UUID.randomUUID().toString().substring(0, 8));
            res.setNumReservation("RES-" + (System.currentTimeMillis() % 100000));
            
            // Ici seulement on utilise UserSession (car c'est une résa "sur place")
            res.setClient(UserSession.getInstance()); 
            
            res.setChambre(chambreSelectionnee);
            res.setDateArrive(convert(dpArrivee.getValue()));
            res.setDateDepart(convert(dpDepart.getValue()));
            res.setNbPersonne(Integer.parseInt(txtNbPersonnes.getText().trim()));
            res.setMontantTotal(Float.parseFloat(txtMontantTotal.getText()));
            res.setStatut(StatutReservation.CONFIRMEE);

            // ON APPELLE SAVE (Pour faire un INSERT)
            success = reservationDao.save(res);
        }

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Opération réussie !");
            handleAnnuler();
        }

    } catch (Exception e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Erreur", "Vérifiez les données saisies.");
    }
}

    private void updateDepartDateConstraints() {
        if (dpArrivee.getValue() != null) {
            dpDepart.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(empty || date.isBefore(dpArrivee.getValue().plusDays(1)));
                }
            });
        }
    }

    private String getValidationErrorMessage() {
        if (txtChambre.getText().trim().isEmpty()) return "Numéro de chambre requis.";
        if (dpArrivee.getValue() == null || dpDepart.getValue() == null) return "Dates manquantes.";
        if (txtNbPersonnes.getText().trim().isEmpty()) return "Nombre de personnes requis.";
        return null;
    }

    private Date convert(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public void setMainController(HomeReceptionnisteController controller) {
        this.mainController = controller;
    }

    @FXML
    private void handleAnnuler() {
        if (mainController != null) mainController.handleAccueil(null);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }
}