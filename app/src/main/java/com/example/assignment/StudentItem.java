package com.example.assignment;

public class StudentItem {
    private String studentId;  // Add this field
    private String roll;
    private String name;
    private String status;

    public StudentItem() {
        // Default constructor required for Firebase
    }

    public StudentItem(String roll, String name) {
        this.roll = roll;
        this.name = name;
        this.status = "";
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

