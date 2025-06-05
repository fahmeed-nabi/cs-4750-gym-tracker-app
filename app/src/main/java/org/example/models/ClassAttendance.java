package org.example.models;

import java.time.LocalDate;

public class ClassAttendance {
    private int classId;
    private int studentId;
    private LocalDate date;

    public ClassAttendance(int classId, int studentId, LocalDate date) {
        this.classId = classId;
        this.studentId = studentId;
        this.date = date;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Student " + studentId + " attended class " + classId + " on " + date;
    }
}
