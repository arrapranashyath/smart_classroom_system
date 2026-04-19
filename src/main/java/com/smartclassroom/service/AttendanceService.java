package com.smartclassroom.service;

import com.smartclassroom.dao.AttendanceDAO;
import com.smartclassroom.dao.CourseDAO;
import com.smartclassroom.model.Attendance;
import com.smartclassroom.model.Course;
import com.smartclassroom.util.InputHelper;
import com.smartclassroom.view.ConsoleView;

import java.time.LocalDate;
import java.util.List;

/**
 * AttendanceService — mark, view, analytics.
 */
public class AttendanceService {

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final CourseDAO     courseDAO     = new CourseDAO();

    // ── Lecturer marks attendance for all enrolled students ────
    public void markAttendance(int courseId, int markedBy) {
        List<Course> courses = courseDAO.findAll();
        // Verify course exists
        Course target = courses.stream()
                .filter(c -> c.getCourseId() == courseId)
                .findFirst().orElse(null);
        if (target == null) { ConsoleView.error("Course not found."); return; }

        String date = LocalDate.now().toString();
        ConsoleView.printSection("Mark Attendance — " + target.getCourseName() + " | " + date);
        System.out.println("  Enter: P=Present  A=Absent  L=Late");
        ConsoleView.printLine();

        // Get enrolled students
        courseDAO.printEnrollments(courseId);
        System.out.println();

        // Get student list for iteration
        List<com.smartclassroom.model.User> students =
                new com.smartclassroom.dao.UserDAO().findByRole("STUDENT");

        // Filter to only enrolled students
        String enrollSql = "SELECT student_id FROM enrollments WHERE course_id=" + courseId;
        java.util.Set<Integer> enrolledIds = new java.util.HashSet<>();
        try {
            java.sql.ResultSet rs = com.smartclassroom.util.DBConnection
                    .getConnection().createStatement().executeQuery(enrollSql);
            while (rs.next()) enrolledIds.add(rs.getInt("student_id"));
        } catch (java.sql.SQLException e) { ConsoleView.error(e.getMessage()); return; }

        int count = 0;
        for (com.smartclassroom.model.User s : students) {
            if (!enrolledIds.contains(s.getUserId())) continue;
            System.out.print("  [" + s.getFullName() + "] (P/A/L): ");
            String input = InputHelper.getScanner().nextLine().trim().toUpperCase();
            String status;
            switch (input) {
                case "P": status = "PRESENT"; break;
                case "L": status = "LATE";    break;
                default:  status = "ABSENT";  break;
            }
            attendanceDAO.upsert(s.getUserId(), courseId, date, status, markedBy);
            count++;
        }
        ConsoleView.success("Attendance marked for " + count + " student(s).");
    }

    // ── Student views their own attendance ────────────────────
    public void printMyAttendance(int studentId) {
        ConsoleView.printSection("My Attendance Record");
        List<Attendance> list = attendanceDAO.findByStudent(studentId);
        if (list.isEmpty()) { ConsoleView.info("No records found."); return; }
        System.out.printf("  %-30s | %-12s | %s%n", "Course", "Date", "Status");
        ConsoleView.printLine();
        for (Attendance a : list) {
            System.out.printf("  %-30s | %-12s | %s%n",
                    a.getCourseName(), a.getDate(), a.getStatus());
        }
    }

    // ── Lecturer views attendance for their course ─────────────
    public void printCourseAttendance(int courseId) {
        ConsoleView.printSection("Course Attendance");
        List<Attendance> list = attendanceDAO.findByCourse(courseId);
        if (list.isEmpty()) { ConsoleView.info("No records found."); return; }
        System.out.printf("  %-25s | %-12s | %s%n", "Student", "Date", "Status");
        ConsoleView.printLine();
        for (Attendance a : list)
            System.out.printf("  %-25s | %-12s | %s%n",
                    a.getStudentName(), a.getDate(), a.getStatus());
    }

    // ── Attendance analytics with bar ──────────────────────────
    public void printAnalytics(int studentId) {
        ConsoleView.printSection("Attendance Analytics");
        attendanceDAO.printAnalytics(studentId);
    }

    // ── Search by student name ─────────────────────────────────
    public void searchByStudent(String keyword) {
        ConsoleView.printSection("Attendance Search: \"" + keyword + "\"");
        List<Attendance> list = attendanceDAO.searchByStudentName(keyword);
        if (list.isEmpty()) { ConsoleView.info("No records found."); return; }
        System.out.printf("  %-25s | %-30s | %-12s | %s%n",
                "Student","Course","Date","Status");
        ConsoleView.printLine();
        for (Attendance a : list)
            System.out.printf("  %-25s | %-30s | %-12s | %s%n",
                    a.getStudentName(), a.getCourseName(), a.getDate(), a.getStatus());
    }
}
