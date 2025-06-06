package org.example.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FacilityService {
    private final Connection connection;

    public FacilityService(Connection connection) {
        this.connection = connection;
    }

    public boolean canUseFacility(int facilityId) throws SQLException {
        String currentUsageQuery = """
            SELECT COUNT(*) AS Current
            FROM FacilityUsage
            WHERE FacilityID = ?
              AND Timestamp >= NOW() - INTERVAL 1 HOUR
        """;

        String maxQuery = "SELECT MaxConcurrentUsers FROM Facility WHERE FacilityID = ?";

        int current = 0, max = 0;

        try (PreparedStatement stmt = connection.prepareStatement(currentUsageQuery)) {
            stmt.setInt(1, facilityId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) current = rs.getInt("Current");
            }
        }

        try (PreparedStatement stmt = connection.prepareStatement(maxQuery)) {
            stmt.setInt(1, facilityId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) max = rs.getInt("MaxConcurrentUsers");
            }
        }

        return current < max;
    }

    public boolean recordFacilityUsage(int facilityId, int studentId) throws SQLException {
        if (!canUseFacility(facilityId)) return false;

        String insert = """
            INSERT INTO FacilityUsage (FacilityID, StudentID, Timestamp)
            VALUES (?, ?, CURRENT_TIMESTAMP)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(insert)) {
            stmt.setInt(1, facilityId);
            stmt.setInt(2, studentId);
            return stmt.executeUpdate() == 1;
        }
    }

    public boolean createFacility(String name, int maxConcurrentUsers, int gymId) throws SQLException {
        String insert = """
        INSERT INTO Facility (Name, MaxConcurrentUsers, GymID)
        VALUES (?, ?, ?)
    """;

        try (PreparedStatement stmt = connection.prepareStatement(insert)) {
            stmt.setString(1, name);
            stmt.setInt(2, maxConcurrentUsers);
            stmt.setInt(3, gymId);
            return stmt.executeUpdate() == 1;
        }
    }

    public boolean deleteFacility(int facilityId) throws SQLException {
        String delete = "DELETE FROM Facility WHERE FacilityID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(delete)) {
            stmt.setInt(1, facilityId);
            return stmt.executeUpdate() == 1;
        }
    }

    public boolean updateFacility(int facilityId, String newName, int newMaxConcurrentUsers) throws SQLException {
        String update = """
        UPDATE Facility
        SET Name = ?, MaxConcurrentUsers = ?
        WHERE FacilityID = ?
    """;
        try (PreparedStatement stmt = connection.prepareStatement(update)) {
            stmt.setString(1, newName);
            stmt.setInt(2, newMaxConcurrentUsers);
            stmt.setInt(3, facilityId);
            return stmt.executeUpdate() == 1;
        }
    }

    public Integer getFacilityIdByNameAndGym(String name, int gymId) throws SQLException {
        String query = "SELECT FacilityID FROM Facility WHERE Name = ? AND GymID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setInt(2, gymId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("FacilityID");
                }
            }
        }
        return null; // Not found
    }

}
