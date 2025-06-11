USE railway;

-- Gym Table
CREATE TABLE IF NOT EXISTS Gym (
                                   GymID INT PRIMARY KEY,
                                   Name VARCHAR(100) NOT NULL,
                                   MaxCapacity INT NOT NULL CHECK (MaxCapacity > 0)
);

-- Facility Table
CREATE TABLE IF NOT EXISTS Facility (
                                        FacilityID INT PRIMARY KEY AUTO_INCREMENT,
                                        Name VARCHAR(100) NOT NULL,
                                        MaxConcurrentUsers INT NOT NULL CHECK (MaxConcurrentUsers > 0),
                                        GymID INT,
                                        FOREIGN KEY (GymID) REFERENCES Gym(GymID)
                                            ON DELETE CASCADE
                                            ON UPDATE CASCADE,
                                        UNIQUE(Name, GymID)
);

-- Instructor Table
CREATE TABLE IF NOT EXISTS Instructor (
                                          InstructorID INT PRIMARY KEY AUTO_INCREMENT,
                                          FirstName VARCHAR(100) NOT NULL,
                                          LastName VARCHAR(100) NOT NULL,
                                          Email VARCHAR(100),
                                          Certification VARCHAR(100),
                                          FocusArea VARCHAR(100)
);

-- Class Table
CREATE TABLE IF NOT EXISTS Class (
                                     ClassID INT PRIMARY KEY AUTO_INCREMENT,
                                     Name VARCHAR(100) NOT NULL,
                                     StartTime DATETIME NOT NULL,
                                     EndTime DATETIME NOT NULL,
                                     AvailableSpots INT NOT NULL,
                                     GymID INT,
                                     InstructorID INT,
                                     FOREIGN KEY (GymID) REFERENCES Gym(GymID)
                                         ON DELETE CASCADE
                                         ON UPDATE CASCADE,
                                     FOREIGN KEY (InstructorID) REFERENCES Instructor(InstructorID)
                                         ON DELETE CASCADE
                                         ON UPDATE CASCADE,
                                     CHECK (EndTime > StartTime)
);

-- Student Table
CREATE TABLE IF NOT EXISTS Student (
                                       StudentID INT PRIMARY KEY AUTO_INCREMENT,
                                       FirstName VARCHAR(100) NOT NULL,
                                       LastName VARCHAR(100) NOT NULL,
                                       Email VARCHAR(100) NOT NULL,
                                       Password VARCHAR(255) NOT NULL
);

-- ClassAttendance Table (Bridge)
CREATE TABLE IF NOT EXISTS ClassAttendance (
                                               ClassID INT,
                                               StudentID INT,
                                               Date DATE NOT NULL,
                                               PRIMARY KEY (ClassID, StudentID),
                                               FOREIGN KEY (ClassID) REFERENCES Class(ClassID)
                                                   ON DELETE CASCADE
                                                   ON UPDATE CASCADE,
                                               FOREIGN KEY (StudentID) REFERENCES Student(StudentID)
                                                   ON DELETE CASCADE
                                                   ON UPDATE CASCADE
);

-- CheckIn Table
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

-- FacilityUsage Table
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

-- Trainer Table
CREATE TABLE IF NOT EXISTS Trainer (
                                       TrainerID INT PRIMARY KEY AUTO_INCREMENT,
                                       FirstName VARCHAR(100) NOT NULL,
                                       LastName VARCHAR(100) NOT NULL,
                                       Email VARCHAR(100),
                                       Password VARCHAR(255) NOT NULL
);

-- Specialty Table
CREATE TABLE IF NOT EXISTS Specialty (
                                         SpecialtyID INT PRIMARY KEY AUTO_INCREMENT,
                                         Name VARCHAR(100) NOT NULL
);

-- TrainerSpecialty Table (Bridge)
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

-- TrainerAvailability Table
CREATE TABLE IF NOT EXISTS TrainerAvailability (
                                                   AvailabilityID INT PRIMARY KEY AUTO_INCREMENT,
                                                   TrainerID INT,
                                                   DayOfWeek ENUM('Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun') NOT NULL,
                                                   StartTime TIME NOT NULL,
                                                   EndTime TIME NOT NULL,
                                                   FOREIGN KEY (TrainerID) REFERENCES Trainer(TrainerID)
                                                       ON DELETE CASCADE
                                                       ON UPDATE CASCADE,
                                                   CHECK (EndTime > StartTime)
);

-- TrainerAppointment Table
CREATE TABLE IF NOT EXISTS TrainerAppointment (
                                                  AppointmentID INT PRIMARY KEY AUTO_INCREMENT,
                                                  StudentID INT,
                                                  TrainerID INT,
                                                  LocationID INT,
                                                  Date DATE NOT NULL,
                                                  StartTime TIME NOT NULL,
                                                  EndTime TIME NOT NULL,
                                                  FOREIGN KEY (StudentID) REFERENCES Student(StudentID)
                                                      ON DELETE CASCADE
                                                      ON UPDATE CASCADE,
                                                  FOREIGN KEY (TrainerID) REFERENCES Trainer(TrainerID)
                                                      ON DELETE CASCADE
                                                      ON UPDATE CASCADE,
                                                  FOREIGN KEY (LocationID) REFERENCES Gym(GymID)
                                                      ON DELETE CASCADE
                                                      ON UPDATE CASCADE,
                                                  CHECK (EndTime > StartTime)
);

-- Admin Table
CREATE TABLE IF NOT EXISTS Admin (
                                     AdminID INT PRIMARY KEY AUTO_INCREMENT,
                                     FirstName VARCHAR(100) NOT NULL,
                                     LastName VARCHAR(100) NOT NULL,
                                     Email VARCHAR(100) NOT NULL,
                                     Password VARCHAR(255) NOT NULL
);
