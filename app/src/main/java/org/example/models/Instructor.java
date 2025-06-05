package org.example.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Instructor {
    private final SimpleIntegerProperty instructorId;
    private final SimpleStringProperty name;
    private final SimpleStringProperty certification;
    private final SimpleStringProperty classAssignments;

    public Instructor(int instructorId, String name, String certification, String classAssignments) {
        this.instructorId = new SimpleIntegerProperty(instructorId);
        this.name = new SimpleStringProperty(name);
        this.certification = new SimpleStringProperty(certification);
        this.classAssignments = new SimpleStringProperty(classAssignments);
    }

    // Property methods for TableView bindings
    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty certificationProperty() {
        return certification;
    }

    public StringProperty classAssignmentsProperty() {
        return classAssignments;
    }

    public int getInstructorId() {
        return instructorId.get();
    }

    public String getName() {
        return name.get();
    }

    public String getCertification() {
        return certification.get();
    }

    public String getClassAssignments() {
        return classAssignments.get();
    }
}
