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

    public String getFullName(String table, String email) throws SQLException {
        String query = "SELECT FirstName, LastName FROM " + table + " WHERE Email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("FirstName") + " " + rs.getString("LastName");
                }
            }
        }
        return null; // not found
    }

    public Integer getUserId(String table, String email) throws SQLException {
        String idColumn;

        // Determine the correct ID column based on table name
        switch (table) {
            case "Student" -> idColumn = "StudentID";
            case "Trainer" -> idColumn = "TrainerID";
            case "Admin"   -> idColumn = "AdminID";
            default -> throw new IllegalArgumentException("Invalid table name: " + table);
        }

        String query = "SELECT " + idColumn + " FROM " + table + " WHERE Email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(idColumn);
                }
            }
        }
        return null; // not found
    }
}
