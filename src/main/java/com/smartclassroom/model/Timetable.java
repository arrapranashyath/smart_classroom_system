package com.smartclassroom.model;

public class Timetable {
    private int    slotId;
    private int    courseId;
    private int    roomId;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    // Joined display fields
    private String courseName;
    private String courseCode;
    private String roomName;
    private String lecturerName;

    public Timetable() {}

    public int    getSlotId()       { return slotId;       }
    public int    getCourseId()     { return courseId;     }
    public int    getRoomId()       { return roomId;       }
    public String getDayOfWeek()    { return dayOfWeek;    }
    public String getStartTime()    { return startTime;    }
    public String getEndTime()      { return endTime;      }
    public String getCourseName()   { return courseName;   }
    public String getCourseCode()   { return courseCode;   }
    public String getRoomName()     { return roomName;     }
    public String getLecturerName() { return lecturerName; }

    public void setSlotId(int v)        { slotId       = v; }
    public void setCourseId(int v)      { courseId     = v; }
    public void setRoomId(int v)        { roomId       = v; }
    public void setDayOfWeek(String v)  { dayOfWeek    = v; }
    public void setStartTime(String v)  { startTime    = v; }
    public void setEndTime(String v)    { endTime      = v; }
    public void setCourseName(String v) { courseName   = v; }
    public void setCourseCode(String v) { courseCode   = v; }
    public void setRoomName(String v)   { roomName     = v; }
    public void setLecturerName(String v){ lecturerName= v; }

    @Override
    public String toString() {
        return String.format("ID:%-3d | %-6s | %-30s | %s-%s | Room: %-10s | %s",
                slotId, dayOfWeek,
                (courseCode != null ? courseCode + " " : "") + (courseName != null ? courseName : ""),
                startTime != null ? startTime.substring(0,5) : "",
                endTime   != null ? endTime.substring(0,5)   : "",
                roomName  != null ? roomName : "Room#" + roomId,
                lecturerName != null ? lecturerName : "");
    }
}
