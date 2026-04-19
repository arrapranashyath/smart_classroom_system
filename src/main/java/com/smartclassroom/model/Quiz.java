package com.smartclassroom.model;

public class Quiz {
    private int    quizId;
    private int    courseId;
    private String title;
    private int    createdBy;
    private int    totalMarks;
    private String scheduledDate;
    private String createdAt;
    // Joined
    private String courseName;
    private String createdByName;

    public Quiz() {}

    public int    getQuizId()         { return quizId;         }
    public int    getCourseId()       { return courseId;       }
    public String getTitle()          { return title;          }
    public int    getCreatedBy()      { return createdBy;      }
    public int    getTotalMarks()     { return totalMarks;     }
    public String getScheduledDate()  { return scheduledDate;  }
    public String getCreatedAt()      { return createdAt;      }
    public String getCourseName()     { return courseName;     }
    public String getCreatedByName()  { return createdByName;  }

    public void setQuizId(int v)          { quizId         = v; }
    public void setCourseId(int v)        { courseId       = v; }
    public void setTitle(String v)        { title          = v; }
    public void setCreatedBy(int v)       { createdBy      = v; }
    public void setTotalMarks(int v)      { totalMarks     = v; }
    public void setScheduledDate(String v){ scheduledDate  = v; }
    public void setCreatedAt(String v)    { createdAt      = v; }
    public void setCourseName(String v)   { courseName     = v; }
    public void setCreatedByName(String v){ createdByName  = v; }

    @Override
    public String toString() {
        String dateStr = scheduledDate != null ? " | Due: " + scheduledDate : "";
        return String.format("ID:%-3d | %-30s | Marks:%-3d | Course: %-20s%s",
                quizId, title, totalMarks,
                courseName != null ? courseName : "Course#" + courseId,
                dateStr);
    }
}
