package org.example.models;

public class TrainerAvailability {
    private int availabilityId;
    private int trainerId;
    private String dayOfWeek;
    private String startTime;
    private String endTime;

    public TrainerAvailability(int availabilityId, int trainerId, String dayOfWeek, String startTime, String endTime) {
        this.availabilityId = availabilityId;
        this.trainerId = trainerId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getAvailabilityId() {
        return availabilityId;
    }

    public void setAvailabilityId(int availabilityId) {
        this.availabilityId = availabilityId;
    }

    public int getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return dayOfWeek + ": " + startTime + " - " + endTime;
    }
}
