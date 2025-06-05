package org.example.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Student {
    private final SimpleIntegerProperty studentId;
    private final StringProperty name;
    private final StringProperty username;
    private final StringProperty role;

    public Student(int studentId, String name, String username, String role) {
        this.studentId = new SimpleIntegerProperty(studentId);
        this.name = new SimpleStringProperty(name);
        this.username = new SimpleStringProperty(username);
        this.role = new SimpleStringProperty(role);
    }

    public int getStudentId() {
        return studentId.get();
    }

    public String getName() {
        return name.get();
    }

    public String getUsername() {
        return username.get();
    }

    public String getRole() {
        return role.get();
    }

    // JavaFX Property Methods
    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty studentIdProperty() {
        return new SimpleStringProperty(String.valueOf(studentId.get()));
    }

    public StringProperty roleProperty() {
        return role;
    }
}
