package com.smartclassroom.controller;

import com.smartclassroom.dao.AnnouncementDAO;
import com.smartclassroom.dao.FeedbackDAO;
import com.smartclassroom.dao.IssueDAO;
import com.smartclassroom.dao.NotificationDAO;
import com.smartclassroom.model.User;
import com.smartclassroom.service.*;
import com.smartclassroom.util.InputHelper;
import com.smartclassroom.view.ConsoleView;

/**
 * CRController — CR can reschedule classes (notifies lecturer + students).
 */
public class CRController {

    private final User              user;
    private final TimetableService  timetableService  = new TimetableService();
    private final AttendanceService attendanceService = new AttendanceService();
    private final QuizService       quizService       = new QuizService();
    private final UserService       userService       = new UserService();
    private final AuthService       authService       = new AuthService();
    private final ReportService     reportService     = new ReportService();
    private final AnnouncementDAO   announcementDAO   = new AnnouncementDAO();
    private final NotificationDAO   notifDAO          = new NotificationDAO();
    private final FeedbackDAO       feedbackDAO       = new FeedbackDAO();
    private final IssueDAO          issueDAO          = new IssueDAO();

    public CRController(User user) { this.user = user; }

    public void run() {
        boolean running = true;
        while (running) {
            ConsoleView.printHeader("CLASS REPRESENTATIVE MENU");
            System.out.println("  --- TIMETABLE ---");
            System.out.println("  1.  View Full Timetable");
            System.out.println("  2.  View Today's Classes");
            System.out.println("  3.  Search Timetable by Subject");
            System.out.println("  4.  Reschedule a Class");
            System.out.println("  --- ATTENDANCE ---");
            System.out.println("  5.  View My Attendance");
            System.out.println("  6.  Attendance Analytics");
            System.out.println("  --- QUIZZES ---");
            System.out.println("  7.  View All Quizzes");
            System.out.println("  8.  View Upcoming Quizzes");
            System.out.println("  9.  Take a Quiz");
            System.out.println("  10. View My Quiz Scores");
            System.out.println("  --- ANNOUNCEMENTS ---");
            System.out.println("  11. Post Announcement");
            System.out.println("  12. View Announcements");
            System.out.println("  --- ISSUES ---");
            System.out.println("  13. Report Issue");
            System.out.println("  14. View All Issues");
            System.out.println("  15. Search Issues by Room");
            System.out.println("  --- FEEDBACK & REPORTS ---");
            System.out.println("  16. Submit Anonymous Feedback");
            System.out.println("  17. My Full Report");
            System.out.println("  --- NOTIFICATIONS & ACCOUNT ---");
            System.out.println("  18. View Notifications");
            System.out.println("  19. Change Password");
            System.out.println("  0.  Logout");
            ConsoleView.printLine();

            int ch = InputHelper.getIntInRange("Choice", 0, 19);
            switch (ch) {
                case 1:  timetableService.printAll();                              break;
                case 2:  timetableService.printToday();                            break;
                case 3:  timetableService.search(InputHelper.getString("Keyword")); break;
                case 4:  rescheduleFlow();                                         break;
                case 5:  attendanceService.printMyAttendance(user.getUserId());    break;
                case 6:  attendanceService.printAnalytics(user.getUserId());       break;
                case 7:  quizService.printAll();                                   break;
                case 8:  quizService.printUpcomingForStudent(user.getUserId());    break;
                case 9:  takeQuizFlow();                                           break;
                case 10: quizService.printMyScores(user.getUserId());              break;
                case 11: postAnnouncementFlow();                                   break;
                case 12: announcementDAO.findForRole(user.getRole()).forEach(a -> {
                             System.out.println("  " + a); ConsoleView.printLine(); }); break;
                case 13: reportIssueFlow();                                        break;
                case 14: issueDAO.findAll().forEach(i -> System.out.println("  " + i)); break;
                case 15: issueDAO.searchByRoom(InputHelper.getString("Room keyword"))
                                 .forEach(i -> System.out.println("  " + i));     break;
                case 16: submitFeedbackFlow();                                     break;
                case 17: reportService.showMyReport(user.getUserId());             break;
                case 18: viewNotificationsFlow();                                  break;
                case 19: changePasswordFlow();                                     break;
                case 0:  running = false; System.out.println("  Logged out.");    break;
            }
            if (running && ch != 0) InputHelper.pressEnter();
        }
    }

    // ── CR Reschedule: update slot + notify lecturer & students ─
    private void rescheduleFlow() {
        ConsoleView.printSection("Reschedule a Class");
        ConsoleView.info("You can reschedule any class slot. Lecturer and students will be notified.");
        timetableService.printAll();
        int slotId = InputHelper.getInt("Slot ID to reschedule");
        String day   = InputHelper.getEnum("New Day","MON","TUE","WED","THU","FRI","SAT");
        String start = InputHelper.getString("New Start Time (HH:MM)");
        String end   = InputHelper.getString("New End Time   (HH:MM)");
        String reason = InputHelper.getOptionalString("Reason for rescheduling");

        if (timetableService.updateSlot(slotId, day, start + ":00", end + ":00")) {
            String msg = "Class rescheduled to " + day + " " + start + "-" + end +
                    " by CR: " + user.getFullName() +
                    (reason.isEmpty() ? "" : ". Reason: " + reason);
            // Notify all students
            notifDAO.broadcastToRole("STUDENT", "Class Rescheduled", msg);
            // Notify all lecturers
            notifDAO.broadcastToRole("LECTURER", "Class Rescheduled by CR", msg);
            ConsoleView.success("Rescheduled. Lecturer and students have been notified.");
        }
    }

    private void takeQuizFlow() {
        quizService.printAll();
        int quizId = InputHelper.getInt("Quiz ID to attempt");
        quizService.takeQuiz(quizId, user.getUserId());
    }

    // ── Announcement: broadcast to ALL notifies everyone ──────
    private void postAnnouncementFlow() {
        ConsoleView.printSection("Post Announcement");
        String title   = InputHelper.getString("Title");
        String content = InputHelper.getString("Content");
        String target  = InputHelper.getEnum("Target Role","ALL","STUDENT");
        if (announcementDAO.insert(user.getUserId(), title, content, target)) {
            ConsoleView.success("Announcement posted.");
            if ("ALL".equals(target)) {
                notifDAO.broadcastAll("New Announcement: " + title, content);
            } else {
                notifDAO.broadcastToRole(target, "New Announcement: " + title, content);
            }
        }
    }

    private void reportIssueFlow() {
        ConsoleView.printSection("Report Issue");
        userService.printRooms();
        int    roomId   = InputHelper.getInt("Room ID");
        String title    = InputHelper.getString("Issue Title");
        String desc     = InputHelper.getOptionalString("Description");
        String priority = InputHelper.getEnum("Priority","LOW","MEDIUM","HIGH");
        if (issueDAO.insert(user.getUserId(), roomId, title,
                desc.isEmpty() ? null : desc, priority)) {
            ConsoleView.success("Issue reported.");
            int techId = issueDAO.getTechnicianForRoom(roomId);
            if (techId > 0) {
                notifDAO.send(techId, "New Issue Assigned", title);
                ConsoleView.info("Technician notified.");
            } else {
                ConsoleView.warn("No technician assigned to this room.");
            }
        }
    }

    private void submitFeedbackFlow() {
        ConsoleView.printSection("Submit Anonymous Feedback");
        ConsoleView.info("Your identity will NOT be stored.");
        userService.printAllCourses();
        int courseId = InputHelper.getInt("Course ID");
        int rating   = InputHelper.getIntInRange("Rating (1-5 stars)", 1, 5);
        String comments = InputHelper.getOptionalString("Comments");
        if (feedbackDAO.insert(courseId, rating, comments.isEmpty() ? null : comments)) {
            ConsoleView.success("Feedback submitted anonymously.");
        }
    }

    private void viewNotificationsFlow() {
        ConsoleView.printSection("My Notifications");
        var list = notifDAO.findByUser(user.getUserId());
        if (list.isEmpty()) { ConsoleView.info("No notifications."); return; }
        list.forEach(n -> System.out.println("  " + n));
        notifDAO.markAllRead(user.getUserId());
    }

    private void changePasswordFlow() {
        ConsoleView.printSection("Change Password");
        String oldP = InputHelper.getString("Current Password");
        String newP = InputHelper.getString("New Password");
        String cnf  = InputHelper.getString("Confirm New Password");
        if (!newP.equals(cnf)) { ConsoleView.error("Passwords do not match."); return; }
        authService.changePassword(user.getUserId(), oldP, newP);
    }
}
