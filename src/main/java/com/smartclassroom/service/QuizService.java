package com.smartclassroom.service;

import com.smartclassroom.dao.QuizDAO;
import com.smartclassroom.dao.NotificationDAO;
import com.smartclassroom.model.Quiz;
import com.smartclassroom.model.QuizQuestion;
import com.smartclassroom.util.InputHelper;
import com.smartclassroom.view.ConsoleView;

import java.util.List;

/**
 * QuizService — create quizzes with MCQ questions, take quiz, view results.
 */
public class QuizService {

    private final QuizDAO         quizDAO  = new QuizDAO();
    private final NotificationDAO notifDAO = new NotificationDAO();

    // ── Lecturer: create quiz with questions ───────────────────
    public void createQuiz(int courseId, int createdBy) {
        ConsoleView.printSection("Create New Quiz");
        String title        = InputHelper.getString("Quiz Title");
        int    totalMarks   = InputHelper.getInt("Total Marks");
        int    numQ         = InputHelper.getIntInRange("Number of Questions", 1, 20);
        String schedDate    = InputHelper.getOptionalString("Scheduled/Due Date (YYYY-MM-DD, leave blank if none)");

        int quizId = quizDAO.insertQuiz(courseId, title, createdBy, totalMarks,
                schedDate.isEmpty() ? null : schedDate);
        if (quizId < 0) { ConsoleView.error("Failed to create quiz."); return; }

        ConsoleView.success("Quiz created. Now add " + numQ + " question(s).");

        for (int i = 1; i <= numQ; i++) {
            ConsoleView.printLine();
            System.out.println("  --- Question " + i + " ---");
            QuizQuestion q = new QuizQuestion();
            q.setQuizId(quizId);
            q.setQuestion(InputHelper.getString("Question"));
            q.setOptionA(InputHelper.getString("Option A"));
            q.setOptionB(InputHelper.getString("Option B"));
            q.setOptionC(InputHelper.getString("Option C"));
            q.setOptionD(InputHelper.getString("Option D"));
            String ans = InputHelper.getString("Correct Answer (A/B/C/D)").toUpperCase();
            q.setCorrectAns(ans);
            q.setMarks(InputHelper.getInt("Marks for this question"));
            quizDAO.insertQuestion(q);
        }
        ConsoleView.success("All questions saved for quiz: " + title);

        // Notify enrolled students
        String notifMsg = "New quiz added: " + title + ". Total marks: " + totalMarks +
                (schedDate.isEmpty() ? "" : ". Due: " + schedDate);
        notifDAO.broadcastToRole("STUDENT", "NEW QUIZ: " + title, notifMsg);
    }

    // ── Student: take a quiz ───────────────────────────────────
    public void takeQuiz(int quizId, int studentId) {
        if (quizDAO.hasSubmitted(quizId, studentId)) {
            ConsoleView.warn("You have already submitted this quiz.");
            return;
        }
        List<QuizQuestion> questions = quizDAO.findQuestions(quizId);
        if (questions.isEmpty()) {
            ConsoleView.info("No questions found for this quiz.");
            return;
        }

        ConsoleView.printSection("Taking Quiz — ID: " + quizId);
        int score = 0;
        int qNum  = 1;

        for (QuizQuestion q : questions) {
            q.display(qNum++);
            String ans = InputHelper.getString("Your answer (A/B/C/D)").toUpperCase();
            if (ans.equals(q.getCorrectAns())) {
                score += q.getMarks();
                System.out.println("  ✓ Correct! +" + q.getMarks() + " mark(s)");
            } else {
                System.out.println("  ✗ Wrong. Correct answer: " + q.getCorrectAns());
            }
            System.out.println();
        }

        quizDAO.saveSubmission(quizId, studentId, score);
        ConsoleView.printLine();
        ConsoleView.success("Quiz submitted! Your score: " + score + " / " +
                questions.stream().mapToInt(QuizQuestion::getMarks).sum());
    }

    // ── Print all quizzes ──────────────────────────────────────
    public void printAll() {
        ConsoleView.printSection("All Quizzes");
        List<Quiz> list = quizDAO.findAll();
        if (list.isEmpty()) { ConsoleView.info("No quizzes found."); return; }
        for (Quiz q : list) System.out.println("  " + q);
    }

    // ── Print quizzes available to a student (enrolled courses only) ──
    public void printAvailableForStudent(int studentId) {
        ConsoleView.printSection("Available Quizzes (Your Enrolled Courses)");
        List<Quiz> list = quizDAO.findAvailableForStudent(studentId);
        if (list.isEmpty()) { ConsoleView.info("No quizzes available for your courses."); return; }
        for (Quiz q : list) {
            String done = quizDAO.hasSubmitted(q.getQuizId(), studentId) ? " [DONE]" : " [PENDING]";
            System.out.println("  " + q + done);
        }
    }

    // ── Print quizzes for a course ─────────────────────────────
    public void printByCourse(int courseId) {
        ConsoleView.printSection("Quizzes for Course");
        List<Quiz> list = quizDAO.findByCourse(courseId);
        if (list.isEmpty()) { ConsoleView.info("No quizzes for this course."); return; }
        for (Quiz q : list) System.out.println("  " + q);
    }

    // ── View results (lecturer) ────────────────────────────────
    public void printResults(int quizId) {
        ConsoleView.printSection("Quiz Results — ID: " + quizId);
        quizDAO.printResults(quizId);
    }

    // ── Student's scores ───────────────────────────────────────
    public void printMyScores(int studentId) {
        ConsoleView.printSection("My Quiz Scores");
        quizDAO.printMyScores(studentId);
    }

    // ── Upcoming quizzes for a student (not yet taken) ────────
    public List<Quiz> getUpcomingForStudent(int studentId) {
        return quizDAO.findUpcomingForStudent(studentId);
    }

    public void printUpcomingForStudent(int studentId) {
        ConsoleView.printSection("Upcoming Quizzes (Not Yet Attempted)");
        List<Quiz> list = quizDAO.findUpcomingForStudent(studentId);
        if (list.isEmpty()) { ConsoleView.info("No pending quizzes."); return; }
        for (Quiz q : list) System.out.println("  " + q);
    }
}
