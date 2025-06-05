package org.example.models;

public class Student {
    private int studentId;
    private String name;
    private String email;
    private String role;

    public Student(int studentId, String name, String email, String role) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public int getStudentId() { return studentId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }

    public void setStudentId(int studentId) { this.studentId = studentId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return name + " (" + role + ")";
    }
}
