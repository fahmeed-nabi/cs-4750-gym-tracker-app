package org.example.database;

import java.sql.*;

public class UserService {
    private final Connection connection;

    public UserService(Connection connection) {
        this.connection = connection;
    }

    public boolean createStudent(String firstName, String lastName, String email, String password) throws SQLException {
        String query = "INSERT INTO Student (FirstName, LastName, Email, Password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, password);
            return stmt.executeUpdate() == 1;
        }
    }

    public boolean createTrainer(String firstName, String lastName, String email, String password) throws SQLException {
        String query = "INSERT INTO Trainer (FirstName, LastName, Email, Password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, password);
            return stmt.executeUpdate() == 1;
        }
    }

    public boolean createAdmin(String firstName, String lastName, String email, String password) throws SQLException {
        String query = "INSERT INTO Admin (FirstName, LastName, Email, Password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, password);
            return stmt.executeUpdate() == 1;
        }
    }

    public boolean emailExists(String table, String email) throws SQLException {
        String query = "SELECT 1 FROM " + table + " WHERE Email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

}
