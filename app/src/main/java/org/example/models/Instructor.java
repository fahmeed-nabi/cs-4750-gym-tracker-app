package org.example.models;

public class Instructor {
    private int instructorId;
    private String firstName;
    private String lastName;
    private String email;
    private String certification;
    private String focusArea;

    public Instructor(int instructorId, String firstName, String lastName,
                      String email, String certification, String focusArea) {
        this.instructorId = instructorId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.certification = certification;
        this.focusArea = focusArea;
    }

    // Getters
    public int getInstructorId() {
        return instructorId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getCertification() {
        return certification;
    }

    public String getFocusArea() {
        return focusArea;
    }

    // Setters
    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public void setFocusArea(String focusArea) {
        this.focusArea = focusArea;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + focusArea + ")";
    }
}
