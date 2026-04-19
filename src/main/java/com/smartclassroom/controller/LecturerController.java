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
 * LecturerController — Phase 4: Reports added, input hardened.
 */
public class LecturerController {

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

    public LecturerController(User user) { this.user = user; }

    public void run() {
        boolean running = true;
        while (running) {
            ConsoleView.printHeader("LECTURER MENU");
            System.out.println("  --- TIMETABLE ---");
            System.out.println("  1.  View My Timetable");
            System.out.println("  2.  View Full Timetable");
            System.out.println("  3.  Search Timetable by Subject");
            System.out.println("  4.  Update My Slot (Reschedule)");
            System.out.println("  --- ATTENDANCE ---");
            System.out.println("  5.  Mark Attendance");
            System.out.println("  6.  View Course Attendance");
            System.out.println("  7.  Search Attendance by Student");
            System.out.println("  --- QUIZZES ---");
            System.out.println("  8.  Create Quiz");
            System.out.println("  9.  View My Quizzes");
            System.out.println("  10. View Quiz Results");
            System.out.println("  --- ANNOUNCEMENTS & FEEDBACK ---");
            System.out.println("  11. Post Announcement");
            System.out.println("  12. View Announcements");
            System.out.println("  13. View Feedback on My Courses");
            System.out.println("  14. Feedback Rating Summary");
            System.out.println("  --- ISSUES ---");
            System.out.println("  15. Report Issue");
            System.out.println("  16. View All Issues");
            System.out.println("  17. Search Issues by Room");
            System.out.println("  --- REPORTS ---");
            System.out.println("  18. Reports Dashboard");
            System.out.println("  --- NOTIFICATIONS & ACCOUNT ---");
            System.out.println("  19. View Notifications");
            System.out.println("  20. Change Password");
            System.out.println("  0.  Logout");
            ConsoleView.printLine();

            int ch = InputHelper.getIntInRange("Choice", 0, 20);
            switch (ch) {
                case 1:  timetableService.printByLecturer(user.getUserId());        break;
                case 2:  timetableService.printAll();                               break;
                case 3:  timetableService.search(InputHelper.getString("Keyword")); break;
                case 4:  updateSlotFlow();                                          break;
                case 5:  markAttendanceFlow();                                      break;
                case 6:  viewCourseAttendanceFlow();                                break;
                case 7:  attendanceService.searchByStudent(InputHelper.getString("Student name keyword")); break;
                case 8:  createQuizFlow();                                          break;
                case 9:  viewMyQuizzesFlow();                                       break;
                case 10: viewQuizResultsFlow();                                     break;
                case 11: postAnnouncementFlow();                                    break;
                case 12: announcementDAO.findForRole(user.getRole()).forEach(a -> {
                             System.out.println("  " + a); ConsoleView.printLine(); }); break;
                case 13: viewFeedbackFlow();                                        break;
                case 14: feedbackDAO.printRatingSummary(user.getUserId());          break;
                case 15: reportIssueFlow();                                         break;
                case 16: printAllIssues();                                          break;
                case 17: issueDAO.searchByRoom(InputHelper.getString("Room keyword"))
                                 .forEach(i -> System.out.println("  " + i));      break;
                case 18: reportService.showLecturerReportsMenu(user.getUserId());   break;
                case 19: viewNotificationsFlow();                                   break;
                case 20: changePasswordFlow();                                      break;
                case 0:  running = false; System.out.println("  Logged out.");     break;
            }
            if (running && ch != 0) InputHelper.pressEnter();
        }
    }

    private void updateSlotFlow() {
        timetableService.printByLecturer(user.getUserId());
        int slotId = InputHelper.getInt("Slot ID to update");
        String day   = InputHelper.getEnum("New Day","MON","TUE","WED","THU","FRI","SAT");
        String start = InputHelper.getString("New Start (HH:MM)");
        String end   = InputHelper.getString("New End   (HH:MM)");
        if (timetableService.updateSlot(slotId, day, start + ":00", end + ":00")) {
            notifDAO.broadcastToRole("STUDENT","Class Rescheduled",
                    "A class has been moved to " + day + " " + start);
        }
    }

    private void markAttendanceFlow() {
        userService.printAllCourses();
        int courseId = InputHelper.getInt("Course ID");
        attendanceService.markAttendance(courseId, user.getUserId());
    }

    private void viewCourseAttendanceFlow() {
        userService.printAllCourses();
        int courseId = InputHelper.getInt("Course ID");
        attendanceService.printCourseAttendance(courseId);
    }

    private void createQuizFlow() {
        ConsoleView.printSection("My Courses");
        new com.smartclassroom.dao.CourseDAO().findByLecturer(user.getUserId())
                .forEach(c -> System.out.println("  " + c));
        int courseId = InputHelper.getInt("Course ID");
        quizService.createQuiz(courseId, user.getUserId());
    }

    private void viewMyQuizzesFlow() {
        new com.smartclassroom.dao.CourseDAO().findByLecturer(user.getUserId())
                .forEach(c -> {
                    ConsoleView.printSection(c.getCourseName());
                    quizService.printByCourse(c.getCourseId());
                });
    }

    private void viewQuizResultsFlow() {
        quizService.printAll();
        int quizId = InputHelper.getInt("Quiz ID");
        quizService.printResults(quizId);
    }

    private void postAnnouncementFlow() {
        ConsoleView.printSection("Post Announcement");
        String title   = InputHelper.getString("Title");
        String content = InputHelper.getString("Content");
        String target  = InputHelper.getEnum("Target Role","ALL","STUDENT","CR");
        if (announcementDAO.insert(user.getUserId(), title, content, target)) {
            ConsoleView.success("Announcement posted.");
            if ("ALL".equals(target)) {
                notifDAO.broadcastAll("New Announcement: " + title, content);
            } else {
                notifDAO.broadcastToRole(target, "New Announcement: " + title, content);
            }
        }
    }

    private void viewFeedbackFlow() {
        new com.smartclassroom.dao.CourseDAO().findByLecturer(user.getUserId())
                .forEach(c -> {
                    ConsoleView.printSection("Feedback — " + c.getCourseName());
                    var list = feedbackDAO.findByCourse(c.getCourseId());
                    if (list.isEmpty()) { ConsoleView.info("No feedback yet."); return; }
                    list.forEach(f -> {
                        System.out.println("  " + f.ratingStars());
                        if (f.getComments() != null && !f.getComments().isEmpty())
                            System.out.println("  \"" + f.getComments() + "\"");
                        System.out.println();
                    });
                });
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
                ConsoleView.info("Technician has been notified.");
            } else {
                ConsoleView.warn("No technician assigned to this room.");
            }
        }
    }

    private void printAllIssues() {
        var list = issueDAO.findAll();
        if (list.isEmpty()) { ConsoleView.info("No issues found."); return; }
        list.forEach(i -> System.out.println("  " + i));
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
