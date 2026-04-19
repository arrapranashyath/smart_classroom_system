package com.smartclassroom.model;

public class Announcement {
    private int    announcementId;
    private String title;
    private String content;
    private int    postedBy;
    private String targetRole;
    private String createdAt;
    // Joined
    private String posterName;

    public Announcement() {}

    public int    getAnnouncementId() { return announcementId; }
    public String getTitle()          { return title;          }
    public String getContent()        { return content;        }
    public int    getPostedBy()       { return postedBy;       }
    public String getTargetRole()     { return targetRole;     }
    public String getCreatedAt()      { return createdAt;      }
    public String getPosterName()     { return posterName;     }

    public void setAnnouncementId(int v)  { announcementId = v; }
    public void setTitle(String v)        { title          = v; }
    public void setContent(String v)      { content        = v; }
    public void setPostedBy(int v)        { postedBy       = v; }
    public void setTargetRole(String v)   { targetRole     = v; }
    public void setCreatedAt(String v)    { createdAt      = v; }
    public void setPosterName(String v)   { posterName     = v; }

    @Override
    public String toString() {
        return String.format("[%s] [→%s] %s — by %s\n   %s",
                createdAt, targetRole != null ? targetRole : "ALL",
                title,
                posterName != null ? posterName : "User#" + postedBy,
                content);
    }
}
