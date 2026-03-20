package com.hotel.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.hotel.database.connexionDB;
import com.hotel.model.Client;
import com.hotel.model.UsersRole;

public class ClientDAO {

    private static final String FIND_BY_EMAIL_SQL =
            "SELECT * FROM users WHERE email = ? AND role = 'CLIENT'";

    public Client findByEmail(String email) throws SQLException {
        try (Connection conn = connexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_EMAIL_SQL)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String id = rs.getString("id");
                    String login = rs.getString("login");
                    String motDePasse = rs.getString("motDePasse");
                    String nom = rs.getString("nom");
                    String prenom = rs.getString("prenom");
                    String telephone = rs.getString("telephone");
                    String adresse = rs.getString("adresse");
                    String nationalite = rs.getString("nationalite");
                    String emailDb = rs.getString("email");
                    UsersRole role = UsersRole.valueOf(rs.getString("role").toUpperCase());

                    return new Client(
                            id,
                            login,
                            motDePasse,
                            nom,
                            prenom,
                            telephone,
                            adresse,
                            emailDb,
                            role,
                            nationalite
                    );
                }
            }
        }
        return null; // aucun client trouvé
    }
}
