package dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import model.User;
import model.Student;   
import model.Librarian;


public class UserDAO {

    public static boolean registerUser(String name, String email, String password, String role) {
        String sql = "INSERT INTO users(name, email, password, role) VALUES(?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, role);
            stmt.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

   public static User login(String email, String password) {
    String sql = "SELECT * FROM users WHERE email=? AND password=?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, email);
        stmt.setString(2, password);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            String role = rs.getString("role");
            User user;
            if (role.equalsIgnoreCase("student")) {
                user = new Student();
            } else {
                user = new Librarian();
            }
            user.setUserID(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setEmail(email);
            return user;
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}

    
}
