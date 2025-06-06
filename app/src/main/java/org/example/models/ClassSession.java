package org.example.models;

import java.time.LocalDateTime;

public class ClassSession {
    private int classId;
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int gymId;
    private int instructorId;

    public ClassSession(int classId, String name, LocalDateTime startTime,
                        LocalDateTime endTime, int gymId, int instructorId) {
        this.classId = classId;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.gymId = gymId;
        this.instructorId = instructorId;
    }

    public int getClassId() { return classId; }
    public String getName() { return name; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public int getGymId() { return gymId; }
    public int getInstructorId() { return instructorId; }

    public void setClassId(int classId) { this.classId = classId; }
    public void setName(String name) { this.name = name; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public void setGymId(int gymId) { this.gymId = gymId; }
    public void setInstructorId(int instructorId) { this.instructorId = instructorId; }

    @Override
    public String toString() {
        return name + " (" + startTime.toLocalTime() + " - " + endTime.toLocalTime() + ")";
    }
}
