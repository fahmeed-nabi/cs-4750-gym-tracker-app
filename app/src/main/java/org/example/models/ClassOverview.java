package org.example.models;

import java.time.format.DateTimeFormatter;

public class ClassOverview {
    private final ClassSession classSession;
    private final String gymName;
    private final Instructor instructor;

    public ClassOverview(ClassSession classSession, String gymName, Instructor instructor) {
        this.classSession = classSession;
        this.gymName = gymName;
        this.instructor = instructor;
    }

    public ClassSession getClassSession() {
        return classSession;
    }

    public String getGymName() {
        return gymName;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    // 🧾 Convenience Getters for TableView
    public String getClassName() {
        return classSession.getName();
    }

    public String getTimeRange() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, HH:mm");
        return classSession.getStartTime().format(fmt) + " - " +
                classSession.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public String getInstructorFullName() {
        return instructor.getFirstName() + " " + instructor.getLastName();
    }

    @Override
    public String toString() {
        return getClassName() + " at " + gymName + " by " + getInstructorFullName();
    }
}
