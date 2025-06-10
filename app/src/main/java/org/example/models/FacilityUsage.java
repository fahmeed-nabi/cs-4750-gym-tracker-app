package org.example.models;

import java.time.LocalDateTime;

public class FacilityUsage {
    private int usageId;
    private int facilityId;
    private int studentId;
    private LocalDateTime timestamp;

    public FacilityUsage(int usageId, int facilityId, int studentId, LocalDateTime timestamp) {
        this.usageId = usageId;
        this.facilityId = facilityId;
        this.studentId = studentId;
        this.timestamp = timestamp;
    }

    public int getUsageId() {
        return usageId;
    }

    public int getFacilityId() {
        return facilityId;
    }

    public int getStudentId() {
        return studentId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setUsageId(int usageId) {
        this.usageId = usageId;
    }

    public void setFacilityId(int facilityId) {
        this.facilityId = facilityId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
