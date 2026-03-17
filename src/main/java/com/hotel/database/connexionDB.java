package com.hotel.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connexionDB {
   private static final String URL = "jdbc:mysql://localhost:3306/hotel_db";
   private static final String USER = "root";
   private static final String PASSWORD = "";

   public connexionDB() {
   }

   public static Connection getConnection() {
      Connection var0 = null;

      try {
         var0 = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_db", "root", "");
         System.out.println("Connexion réussie à la base de données");
      } catch (SQLException var2) {
         System.out.println("Erreur de connexion");
         var2.printStackTrace();
      }

      return var0;
   }
      public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Connexion à la base réussie !");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur de connexion : " + e.getMessage());
        }
    }
}
