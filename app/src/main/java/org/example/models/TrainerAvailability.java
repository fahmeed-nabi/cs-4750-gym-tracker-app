package org.example.models;

import javafx.beans.property.*;

public class TrainerAvailability {

    private final IntegerProperty availabilityId = new SimpleIntegerProperty();
    private final IntegerProperty trainerId = new SimpleIntegerProperty();
    private final StringProperty dayOfWeek = new SimpleStringProperty();
    private final StringProperty startTime = new SimpleStringProperty();
    private final StringProperty endTime = new SimpleStringProperty();

    public TrainerAvailability(int availabilityId, int trainerId, String dayOfWeek, String startTime, String endTime) {
        this.availabilityId.set(availabilityId);
        this.trainerId.set(trainerId);
        this.dayOfWeek.set(dayOfWeek);
        this.startTime.set(startTime);
        this.endTime.set(endTime);
    }

    public int getAvailabilityId() {
        return availabilityId.get();
    }

    public IntegerProperty availabilityIdProperty() {
        return availabilityId;
    }

    public int getTrainerId() {
        return trainerId.get();
    }

    public IntegerProperty trainerIdProperty() {
        return trainerId;
    }

    public String getDayOfWeek() {
        return dayOfWeek.get();
    }

    public StringProperty dayOfWeekProperty() {
        return dayOfWeek;
    }

    public String getStartTime() {
        return startTime.get();
    }

    public StringProperty startTimeProperty() {
        return startTime;
    }

    public String getEndTime() {
        return endTime.get();
    }

    public StringProperty endTimeProperty() {
        return endTime;
    }

    @Override
    public String toString() {
        return getDayOfWeek() + ": " + getStartTime() + " - " + getEndTime();
    }
}
