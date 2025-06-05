package org.example.models;

public class TrainerSpecialty {
    private int trainerId;
    private String specialty;

    public TrainerSpecialty(int trainerId, String specialty) {
        this.trainerId = trainerId;
        this.specialty = specialty;
    }

    public int getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    @Override
    public String toString() {
        return "TrainerID: " + trainerId + ", Specialty: " + specialty;
    }
}
