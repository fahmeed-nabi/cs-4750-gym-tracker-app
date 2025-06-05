USE railway;

-- SELECT statements that summarize data

-- Current Occupancy per Gym
SELECT GymID, COUNT(*) AS CurrentOccupancy
FROM CheckIn
WHERE CheckOutTime IS NULL
GROUP BY GymID;

-- Total Class Registrations per Class
SELECT ClassID, COUNT(*) AS TotalRegistrations
FROM ClassAttendance
GROUP BY ClassID;

-- Number of Appointments per Trainer
SELECT TrainerID, COUNT(*) AS AppointmentCount
FROM TrainerAppointment
GROUP BY TrainerID;


-- Peak Usage Times for Facilities
SELECT HOUR(TimeStamp) AS HourOfDay, COUNT(*) AS UsageCount
FROM FacilityUsage
GROUP BY HOUR(TimeStamp)
ORDER BY UsageCount DESC;

-- Average Class Duration (Minutes)
SELECT AVG(TIMESTAMPDIFF(MINUTE, StartTime, EndTime)) AS AvgClassDuration
FROM Class;

-- Check-In Count per Student
SELECT StudentID, COUNT(*) AS TotalCheckIns
FROM CheckIn
GROUP BY StudentID;

-- Most Popular Facilities
SELECT FacilityID, COUNT(*) AS UsageCount
FROM FacilityUsage
GROUP BY FacilityID
ORDER BY UsageCount DESC;

-- Trainer Availability Summary
SELECT TrainerID, COUNT(*) AS TotalSlots,
       MIN(StartTime) AS EarliestSlot,
       MAX(EndTime) AS LatestSlot
FROM TrainerAvailability
GROUP BY TrainerID;



