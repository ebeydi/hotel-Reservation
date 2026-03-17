package com.hotel.dao;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.hotel.database.*;

import com.hotel.model.Users;

public class USERDAO {

    public Users login(String login, String password) {

        Users user = null;

        String sql = "SELECT * FROM users WHERE login=? AND motDePasse=?";

        try {
            Connection conn = connexionDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, login);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                user = new Users();
                user.setId(rs.getString("id"));
                user.setLogin(rs.getString("login"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setEmail(rs.getString("email"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    public static boolean login(Users user) {
        
        throw new UnsupportedOperationException("Unimplemented method 'login'");
    }
}
