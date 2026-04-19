package com.smartclassroom.model;

public class Notification {
    private int    notificationId;
    private int    userId;
    private String title;
    private String message;
    private int    isRead;
    private String createdAt;

    public Notification() {}

    public int    getNotificationId() { return notificationId; }
    public int    getUserId()         { return userId;         }
    public String getTitle()          { return title;          }
    public String getMessage()        { return message;        }
    public int    getIsRead()         { return isRead;         }
    public String getCreatedAt()      { return createdAt;      }

    public void setNotificationId(int v) { notificationId = v; }
    public void setUserId(int v)         { userId         = v; }
    public void setTitle(String v)       { title          = v; }
    public void setMessage(String v)     { message        = v; }
    public void setIsRead(int v)         { isRead         = v; }
    public void setCreatedAt(String v)   { createdAt      = v; }

    @Override
    public String toString() {
        String badge = isRead == 0 ? "● " : "  ";
        return badge + "[" + createdAt + "] " + title
                + (message != null && !message.isEmpty() ? " — " + message : "");
    }
}
