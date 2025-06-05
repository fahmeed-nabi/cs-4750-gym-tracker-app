package org.example.models;

public class Trainer {
    private int trainerId;
    private String name;
    private String specialty;
    private String email;

    public Trainer(int trainerId, String name, String specialty, String email) {
        this.trainerId = trainerId;
        this.name = name;
        this.specialty = specialty;
        this.email = email;
    }

    public int getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return name + " (" + specialty + ")";
    }
}
