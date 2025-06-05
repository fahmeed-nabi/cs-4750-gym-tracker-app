USE crowd_ctrl;

-- Gym
INSERT INTO Gym (GymID, Name, MaxCapacity) VALUES
                                               (1, 'Aquatic and Fitness Center', 200),
                                               (2, 'Memorial Gymnasium', 150),
                                               (3, 'North Grounds Recreation Center', 300),
                                               (4, 'Slaughter Recreation Center', 80);

-- Facility
INSERT INTO Facility (FacilityID, Name, MaxConcurrentUsers, GymID) VALUES
                                                                       (1, 'Basketball Court', 50, 1),
                                                                       (2, 'Swimming Pool', 30, 1),
                                                                       (3, 'Weight Room', 40, 2),
                                                                       (4, 'Cardio Zone', 25, 4);

-- Instructor
INSERT INTO Instructor (InstructorID, FirstName, LastName, Email, Certification, FocusArea) VALUES
                                                                                                (1, 'Alice', 'Johnson', 'alice.johnson@uva.edu', 'Yoga Certified', 'Yoga'),
                                                                                                (2, 'Bob', 'Smith', 'bob.smith@uva.edu', 'CPR Certified', 'Strength Training');

-- Class
INSERT INTO Class (ClassID, Name, StartTime, EndTime, GymID, InstructorID) VALUES
                                                                               (1, 'Morning Yoga', '2025-06-01 08:00:00', '2025-06-01 09:00:00', 1, 1),
                                                                               (2, 'Evening Lifting', '2025-06-01 18:00:00', '2025-06-01 19:30:00', 2, 2);

-- Student
INSERT INTO Student (StudentID, FirstName, LastName, Email, Password) VALUES
                                                                          (1001, 'Jane', 'Doe', 'jane@example.com', '100e060425c270b01138bc4ed9b498897d2ec525baa766d9a57004b318e99e19'),
                                                                          (1002, 'John', 'Smith', 'john@example.com', 'dcffce09862520d2eb2c98534ee8caf446a6664e57f64ce5d3d1c33418971a1a');

-- ClassAttendance
INSERT INTO ClassAttendance (ClassID, StudentID, Date) VALUES
                                                           (1, 1001, '2025-06-01'),
                                                           (2, 1002, '2025-06-01');

-- CheckIn
INSERT INTO CheckIn (CheckInID, StudentID, GymID, CheckInTime, CheckOutTime) VALUES
                                                                                 (1, 1001, 1, '2025-06-01 07:50:00', '2025-06-01 09:10:00'),
                                                                                 (2, 1002, 2, '2025-06-01 17:45:00', '2025-06-01 19:45:00');

-- FacilityUsage
INSERT INTO FacilityUsage (UsageID, FacilityID, StudentID, Timestamp) VALUES
                                                                          (1, 1, 1001, '2025-06-01 08:15:00'),
                                                                          (2, 3, 1002, '2025-06-01 18:30:00');

-- Trainer
INSERT INTO Trainer (TrainerID, FirstName, LastName, Email, Password) VALUES
                                                                          (1, 'Coach', 'Carter', 'carter@uva.edu', 'd159740f4782d24b521bce61d9f6604e8741d268870721b9885346247e1f691c'),
                                                                          (2, 'Trainer', 'Tina', 'tina@uva.edu', '3aff336eef670fff095e5c37c23043a11f55e9fdd4b19b93b762fc48ee7cf3f3');

-- Specialty
INSERT INTO Specialty (SpecialtyID, Name) VALUES
                                              (1, 'Weightlifting'),
                                              (2, 'Cardio');

-- TrainerSpecialty
INSERT INTO TrainerSpecialty (TrainerID, SpecialtyID) VALUES
                                                          (1, 1),
                                                          (2, 2);

-- TrainerAvailability
INSERT INTO TrainerAvailability (AvailabilityID, TrainerID, DayOfWeek, StartTime, EndTime) VALUES
                                                                                               (1, 1, 'Mon', '09:00:00', '12:00:00'),
                                                                                               (2, 2, 'Wed', '13:00:00', '17:00:00');

-- TrainerAppointment
INSERT INTO TrainerAppointment (AppointmentID, StudentID, TrainerID, Date, StartTime, EndTime) VALUES
                                                                                                   (1, 1001, 1, '2025-06-03', '09:30:00', '10:00:00'),
                                                                                                   (2, 1002, 2, '2025-06-04', '14:00:00', '14:45:00');

-- Admin
INSERT INTO Admin (FirstName, LastName, Email, Password) VALUES
                                                             ('Samantha', 'Reed', 'sreed@uva.edu', 'ebcfc99aa881883fd9a06b78b50b140df65f2794470e444d57470345dacdb536'),
                                                             ('Kevin', 'Bates', 'kbates@uva.edu', '804da2dbc2b9d7331b319995b78feca56572aad00243db0f2b10beba4c224d29');
