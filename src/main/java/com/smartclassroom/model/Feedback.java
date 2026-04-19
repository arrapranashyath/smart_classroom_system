package com.smartclassroom.model;

public class Feedback {
    private int    feedbackId;
    private int    courseId;
    private int    rating;       // 1–5
    private String comments;
    private String submittedAt;
    // Joined
    private String courseName;

    public Feedback() {}

    public int    getFeedbackId()  { return feedbackId;  }
    public int    getCourseId()    { return courseId;    }
    public int    getRating()      { return rating;      }
    public String getComments()    { return comments;    }
    public String getSubmittedAt() { return submittedAt; }
    public String getCourseName()  { return courseName;  }

    public void setFeedbackId(int v)    { feedbackId  = v; }
    public void setCourseId(int v)      { courseId    = v; }
    public void setRating(int v)        { rating      = v; }
    public void setComments(String v)   { comments    = v; }
    public void setSubmittedAt(String v){ submittedAt = v; }
    public void setCourseName(String v) { courseName  = v; }

    public String ratingStars() {
        return "★".repeat(rating) + "☆".repeat(5 - rating);
    }

    @Override
    public String toString() {
        return String.format("ID:%-3d | Course: %-25s | %s | %s",
                feedbackId,
                courseName != null ? courseName : "Course#" + courseId,
                ratingStars(), submittedAt);
    }
}
