package com.hotel.controller;

import com.hotel.dao.RESERVATIONDAO;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import java.util.Map;

public class AdminDashboardController {

    @FXML private Label lblRevenue;
    @FXML private Label lblOccupation;
    @FXML private Label lblStaffCount;
    @FXML private AreaChart<String, Number> revenueChart;

    private RESERVATIONDAO reservationDao = new RESERVATIONDAO();

    /**
     * Cette méthode est appelée par HomeAdminController.loadView()
     */
    public void setHotelContext(String hotelId) {

        if (hotelId == null) return;
        
    
        // Récupération des données réelles via le DAO
        Map<String, Double> stats = reservationDao.getStatsByHotel(hotelId);

        // Mise à jour des labels
        lblRevenue.setText(String.format("%,.0f FCFA", stats.getOrDefault("CA", 0.0)));
        lblStaffCount.setText(String.valueOf(stats.getOrDefault("STAFF", 0.0).intValue()));
        
        // Calcul du taux d'occupation (Exemple: sur une base de 50 chambres)
        double totalChambres = 50.0; 
        double occup = (stats.getOrDefault("OCCUPATION", 0.0) / totalChambres) * 100;
        lblOccupation.setText(String.format("%.1f%%", occup));

        // Charger le graphique
        updateChart();
    }

    private void updateChart() {
        revenueChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenus");
        // Tu peux ajouter des données réelles ici plus tard
        series.getData().add(new XYChart.Data<>("Lun", 200000));
        series.getData().add(new XYChart.Data<>("Mar", 450000));
        series.getData().add(new XYChart.Data<>("Mer", 300000));
        revenueChart.getData().add(series);
    }
    
}