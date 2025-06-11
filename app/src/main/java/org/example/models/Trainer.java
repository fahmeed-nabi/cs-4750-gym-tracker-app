package org.example.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Trainer {
    private final SimpleIntegerProperty trainerId;
    private final StringProperty name;
    private final StringProperty specialty;
    private final StringProperty availability;
    private final StringProperty firstName = new SimpleStringProperty();
    private final StringProperty lastName = new SimpleStringProperty();

    public Trainer(int trainerId, String name, String specialty, String availability) {
        this.trainerId = new SimpleIntegerProperty(trainerId);
        this.name = new SimpleStringProperty(name);
        this.specialty = new SimpleStringProperty(specialty);
        this.availability = new SimpleStringProperty(availability);
    }

    public int getTrainerId() {
        return trainerId.get();
    }

    public String getName() {
        return name.get();
    }

    public String getFirstName() {
        return firstName.get();
    }

    public String getLastName() {
        return lastName.get();
    }

    public String getSpecialty() {
        return specialty.get();
    }

    public String getAvailability() {
        return availability.get();
    }

    // JavaFX Property Methods
    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty specialtyProperty() {
        return specialty;
    }

    public StringProperty availabilityProperty() {
        return availability;
    }

    @Override
    public String toString() {
        return getName();
    }
}
