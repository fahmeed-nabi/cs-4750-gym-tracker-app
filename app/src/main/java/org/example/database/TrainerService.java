package org.example.database;

import org.example.models.TrainerAppointment;
import org.example.models.TrainerAvailability;
import org.example.models.TrainerSpecialty;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TrainerService {
    private final Connection connection;

    public TrainerService(Connection connection) {
        this.connection = connection;
    }

    public boolean isTrainerAvailable(int trainerId, LocalDate date, LocalTime start, LocalTime end) throws SQLException {
        String dayOfWeek = date.getDayOfWeek().toString().substring(0, 1).toUpperCase() +
                date.getDayOfWeek().toString().substring(1, 3).toLowerCase(); // 'Mon', 'Tue', etc.

        // 1. Is trainer available on that day and time?
        String availabilityQuery = """
            SELECT 1
            FROM TrainerAvailability
            WHERE TrainerID = ?
              AND DayOfWeek = ?
              AND StartTime <= ?
              AND EndTime >= ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(availabilityQuery)) {
            stmt.setInt(1, trainerId);
            stmt.setString(2, dayOfWeek);
            stmt.setTime(3, Time.valueOf(start));
            stmt.setTime(4, Time.valueOf(end));
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return false;
            }
        }

        // 2. Does the trainer already have overlapping appointments?
        String conflictQuery = """
            SELECT 1
            FROM TrainerAppointment
            WHERE TrainerID = ?
              AND Date = ?
              AND (StartTime < ? AND EndTime > ?)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(conflictQuery)) {
            stmt.setInt(1, trainerId);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setTime(3, Time.valueOf(end));
            stmt.setTime(4, Time.valueOf(start));
            try (ResultSet rs = stmt.executeQuery()) {
                return !rs.next(); // return true if no conflicts
            }
        }
    }

    public boolean bookTrainerAppointment(int studentId, int trainerId, LocalDate date,
                                          LocalTime start, LocalTime end) throws SQLException {
        if (!isTrainerAvailable(trainerId, date, start, end)) return false;

        String insert = """
            INSERT INTO TrainerAppointment (StudentID, TrainerID, Date, StartTime, EndTime)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(insert)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, trainerId);
            stmt.setDate(3, Date.valueOf(date));
            stmt.setTime(4, Time.valueOf(start));
            stmt.setTime(5, Time.valueOf(end));
            return stmt.executeUpdate() == 1;
        }
    }

    public List<TrainerAppointment> getTrainerAppointments(int trainerId) throws SQLException {
        String query = """
        SELECT ta.AppointmentID, ta.StudentID, ta.TrainerID, ta.Date, ta.StartTime, ta.EndTime,
               s.FirstName, s.LastName,
               g.Name AS GymName
        FROM TrainerAppointment ta
        JOIN Student s ON ta.StudentID = s.StudentID
        JOIN Gym g ON ta.LocationID = g.GymID
        WHERE ta.TrainerID = ?
        ORDER BY ta.Date, ta.StartTime
    """;

        List<TrainerAppointment> appointments = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, trainerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TrainerAppointment appt = new TrainerAppointment(
                            rs.getInt("AppointmentID"),
                            rs.getInt("StudentID"),
                            rs.getInt("TrainerID"),
                            rs.getDate("Date").toLocalDate(),
                            rs.getTime("StartTime").toLocalTime(),
                            rs.getTime("EndTime").toLocalTime(),
                            rs.getString("FirstName"),
                            rs.getString("LastName")
                    );
                    appt.setLocationName(rs.getString("GymName"));
                    appointments.add(appt);
                }
            }
        }

        return appointments;
    }

    public List<TrainerAppointment> getPastAppointments(int trainerId) throws SQLException {
        String query = """
        SELECT ta.AppointmentID, ta.StudentID, ta.TrainerID, ta.Date, ta.StartTime, ta.EndTime,
               s.FirstName, s.LastName,
               g.Name AS GymName
        FROM TrainerAppointment ta
        JOIN Student s ON ta.StudentID = s.StudentID
        JOIN Gym g ON ta.LocationID = g.GymID
        WHERE ta.TrainerID = ? AND ta.Date < CURRENT_DATE
        ORDER BY ta.Date DESC, ta.StartTime DESC
    """;

        List<TrainerAppointment> pastAppointments = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, trainerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TrainerAppointment appt = new TrainerAppointment(
                            rs.getInt("AppointmentID"),
                            rs.getInt("StudentID"),
                            rs.getInt("TrainerID"),
                            rs.getDate("Date").toLocalDate(),
                            rs.getTime("StartTime").toLocalTime(),
                            rs.getTime("EndTime").toLocalTime(),
                            rs.getString("FirstName"),
                            rs.getString("LastName")
                    );
                    appt.setLocationName(rs.getString("GymName"));
                    pastAppointments.add(appt);
                }
            }
        }

        return pastAppointments;
    }


    // Helper method used in updating/rescheduling an appointment
    private boolean isTrainerAvailableExcluding(int excludeAppointmentId, int trainerId,
                                                LocalDate date, LocalTime start, LocalTime end) throws SQLException {
        String dayOfWeek = date.getDayOfWeek().toString().substring(0, 1).toUpperCase() +
                date.getDayOfWeek().toString().substring(1, 3).toLowerCase();

        // 1. Still only count if available that day/time
        String availabilityQuery = """
        SELECT 1
        FROM TrainerAvailability
        WHERE TrainerID = ?
          AND DayOfWeek = ?
          AND StartTime <= ?
          AND EndTime >= ?
    """;

        try (PreparedStatement stmt = connection.prepareStatement(availabilityQuery)) {
            stmt.setInt(1, trainerId);
            stmt.setString(2, dayOfWeek);
            stmt.setTime(3, Time.valueOf(start));
            stmt.setTime(4, Time.valueOf(end));
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return false;
            }
        }

        // 2. Check for overlap (excluding the current appointment)
        String conflictQuery = """
        SELECT 1
        FROM TrainerAppointment
        WHERE TrainerID = ?
          AND Date = ?
          AND AppointmentID != ?
          AND (StartTime < ? AND EndTime > ?)
    """;

        try (PreparedStatement stmt = connection.prepareStatement(conflictQuery)) {
            stmt.setInt(1, trainerId);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setInt(3, excludeAppointmentId);
            stmt.setTime(4, Time.valueOf(end));
            stmt.setTime(5, Time.valueOf(start));
            try (ResultSet rs = stmt.executeQuery()) {
                return !rs.next();
            }
        }
    }

    public boolean deleteTrainerAppointment(int appointmentId) throws SQLException {
        String delete = "DELETE FROM TrainerAppointment WHERE AppointmentID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(delete)) {
            stmt.setInt(1, appointmentId);
            int rows = stmt.executeUpdate();
            if (rows == 1) {
                connection.commit();
                return true;
            }
        }
        return false;
    }


    public boolean updateTrainerAppointment(int appointmentId, int studentId, int trainerId,
                                            LocalDate date, LocalTime start, LocalTime end) throws SQLException {
        // Avoid conflict by checking availability (excluding the appointment being updated)
        if (!isTrainerAvailableExcluding(appointmentId, trainerId, date, start, end)) return false;

        String update = """
        UPDATE TrainerAppointment
        SET StudentID = ?, TrainerID = ?, Date = ?, StartTime = ?, EndTime = ?
        WHERE AppointmentID = ?
    """;

        try (PreparedStatement stmt = connection.prepareStatement(update)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, trainerId);
            stmt.setDate(3, Date.valueOf(date));
            stmt.setTime(4, Time.valueOf(start));
            stmt.setTime(5, Time.valueOf(end));
            stmt.setInt(6, appointmentId);

            int rows = stmt.executeUpdate();
            if (rows == 1) {
                connection.commit();
                return true;
            }
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }

        return false;
    }

    public List<TrainerAvailability> getAvailabilityByTrainer(int trainerId) throws SQLException {
        String query = """
        SELECT AvailabilityID, TrainerID, DayOfWeek, StartTime, EndTime
        FROM TrainerAvailability
        WHERE TrainerID = ?
        ORDER BY FIELD(DayOfWeek, 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'), StartTime
    """;

        List<TrainerAvailability> list = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, trainerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new TrainerAvailability(
                            rs.getInt("AvailabilityID"),
                            rs.getInt("TrainerID"),
                            rs.getString("DayOfWeek"),
                            rs.getTime("StartTime").toString(),
                            rs.getTime("EndTime").toString()
                    ));
                }
            }
        }

        return list;
    }

    private String normalizeToSqlTimeFormat(String time) {
        String[] parts = time.split(":");
        String hour = parts[0].length() == 1 ? "0" + parts[0] : parts[0];
        String minute = parts.length > 1 ? parts[1] : "00";
        return hour + ":" + minute + ":00";
    }

    public boolean addAvailability(int trainerId, String dayOfWeek, String startTime, String endTime) throws SQLException {
        String insert = """
        INSERT INTO TrainerAvailability (TrainerID, DayOfWeek, StartTime, EndTime)
        VALUES (?, ?, ?, ?)
    """;

        // Normalize to HH:mm:ss format
        String normalizedStart = normalizeToSqlTimeFormat(startTime);
        String normalizedEnd = normalizeToSqlTimeFormat(endTime);

        try (PreparedStatement stmt = connection.prepareStatement(insert)) {
            stmt.setInt(1, trainerId);
            stmt.setString(2, dayOfWeek);
            stmt.setTime(3, Time.valueOf(normalizedStart));
            stmt.setTime(4, Time.valueOf(normalizedEnd));
            int rows = stmt.executeUpdate();
            if (rows == 1) {
                connection.commit();
                return true;
            }
        }

        return false;
    }

    public boolean removeAvailability(int availabilityId) throws SQLException {
        String delete = "DELETE FROM TrainerAvailability WHERE AvailabilityID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(delete)) {
            stmt.setInt(1, availabilityId);
            int rows = stmt.executeUpdate();
            if (rows == 1) {
                connection.commit();
                return true;
            }
        }

        return false;
    }

    public boolean clearAvailabilityForTrainer(int trainerId) throws SQLException {
        String delete = "DELETE FROM TrainerAvailability WHERE TrainerID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(delete)) {
            stmt.setInt(1, trainerId);
            stmt.executeUpdate();
            connection.commit();
            return true;
        } catch (SQLException e) {
            connection.rollback();  // optional but good practice
            throw e;
        }
    }

    public List<TrainerSpecialty> getTrainerSpecialties(int trainerId) throws SQLException {
        String query = """
        SELECT ts.TrainerID, s.Name AS Specialty
        FROM TrainerSpecialty ts
        JOIN Specialty s ON ts.SpecialtyID = s.SpecialtyID
        WHERE ts.TrainerID = ?
    """;

        List<TrainerSpecialty> list = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, trainerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new TrainerSpecialty(
                            rs.getInt("TrainerID"),
                            rs.getString("Specialty")
                    ));
                }
            }
        }
        return list;
    }

    public boolean addTrainerSpecialty(int trainerId, String specialtyName) throws SQLException {
        // Insert into Specialty if it doesn't exist
        String getOrInsertSpecialty = """
        INSERT INTO Specialty (Name)
        SELECT * FROM (SELECT ?) AS tmp
        WHERE NOT EXISTS (
            SELECT 1 FROM Specialty WHERE Name = ?
        ) LIMIT 1
    """;

        String getId = "SELECT SpecialtyID FROM Specialty WHERE Name = ?";
        String insertTrainerSpecialty = "INSERT INTO TrainerSpecialty (TrainerID, SpecialtyID) VALUES (?, ?)";

        try {
            // Step 1: Insert specialty if missing
            try (PreparedStatement insertSpec = connection.prepareStatement(getOrInsertSpecialty)) {
                insertSpec.setString(1, specialtyName);
                insertSpec.setString(2, specialtyName);
                insertSpec.executeUpdate();  // Safe to run even if already exists
            }

            // Step 2: Lookup specialty ID
            int specialtyId;
            try (PreparedStatement lookup = connection.prepareStatement(getId)) {
                lookup.setString(1, specialtyName);
                ResultSet rs = lookup.executeQuery();
                if (!rs.next()) return false;
                specialtyId = rs.getInt("SpecialtyID");
            }

            // Step 3: Insert trainer-specialty (if not duplicate)
            try (PreparedStatement insertTS = connection.prepareStatement(insertTrainerSpecialty)) {
                insertTS.setInt(1, trainerId);
                insertTS.setInt(2, specialtyId);
                int rows = insertTS.executeUpdate();
                if (rows == 1) {
                    connection.commit();
                    return true;
                }
            }
        } catch (SQLException e) {
            connection.rollback();  // Always rollback on error
            throw e;
        }

        return false;
    }

    public boolean removeTrainerSpecialty(int trainerId, String specialtyName) throws SQLException {
        String lookup = "SELECT SpecialtyID FROM Specialty WHERE Name = ?";
        String delete = "DELETE FROM TrainerSpecialty WHERE TrainerID = ? AND SpecialtyID = ?";

        try (PreparedStatement lookupStmt = connection.prepareStatement(lookup)) {
            lookupStmt.setString(1, specialtyName);
            try (ResultSet rs = lookupStmt.executeQuery()) {
                if (!rs.next()) return false;
                int specialtyId = rs.getInt("SpecialtyID");

                try (PreparedStatement deleteStmt = connection.prepareStatement(delete)) {
                    deleteStmt.setInt(1, trainerId);
                    deleteStmt.setInt(2, specialtyId);
                    int rows = deleteStmt.executeUpdate();
                    if (rows == 1) {
                        connection.commit();
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
