package org.example.models;

public class Facility {
    private int facilityId;
    private String location;
    private String name;
    private String facilityType;

    public Facility(int facilityId, String location, String name, String facilityType) {
        this.facilityId = facilityId;
        this.location = location;
        this.name = name;
        this.facilityType = facilityType;
    }

    public int getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(int facilityId) {
        this.facilityId = facilityId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    @Override
    public String toString() {
        return name + " (" + facilityType + ") - " + location;
    }
}
