package com.smartclassroom.controller;

import com.smartclassroom.dao.AnnouncementDAO;
import com.smartclassroom.dao.NotificationDAO;
import com.smartclassroom.model.Announcement;
import com.smartclassroom.model.Quiz;
import com.smartclassroom.model.Timetable;
import com.smartclassroom.model.User;
import com.smartclassroom.service.AuthService;
import com.smartclassroom.service.QuizService;
import com.smartclassroom.service.TimetableService;
import com.smartclassroom.util.InputHelper;
import com.smartclassroom.view.ConsoleView;

import java.util.List;

/**
 * LoginController — login screen, full dashboard after login.
 */
public class LoginController {

    private final AuthService      authService      = new AuthService();
    private final NotificationDAO  notifDAO         = new NotificationDAO();
    private final AnnouncementDAO  announcementDAO  = new AnnouncementDAO();
    private final TimetableService timetableService = new TimetableService();
    private final QuizService      quizService      = new QuizService();

    private boolean exitRequested = false;

    public User promptLogin() {
        exitRequested = false;
        ConsoleView.printHeader("LOGIN");
        System.out.println("  Type 'exit' as username to quit the application.");
        ConsoleView.printLine();

        String username = InputHelper.getString("Username");
        if ("exit".equalsIgnoreCase(username)) { exitRequested = true; return null; }

        String password = InputHelper.getString("Password");
        return authService.login(username, password);
    }

    public boolean isExitRequested() { return exitRequested; }

    // ── Full dashboard after login ─────────────────────────────
    public void showDashboard(User user) {
        ConsoleView.printHeader("DASHBOARD — " + user.getFullName() +
                "  " + ConsoleView.roleTag(user.getRole()));

        // ── 1. Unread notifications ────────────────────────────
        int unread = notifDAO.countUnread(user.getUserId());
        if (unread > 0)
            ConsoleView.warn("You have " + unread + " unread notification(s). [View from menu]");
        else
            ConsoleView.info("No new notifications.");

        // ── 2. Today's classes (not shown for ADMIN) ──────────
        if (!"ADMIN".equals(user.getRole())) {
            ConsoleView.printSection("Today's Classes");
            List<Timetable> todaySlots = "LECTURER".equals(user.getRole())
                    ? timetableService.getTodaySlotsForLecturer(user.getUserId())
                    : timetableService.getTodaySlots();
            if (todaySlots.isEmpty()) {
                ConsoleView.info("No classes scheduled today.");
            } else {
                System.out.printf("  %-4s | %-4s | %-10s | %-35s | %s-%s%n",
                        "ID","Day","Room","Course","Start","End");
                ConsoleView.printLine();
                for (Timetable t : todaySlots) {
                    System.out.printf("  %-4d | %-4s | %-10s | %-35s | %s-%s%n",
                            t.getSlotId(), t.getDayOfWeek(),
                            t.getRoomName() != null ? t.getRoomName() : "Room#" + t.getRoomId(),
                            (t.getCourseCode() != null ? t.getCourseCode() + " " : "") +
                            (t.getCourseName() != null ? t.getCourseName() : ""),
                            t.getStartTime() != null ? t.getStartTime().substring(0,5) : "?",
                            t.getEndTime()   != null ? t.getEndTime().substring(0,5)   : "?");
                }
            }
        }

        // ── 3. Next class reminder (not shown for ADMIN) ──────
        if (!"ADMIN".equals(user.getRole())) {
            if ("LECTURER".equals(user.getRole())) {
                timetableService.showNextClassReminderForLecturer(user.getUserId());
            } else {
                timetableService.showNextClassReminder();
            }
        }

        // ── 4. Upcoming quizzes (students & CR only) ──────────
        if ("STUDENT".equals(user.getRole()) || "CR".equals(user.getRole())) {
            ConsoleView.printSection("Upcoming Quizzes");
            List<Quiz> quizzes = quizService.getUpcomingForStudent(user.getUserId());
            if (quizzes.isEmpty()) {
                ConsoleView.info("No upcoming quizzes.");
            } else {
                for (Quiz q : quizzes)
                    System.out.println("  " + q);
            }
        }

        // ── 5. Latest announcements (up to 3) ─────────────────
        ConsoleView.printSection("Latest Announcements");
        List<Announcement> announcements = announcementDAO.findForRole(user.getRole());
        if (announcements.isEmpty()) {
            ConsoleView.info("No announcements.");
        } else {
            int shown = 0;
            for (Announcement a : announcements) {
                if (shown++ >= 3) break;
                System.out.println("  [" + (a.getCreatedAt() != null ?
                        a.getCreatedAt().substring(0,10) : "?") + "]  " + a.getTitle());
                if (a.getContent() != null && !a.getContent().isEmpty())
                    System.out.println("    " + a.getContent());
                ConsoleView.printLine();
            }
            if (announcements.size() > 3)
                ConsoleView.info("... and " + (announcements.size()-3) + " more. View from menu.");
        }
    }
}
