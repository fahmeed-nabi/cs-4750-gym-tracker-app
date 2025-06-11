package org.example.database;

import java.sql.*;

public class SchemaManager {
    private final Connection connection;

    public SchemaManager(Connection connection) {
        this.connection = connection;
    }

    public void createTables() throws SQLException {
        String[] tableStatements = {
                """
        CREATE TABLE IF NOT EXISTS Gym (
            GymID TINYINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
            Name VARCHAR(60) NOT NULL,
            MaxCapacity SMALLINT UNSIGNED NOT NULL CHECK (MaxCapacity > 0)
        );
        """,

                """
        CREATE TABLE IF NOT EXISTS Facility (
            FacilityID TINYINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
            Name VARCHAR(60) NOT NULL,
            MaxConcurrentUsers TINYINT UNSIGNED NOT NULL CHECK (MaxConcurrentUsers > 0),
            GymID TINYINT UNSIGNED,
            FOREIGN KEY (GymID) REFERENCES Gym(GymID)
                ON DELETE CASCADE ON UPDATE CASCADE,
            UNIQUE(Name, GymID)
        );
        """,

                """
        CREATE TABLE IF NOT EXISTS Instructor (
            InstructorID SMALLINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
            FirstName VARCHAR(50) NOT NULL,
            LastName VARCHAR(50) NOT NULL,
            Email VARCHAR(100),
            Certification VARCHAR(100),
            FocusArea VARCHAR(100)
        );
        """,

                """
        CREATE TABLE IF NOT EXISTS Class (
            ClassID INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
            Name VARCHAR(60) NOT NULL,
            StartTime DATETIME NOT NULL,
            EndTime DATETIME NOT NULL CHECK (EndTime > StartTime),
            AvailableSpots SMALLINT UNSIGNED NOT NULL,
            GymID TINYINT UNSIGNED,
            InstructorID SMALLINT UNSIGNED,
            FOREIGN KEY (GymID) REFERENCES Gym(GymID)
                ON DELETE CASCADE ON UPDATE CASCADE,
            FOREIGN KEY (InstructorID) REFERENCES Instructor(InstructorID)
                ON DELETE CASCADE ON UPDATE CASCADE
        );
        """,

                """
        CREATE TABLE IF NOT EXISTS Student (
            StudentID INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
            FirstName VARCHAR(50) NOT NULL,
            LastName VARCHAR(50) NOT NULL,
            Email VARCHAR(100) NOT NULL,
            Password VARCHAR(60) NOT NULL
        );
        """,

                """
        CREATE TABLE IF NOT EXISTS ClassAttendance (
            ClassID INT UNSIGNED,
            StudentID INT UNSIGNED,
            Date DATE NOT NULL,
            PRIMARY KEY (ClassID, StudentID),
            FOREIGN KEY (ClassID) REFERENCES Class(ClassID)
                ON DELETE CASCADE ON UPDATE CASCADE,
            FOREIGN KEY (StudentID) REFERENCES Student(StudentID)
                ON DELETE CASCADE ON UPDATE CASCADE
        );
        """,

                """
        CREATE TABLE IF NOT EXISTS CheckIn (
            CheckInID INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
            StudentID INT UNSIGNED,
            GymID TINYINT UNSIGNED,
            CheckInTime DATETIME NOT NULL,
            CheckOutTime DATETIME,
            FOREIGN KEY (StudentID) REFERENCES Student(StudentID)
                ON DELETE CASCADE ON UPDATE CASCADE,
            FOREIGN KEY (GymID) REFERENCES Gym(GymID)
                ON DELETE CASCADE ON UPDATE CASCADE,
            CHECK (CheckOutTime IS NULL OR CheckOutTime > CheckInTime)
        );
        """,

                """
        CREATE TABLE IF NOT EXISTS FacilityUsage (
            FacilityID TINYINT UNSIGNED,
            StudentID INT UNSIGNED,
            Timestamp DATETIME NOT NULL,
            PRIMARY KEY (FacilityID, StudentID, Timestamp),
            FOREIGN KEY (FacilityID) REFERENCES Facility(FacilityID)
                ON DELETE CASCADE ON UPDATE CASCADE,
            FOREIGN KEY (StudentID) REFERENCES Student(StudentID)
                ON DELETE CASCADE ON UPDATE CASCADE
        );
        """,

                """
        CREATE TABLE IF NOT EXISTS Trainer (
            TrainerID SMALLINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
            FirstName VARCHAR(50) NOT NULL,
            LastName VARCHAR(50) NOT NULL,
            Email VARCHAR(100),
            Password VARCHAR(60) NOT NULL
        );
        """,

                """
        CREATE TABLE IF NOT EXISTS Specialty (
            SpecialtyID TINYINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
            Name VARCHAR(60) NOT NULL
        );
        """,

                """
        CREATE TABLE IF NOT EXISTS TrainerSpecialty (
            TrainerID SMALLINT UNSIGNED,
            SpecialtyID TINYINT UNSIGNED,
            PRIMARY KEY (TrainerID, SpecialtyID),
            FOREIGN KEY (TrainerID) REFERENCES Trainer(TrainerID)
                ON DELETE CASCADE ON UPDATE CASCADE,
            FOREIGN KEY (SpecialtyID) REFERENCES Specialty(SpecialtyID)
                ON DELETE CASCADE ON UPDATE CASCADE
        );
        """,

                """
        CREATE TABLE IF NOT EXISTS TrainerAvailability (
            AvailabilityID INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
            TrainerID SMALLINT UNSIGNED,
            DayOfWeek ENUM('Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun') NOT NULL,
            StartTime TIME NOT NULL,
            EndTime TIME NOT NULL,
            FOREIGN KEY (TrainerID) REFERENCES Trainer(TrainerID)
                ON DELETE CASCADE ON UPDATE CASCADE,
            CHECK (EndTime > StartTime)
        );
        """,

                """
        CREATE TABLE IF NOT EXISTS TrainerAppointment (
            AppointmentID INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
            StudentID INT UNSIGNED,
            TrainerID SMALLINT UNSIGNED,
            LocationID TINYINT UNSIGNED,
            Date DATE NOT NULL,
            StartTime TIME NOT NULL,
            EndTime TIME NOT NULL,
            FOREIGN KEY (StudentID) REFERENCES Student(StudentID)
                ON DELETE CASCADE ON UPDATE CASCADE,
            FOREIGN KEY (TrainerID) REFERENCES Trainer(TrainerID)
                ON DELETE CASCADE ON UPDATE CASCADE,
            FOREIGN KEY (LocationID) REFERENCES Gym(GymID)
                ON DELETE CASCADE ON UPDATE CASCADE,
            CHECK (EndTime > StartTime)
        );
        """,

                """
        CREATE TABLE IF NOT EXISTS Admin (
            AdminID SMALLINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
            FirstName VARCHAR(50) NOT NULL,
            LastName VARCHAR(50) NOT NULL,
            Email VARCHAR(100) NOT NULL,
            Password VARCHAR(60) NOT NULL
        );
        """
        };

        for (String sql : tableStatements) {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.execute();
            } catch (SQLException e) {
                connection.rollback();
                System.out.println(e.getMessage());
            }
        }
    }
}
