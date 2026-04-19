package com.smartclassroom.view;

/**
 * ConsoleView — all display/formatting helpers for the console UI.
 */
public class ConsoleView {

    private static final int WIDTH = 65;

    public static void printLine() {
        System.out.println("─".repeat(WIDTH));
    }

    public static void printDoubleLine() {
        System.out.println("═".repeat(WIDTH));
    }

    public static void printHeader(String title) {
        System.out.println();
        printDoubleLine();
        int pad = Math.max(0, (WIDTH - title.length()) / 2);
        System.out.println(" ".repeat(pad) + title);
        printDoubleLine();
    }

    public static void printSection(String title) {
        System.out.println();
        printLine();
        System.out.println("  " + title);
        printLine();
    }

    public static void success(String msg) { System.out.println("  [OK]  " + msg); }
    public static void error(String msg)   { System.out.println("  [ERR] " + msg); }
    public static void info(String msg)    { System.out.println("  [i]   " + msg); }
    public static void warn(String msg)    { System.out.println("  [!]   " + msg); }

    /** Visual attendance bar: e.g. [████████░░░░░░░░░░░░] 40.0% */
    public static String attendanceBar(double pct) {
        int filled = (int) (pct / 5);
        int empty  = 20 - Math.max(0, Math.min(20, filled));
        filled     = 20 - empty;
        return "[" + "█".repeat(filled) + "░".repeat(empty) + "] "
                + String.format("%.1f%%", pct);
    }

    /** Status label with safe/warning/critical tag */
    public static String attendanceStatus(double pct) {
        if (pct >= 75) return "SAFE";
        if (pct >= 60) return "WARNING";
        return "CRITICAL";
    }

    public static String priorityTag(String priority) {
        switch (priority == null ? "" : priority) {
            case "HIGH":   return "[HIGH  ]";
            case "MEDIUM": return "[MEDIUM]";
            case "LOW":    return "[LOW   ]";
            default:       return "[?     ]";
        }
    }

    public static String roleTag(String role) {
        switch (role == null ? "" : role) {
            case "ADMIN":      return "[ADMIN]";
            case "LECTURER":   return "[LECT ]";
            case "CR":         return "[CR   ]";
            case "STUDENT":    return "[STU  ]";
            case "TECHNICIAN": return "[TECH ]";
            default:           return "[?    ]";
        }
    }
}
