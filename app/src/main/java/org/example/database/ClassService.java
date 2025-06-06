package org.example.database;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.example.models.ClassSession;

public class ClassService {
    private final Connection connection;

    public ClassService(Connection connection) {
        this.connection = connection;
    }

    public boolean isStudentAvailableForClass(int studentId, LocalDate date, LocalDateTime startTime) throws SQLException {
        String query = """
            SELECT 1
            FROM ClassAttendance ca
            JOIN Class c ON ca.ClassID = c.ClassID
            WHERE ca.StudentID = ?
              AND ca.Date = ?
              AND ? BETWEEN c.StartTime AND c.EndTime
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setTimestamp(3, Timestamp.valueOf(startTime));
            try (ResultSet rs = stmt.executeQuery()) {
                return !rs.next(); // true if no conflict
            }
        }
    }

    public boolean registerStudentForClass(int studentId, int classId, LocalDate date) throws SQLException {
        // 1. Fetch class start time
        String getStart = "SELECT StartTime FROM Class WHERE ClassID = ?";
        LocalDateTime startTime = null;
        try (PreparedStatement stmt = connection.prepareStatement(getStart)) {
            stmt.setInt(1, classId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    startTime = rs.getTimestamp("StartTime").toLocalDateTime();
                }
            }
        }

        if (startTime == null || !isStudentAvailableForClass(studentId, date, startTime)) {
            return false;
        }

        // 2. Register student
        String insert = "INSERT INTO ClassAttendance (ClassID, StudentID, Date) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insert)) {
            stmt.setInt(1, classId);
            stmt.setInt(2, studentId);
            stmt.setDate(3, Date.valueOf(date));
            return stmt.executeUpdate() == 1;
        }
    }

    public List<ClassSession> getStudentClassHistory(int studentId) throws SQLException {
        List<ClassSession> classes = new ArrayList<>();

        String query = """
            SELECT c.ClassID, c.Name, c.StartTime, c.EndTime, c.GymID, c.InstructorID
            FROM ClassAttendance ca
            JOIN Class c ON ca.ClassID = c.ClassID
            WHERE ca.StudentID = ?
            ORDER BY ca.Date DESC
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ClassSession cs = new ClassSession(
                            rs.getInt("ClassID"),
                            rs.getString("Name"),
                            rs.getTimestamp("StartTime").toLocalDateTime(),
                            rs.getTimestamp("EndTime").toLocalDateTime(),
                            rs.getInt("GymID"),
                            rs.getInt("InstructorID")
                    );
                    classes.add(cs);
                }
            }
        }

        return classes;
    }

    public boolean addClass(String name, LocalDateTime startTime, LocalDateTime endTime,
                            int gymId, int instructorId) throws SQLException {
        String insert = """
        INSERT INTO Class (Name, StartTime, EndTime, GymID, InstructorID)
        VALUES (?, ?, ?, ?, ?)
    """;
        try (PreparedStatement stmt = connection.prepareStatement(insert)) {
            stmt.setString(1, name);
            stmt.setTimestamp(2, Timestamp.valueOf(startTime));
            stmt.setTimestamp(3, Timestamp.valueOf(endTime));
            stmt.setInt(4, gymId);
            stmt.setInt(5, instructorId);
            return stmt.executeUpdate() == 1;
        }
    }

    public boolean deleteClass(int classId) throws SQLException {
        String delete = "DELETE FROM Class WHERE ClassID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(delete)) {
            stmt.setInt(1, classId);
            return stmt.executeUpdate() == 1;
        }
    }

    public boolean updateClass(int classId, String name, LocalDateTime startTime,
                               LocalDateTime endTime, int gymId, int instructorId) throws SQLException {
        String update = """
        UPDATE Class
        SET Name = ?, StartTime = ?, EndTime = ?, GymID = ?, InstructorID = ?
        WHERE ClassID = ?
    """;
        try (PreparedStatement stmt = connection.prepareStatement(update)) {
            stmt.setString(1, name);
            stmt.setTimestamp(2, Timestamp.valueOf(startTime));
            stmt.setTimestamp(3, Timestamp.valueOf(endTime));
            stmt.setInt(4, gymId);
            stmt.setInt(5, instructorId);
            stmt.setInt(6, classId);
            return stmt.executeUpdate() == 1;
        }
    }

}
