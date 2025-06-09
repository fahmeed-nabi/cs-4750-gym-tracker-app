package org.example.database;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.example.models.ClassOverview;
import org.example.models.ClassSession;
import org.example.models.Instructor;

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

    public boolean registerStudentForClass(int studentId, int classId) throws SQLException {
        if (isClassInPast(classId)) return false;
        if (isStudentAlreadyRegistered(studentId, classId)) return false;
        if (hasTimeConflict(studentId, classId)) return false;
        if (isClassFull(classId)) return false;

        LocalDate classDate = getClassStartDate(classId);
        if (classDate == null) return false;

        String sql = "INSERT INTO ClassAttendance (ClassID, StudentID, Date) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, classId);
            stmt.setInt(2, studentId);
            stmt.setDate(3, java.sql.Date.valueOf(classDate));
            int inserted = stmt.executeUpdate();
            if (inserted == 1) {
                decrementAvailableSpots(classId);
                connection.commit();
                return true;
            }
        }
        return false;
    }

    private LocalDate getClassStartDate(int classId) throws SQLException {
        String sql = "SELECT DATE(StartTime) AS ClassDate FROM Class WHERE ClassID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, classId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDate("ClassDate").toLocalDate();
            }
        }
        return null;
    }

    private boolean isClassInPast(int classId) throws SQLException {
        String sql = "SELECT StartTime FROM Class WHERE ClassID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, classId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Timestamp start = rs.getTimestamp("StartTime");
                return start.toLocalDateTime().isBefore(LocalDateTime.now());
            }
        }
        return true; // Default deny
    }

    private boolean isStudentAlreadyRegistered(int studentId, int classId) throws SQLException {
        String sql = "SELECT 1 FROM ClassAttendance WHERE ClassID = ? AND StudentID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, classId);
            stmt.setInt(2, studentId);
            return stmt.executeQuery().next();
        }
    }

    private boolean hasTimeConflict(int studentId, int classId) throws SQLException {
        String targetQuery = "SELECT StartTime, EndTime FROM Class WHERE ClassID = ?";
        LocalDateTime start, end;
        try (PreparedStatement stmt = connection.prepareStatement(targetQuery)) {
            stmt.setInt(1, classId);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) return true; // block if class not found
            start = rs.getTimestamp("StartTime").toLocalDateTime();
            end = rs.getTimestamp("EndTime").toLocalDateTime();
        }

        String conflictQuery = """
        SELECT c.StartTime, c.EndTime
        FROM ClassAttendance a
        JOIN Class c ON a.ClassID = c.ClassID
        WHERE a.StudentID = ? AND a.Date = CURRENT_DATE
    """;

        try (PreparedStatement stmt = connection.prepareStatement(conflictQuery)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LocalDateTime s = rs.getTimestamp("StartTime").toLocalDateTime();
                LocalDateTime e = rs.getTimestamp("EndTime").toLocalDateTime();
                if (start.isBefore(e) && end.isAfter(s)) {
                    return true; // overlapping
                }
            }
        }
        return false;
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


    public List<ClassOverview> getUpcomingClassOverviews() throws SQLException {
        List<ClassOverview> list = new ArrayList<>();
        String sql = """
        SELECT c.ClassID, c.Name AS ClassName, c.StartTime, c.EndTime,
               c.GymID, c.AvailableSpots,
               g.Name AS GymName,
               i.InstructorID, i.FirstName, i.LastName, i.Email, i.Certification, i.FocusArea
        FROM Class c
        JOIN Gym g ON c.GymID = g.GymID
        JOIN Instructor i ON c.InstructorID = i.InstructorID
        WHERE c.StartTime >= NOW()
        ORDER BY c.StartTime ASC
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ClassSession classSession = new ClassSession(
                        rs.getInt("ClassID"),
                        rs.getString("ClassName"),
                        rs.getTimestamp("StartTime").toLocalDateTime(),
                        rs.getTimestamp("EndTime").toLocalDateTime(),
                        rs.getInt("GymID"),
                        rs.getInt("InstructorID"),
                        rs.getTimestamp("StartTime").toLocalDateTime().toLocalDate(),  // extract date
                        rs.getInt("AvailableSpots")
                );

                Instructor instructor = new Instructor(
                        rs.getInt("InstructorID"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Email"),
                        rs.getString("Certification"),
                        rs.getString("FocusArea")
                );

                String gymName = rs.getString("GymName");
                list.add(new ClassOverview(classSession, gymName, instructor));
            }
        }

        return list;
    }

    public List<ClassOverview> getRegisteredClassOverviews(int studentId) throws SQLException {
        List<ClassOverview> registeredClasses = new ArrayList<>();

        String sql = """
        SELECT 
            c.ClassID, c.Name AS ClassName, c.StartTime, c.EndTime, c.AvailableSpots,
            g.GymID, g.Name AS GymName,
            i.InstructorID, i.FirstName, i.LastName, i.Email, i.Certification, i.FocusArea
        FROM ClassAttendance ca
        JOIN Class c ON ca.ClassID = c.ClassID
        JOIN Gym g ON c.GymID = g.GymID
        JOIN Instructor i ON c.InstructorID = i.InstructorID
        WHERE ca.StudentID = ? AND c.StartTime >= NOW()
        ORDER BY c.StartTime
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ClassSession session = new ClassSession(
                        rs.getInt("ClassID"),
                        rs.getString("ClassName"),
                        rs.getTimestamp("StartTime").toLocalDateTime(),
                        rs.getTimestamp("EndTime").toLocalDateTime(),
                        rs.getInt("GymID"),
                        rs.getInt("InstructorID"),
                        rs.getTimestamp("StartTime").toLocalDateTime().toLocalDate(),
                        rs.getInt("AvailableSpots")
                );

                Instructor instructor = new Instructor(
                        rs.getInt("InstructorID"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Email"),
                        rs.getString("Certification"),
                        rs.getString("FocusArea")
                );

                String gymName = rs.getString("GymName");

                registeredClasses.add(new ClassOverview(session, gymName, instructor));
            }
        }

        return registeredClasses;
    }


    public boolean unregisterStudentFromClass(int studentId, int classId) throws SQLException {
        String sql = "DELETE FROM ClassAttendance WHERE ClassID = ? AND StudentID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, classId);
            stmt.setInt(2, studentId);
            int deleted = stmt.executeUpdate();
            if (deleted == 1) {
                incrementAvailableSpots(classId);
                connection.commit();
                return true;
            }
        }
        return false;
    }

    public List<ClassOverview> getClassHistory(int studentId) throws SQLException {
        List<ClassOverview> registeredClasses = new ArrayList<>();

        String sql = """
        SELECT 
            c.ClassID, c.Name AS ClassName, c.StartTime, c.EndTime, c.AvailableSpots,
            g.GymID, g.Name AS GymName,
            i.InstructorID, i.FirstName, i.LastName, i.Email, i.Certification, i.FocusArea
        FROM ClassAttendance ca
        JOIN Class c ON ca.ClassID = c.ClassID
        JOIN Gym g ON c.GymID = g.GymID
        JOIN Instructor i ON c.InstructorID = i.InstructorID
        WHERE ca.StudentID = ? AND c.StartTime < NOW()
        ORDER BY c.StartTime
        LIMIT 15;
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ClassSession session = new ClassSession(
                        rs.getInt("ClassID"),
                        rs.getString("ClassName"),
                        rs.getTimestamp("StartTime").toLocalDateTime(),
                        rs.getTimestamp("EndTime").toLocalDateTime(),
                        rs.getInt("GymID"),
                        rs.getInt("InstructorID"),
                        rs.getTimestamp("StartTime").toLocalDateTime().toLocalDate(),
                        rs.getInt("AvailableSpots")
                );

                Instructor instructor = new Instructor(
                        rs.getInt("InstructorID"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Email"),
                        rs.getString("Certification"),
                        rs.getString("FocusArea")
                );

                String gymName = rs.getString("GymName");

                registeredClasses.add(new ClassOverview(session, gymName, instructor));
            }
        }

        return registeredClasses;
    }

    private void decrementAvailableSpots(int classId) throws SQLException {
        String sql = "UPDATE Class SET AvailableSpots = AvailableSpots - 1 WHERE ClassID = ? AND AvailableSpots > 0";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, classId);
            stmt.executeUpdate();
        }
    }

    private void incrementAvailableSpots(int classId) throws SQLException {
        String sql = "UPDATE Class SET AvailableSpots = AvailableSpots + 1 WHERE ClassID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, classId);
            stmt.executeUpdate();
        }
    }

    private boolean isClassFull(int classId) throws SQLException {
        String sql = "SELECT AvailableSpots FROM Class WHERE ClassID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, classId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int spots = rs.getInt("AvailableSpots");
                return spots <= 0;
            }
        }
        // If the class isn't found, treat it as full by default
        return true;
    }
}
