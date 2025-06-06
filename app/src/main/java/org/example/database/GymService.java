package org.example.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class GymService {
    private final Connection connection;

    public GymService(Connection connection) {
        this.connection = connection;
    }

    public int getCurrentOccupancy(int gymId) throws SQLException {
        String query = "SELECT COUNT(*) FROM CheckIn WHERE GymID = ? AND CheckOutTime IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, gymId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public int getCurrentOccupancyPct(int gymId) throws SQLException {
        int currentOccupancy = getCurrentOccupancy(gymId);
        int maxCapacity;
        String query = "SELECT MaxCapacity FROM Gym WHERE GymID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, gymId);
            try (ResultSet rs = stmt.executeQuery()) {
                maxCapacity = rs.next() ? rs.getInt(1) : -1;
            }
        }
        return currentOccupancy / maxCapacity;
    }

    public Map<String, Integer> getOccupancyForAllGyms() throws SQLException {
        Map<String, Integer> result = new HashMap<>();
        String query = """
            SELECT g.Name AS GymName, COUNT(*) AS Occupancy
            FROM CheckIn c
            INNER JOIN Gym g ON c.GymID = g.GymID
            WHERE CheckOutTime IS NULL
            GROUP BY g.Name
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getString("GymName"), rs.getInt("Occupancy"));
            }
        }

        return result;
    }

    public boolean canStudentCheckIn(int studentId, int gymId) throws SQLException {
        // 1. Is the student already checked in?
        String checkExisting = "SELECT 1 FROM CheckIn WHERE StudentID = ? AND CheckOutTime IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(checkExisting)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return false; // already checked in
            }
        }

        // 2. Is the gym at capacity?
        String occupancyQuery = """
        SELECT COUNT(*) AS Occupancy
        FROM CheckIn
        WHERE GymID = ? AND CheckOutTime IS NULL
    """;
        try (PreparedStatement stmt = connection.prepareStatement(occupancyQuery)) {
            stmt.setInt(1, gymId);
            ResultSet rs = stmt.executeQuery();
            int currentOccupancy = rs.next() ? rs.getInt("Occupancy") : 0;

            String capacityQuery = "SELECT MaxCapacity FROM Gym WHERE GymID = ?";
            try (PreparedStatement capStmt = connection.prepareStatement(capacityQuery)) {
                capStmt.setInt(1, gymId);
                ResultSet capRs = capStmt.executeQuery();
                if (capRs.next()) {
                    int max = capRs.getInt("MaxCapacity");
                    return currentOccupancy < max;
                }
            }
        }

        return false; // gym not found or at capacity
    }

    public boolean checkInStudent(int studentId, int gymId) throws SQLException {
        if (!canStudentCheckIn(studentId, gymId)) return false;

        String insert = "INSERT INTO CheckIn (StudentID, GymID, CheckInTime) VALUES (?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement stmt = connection.prepareStatement(insert)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, gymId);
            return stmt.executeUpdate() == 1;
        }
    }

    public boolean checkOutStudent(int studentId) throws SQLException {
        String update = """
        UPDATE CheckIn
        SET CheckOutTime = CURRENT_TIMESTAMP
        WHERE StudentID = ? AND CheckOutTime IS NULL
    """;
        try (PreparedStatement stmt = connection.prepareStatement(update)) {
            stmt.setInt(1, studentId);
            return stmt.executeUpdate() == 1;
        }
    }

}
