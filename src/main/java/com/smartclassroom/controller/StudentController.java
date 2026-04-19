package com.smartclassroom.controller;

import com.smartclassroom.dao.AnnouncementDAO;
import com.smartclassroom.dao.FeedbackDAO;
import com.smartclassroom.dao.NotificationDAO;
import com.smartclassroom.model.User;
import com.smartclassroom.service.*;
import com.smartclassroom.util.InputHelper;
import com.smartclassroom.view.ConsoleView;

/**
 * StudentController — Phase 4: My Report added, input hardened.
 */
public class StudentController {

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

    public StudentController(User user) { this.user = user; }

    public void run() {
        boolean running = true;
        while (running) {
            ConsoleView.printHeader("STUDENT MENU");
            System.out.println("  --- TIMETABLE ---");
            System.out.println("  1.  View Full Timetable");
            System.out.println("  2.  View Today's Classes");
            System.out.println("  3.  Search Timetable by Subject");
            System.out.println("  --- ATTENDANCE ---");
            System.out.println("  4.  View My Attendance");
            System.out.println("  5.  Attendance Analytics");
            System.out.println("  --- QUIZZES ---");
            System.out.println("  6.  View Available Quizzes");
            System.out.println("  7.  View Upcoming Quizzes (Pending)");
            System.out.println("  8.  Take a Quiz");
            System.out.println("  9.  View My Quiz Scores");
            System.out.println("  --- ANNOUNCEMENTS & FEEDBACK ---");
            System.out.println("  10. View Announcements");
            System.out.println("  11. Submit Anonymous Feedback");
            System.out.println("  --- REPORTS ---");
            System.out.println("  12. My Full Report");
            System.out.println("  --- NOTIFICATIONS & ACCOUNT ---");
            System.out.println("  13. View Notifications");
            System.out.println("  14. Change Password");
            System.out.println("  0.  Logout");
            ConsoleView.printLine();

            int ch = InputHelper.getIntInRange("Choice", 0, 14);
            switch (ch) {
                case 1:  timetableService.printAll();                              break;
                case 2:  timetableService.printToday();                            break;
                case 3:  timetableService.search(InputHelper.getString("Keyword")); break;
                case 4:  attendanceService.printMyAttendance(user.getUserId());    break;
                case 5:  attendanceService.printAnalytics(user.getUserId());       break;
                case 6:  quizService.printAvailableForStudent(user.getUserId());    break;
                case 7:  quizService.printUpcomingForStudent(user.getUserId());    break;
                case 8:  takeQuizFlow();                                           break;
                case 9:  quizService.printMyScores(user.getUserId());              break;
                case 10: announcementDAO.findForRole(user.getRole()).forEach(a -> {
                             System.out.println("  " + a); ConsoleView.printLine(); }); break;
                case 11: submitFeedbackFlow();                                     break;
                case 12: reportService.showMyReport(user.getUserId());             break;
                case 13: viewNotificationsFlow();                                  break;
                case 14: changePasswordFlow();                                     break;
                case 0:  running = false; System.out.println("  Logged out.");    break;
            }
            if (running && ch != 0) InputHelper.pressEnter();
        }
    }

    private void takeQuizFlow() {
        quizService.printAll();
        int quizId = InputHelper.getInt("Quiz ID to attempt");
        quizService.takeQuiz(quizId, user.getUserId());
    }

    private void submitFeedbackFlow() {
        ConsoleView.printSection("Submit Anonymous Feedback");
        ConsoleView.info("Your identity will NOT be stored. This is fully anonymous.");
        userService.printAllCourses();
        int courseId = InputHelper.getInt("Course ID");
        int rating   = InputHelper.getIntInRange("Rating (1-5 stars)", 1, 5);
        String comments = InputHelper.getOptionalString("Comments");
        if (feedbackDAO.insert(courseId, rating, comments.isEmpty() ? null : comments)) {
            ConsoleView.success("Feedback submitted anonymously. Thank you!");
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
