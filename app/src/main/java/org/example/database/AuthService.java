package org.example.database;

import java.sql.*;

public class AuthService {
    private final Connection connection;

    public AuthService(Connection connection) {
        this.connection = connection;
    }

    public boolean authenticate(String table, String email, String password) throws SQLException {
        String query = "SELECT 1 FROM " + table + " WHERE Email = ? AND Password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean authenticateStudent(String email, String password) throws SQLException {
        return authenticate("Student", email, password);
    }

    public boolean authenticateTrainer(String email, String password) throws SQLException {
        return authenticate("Trainer", email, password);
    }

    public boolean authenticateAdmin(String email, String password) throws SQLException {
        return authenticate("Admin", email, password);
    }
}

