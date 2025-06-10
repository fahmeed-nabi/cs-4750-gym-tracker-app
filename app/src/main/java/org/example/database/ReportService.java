package org.example.database;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class ReportService {
    private final Connection connection;

    public ReportService(Connection connection) {
        this.connection = connection;
    }

    public int getTotalCheckInsThisWeek() throws SQLException {
        String query = """
            SELECT COUNT(*) FROM CheckIn
            WHERE CheckInTime >= CURRENT_DATE - INTERVAL 7 DAY
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public Map<String, Integer> getMostUsedFacilities(int limit) throws SQLException {
        Map<String, Integer> result = new LinkedHashMap<>();
        String query = """
            SELECT f.Name, COUNT(*) AS UsageCount
            FROM FacilityUsage fu
            JOIN Facility f ON fu.FacilityID = f.FacilityID
            GROUP BY f.Name
            ORDER BY UsageCount DESC
            LIMIT ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString("Name"), rs.getInt("UsageCount"));
                }
            }
        }

        return result;
    }

    public Map<String, Integer> getClassAttendanceCounts() throws SQLException {
        Map<String, Integer> result = new LinkedHashMap<>();
        String query = """
            SELECT c.Name, COUNT(*) AS Attendees
            FROM ClassAttendance ca
            JOIN Class c ON ca.ClassID = c.ClassID
            GROUP BY c.Name
            ORDER BY Attendees DESC
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getString("Name"), rs.getInt("Attendees"));
            }
        }

        return result;
    }

    public Map<String, Integer> getTrainerAppointmentCounts() throws SQLException {
        Map<String, Integer> result = new LinkedHashMap<>();
        String query = """
            SELECT CONCAT(t.FirstName, ' ', t.LastName) AS TrainerName, COUNT(*) AS Appointments
            FROM TrainerAppointment ta
            JOIN Trainer t ON ta.TrainerID = t.TrainerID
            GROUP BY TrainerName
            ORDER BY Appointments DESC
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getString("TrainerName"), rs.getInt("Appointments"));
            }
        }

        return result;
    }

    public Map<String, Integer> getCurrentGymOccupancyByName() throws SQLException {
        Map<String, Integer> result = new LinkedHashMap<>();
        String query = """
            SELECT g.Name, COUNT(*) AS Occupancy
            FROM CheckIn ci
            JOIN Gym g ON ci.GymID = g.GymID
            WHERE ci.CheckOutTime IS NULL
            GROUP BY g.Name
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getString("Name"), rs.getInt("Occupancy"));
            }
        }

        return result;
    }

    public Map<Integer, Integer> getTotalRegistrationsPerClass() throws SQLException {
        Map<Integer, Integer> result = new HashMap<>();
        String query = "SELECT ClassID, COUNT(*) AS TotalRegistrations FROM ClassAttendance GROUP BY ClassID";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getInt("ClassID"), rs.getInt("TotalRegistrations"));
            }
        }
        return result;
    }

    public double getAverageClassDuration() throws SQLException {
        String query = "SELECT AVG(TIMESTAMPDIFF(MINUTE, StartTime, EndTime)) AS AvgClassDuration FROM Class";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getDouble("AvgClassDuration") : 0;
        }
    }

    public Map<Integer, Integer> getAppointmentCounts() throws SQLException {
        Map<Integer, Integer> result = new HashMap<>();
        String query = "SELECT TrainerID, COUNT(*) AS AppointmentCount FROM TrainerAppointment GROUP BY TrainerID";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getInt("TrainerID"), rs.getInt("AppointmentCount"));
            }
        }
        return result;
    }

    public Map<Integer, Map<String, Object>> getAvailabilitySummary() throws SQLException {
        Map<Integer, Map<String, Object>> summary = new HashMap<>();
        String query = """
        SELECT TrainerID, COUNT(*) AS TotalSlots,
               MIN(StartTime) AS EarliestSlot,
               MAX(EndTime) AS LatestSlot
        FROM TrainerAvailability
        GROUP BY TrainerID
    """;

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> info = new HashMap<>();
                info.put("TotalSlots", rs.getInt("TotalSlots"));
                info.put("EarliestSlot", rs.getTime("EarliestSlot"));
                info.put("LatestSlot", rs.getTime("LatestSlot"));
                summary.put(rs.getInt("TrainerID"), info);
            }
        }
        return summary;
    }

    public Map<Integer, Integer> getPeakFacilityUsageTimes() throws SQLException {
        Map<Integer, Integer> result = new LinkedHashMap<>();
        String query = """
        SELECT HOUR(Timestamp) AS HourOfDay, COUNT(*) AS UsageCount
        FROM FacilityUsage
        GROUP BY HOUR(Timestamp)
        ORDER BY UsageCount DESC
    """;

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getInt("HourOfDay"), rs.getInt("UsageCount"));
            }
        }
        return result;
    }

    public Map<Integer, Integer> getCheckInCountsPerStudent() throws SQLException {
        Map<Integer, Integer> result = new HashMap<>();
        String query = "SELECT StudentID, COUNT(*) AS TotalCheckIns FROM CheckIn GROUP BY StudentID";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getInt("StudentID"), rs.getInt("TotalCheckIns"));
            }
        }
        return result;
    }

    public Map<String, String> getMostPopularFacilitiesWithGym(int limit) throws SQLException {
        Map<String, String> result = new LinkedHashMap<>();
        String query = """
        SELECT f.Name AS FacilityName, g.Name AS GymName, COUNT(*) AS UsageCount
        FROM FacilityUsage fu
        JOIN Facility f ON fu.FacilityID = f.FacilityID
        JOIN Gym g ON f.GymID = g.GymID
        GROUP BY f.FacilityID
        ORDER BY UsageCount DESC
        LIMIT ?
    """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString("FacilityName"), rs.getString("GymName"));
                }
            }
        }

        return result;
    }

    public Map<String, String> getCurrentFacilityOccupancyRate() throws SQLException {
        Map<String, String> result = new LinkedHashMap<>();
        String query = """
        SELECT f.Name AS FacilityName,
               g.Name AS GymName,
               f.MaxConcurrentUsers,
               COUNT(DISTINCT fu.StudentID) AS CurrentUsers
        FROM Facility f
        JOIN Gym g ON f.GymID = g.GymID
        LEFT JOIN FacilityUsage fu ON f.FacilityID = fu.FacilityID
        LEFT JOIN CheckIn ci ON fu.StudentID = ci.StudentID AND f.GymID = ci.GymID AND ci.CheckOutTime IS NULL
        GROUP BY f.FacilityID, f.Name, g.Name, f.MaxConcurrentUsers
    """;

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String facilityName = rs.getString("FacilityName");
                String gymName = rs.getString("GymName");
                int maxConcurrent = rs.getInt("MaxConcurrentUsers");
                int currentUsers = rs.getInt("CurrentUsers");

                double occupancyRate = maxConcurrent > 0
                        ? ((double) currentUsers / maxConcurrent) * 100
                        : 0;

                String key = facilityName + " (" + gymName + ")";
                String value = currentUsers + " users (" + String.format("%.0f", occupancyRate) + "% occupied)";
                result.put(key, value);
            }
        }

        return result;
    }

}
