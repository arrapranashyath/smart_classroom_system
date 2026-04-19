package com.smartclassroom.model;

public class Attendance {
    private int    attendanceId;
    private int    studentId;
    private int    courseId;
    private String date;
    private String status;
    private int    markedBy;
    // Joined display fields
    private String studentName;
    private String courseName;
    private String markedByName;

    public Attendance() {}

    public int    getAttendanceId() { return attendanceId; }
    public int    getStudentId()    { return studentId;    }
    public int    getCourseId()     { return courseId;     }
    public String getDate()         { return date;         }
    public String getStatus()       { return status;       }
    public int    getMarkedBy()     { return markedBy;     }
    public String getStudentName()  { return studentName;  }
    public String getCourseName()   { return courseName;   }
    public String getMarkedByName() { return markedByName; }

    public void setAttendanceId(int v)    { attendanceId = v; }
    public void setStudentId(int v)       { studentId    = v; }
    public void setCourseId(int v)        { courseId     = v; }
    public void setDate(String v)         { date         = v; }
    public void setStatus(String v)       { status       = v; }
    public void setMarkedBy(int v)        { markedBy     = v; }
    public void setStudentName(String v)  { studentName  = v; }
    public void setCourseName(String v)   { courseName   = v; }
    public void setMarkedByName(String v) { markedByName = v; }

    @Override
    public String toString() {
        return String.format("%-25s | %-30s | %-10s | %s",
                studentName != null ? studentName : "Student#" + studentId,
                courseName  != null ? courseName  : "Course#"  + courseId,
                date, status);
    }
}
