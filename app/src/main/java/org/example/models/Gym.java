package org.example.models;

public class Gym {
    private int gymId;
    private String name;
    private String location;
    private int maxOccupancy;

    public Gym(int gymId, String name, String location, int maxOccupancy) {
        this.gymId = gymId;
        this.name = name;
        this.location = location;
        this.maxOccupancy = maxOccupancy;
    }

    public int getGymId() {
        return gymId;
    }

    public void setGymId(int gymId) {
        this.gymId = gymId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getMaxOccupancy() {
        return maxOccupancy;
    }

    public void setMaxOccupancy(int maxOccupancy) {
        this.maxOccupancy = maxOccupancy;
    }

    @Override
    public String toString() {
        return name + " (" + location + "), Max: " + maxOccupancy;
    }
}
