package com.smartclassroom.model;

public class Course {
    private int    courseId;
    private String courseCode;
    private String courseName;
    private int    lecturerId;
    private String lecturerName; // joined display field

    public Course() {}

    public int    getCourseId()     { return courseId;     }
    public String getCourseCode()   { return courseCode;   }
    public String getCourseName()   { return courseName;   }
    public int    getLecturerId()   { return lecturerId;   }
    public String getLecturerName() { return lecturerName; }

    public void setCourseId(int v)       { courseId     = v; }
    public void setCourseCode(String v)  { courseCode   = v; }
    public void setCourseName(String v)  { courseName   = v; }
    public void setLecturerId(int v)     { lecturerId   = v; }
    public void setLecturerName(String v){ lecturerName = v; }

    @Override
    public String toString() {
        return String.format("ID:%-3d | %-10s | %-30s | Lecturer: %s",
                courseId, courseCode, courseName,
                lecturerName != null ? lecturerName : "ID#" + lecturerId);
    }
}
