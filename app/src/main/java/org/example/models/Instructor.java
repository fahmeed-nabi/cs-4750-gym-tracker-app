package org.example.models;

public class Instructor {
    private int instructorId;
    private String name;
    private String certification;
    private String email;

    public Instructor(int instructorId, String name, String certification, String email) {
        this.instructorId = instructorId;
        this.name = name;
        this.certification = certification;
        this.email = email;
    }

    public int getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return name + " (" + certification + ")";
    }
}
