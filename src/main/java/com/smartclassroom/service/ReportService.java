package com.smartclassroom.service;

import com.smartclassroom.dao.ReportDAO;
import com.smartclassroom.dao.UserDAO;
import com.smartclassroom.model.User;
import com.smartclassroom.util.InputHelper;
import com.smartclassroom.view.ConsoleView;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ReportService — wraps ReportDAO calls with headers, timestamps, and menus.
 */
public class ReportService {

    private final ReportDAO reportDAO = new ReportDAO();
    private final UserDAO   userDAO   = new UserDAO();

    // ── Admin: full reports menu ───────────────────────────────
    public void showAdminReportsMenu() {
        boolean running = true;
        while (running) {
            ConsoleView.printHeader("REPORTS — ADMIN");
            System.out.println("  1. Attendance Summary by Course");
            System.out.println("  2. At-Risk Students (attendance < 75%)");
            System.out.println("  3. Quiz Performance Summary");
            System.out.println("  4. Issue Resolution Report");
            System.out.println("  5. Feedback Rating Report");
            System.out.println("  6. Technician Workload Report");
            System.out.println("  7. Course Enrollment Report");
            System.out.println("  8. Full Student Report");
            System.out.println("  9. Lecturer Summary Report");
            System.out.println("  0. Back");
            ConsoleView.printLine();
            int ch = InputHelper.getIntInRange("Choice", 0, 9);
            switch (ch) {
                case 1: printWithHeader("Attendance Summary by Course",
                            () -> reportDAO.attendanceSummaryByCourse());         break;
                case 2: printWithHeader("At-Risk Students (Attendance < 75%)",
                            () -> reportDAO.atRiskStudents());                    break;
                case 3: printWithHeader("Quiz Performance Summary",
                            () -> reportDAO.quizPerformanceSummary());            break;
                case 4: printWithHeader("Issue Resolution Report",
                            () -> reportDAO.issueResolutionReport());             break;
                case 5: printWithHeader("Feedback Rating Report",
                            () -> reportDAO.feedbackRatingReport());              break;
                case 6: printWithHeader("Technician Workload Report",
                            () -> reportDAO.technicianWorkloadReport());          break;
                case 7: printWithHeader("Course Enrollment Report",
                            () -> reportDAO.courseEnrollmentReport());            break;
                case 8: studentReportFlow();                                      break;
                case 9: lecturerReportFlow();                                     break;
                case 0: running = false;                                          break;
            }
            if (running && ch != 0) InputHelper.pressEnter();
        }
    }

    // ── Lecturer: their own summary + course-specific reports ──
    public void showLecturerReportsMenu(int lecturerId) {
        boolean running = true;
        while (running) {
            ConsoleView.printHeader("REPORTS — LECTURER");
            System.out.println("  1. My Summary Report");
            System.out.println("  2. Attendance Summary (My Courses)");
            System.out.println("  3. At-Risk Students (My Courses)");
            System.out.println("  4. Quiz Performance (My Courses)");
            System.out.println("  5. Feedback Ratings (My Courses)");
            System.out.println("  6. Full Student Report");
            System.out.println("  0. Back");
            ConsoleView.printLine();
            int ch = InputHelper.getIntInRange("Choice", 0, 6);
            switch (ch) {
                case 1: printWithHeader("My Summary Report",
                            () -> reportDAO.lecturerSummaryReport(lecturerId));   break;
                case 2: printWithHeader("Attendance Summary — My Courses",
                            () -> reportDAO.attendanceSummaryByCourse());         break;
                case 3: printWithHeader("At-Risk Students",
                            () -> reportDAO.atRiskStudents());                    break;
                case 4: printWithHeader("Quiz Performance Summary",
                            () -> reportDAO.quizPerformanceSummary());            break;
                case 5: printWithHeader("Feedback Rating Report",
                            () -> reportDAO.feedbackRatingReport());              break;
                case 6: studentReportFlow();                                      break;
                case 0: running = false;                                          break;
            }
            if (running && ch != 0) InputHelper.pressEnter();
        }
    }

    // ── Student: their own report ──────────────────────────────
    public void showMyReport(int studentId) {
        printWithHeader("My Full Report", () -> reportDAO.fullStudentReport(studentId));
    }

    // ── Helpers ────────────────────────────────────────────────
    private void studentReportFlow() {
        List<User> students = userDAO.findByRole("STUDENT");
        ConsoleView.printSection("Select Student");
        for (User u : students)
            System.out.printf("  ID:%-3d | %s%n", u.getUserId(), u.getFullName());
        if (students.isEmpty()) { ConsoleView.info("No students found."); return; }
        int id = InputHelper.getInt("Student ID");
        // Validate ID exists in list
        boolean valid = students.stream().anyMatch(u -> u.getUserId() == id);
        if (!valid) { ConsoleView.error("Invalid student ID."); return; }
        printWithHeader("Full Student Report", () -> reportDAO.fullStudentReport(id));
    }

    private void lecturerReportFlow() {
        List<User> lecturers = userDAO.findByRole("LECTURER");
        ConsoleView.printSection("Select Lecturer");
        for (User u : lecturers)
            System.out.printf("  ID:%-3d | %s%n", u.getUserId(), u.getFullName());
        if (lecturers.isEmpty()) { ConsoleView.info("No lecturers found."); return; }
        int id = InputHelper.getInt("Lecturer ID");
        boolean valid = lecturers.stream().anyMatch(u -> u.getUserId() == id);
        if (!valid) { ConsoleView.error("Invalid lecturer ID."); return; }
        printWithHeader("Lecturer Summary Report", () -> reportDAO.lecturerSummaryReport(id));
    }

    /** Wraps any report with a header and timestamp */
    private void printWithHeader(String title, Runnable report) {
        ConsoleView.printSection(title);
        System.out.println("  Generated : " + LocalDateTime.now()
                .toString().replace("T", " ").substring(0, 19));
        ConsoleView.printLine();
        report.run();
    }
}
