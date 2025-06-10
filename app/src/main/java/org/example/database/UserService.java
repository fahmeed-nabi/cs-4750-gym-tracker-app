package org.example.database;

import java.sql.*;
import org.example.models.Student;       
import java.util.List;                   
import java.util.ArrayList;              


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
    public List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        String query = "SELECT StudentID, FirstName, LastName, Email FROM Student ORDER BY LastName";

        try (PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("StudentID");
                String fullName = rs.getString("FirstName") + " " + rs.getString("LastName");
                String email = rs.getString("Email");
                students.add(new Student(id, fullName, email, "Student"));
        }
    }

    return students;
}
    public boolean deleteStudentById(int studentId) throws SQLException {
        String query = "DELETE FROM Student WHERE StudentID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            return stmt.executeUpdate() == 1;
    }
}
    public boolean deleteStudentByEmail(String email) throws SQLException {
        String query = "DELETE FROM Student WHERE Email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            return stmt.executeUpdate() == 1;
    }
}



}
