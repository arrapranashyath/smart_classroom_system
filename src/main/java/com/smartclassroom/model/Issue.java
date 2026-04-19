package com.smartclassroom.model;

public class Issue {
    private int    issueId;
    private String title;
    private String description;
    private int    reportedBy;
    private int    assignedTo;
    private int    roomId;
    private String status;
    private String priority;
    private String createdAt;
    private String resolvedAt;
    // Joined
    private String reporterName;
    private String assigneeName;
    private String roomName;

    public Issue() {}

    public int    getIssueId()     { return issueId;     }
    public String getTitle()       { return title;       }
    public String getDescription() { return description; }
    public int    getReportedBy()  { return reportedBy;  }
    public int    getAssignedTo()  { return assignedTo;  }
    public int    getRoomId()      { return roomId;      }
    public String getStatus()      { return status;      }
    public String getPriority()    { return priority;    }
    public String getCreatedAt()   { return createdAt;   }
    public String getResolvedAt()  { return resolvedAt;  }
    public String getReporterName(){ return reporterName;}
    public String getAssigneeName(){ return assigneeName;}
    public String getRoomName()    { return roomName;    }

    public void setIssueId(int v)       { issueId     = v; }
    public void setTitle(String v)      { title       = v; }
    public void setDescription(String v){ description = v; }
    public void setReportedBy(int v)    { reportedBy  = v; }
    public void setAssignedTo(int v)    { assignedTo  = v; }
    public void setRoomId(int v)        { roomId      = v; }
    public void setStatus(String v)     { status      = v; }
    public void setPriority(String v)   { priority    = v; }
    public void setCreatedAt(String v)  { createdAt   = v; }
    public void setResolvedAt(String v) { resolvedAt  = v; }
    public void setReporterName(String v){ reporterName= v;}
    public void setAssigneeName(String v){ assigneeName= v;}
    public void setRoomName(String v)   { roomName    = v; }

    @Override
    public String toString() {
        return String.format("ID:%-3d | %-25s | Room:%-8s | %-9s | %-11s | By: %s",
                issueId, title,
                roomName     != null ? roomName     : "Room#" + roomId,
                priority, status,
                reporterName != null ? reporterName : "User#" + reportedBy);
    }
}
