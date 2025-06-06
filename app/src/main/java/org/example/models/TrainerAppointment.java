package org.example.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class TrainerAppointment {
    private int appointmentId;
    private int studentId;
    private int trainerId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    public TrainerAppointment(int appointmentId, int studentId, int trainerId,
                              LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.appointmentId = appointmentId;
        this.studentId = studentId;
        this.trainerId = trainerId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters
    public int getAppointmentId() {
        return appointmentId;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getTrainerId() {
        return trainerId;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    // Setters
    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Appointment on " + date + " from " + startTime + " to " + endTime +
                " (Trainer ID: " + trainerId + ", Student ID: " + studentId + ")";
    }
}
