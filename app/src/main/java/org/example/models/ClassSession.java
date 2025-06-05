package org.example.models;

public class ClassSession {
    private int classId;
    private String name;
    private String classType;
    private String description;

    public ClassSession(int classId, String name, String classType, String description) {
        this.classId = classId;
        this.name = name;
        this.classType = classType;
        this.description = description;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name + " (" + classType + ")";
    }
}
