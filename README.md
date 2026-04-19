# Smart Classroom Management System
### Core Java + JDBC + MySQL | Console-Based

---

## Project Structure

```
smart_classroom/
├── src/main/java/com/smartclassroom/
│   ├── Main.java                          ← Entry point
│   ├── controller/
│   │   ├── LoginController.java           ← Login screen + full dashboard
│   │   ├── AdminController.java           ← 22-option admin menu
│   │   ├── LecturerController.java        ← 20-option lecturer menu
│   │   ├── CRController.java              ← 19-option CR menu
│   │   ├── StudentController.java         ← 14-option student menu
│   │   └── TechnicianController.java      ← 6-option technician menu
│   ├── dao/
│   │   ├── UserDAO.java                   ← User CRUD + auth query
│   │   ├── CourseDAO.java                 ← Courses + enrollments
│   │   ├── RoomDAO.java                   ← Rooms + room_technicians + free rooms
│   │   ├── TimetableDAO.java              ← Timetable slots
│   │   ├── AttendanceDAO.java             ← Attendance + analytics
│   │   ├── QuizDAO.java                   ← Quizzes, questions, submissions
│   │   ├── IssueDAO.java                  ← Issues + auto-assign technician
│   │   ├── NotificationDAO.java           ← Notifications + broadcast
│   │   ├── AnnouncementDAO.java           ← Announcements + role filter
│   │   └── FeedbackDAO.java               ← Anonymous feedback + ratings
│   ├── model/
│   │   ├── User.java
│   │   ├── Course.java
│   │   ├── Timetable.java
│   │   ├── Attendance.java
│   │   ├── Quiz.java
│   │   ├── QuizQuestion.java
│   │   ├── Feedback.java
│   │   ├── Issue.java
│   │   ├── Notification.java
│   │   └── Announcement.java
│   ├── service/
│   │   ├── AuthService.java               ← SHA-256 login + password change
│   │   ├── UserService.java               ← User, room, course management
│   │   ├── TimetableService.java          ← Timetable + next-class reminder
│   │   ├── AttendanceService.java         ← Mark, view, analytics with bar
│   │   ├── QuizService.java               ← Create MCQ quiz, take quiz, results
│   │   └── ReportService.java             ← Report menus for admin/lecturer/student
│   ├── util/
│   │   ├── DBConnection.java              ← Singleton JDBC connection
│   │   └── InputHelper.java               ← Console input helpers
│   └── view/
│       └── ConsoleView.java               ← Borders, tags, attendance bar
├── database/
│   ├── schema.sql                         ← All 14 tables + FKs
│   └── sample_data.sql                    ← 13 users, courses, attendance etc.
├── lib/
│   └── mysql-connector-j-9.6.0.jar        ← JDBC driver (already included)
├── out/                                   ← Compiled .class files go here
├── compile.bat                            ← Windows build script
├── compile.sh                             ← Linux/Mac build script
└── README.md
```

---

## Setup — 5 Steps

### Step 1 — MySQL Setup
Open MySQL Workbench (or terminal) and run both SQL files in order:
```sql
SOURCE /path/to/database/schema.sql;
SOURCE /path/to/database/sample_data.sql;
```

### Step 2 — JDBC Driver
`mysql-connector-j-9.6.0.jar` is already in `lib/`. No action needed.

### Step 3 — Configure Database Connection
Open `src/main/java/com/smartclassroom/util/DBConnection.java` and update:
```java
private static final String DB_USER = "root";        // your MySQL username
private static final String DB_PASS = "your_pass";   // your MySQL password
```

### Step 4 — Compile

**Windows:**
```cmd
compile.bat
```

**Linux/Mac:**
```bash
chmod +x compile.sh && ./compile.sh
```

### Step 5 — Run

**Windows:**
```cmd
java -cp ".;out;lib\mysql-connector-j-9.6.0.jar" com.smartclassroom.Main
```

**Linux/Mac:**
```bash
java -cp ".:out:lib/mysql-connector-j-9.6.0.jar" com.smartclassroom.Main
```

---

## Login Credentials (all passwords: `pass123`)

| Role       | Username      | Full Name          |
|------------|---------------|--------------------|
| Admin      | admin         | Dr. Admin Singh    |
| Lecturer   | prof_sharma   | Prof. Ravi Sharma  |
| Lecturer   | prof_nair     | Prof. Priya Nair   |
| Lecturer   | prof_verma    | Prof. Anil Verma   |
| CR         | cr_ravi       | Ravi Patil         |
| CR         | cr_sneha      | Sneha Joshi        |
| Student    | student1      | Anjali Mehta       |
| Student    | student2      | Rohan Desai        |
| Student    | student3      | Pooja Kulkarni     |
| Student    | student4      | Arjun Reddy        |
| Student    | student5      | Meera Iyer         |
| Technician | tech_suresh   | Suresh Kumar       |
| Technician | tech_ramesh   | Ramesh Pillai      |

---

## Features by Role

### Admin (22 options)
- User management: add, view, deactivate users
- Course management: add/delete courses, enroll/unenroll students, view enrollments
- Room management: add rooms, assign technicians via `room_technicians`
- Full timetable control: add/update/delete slots
- Post announcements with target role (ALL notifies every user via `broadcastAll`)
- Broadcast notifications to all users
- Reports dashboard (9 report types)
- Change password

### Lecturer (20 options)
- View own timetable, update/reschedule slots (notifies students)
- Mark attendance for enrolled students (PRESENT/ABSENT/LATE)
- View course attendance, search by student name
- Create MCQ quizzes with scheduled date, view results
- Post announcements (ALL uses `broadcastAll`, specific role uses `broadcastToRole`)
- View anonymous feedback with star ratings and rating summary
- Report issues (auto-assigns technician from `room_technicians`)
- Reports dashboard (6 report types)
- Change password

### Class Representative (19 options)
- View/search timetable
- Reschedule any class slot — notifies both lecturers and students with reason
- View own attendance + analytics with progress bar
- View all quizzes, view upcoming (pending) quizzes, take quizzes, view scores
- Post announcements (ALL uses `broadcastAll`)
- Report issues with priority (technician auto-notified)
- Submit anonymous course feedback
- My full report
- Change password

### Student (14 options)
- View timetable, today's classes, search by subject
- View own attendance with per-course analytics and visual bar
- View available quizzes (filtered to enrolled courses only, shows DONE/PENDING status)
- View upcoming quizzes (pending only, not yet submitted)
- Take quizzes (MCQ with instant scoring), view past scores
- View announcements, receive notifications
- Submit anonymous feedback (course + rating + optional comment)
- My full report
- Change password

### Technician (6 options)
- View only assigned issues (filtered by `assigned_to = user_id`)
- Update issue status: OPEN → IN_PROGRESS → RESOLVED → CLOSED
- Reporter is notified automatically on every status change
- View all rooms
- View today's timetable with room availability (BUSY/FREE breakdown)
- Change password

---

## Key Design Points

| Concept | How it's implemented |
|---|---|
| Password security | SHA-256 via `AuthService.hash()` — matches MySQL's `SHA2(p,256)` |
| Anonymous feedback | `feedback` table has NO `student_id` column — identity impossible to trace |
| Technician auto-assign | `IssueDAO.getTechnicianForRoom()` looks up `room_technicians` table |
| Attendance | `ON DUPLICATE KEY UPDATE` prevents double-marking for same day |
| Announcements (ALL) | `broadcastAll()` sends to every active user — not `broadcastToRole("ALL")` |
| Quiz availability | Students only see quizzes for courses they are enrolled in |
| Room availability | Technician view compares today's timetable against all rooms to show free/busy |
| Quiz | Full MCQ flow: create → scheduled date → take → instant score → save submission |
| OOP layers | Controller → Service → DAO → Model — clean separation |
| DB connection | Singleton pattern in `DBConnection` — one connection reused throughout |
