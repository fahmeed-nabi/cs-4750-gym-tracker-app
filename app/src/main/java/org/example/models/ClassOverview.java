package org.example.models;

import java.time.LocalDateTime;

public class ClassOverview {
    private ClassSession classSession;
    private String gymName;
    private Instructor instructor;

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

    @Override
    public String toString() {
        return classSession.getName() + " at " + gymName + " by " +
                instructor.getFirstName() + " " + instructor.getLastName();
    }
}
