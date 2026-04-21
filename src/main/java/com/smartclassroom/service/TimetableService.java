package com.smartclassroom.service;

import com.smartclassroom.dao.TimetableDAO;
import com.smartclassroom.model.Timetable;
import com.smartclassroom.view.ConsoleView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * TimetableService — add, view, update, search timetable slots.
 */
public class TimetableService {

    private final TimetableDAO dao = new TimetableDAO();

    // ── Print all slots ────────────────────────────────────────
    public void printAll() {
        ConsoleView.printSection("Full Timetable");
        printList(dao.findAll());
    }

    // ── Print today's slots ────────────────────────────────────
    public void printToday() {
        String today = getTodayAbbr();
        ConsoleView.printSection("Today's Classes (" + today + ")");
        List<Timetable> list = dao.findByDay(today);
        printList(list);
        if (list.isEmpty()) ConsoleView.info("No classes scheduled today.");
    }

    // ── Return today's slots as a list (for dashboard) ────────
    public List<Timetable> getTodaySlots() {
        return dao.findByDay(getTodayAbbr());
    }

    // ── Return today's slots for a specific lecturer (for dashboard) ──
    public List<Timetable> getTodaySlotsForLecturer(int lecturerId) {
        return dao.findByDayAndLecturer(getTodayAbbr(), lecturerId);
    }

    // ── Print by lecturer ──────────────────────────────────────
    public void printByLecturer(int lecturerId) {
        ConsoleView.printSection("My Timetable");
        printList(dao.findByLecturer(lecturerId));
    }

    // ── Search by course name / code ───────────────────────────
    public void search(String keyword) {
        ConsoleView.printSection("Search: \"" + keyword + "\"");
        printList(dao.searchByCourseName(keyword));
    }

    // ── Add slot ───────────────────────────────────────────────
    public boolean addSlot(int courseId, int roomId, String day,
                           String start, String end) {
        boolean ok = dao.insert(courseId, roomId, day.toUpperCase(), start, end);
        if (ok) ConsoleView.success("Timetable slot added.");
        return ok;
    }

    // ── Update slot ────────────────────────────────────────────
    public boolean updateSlot(int slotId, String day, String start, String end) {
        boolean ok = dao.update(slotId, day.toUpperCase(), start, end);
        if (ok) {
            ConsoleView.success("Slot updated.");
        } else {
            ConsoleView.error("Slot not found.");
        }
        return ok;
    }

    // ── Delete slot ────────────────────────────────────────────
    public boolean deleteSlot(int slotId) {
        boolean ok = dao.delete(slotId);
        if (ok) ConsoleView.success("Slot deleted.");
        else    ConsoleView.error("Slot not found.");
        return ok;
    }

    // ── Next class reminder ────────────────────────────────────
    public void showNextClassReminder() {
        showNextClassReminderForSlots(dao.findByDay(getTodayAbbr()));
    }

    public void showNextClassReminderForLecturer(int lecturerId) {
        showNextClassReminderForSlots(dao.findByDayAndLecturer(getTodayAbbr(), lecturerId));
    }

    private void showNextClassReminderForSlots(List<Timetable> slots) {
        String nowTime = LocalTime.now().toString().substring(0, 5);

        Timetable next = null;
        int minDiff    = Integer.MAX_VALUE;

        for (Timetable t : slots) {
            String st = t.getStartTime();
            if (st == null) continue;
            String[] parts = st.split(":");
            int classMin = Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
            String[] np  = nowTime.split(":");
            int nowMin   = Integer.parseInt(np[0]) * 60 + Integer.parseInt(np[1]);
            int diff     = classMin - nowMin;
            if (diff > 0 && diff < minDiff) { minDiff = diff; next = t; }
        }

        System.out.println();
        ConsoleView.printLine();
        if (next != null) {
            System.out.println("  NEXT CLASS TODAY");
            System.out.println("  Subject : " + next.getCourseCode() + " — " + next.getCourseName());
            System.out.println("  Time    : " + next.getStartTime().substring(0,5) +
                               "  (in " + minDiff + " min)");
            System.out.println("  Room    : " + next.getRoomName());
            if (minDiff <= 15) ConsoleView.warn("Starting in " + minDiff + " minutes — get ready!");
        } else {
            ConsoleView.info("No more classes scheduled for today.");
        }
        ConsoleView.printLine();
    }

    // ── Helper ─────────────────────────────────────────────────
    private void printList(List<Timetable> list) {
        if (list.isEmpty()) { ConsoleView.info("No slots found."); return; }
        System.out.printf("  %-4s | %-4s | %-10s | %-35s | %s-%s | %s%n",
                "ID","Day","Room","Course","Start","End  ","Lecturer");
        ConsoleView.printLine();
        for (Timetable t : list) {
            System.out.printf("  %-4d | %-4s | %-10s | %-35s | %s-%s | %s%n",
                    t.getSlotId(), t.getDayOfWeek(),
                    t.getRoomName() != null ? t.getRoomName() : "Room#" + t.getRoomId(),
                    (t.getCourseCode() != null ? t.getCourseCode() + " " : "") +
                    (t.getCourseName() != null ? t.getCourseName() : ""),
                    t.getStartTime() != null ? t.getStartTime().substring(0,5) : "?",
                    t.getEndTime()   != null ? t.getEndTime().substring(0,5)   : "?",
                    t.getLecturerName() != null ? t.getLecturerName() : "-");
        }
    }

    private String getTodayAbbr() {
        String day = LocalDate.now().getDayOfWeek().toString().substring(0, 3);
        return day.charAt(0) + day.substring(1).toLowerCase();
    }
}
