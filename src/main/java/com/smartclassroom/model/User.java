package com.smartclassroom.model;

public class User {
    private int    userId;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String role;
    private int    isActive;
    private String createdAt;

    public User() {}

    public User(int userId, String username, String fullName, String email, String role, int isActive) {
        this.userId   = userId;
        this.username = username;
        this.fullName = fullName;
        this.email    = email;
        this.role     = role;
        this.isActive = isActive;
    }

    public int    getUserId()   { return userId;   }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getEmail()    { return email;    }
    public String getRole()     { return role;     }
    public int    getIsActive() { return isActive; }
    public String getCreatedAt(){ return createdAt;}

    public void setUserId(int v)      { userId   = v; }
    public void setUsername(String v) { username = v; }
    public void setPassword(String v) { password = v; }
    public void setFullName(String v) { fullName = v; }
    public void setEmail(String v)    { email    = v; }
    public void setRole(String v)     { role     = v; }
    public void setIsActive(int v)    { isActive = v; }
    public void setCreatedAt(String v){ createdAt= v; }

    @Override
    public String toString() {
        return String.format("ID:%-3d | %-12s | %-11s | %-25s | %s",
                userId, username, role, fullName, email != null ? email : "-");
    }
}
