package org.example.models;

import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.LocalTime;

public class TrainerAppointment {

    private final IntegerProperty appointmentId = new SimpleIntegerProperty();
    private final IntegerProperty studentId = new SimpleIntegerProperty();
    private final IntegerProperty trainerId = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> startTime = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> endTime = new SimpleObjectProperty<>();
    private final StringProperty studentFirstName = new SimpleStringProperty();
    private final StringProperty studentLastName = new SimpleStringProperty();
    private final StringProperty locationName = new SimpleStringProperty();

    public TrainerAppointment(int appointmentId, int studentId, int trainerId,
                              LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.appointmentId.set(appointmentId);
        this.studentId.set(studentId);
        this.trainerId.set(trainerId);
        this.date.set(date);
        this.startTime.set(startTime);
        this.endTime.set(endTime);
    }

    public TrainerAppointment(int appointmentId, int studentId, int trainerId,
                              LocalDate date, LocalTime startTime, LocalTime endTime,
                              String studentFirstName, String studentLastName) {
        this(appointmentId, studentId, trainerId, date, startTime, endTime);
        this.studentFirstName.set(studentFirstName);
        this.studentLastName.set(studentLastName);
    }

    // Appointment ID
    public int getAppointmentId() {
        return appointmentId.get();
    }

    public void setAppointmentId(int id) {
        this.appointmentId.set(id);
    }

    public IntegerProperty appointmentIdProperty() {
        return appointmentId;
    }

    // Student ID
    public int getStudentId() {
        return studentId.get();
    }

    public void setStudentId(int id) {
        this.studentId.set(id);
    }

    public IntegerProperty studentIdProperty() {
        return studentId;
    }

    public String getLocationName() {
        return locationName.get();
    }
    public void setLocationName(String name) {
        locationName.set(name);
    }
    public StringProperty locationNameProperty() {
        return locationName;
    }

    // Trainer ID
    public int getTrainerId() {
        return trainerId.get();
    }

    public void setTrainerId(int id) {
        this.trainerId.set(id);
    }

    public IntegerProperty trainerIdProperty() {
        return trainerId;
    }

    // Date
    public LocalDate getDate() {
        return date.get();
    }

    public void setDate(LocalDate d) {
        this.date.set(d);
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    // Start Time
    public LocalTime getStartTime() {
        return startTime.get();
    }

    public void setStartTime(LocalTime t) {
        this.startTime.set(t);
    }

    public ObjectProperty<LocalTime> startTimeProperty() {
        return startTime;
    }

    // End Time
    public LocalTime getEndTime() {
        return endTime.get();
    }

    public void setEndTime(LocalTime t) {
        this.endTime.set(t);
    }

    public ObjectProperty<LocalTime> endTimeProperty() {
        return endTime;
    }

    public String getStudentFirstName() {
        return studentFirstName.get();
    }
    public void setStudentFirstName(String firstName) {
        this.studentFirstName.set(firstName);
    }
    public StringProperty studentFirstNameProperty() {
        return studentFirstName;
    }

    public String getStudentLastName() {
        return studentLastName.get();
    }
    public void setStudentLastName(String lastName) {
        this.studentLastName.set(lastName);
    }
    public StringProperty studentLastNameProperty() {
        return studentLastName;
    }

    @Override
    public String toString() {
        return "Appointment on " + getDate() + " from " + getStartTime() + " to " + getEndTime() +
                " (Trainer ID: " + getTrainerId() + ", Student ID: " + getStudentId() + ")";
    }
}
