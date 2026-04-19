package com.smartclassroom.dao;

import com.smartclassroom.model.Quiz;
import com.smartclassroom.model.QuizQuestion;
import com.smartclassroom.util.DBConnection;
import com.smartclassroom.view.ConsoleView;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizDAO {

    // ── Insert quiz ───────────────────────────────────────────
    public int insertQuiz(int courseId, String title, int createdBy, int totalMarks, String scheduledDate) {
        String sql = "INSERT INTO quizzes (course_id,title,created_by,total_marks,scheduled_date) VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, courseId); ps.setString(2, title);
            ps.setInt(3, createdBy); ps.setInt(4, totalMarks);
            if (scheduledDate != null && !scheduledDate.isEmpty())
                ps.setString(5, scheduledDate);
            else
                ps.setNull(5, Types.DATE);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
        return -1;
    }

    // ── Insert question ───────────────────────────────────────
    public boolean insertQuestion(QuizQuestion q) {
        String sql = "INSERT INTO quiz_questions (quiz_id,question,option_a,option_b,option_c,option_d,correct_ans,marks) " +
                     "VALUES(?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, q.getQuizId()); ps.setString(2, q.getQuestion());
            ps.setString(3, q.getOptionA()); ps.setString(4, q.getOptionB());
            ps.setString(5, q.getOptionC()); ps.setString(6, q.getOptionD());
            ps.setString(7, q.getCorrectAns()); ps.setInt(8, q.getMarks());
            ps.executeUpdate(); return true;
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); return false; }
    }

    // ── Get all quizzes ───────────────────────────────────────
    public List<Quiz> findAll() {
        String sql = "SELECT q.*, c.course_name, u.full_name AS created_by_name " +
                     "FROM quizzes q JOIN courses c ON q.course_id=c.course_id " +
                     "JOIN users u ON q.created_by=u.user_id ORDER BY q.created_at DESC";
        List<Quiz> list = new ArrayList<>();
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) list.add(mapQuiz(rs));
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
        return list;
    }

    // ── Quizzes for courses a student is enrolled in ──────────
    public List<Quiz> findAvailableForStudent(int studentId) {
        String sql = "SELECT q.*, c.course_name, u.full_name AS created_by_name " +
                     "FROM quizzes q " +
                     "JOIN courses c ON q.course_id=c.course_id " +
                     "JOIN users u ON q.created_by=u.user_id " +
                     "JOIN enrollments e ON e.course_id=q.course_id AND e.student_id=? " +
                     "ORDER BY q.created_at DESC";
        List<Quiz> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapQuiz(rs));
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
        return list;
    }

    // ── Get quizzes for a course ──────────────────────────────
    public List<Quiz> findByCourse(int courseId) {
        String sql = "SELECT q.*, c.course_name, u.full_name AS created_by_name " +
                     "FROM quizzes q JOIN courses c ON q.course_id=c.course_id " +
                     "JOIN users u ON q.created_by=u.user_id WHERE q.course_id=?";
        List<Quiz> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapQuiz(rs));
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
        return list;
    }

    // ── Get questions for a quiz ──────────────────────────────
    public List<QuizQuestion> findQuestions(int quizId) {
        String sql = "SELECT * FROM quiz_questions WHERE quiz_id=? ORDER BY question_id";
        List<QuizQuestion> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, quizId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                QuizQuestion q = new QuizQuestion();
                q.setQuestionId(rs.getInt("question_id"));
                q.setQuizId(rs.getInt("quiz_id"));
                q.setQuestion(rs.getString("question"));
                q.setOptionA(rs.getString("option_a"));
                q.setOptionB(rs.getString("option_b"));
                q.setOptionC(rs.getString("option_c"));
                q.setOptionD(rs.getString("option_d"));
                q.setCorrectAns(rs.getString("correct_ans"));
                q.setMarks(rs.getInt("marks"));
                list.add(q);
            }
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
        return list;
    }

    // ── Check if student already submitted ───────────────────
    public boolean hasSubmitted(int quizId, int studentId) {
        String sql = "SELECT submission_id FROM quiz_submissions WHERE quiz_id=? AND student_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, quizId); ps.setInt(2, studentId);
            return ps.executeQuery().next();
        } catch (SQLException e) { return false; }
    }

    // ── Save submission ───────────────────────────────────────
    public boolean saveSubmission(int quizId, int studentId, int score) {
        String sql = "INSERT INTO quiz_submissions (quiz_id, student_id, score) VALUES(?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, quizId); ps.setInt(2, studentId); ps.setInt(3, score);
            ps.executeUpdate(); return true;
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); return false; }
    }

    // ── View results for a quiz (lecturer view) ───────────────
    public void printResults(int quizId) {
        String sql = "SELECT u.full_name, qs.score, qs.submitted_at " +
                     "FROM quiz_submissions qs JOIN users u ON qs.student_id=u.user_id " +
                     "WHERE qs.quiz_id=? ORDER BY qs.score DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, quizId);
            ResultSet rs = ps.executeQuery();
            System.out.printf("  %-25s | %-6s | %s%n", "Student","Score","Submitted At");
            ConsoleView.printLine();
            while (rs.next()) {
                System.out.printf("  %-25s | %-6d | %s%n",
                        rs.getString("full_name"), rs.getInt("score"), rs.getString("submitted_at"));
            }
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
    }

    // ── Student's own quiz scores ─────────────────────────────
    public void printMyScores(int studentId) {
        String sql = "SELECT q.title, qs.score, q.total_marks, qs.submitted_at " +
                     "FROM quiz_submissions qs JOIN quizzes q ON qs.quiz_id=q.quiz_id " +
                     "WHERE qs.student_id=? ORDER BY qs.submitted_at DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            System.out.printf("  %-30s | %-12s | %s%n", "Quiz","Score","Submitted At");
            ConsoleView.printLine();
            while (rs.next()) {
                System.out.printf("  %-30s | %d / %-8d | %s%n",
                        rs.getString("title"), rs.getInt("score"),
                        rs.getInt("total_marks"), rs.getString("submitted_at"));
            }
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
    }

    // ── Upcoming quizzes for a student (not yet submitted) ───
    public List<Quiz> findUpcomingForStudent(int studentId) {
        String sql = "SELECT q.*, c.course_name, u.full_name AS created_by_name " +
                     "FROM quizzes q " +
                     "JOIN courses c ON q.course_id=c.course_id " +
                     "JOIN users u ON q.created_by=u.user_id " +
                     "JOIN enrollments e ON e.course_id=q.course_id AND e.student_id=? " +
                     "WHERE q.quiz_id NOT IN " +
                     "  (SELECT quiz_id FROM quiz_submissions WHERE student_id=?) " +
                     "ORDER BY q.scheduled_date ASC, q.created_at DESC";
        List<Quiz> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId); ps.setInt(2, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapQuiz(rs));
        } catch (SQLException e) { ConsoleView.error(e.getMessage()); }
        return list;
    }

    private Quiz mapQuiz(ResultSet rs) throws SQLException {
        Quiz q = new Quiz();
        q.setQuizId(rs.getInt("quiz_id"));
        q.setCourseId(rs.getInt("course_id"));
        q.setTitle(rs.getString("title"));
        q.setCreatedBy(rs.getInt("created_by"));
        q.setTotalMarks(rs.getInt("total_marks"));
        q.setScheduledDate(rs.getString("scheduled_date"));
        q.setCreatedAt(rs.getString("created_at"));
        q.setCourseName(rs.getString("course_name"));
        q.setCreatedByName(rs.getString("created_by_name"));
        return q;
    }
}
