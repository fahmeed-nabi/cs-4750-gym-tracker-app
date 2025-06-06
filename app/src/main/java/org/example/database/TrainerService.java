package org.example.database;

import org.example.models.TrainerAppointment;

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
            SELECT AppointmentID, StudentID, TrainerID, Date, StartTime, EndTime
            FROM TrainerAppointment
            WHERE TrainerID = ?
            ORDER BY Date, StartTime
        """;

        List<TrainerAppointment> appointments = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, trainerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(new TrainerAppointment(
                            rs.getInt("AppointmentID"),
                            rs.getInt("StudentID"),
                            rs.getInt("TrainerID"),
                            rs.getDate("Date").toLocalDate(),
                            rs.getTime("StartTime").toLocalTime(),
                            rs.getTime("EndTime").toLocalTime()
                    ));
                }
            }
        }

        return appointments;
    }

    public List<TrainerAppointment> getStudentAppointments(int studentId) throws SQLException {
        String query = """
            SELECT AppointmentID, StudentID, TrainerID, Date, StartTime, EndTime
            FROM TrainerAppointment
            WHERE StudentID = ?
            ORDER BY Date, StartTime
        """;

        List<TrainerAppointment> appointments = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(new TrainerAppointment(
                            rs.getInt("AppointmentID"),
                            rs.getInt("StudentID"),
                            rs.getInt("TrainerID"),
                            rs.getDate("Date").toLocalDate(),
                            rs.getTime("StartTime").toLocalTime(),
                            rs.getTime("EndTime").toLocalTime()
                    ));
                }
            }
        }

        return appointments;
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
            return stmt.executeUpdate() == 1;
        }
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
            return stmt.executeUpdate() == 1;
        }
    }

}
