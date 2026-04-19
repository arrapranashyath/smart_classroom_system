package com.smartclassroom.dao;

import com.smartclassroom.util.DBConnection;
import com.smartclassroom.view.ConsoleView;

import java.sql.*;

/**
 * ReportDAO — raw SQL for all admin/lecturer reports.
 * Each method prints a formatted table directly.
 */
public class ReportDAO {

    // ── 1. Attendance summary per course (admin view) ──────────
    public void attendanceSummaryByCourse() {
        String sql =
            "SELECT c.course_code, c.course_name, " +
            "COUNT(DISTINCT a.student_id) AS students, " +
            "COUNT(a.attendance_id)       AS total_records, " +
            "SUM(CASE WHEN a.status IN ('PRESENT','LATE') THEN 1 ELSE 0 END) AS present_count, " +
            "ROUND(SUM(CASE WHEN a.status IN ('PRESENT','LATE') THEN 1 ELSE 0 END) " +
            "     * 100.0 / NULLIF(COUNT(a.attendance_id),0), 1) AS avg_pct " +
            "FROM courses c " +
            "LEFT JOIN attendance a ON c.course_id = a.course_id " +
            "GROUP BY c.course_id ORDER BY avg_pct DESC";
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            System.out.printf("  %-8s | %-32s | %-8s | %-6s | %-7s | %s%n",
                    "Code","Course","Students","Records","Present","Avg %");
            ConsoleView.printLine();
            boolean found = false;
            while (rs.next()) {
                found = true;
                double pct = rs.getDouble("avg_pct");
                System.out.printf("  %-8s | %-32s | %-8d | %-6d | %-7d | %s%n",
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getInt("students"),
                        rs.getInt("total_records"),
                        rs.getInt("present_count"),
                        ConsoleView.attendanceBar(pct));
            }
            if (!found) ConsoleView.info("No attendance data recorded yet.");
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
    }

    // ── 2. At-risk students (attendance < 75%) ─────────────────
    public void atRiskStudents() {
        String sql =
            "SELECT u.full_name, c.course_name, " +
            "COUNT(*) AS total, " +
            "SUM(CASE WHEN a.status IN ('PRESENT','LATE') THEN 1 ELSE 0 END) AS attended, " +
            "ROUND(SUM(CASE WHEN a.status IN ('PRESENT','LATE') THEN 1 ELSE 0 END) " +
            "     * 100.0 / NULLIF(COUNT(*),0), 1) AS pct " +
            "FROM attendance a " +
            "JOIN users u   ON a.student_id = u.user_id " +
            "JOIN courses c ON a.course_id  = c.course_id " +
            "GROUP BY a.student_id, a.course_id " +
            "HAVING pct < 75 " +
            "ORDER BY pct ASC";
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            System.out.printf("  %-25s | %-30s | %-6s | %-8s | %s%n",
                    "Student","Course","Total","Attended","Attendance %");
            ConsoleView.printLine();
            boolean found = false;
            while (rs.next()) {
                found = true;
                double pct = rs.getDouble("pct");
                System.out.printf("  %-25s | %-30s | %-6d | %-8d | %s  [%s]%n",
                        rs.getString("full_name"),
                        rs.getString("course_name"),
                        rs.getInt("total"),
                        rs.getInt("attended"),
                        String.format("%.1f%%", pct),
                        pct < 60 ? "CRITICAL" : "WARNING");
            }
            if (!found) ConsoleView.info("No at-risk students found. Everyone is above 75%.");
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
    }

    // ── 3. Quiz performance summary per course ─────────────────
    public void quizPerformanceSummary() {
        String sql =
            "SELECT c.course_name, q.title, q.total_marks, " +
            "COUNT(qs.submission_id)                     AS submissions, " +
            "ROUND(AVG(qs.score), 1)                     AS avg_score, " +
            "MAX(qs.score)                               AS top_score, " +
            "MIN(qs.score)                               AS low_score " +
            "FROM quizzes q " +
            "JOIN courses c ON q.course_id = c.course_id " +
            "LEFT JOIN quiz_submissions qs ON q.quiz_id = qs.quiz_id " +
            "GROUP BY q.quiz_id ORDER BY c.course_name, q.created_at";
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            System.out.printf("  %-28s | %-28s | %-6s | %-5s | %-5s | %-5s | %s%n",
                    "Course","Quiz","Marks","Sub","Avg","Top","Low");
            ConsoleView.printLine();
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("  %-28s | %-28s | %-6d | %-5d | %-5.1f | %-5d | %d%n",
                        rs.getString("course_name"),
                        rs.getString("title"),
                        rs.getInt("total_marks"),
                        rs.getInt("submissions"),
                        rs.getDouble("avg_score"),
                        rs.getInt("top_score"),
                        rs.getInt("low_score"));
            }
            if (!found) ConsoleView.info("No quiz submissions yet.");
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
    }

    // ── 4. Issue resolution report ─────────────────────────────
    public void issueResolutionReport() {
        String sql =
            "SELECT " +
            "COUNT(*)                                              AS total, " +
            "SUM(CASE WHEN status='OPEN'        THEN 1 ELSE 0 END) AS open_count, " +
            "SUM(CASE WHEN status='IN_PROGRESS' THEN 1 ELSE 0 END) AS inprog, " +
            "SUM(CASE WHEN status='RESOLVED'    THEN 1 ELSE 0 END) AS resolved, " +
            "SUM(CASE WHEN status='CLOSED'      THEN 1 ELSE 0 END) AS closed, " +
            "SUM(CASE WHEN priority='HIGH'      THEN 1 ELSE 0 END) AS high_priority " +
            "FROM issues";
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                int total    = rs.getInt("total");
                int resolved = rs.getInt("resolved") + rs.getInt("closed");
                double resPct = total > 0 ? resolved * 100.0 / total : 0;
                System.out.println("  Total Issues     : " + total);
                System.out.println("  Open             : " + rs.getInt("open_count"));
                System.out.println("  In Progress      : " + rs.getInt("inprog"));
                System.out.println("  Resolved/Closed  : " + resolved);
                System.out.println("  High Priority    : " + rs.getInt("high_priority"));
                System.out.printf ("  Resolution Rate  : %.1f%%%n", resPct);
                System.out.println("  Progress         : " + ConsoleView.attendanceBar(resPct));
            }
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }

        // Per-room breakdown
        String roomSql =
            "SELECT r.room_name, COUNT(*) AS total, " +
            "SUM(CASE WHEN i.status IN ('RESOLVED','CLOSED') THEN 1 ELSE 0 END) AS resolved " +
            "FROM issues i JOIN rooms r ON i.room_id = r.room_id " +
            "GROUP BY r.room_id ORDER BY total DESC";
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(roomSql);
            System.out.println();
            System.out.printf("  %-15s | %-6s | %s%n", "Room","Total","Resolved");
            ConsoleView.printLine();
            while (rs.next()) {
                System.out.printf("  %-15s | %-6d | %d%n",
                        rs.getString("room_name"),
                        rs.getInt("total"),
                        rs.getInt("resolved"));
            }
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
    }

    // ── 5. Feedback rating report per course ───────────────────
    public void feedbackRatingReport() {
        String sql =
            "SELECT c.course_code, c.course_name, u.full_name AS lecturer, " +
            "COUNT(f.feedback_id)     AS total_feedback, " +
            "ROUND(AVG(f.rating), 2)  AS avg_rating, " +
            "SUM(CASE WHEN f.rating=5 THEN 1 ELSE 0 END) AS five_star, " +
            "SUM(CASE WHEN f.rating=1 THEN 1 ELSE 0 END) AS one_star " +
            "FROM courses c " +
            "LEFT JOIN feedback f ON c.course_id  = f.course_id " +
            "LEFT JOIN users u    ON c.lecturer_id = u.user_id " +
            "GROUP BY c.course_id ORDER BY avg_rating DESC";
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            System.out.printf("  %-8s | %-28s | %-20s | %-5s | %-6s | %s%n",
                    "Code","Course","Lecturer","Count","Avg","Stars");
            ConsoleView.printLine();
            boolean found = false;
            while (rs.next()) {
                found = true;
                double avg = rs.getDouble("avg_rating");
                int rounded = (int) Math.round(avg);
                String stars = "★".repeat(Math.max(0, rounded)) +
                               "☆".repeat(Math.max(0, 5 - rounded));
                System.out.printf("  %-8s | %-28s | %-20s | %-5d | %-6.2f | %s%n",
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getString("lecturer") != null ? rs.getString("lecturer") : "N/A",
                        rs.getInt("total_feedback"),
                        avg, stars);
            }
            if (!found) ConsoleView.info("No feedback submitted yet.");
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
    }

    // ── 6. Technician workload report ──────────────────────────
    public void technicianWorkloadReport() {
        String sql =
            "SELECT u.full_name, " +
            "COUNT(i.issue_id)                                          AS total_assigned, " +
            "SUM(CASE WHEN i.status='OPEN'        THEN 1 ELSE 0 END)   AS open_issues, " +
            "SUM(CASE WHEN i.status='IN_PROGRESS' THEN 1 ELSE 0 END)   AS in_progress, " +
            "SUM(CASE WHEN i.status IN ('RESOLVED','CLOSED') THEN 1 ELSE 0 END) AS resolved, " +
            "SUM(CASE WHEN i.priority='HIGH'      THEN 1 ELSE 0 END)   AS high_priority " +
            "FROM users u " +
            "LEFT JOIN issues i ON u.user_id = i.assigned_to " +
            "WHERE u.role = 'TECHNICIAN' " +
            "GROUP BY u.user_id ORDER BY total_assigned DESC";
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            System.out.printf("  %-22s | %-8s | %-5s | %-5s | %-8s | %s%n",
                    "Technician","Assigned","Open","InPrg","Resolved","High Pri");
            ConsoleView.printLine();
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("  %-22s | %-8d | %-5d | %-5d | %-8d | %d%n",
                        rs.getString("full_name"),
                        rs.getInt("total_assigned"),
                        rs.getInt("open_issues"),
                        rs.getInt("in_progress"),
                        rs.getInt("resolved"),
                        rs.getInt("high_priority"));
            }
            if (!found) ConsoleView.info("No technicians found.");
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
    }

    // ── 7. Course enrollment report ────────────────────────────
    public void courseEnrollmentReport() {
        String sql =
            "SELECT c.course_code, c.course_name, u.full_name AS lecturer, " +
            "COUNT(e.enrollment_id) AS enrolled " +
            "FROM courses c " +
            "LEFT JOIN enrollments e ON c.course_id  = e.course_id " +
            "LEFT JOIN users u       ON c.lecturer_id = u.user_id " +
            "GROUP BY c.course_id ORDER BY enrolled DESC";
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            System.out.printf("  %-8s | %-32s | %-22s | %s%n",
                    "Code","Course","Lecturer","Enrolled");
            ConsoleView.printLine();
            while (rs.next()) {
                System.out.printf("  %-8s | %-32s | %-22s | %d%n",
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getString("lecturer") != null ? rs.getString("lecturer") : "Unassigned",
                        rs.getInt("enrolled"));
            }
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
    }

    // ── 8. Student full report (one student, all modules) ──────
    public void fullStudentReport(int studentId) {
        // Basic info
        String infoSql = "SELECT full_name, email FROM users WHERE user_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(infoSql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("  Name  : " + rs.getString("full_name"));
                System.out.println("  Email : " + rs.getString("email"));
            }
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }

        // Courses enrolled
        System.out.println();
        System.out.println("  ENROLLED COURSES:");
        String courseSql =
            "SELECT c.course_code, c.course_name FROM enrollments e " +
            "JOIN courses c ON e.course_id=c.course_id WHERE e.student_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(courseSql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                System.out.println("    " + rs.getString("course_code") +
                                   " — " + rs.getString("course_name"));
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }

        // Attendance per course
        System.out.println();
        System.out.println("  ATTENDANCE:");
        String attSql =
            "SELECT c.course_name, COUNT(*) AS total, " +
            "SUM(CASE WHEN a.status IN ('PRESENT','LATE') THEN 1 ELSE 0 END) AS attended " +
            "FROM attendance a JOIN courses c ON a.course_id=c.course_id " +
            "WHERE a.student_id=? GROUP BY a.course_id";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(attSql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int t = rs.getInt("total"), a = rs.getInt("attended");
                double pct = t > 0 ? a * 100.0 / t : 0;
                System.out.printf("    %-30s | %s  [%s]%n",
                        rs.getString("course_name"),
                        ConsoleView.attendanceBar(pct),
                        ConsoleView.attendanceStatus(pct));
            }
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }

        // Quiz scores
        System.out.println();
        System.out.println("  QUIZ SCORES:");
        String quizSql =
            "SELECT q.title, qs.score, q.total_marks " +
            "FROM quiz_submissions qs JOIN quizzes q ON qs.quiz_id=q.quiz_id " +
            "WHERE qs.student_id=? ORDER BY qs.submitted_at DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(quizSql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.printf("    %-35s : %d / %d%n",
                        rs.getString("title"),
                        rs.getInt("score"), rs.getInt("total_marks"));
            }
            if (!any) System.out.println("    No quiz submissions yet.");
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
    }

    // ── 9. Lecturer summary report ─────────────────────────────
    public void lecturerSummaryReport(int lecturerId) {
        String infoSql = "SELECT full_name FROM users WHERE user_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(infoSql)) {
            ps.setInt(1, lecturerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) System.out.println("  Lecturer: " + rs.getString("full_name"));
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }

        // Courses taught
        System.out.println();
        System.out.println("  COURSES TAUGHT:");
        String cSql =
            "SELECT c.course_code, c.course_name, COUNT(e.enrollment_id) AS enrolled " +
            "FROM courses c LEFT JOIN enrollments e ON c.course_id=e.course_id " +
            "WHERE c.lecturer_id=? GROUP BY c.course_id";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(cSql)) {
            ps.setInt(1, lecturerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                System.out.printf("    %-10s | %-30s | %d students%n",
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getInt("enrolled"));
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }

        // Quizzes created
        System.out.println();
        System.out.println("  QUIZZES CREATED:");
        String qSql =
            "SELECT q.title, COUNT(qs.submission_id) AS subs, " +
            "ROUND(AVG(qs.score),1) AS avg " +
            "FROM quizzes q LEFT JOIN quiz_submissions qs ON q.quiz_id=qs.quiz_id " +
            "WHERE q.created_by=? GROUP BY q.quiz_id";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(qSql)) {
            ps.setInt(1, lecturerId);
            ResultSet rs = ps.executeQuery();
            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.printf("    %-35s | %d submissions | Avg: %.1f%n",
                        rs.getString("title"),
                        rs.getInt("subs"),
                        rs.getDouble("avg"));
            }
            if (!any) System.out.println("    No quizzes created yet.");
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }

        // Feedback summary
        System.out.println();
        System.out.println("  FEEDBACK RATINGS:");
        String fSql =
            "SELECT c.course_name, COUNT(f.feedback_id) AS count, " +
            "ROUND(AVG(f.rating),2) AS avg " +
            "FROM courses c LEFT JOIN feedback f ON c.course_id=f.course_id " +
            "WHERE c.lecturer_id=? GROUP BY c.course_id";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(fSql)) {
            ps.setInt(1, lecturerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                double avg = rs.getDouble("avg");
                int rounded = (int) Math.round(avg);
                System.out.printf("    %-30s | %d responses | %s (%.2f/5)%n",
                        rs.getString("course_name"),
                        rs.getInt("count"),
                        "★".repeat(Math.max(0,rounded)) + "☆".repeat(Math.max(0,5-rounded)),
                        avg);
            }
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
    }
}
