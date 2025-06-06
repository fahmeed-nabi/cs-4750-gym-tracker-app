package org.example.database;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.*;

public class DBManager {
    private Connection connection;

    public void connect() throws SQLException {
        Dotenv dotenv = Dotenv.load();

        String url = dotenv.get("DB_URL");
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        connection = DriverManager.getConnection(url, user, password);

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("SET foreign_key_checks = 1");
        }

        connection.setAutoCommit(false);
    }

    /**
     * Commit all changes since the connection was opened OR since the last commit/rollback
     */
    public void commit() throws SQLException {
        connection.commit();
    }

    /**
     * Rollback to the last commit, or when the connection was opened
     */
    public void rollback() throws SQLException {
        connection.rollback();
    }

    /**
     * Ends the connection to the database
     */
    public void disconnect() throws SQLException {
        connection.close();
    }

    public void createTables() throws SQLException {
        String[] tableStatements = {
                """
        CREATE TABLE IF NOT EXISTS Gym (
            GymID INT PRIMARY KEY,
            Name VARCHAR(100) NOT NULL,
            MaxCapacity INT NOT NULL CHECK (MaxCapacity > 0)
        );
        """,

                """
        CREATE TABLE IF NOT EXISTS Facility (
            FacilityID INT PRIMARY KEY,
            Name VARCHAR(100) NOT NULL,
            MaxConcurrentUsers INT NOT NULL CHECK (MaxConcurrentUsers > 0),
            GymID INT,
            FOREIGN KEY (GymID) REFERENCES Gym(GymID)
                ON DELETE CASCADE
                ON UPDATE CASCADE
        );
        """,

                """
        CREATE TABLE IF NOT EXISTS Instructor (
            InstructorID INT PRIMARY KEY AUTO_INCREMENT,
            FirstName VARCHAR(100) NOT NULL,
            LastName VARCHAR(100) NOT NULL,
            Email VARCHAR(100),
            Certification VARCHAR(100),
            FocusArea VARCHAR(100)
        );
        """,

                """
        CREATE TABLE IF NOT EXISTS Class (
            ClassID INT PRIMARY KEY AUTO_INCREMENT,
            Name VARCHAR(100) NOT NULL,
            StartTime DATETIME NOT NULL,
            EndTime DATETIME NOT NULL CHECK (EndTime > StartTime),
            GymID INT,
            InstructorID INT,
            FOREIGN KEY (GymID) REFERENCES Gym(GymID)
                ON DELETE CASCADE
                ON UPDATE CASCADE,
            FOREIGN KEY (InstructorID) REFERENCES Instructor(InstructorID)
                ON DELETE CASCADE
                ON UPDATE CASCADE
        );
        """,

                """
        CREATE TABLE IF NOT EXISTS Student (
            StudentID INT PRIMARY KEY,
            FirstName VARCHAR(100) NOT NULL,
            LastName VARCHAR(100) NOT NULL,
            Email VARCHAR(100) NOT NULL,
            Password VARCHAR(255) NOT NULL
        );
        """,

                """
        CREATE TABLE IF NOT EXISTS ClassAttendance (
            ClassID INT,
            StudentID INT,
            Date DATE NOT NULL,
            PRIMARY KEY (ClassID, StudentID, Date),
            FOREIGN KEY (ClassID) REFERENCES Class(ClassID)
                ON DELETE CASCADE
                ON UPDATE CASCADE,
            FOREIGN KEY (StudentID) REFERENCES Student(StudentID)
                ON DELETE CASCADE
                ON UPDATE CASCADE
        );
        """,

                """
        CREATE TABLE IF NOT EXISTS CheckIn (
            CheckInID INT PRIMARY KEY AUTO_INCREMENT,
            StudentID INT,
            GymID INT,
            CheckInTime DATETIME NOT NULL,
            CheckOutTime DATETIME,
            FOREIGN KEY (StudentID) REFERENCES Student(StudentID)
                ON DELETE CASCADE
                ON UPDATE CASCADE,
            FOREIGN KEY (GymID) REFERENCES Gym(GymID)
                ON DELETE CASCADE
                ON UPDATE CASCADE,
            CHECK (CheckOutTime IS NULL OR CheckOutTime > CheckInTime)
        );
        """,

                """
        CREATE TABLE IF NOT EXISTS FacilityUsage (
            UsageID INT PRIMARY KEY AUTO_INCREMENT,
            FacilityID INT,
            StudentID INT,
            Timestamp DATETIME NOT NULL,
            FOREIGN KEY (FacilityID) REFERENCES Facility(FacilityID)
                ON DELETE CASCADE
                ON UPDATE CASCADE,
            FOREIGN KEY (StudentID) REFERENCES Student(StudentID)
                ON DELETE CASCADE
                ON UPDATE CASCADE
        );
        """,

                """
        CREATE TABLE IF NOT EXISTS Trainer (
            TrainerID INT PRIMARY KEY,
            FirstName VARCHAR(100) NOT NULL,
            LastName VARCHAR(100) NOT NULL,
            Email VARCHAR(100),
            Password VARCHAR(255) NOT NULL
        );
        """,

                """
        CREATE TABLE IF NOT EXISTS Specialty (
            SpecialtyID INT PRIMARY KEY,
            Name VARCHAR(100) NOT NULL
        );
        """,

                """
        CREATE TABLE IF NOT EXISTS TrainerSpecialty (
            TrainerID INT,
            SpecialtyID INT,
            PRIMARY KEY (TrainerID, SpecialtyID),
            FOREIGN KEY (TrainerID) REFERENCES Trainer(TrainerID)
                ON DELETE CASCADE
                ON UPDATE CASCADE,
            FOREIGN KEY (SpecialtyID) REFERENCES Specialty(SpecialtyID)
                ON DELETE CASCADE
                ON UPDATE CASCADE
        );
        """,

                """
        CREATE TABLE IF NOT EXISTS TrainerAvailability (
            AvailabilityID INT PRIMARY KEY AUTO_INCREMENT,
            TrainerID INT,
            DayOfWeek ENUM('Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun') NOT NULL,
            StartTime TIME NOT NULL,
            EndTime TIME NOT NULL CHECK (EndTime > StartTime),
            FOREIGN KEY (TrainerID) REFERENCES Trainer(TrainerID)
                ON DELETE CASCADE
                ON UPDATE CASCADE
        );
        """,

                """
        CREATE TABLE IF NOT EXISTS TrainerAppointment (
            AppointmentID INT PRIMARY KEY AUTO_INCREMENT,
            StudentID INT,
            TrainerID INT,
            Date DATE NOT NULL,
            StartTime TIME NOT NULL,
            EndTime TIME NOT NULL CHECK (EndTime > StartTime),
            FOREIGN KEY (StudentID) REFERENCES Student(StudentID)
                ON DELETE CASCADE
                ON UPDATE CASCADE,
            FOREIGN KEY (TrainerID) REFERENCES Trainer(TrainerID)
                ON DELETE CASCADE
                ON UPDATE CASCADE
        );
        """,

                """
        CREATE TABLE IF NOT EXISTS Admin (
            AdminID INT PRIMARY KEY AUTO_INCREMENT,
            FirstName VARCHAR(100) NOT NULL,
            LastName VARCHAR(100) NOT NULL,
            Email VARCHAR(100) NOT NULL,
            Password VARCHAR(255) NOT NULL
        );
        """
        };

        for (String sql : tableStatements) {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.execute();
            } catch (SQLException e) {
                rollback();
                System.out.println(e.getMessage());
            }

        }
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean authenticateStudent(String email, String password) throws SQLException {
        String query = "SELECT 1 FROM Student WHERE Email = ? AND Password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean authenticateTrainer(String email, String password) throws SQLException {
        String query = "SELECT 1 FROM Trainer WHERE Email = ? AND Password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean authenticateAdmin(String email, String password) throws SQLException {
        String query = "SELECT 1 FROM Admin WHERE Email = ? AND Password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

}
