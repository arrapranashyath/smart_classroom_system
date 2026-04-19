package com.smartclassroom.dao;

import com.smartclassroom.model.Feedback;
import com.smartclassroom.util.DBConnection;
import com.smartclassroom.view.ConsoleView;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO {

    // ── Submit anonymous feedback ──────────────────────────────
    // NOTE: NO student_id or user_id is ever stored — fully anonymous.
    public boolean insert(int courseId, int rating, String comments) {
        String sql = "INSERT INTO feedback (course_id, rating, comments) VALUES(?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.setInt(2, rating);
            ps.setString(3, comments);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); return false; }
    }

    // ── Feedback for a specific course (lecturer view) ─────────
    public List<Feedback> findByCourse(int courseId) {
        String sql = "SELECT f.*, c.course_name FROM feedback f " +
                     "JOIN courses c ON f.course_id=c.course_id " +
                     "WHERE f.course_id=? ORDER BY f.submitted_at DESC";
        List<Feedback> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
        return list;
    }

    // ── All feedback (admin view) ──────────────────────────────
    public List<Feedback> findAll() {
        String sql = "SELECT f.*, c.course_name FROM feedback f " +
                     "JOIN courses c ON f.course_id=c.course_id ORDER BY f.submitted_at DESC";
        List<Feedback> list = new ArrayList<>();
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
        return list;
    }

    // ── Average rating per course (lecturer summary) ───────────
    public void printRatingSummary(int lecturerId) {
        String sql =
            "SELECT c.course_name, COUNT(f.feedback_id) AS total, " +
            "ROUND(AVG(f.rating),2) AS avg_rating " +
            "FROM feedback f JOIN courses c ON f.course_id=c.course_id " +
            "WHERE c.lecturer_id=? GROUP BY c.course_name";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            ResultSet rs = ps.executeQuery();
            System.out.printf("  %-30s | %-8s | %s%n", "Course", "Count", "Avg Rating");
            ConsoleView.printLine();
            while (rs.next()) {
                double avg = rs.getDouble("avg_rating");
                String stars = "★".repeat((int) Math.round(avg)) +
                               "☆".repeat(5 - (int) Math.round(avg));
                System.out.printf("  %-30s | %-8d | %.2f %s%n",
                        rs.getString("course_name"), rs.getInt("total"), avg, stars);
            }
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
    }

    private Feedback mapRow(ResultSet rs) throws SQLException {
        Feedback f = new Feedback();
        f.setFeedbackId(rs.getInt("feedback_id"));
        f.setCourseId(rs.getInt("course_id"));
        f.setRating(rs.getInt("rating"));
        f.setComments(rs.getString("comments"));
        f.setSubmittedAt(rs.getString("submitted_at"));
        f.setCourseName(rs.getString("course_name"));
        return f;
    }
}
