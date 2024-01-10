//package com.example.assignment;
//
//public class ClassItem {
//    private String classId;  // Add a property for the unique identifier
//    private String className;
//    private String subjectName;
//    private String classDate;
//
//    public ClassItem() {
//        // Default constructor required for calls to DataSnapshot.getValue(ClassItem.class)
//    }
//
//    public ClassItem(String classId, String className, String subjectName) {
//        this.classId = classId;
//        this.className = className;
//        this.subjectName = subjectName;
//    }
//
//    public String getClassId() {
//        return classId;
//    }
//
//    public void setClassId(String classId) {
//        this.classId = classId;
//    }
//
//    public String getClassName() {
//        return className;
//    }
//
//    public void setClassName(String className) {
//        this.className = className;
//    }
//
//    public String getSubjectName() {
//        return subjectName;
//    }
//
//    public void setSubjectName(String subjectName) {
//        this.subjectName = subjectName;
//    }
//
//    public String getClassDate() { return classDate; }
//
//    public void setClassDate(String classDate) { this.classDate = classDate;}
//}

package com.example.assignment;

public class ClassItem {
    private String classId;
    private String className;
    private String subjectName;
    private String classDate;

    // Default constructor required for calls to DataSnapshot.getValue(ClassItem.class)
    public ClassItem() {
    }

    // Constructor with classId, className, subjectName, and classDate
    public ClassItem(String classId, String className, String subjectName, String classDate) {
        this.classId = classId;
        this.className = className;
        this.subjectName = subjectName;
        this.classDate = classDate;
    }

    // Getters and setters for classId, className, subjectName, and classDate

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getClassDate() {
        return classDate;
    }

    public void setClassDate(String classDate) {
        this.classDate = classDate;
    }
}