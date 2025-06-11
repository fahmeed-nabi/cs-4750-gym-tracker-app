-- SELECT STATEMENTS
SELECT * FROM Gym;
SELECT * FROM Facility;
SELECT * FROM Instructor;
SELECT * FROM Class;
SELECT * FROM Student;
SELECT * FROM ClassAttendance;
SELECT * FROM CheckIn;
SELECT * FROM FacilityTable;
SELECT * FROM Trainer;
SELECT * FROM Specialty;
SELECT * FROM TrainerSpecialty;
SELECT * FROM TrainerAvailability;
SELECT * FROM TrainerAppointment;
SELECT * FROM Admin;

-- Classes taught at Main Gym (GymID 1)
SELECT * FROM Class WHERE GymID = 1;

-- Students who checked into West End Gym (GymID 2)
SELECT * FROM CheckIn WHERE GymID = 2;

-- Instructors focused on 'Yoga'
SELECT * FROM Instructor WHERE FocusArea = 'Yoga';

-- Trainer appointments on a Wednesday
SELECT * FROM TrainerAppointment WHERE Date = '2025-06-04';