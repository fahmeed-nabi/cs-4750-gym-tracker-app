package org.example.database;

import org.example.models.Facility;
import org.example.models.Gym;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GymService {
    private final Connection connection;

    public GymService(Connection connection) {
        this.connection = connection;
    }

    public List<Gym> getAllGyms() throws SQLException {
        List<Gym> gyms = new ArrayList<>();
        String query = "SELECT GymID, Name, MaxCapacity FROM Gym ORDER BY Name";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                gyms.add(new Gym(
                        rs.getInt("GymID"),
                        rs.getString("Name"),
                        rs.getInt("MaxCapacity")
                ));
            }
        }

        return gyms;
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

    public double getCurrentOccupancyPct(int gymId) throws SQLException {
        int currentOccupancy = getCurrentOccupancy(gymId);
        int maxCapacity;
        String query = "SELECT MaxCapacity FROM Gym WHERE GymID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, gymId);
            try (ResultSet rs = stmt.executeQuery()) {
                maxCapacity = rs.next() ? rs.getInt(1) : -1;
            }
        }
        return Math.round(( (double) (currentOccupancy / maxCapacity) * 100) / 100);
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

    public Map<String, Double> getOccupancyPctForAllGyms() throws SQLException {
        Map<String, Double> result = new HashMap<>();

        String query = """
        SELECT g.Name AS GymName, g.MaxCapacity, COUNT(c.CheckInID) AS CurrentOccupancy
        FROM Gym g
        LEFT JOIN CheckIn c ON g.GymID = c.GymID AND c.CheckOutTime IS NULL
        GROUP BY g.GymID, g.Name, g.MaxCapacity
    """;

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("GymName");
                int max = rs.getInt("MaxCapacity");
                int current = rs.getInt("CurrentOccupancy");

                double percent = 0.0;
                if (max > 0) {
                    percent = (current * 100.0) / max;
                    percent = Math.round(percent * 100.0) / 100.0; // Round to 2 decimal places
                }

                result.put(name, percent);
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

    public boolean checkInStudentWithFacilities(int studentId, int gymId, List<Facility> facilities) throws SQLException {
        if (!canStudentCheckIn(studentId, gymId)) return false;

        String checkInSql = "INSERT INTO CheckIn (StudentID, GymID, CheckInTime) VALUES (?, ?, CURRENT_TIMESTAMP)";
        String usageSql = "INSERT INTO FacilityUsage (FacilityID, StudentID, Timestamp) VALUES (?, ?, CURRENT_TIMESTAMP)";

        try (
                PreparedStatement checkInStmt = connection.prepareStatement(checkInSql);
                PreparedStatement usageStmt = connection.prepareStatement(usageSql)
        ) {
            // Insert into CheckIn
            checkInStmt.setInt(1, studentId);
            checkInStmt.setInt(2, gymId);
            checkInStmt.executeUpdate();

            // Insert into FacilityUsage for each facility
            for (Facility facility : facilities) {
                usageStmt.setInt(1, facility.getFacilityId());
                usageStmt.setInt(2, studentId);
                usageStmt.addBatch();
            }
            usageStmt.executeBatch();

            connection.commit();
            return true;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
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
            if (stmt.executeUpdate() == 1) {
                connection.commit();
                return true;
            }
        }
        return false;
    }

    public int getStudentIdByUsername(String username) throws SQLException {
        String query = "SELECT StudentID FROM Student WHERE Username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("StudentID");
        }
    }
        return -1; // not found
    }

    public List<Facility> getFacilitiesByGym(int gymId) throws SQLException {
        List<Facility> facilities = new ArrayList<>();

        String query = """
            SELECT f.FacilityID, f.Name, f.MaxConcurrentUsers, g.Name AS GymName
            FROM Facility f
            JOIN Gym g ON f.GymID = g.GymID
            WHERE f.GymID = ?
            ORDER BY f.Name
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, gymId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Facility facility = new Facility(
                            rs.getInt("FacilityID"),
                            rs.getString("GymName"),  // location = gym name
                            rs.getString("Name"),
                            "", // facilityType placeholder (not in DB schema)
                            rs.getInt("MaxConcurrentUsers")
                    );
                    facilities.add(facility);
                }
            }
        }

        return facilities;
    }

    public boolean updateGym(int gymId, String newName, int newMaxCapacity) throws SQLException {
        String update = """
        UPDATE Gym
        SET Name = ?, MaxCapacity = ?
        WHERE GymID = ?
    """;

        try (PreparedStatement stmt = connection.prepareStatement(update)) {
            stmt.setString(1, newName);
            stmt.setInt(2, newMaxCapacity);
            stmt.setInt(3, gymId);

            int rowsAffected = stmt.executeUpdate();
            connection.commit();
            return rowsAffected == 1;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public boolean deleteGym(int gymId) throws SQLException {
        String query = "DELETE FROM Gym WHERE GymID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, gymId);
            int rowsAffected = stmt.executeUpdate();
            connection.commit();
            return rowsAffected == 1;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public boolean createGym(String name, int maxCapacity) throws SQLException {
        String query = "INSERT INTO Gym (Name, MaxCapacity) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setInt(2, maxCapacity);
            int rowsInserted = stmt.executeUpdate();
            connection.commit();
            return rowsInserted == 1;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

}
