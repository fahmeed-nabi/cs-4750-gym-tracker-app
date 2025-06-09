package org.example.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.example.models.Instructor;

public class InstructorService {
    private final Connection connection;

    public InstructorService(Connection connection) {
        this.connection = connection;
    }

    public boolean addInstructor(String firstName, String lastName, String email,
                                 String certification, String focusArea) throws SQLException {

        // Check if instructor with the same email already exists
        String checkEmail = "SELECT 1 FROM Instructor WHERE Email = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkEmail)) {
            checkStmt.setString(1, email);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) return false; // duplicate found
            }
        }

        String insert = """
        INSERT INTO Instructor (FirstName, LastName, Email, Certification, FocusArea)
        VALUES (?, ?, ?, ?, ?)
    """;

        try (PreparedStatement stmt = connection.prepareStatement(insert)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, certification);
            stmt.setString(5, focusArea);

            if (stmt.executeUpdate() == 1) {
                connection.commit();
                return true;
            }
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }

        return false;
    }

    public boolean updateInstructor(int instructorId, String firstName, String lastName, String email,
                                    String certification, String focusArea) throws SQLException {
        String update = """
            UPDATE Instructor
            SET FirstName = ?, LastName = ?, Email = ?, Certification = ?, FocusArea = ?
            WHERE InstructorID = ?
        """;
        try (PreparedStatement stmt = connection.prepareStatement(update)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, certification);
            stmt.setString(5, focusArea);
            stmt.setInt(6, instructorId);
            if (stmt.executeUpdate() == 1) {
                connection.commit();
                return true;
            }
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
        return false;
    }

    public boolean deleteInstructor(int instructorId) throws SQLException {
        String delete = "DELETE FROM Instructor WHERE InstructorID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(delete)) {
            stmt.setInt(1, instructorId);
            if (stmt.executeUpdate() == 1) {
                connection.commit();
                return true;
            }
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
        return false;
    }

    public List<Instructor> getAllInstructors() throws SQLException {
        List<Instructor> instructors = new ArrayList<>();
        String query = "SELECT * FROM Instructor";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                instructors.add(new Instructor(
                        rs.getInt("InstructorID"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Email"),
                        rs.getString("Certification"),
                        rs.getString("FocusArea")
                ));
            }
        }
        return instructors;
    }

    public Instructor getInstructorById(int instructorId) throws SQLException {
        String query = "SELECT * FROM Instructor WHERE InstructorID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, instructorId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Instructor(
                            rs.getInt("InstructorID"),
                            rs.getString("FirstName"),
                            rs.getString("LastName"),
                            rs.getString("Email"),
                            rs.getString("Certification"),
                            rs.getString("FocusArea")
                    );
                }
            }
        }
        return null;
    }
}
