package com.librarymanagement.dao;

import com.librarymanagement.config.DatabaseConfig;
import com.librarymanagement.model.BorrowRecord;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowRecordDAO {

    public static void createBorrowRecord(BorrowRecord record) throws SQLException {
        String sql = "INSERT INTO borrow_records (student_id, book_id, book_title, author, " +
                     "borrow_date, due_date, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, record.getStudentId());
            pstmt.setInt(2, record.getBookId());
            pstmt.setString(3, record.getBookTitle());
            pstmt.setString(4, record.getAuthor());
            pstmt.setDate(5, Date.valueOf(record.getBorrowDate()));
            pstmt.setDate(6, Date.valueOf(record.getDueDate()));
            pstmt.setString(7, record.getStatus());

            pstmt.executeUpdate();
        }
    }

    public static boolean returnBook(int recordId, LocalDate returnDate, double fine) throws SQLException {
        String sql = "UPDATE borrow_records SET return_date = ?, status = ?, fine = ? WHERE record_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(returnDate));
            pstmt.setString(2, "Returned");
            pstmt.setDouble(3, fine);
            pstmt.setInt(4, recordId);

            int updated = pstmt.executeUpdate();
            return updated > 0;
        }
    }

    public static List<BorrowRecord> getStudentBorrowHistory(int studentId) throws SQLException {
        List<BorrowRecord> history = new ArrayList<>();
        String sql = "SELECT * FROM borrow_records WHERE student_id = ? ORDER BY borrow_date DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                BorrowRecord record = new BorrowRecord();
                record.setRecordId(rs.getInt("record_id"));
                record.setStudentId(rs.getInt("student_id"));
                record.setBookId(rs.getInt("book_id"));
                record.setBookTitle(rs.getString("book_title"));
                record.setAuthor(rs.getString("author"));
                record.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
                record.setDueDate(rs.getDate("due_date").toLocalDate());

                Date returnDateSQL = rs.getDate("return_date");
                if (returnDateSQL != null) {
                    record.setReturnDate(returnDateSQL.toLocalDate());
                }

                record.setStatus(rs.getString("status"));
                record.setFine(rs.getDouble("fine"));

                history.add(record);
            }
        }

        return history;
    }

    public static List<BorrowRecord> getStudentActiveBorrows(int studentId) throws SQLException {
        List<BorrowRecord> activeBorrows = new ArrayList<>();
        String sql = "SELECT * FROM borrow_records WHERE student_id = ? AND status = 'Active' " +
                     "ORDER BY due_date ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                BorrowRecord record = new BorrowRecord();
                record.setRecordId(rs.getInt("record_id"));
                record.setStudentId(rs.getInt("student_id"));
                record.setBookId(rs.getInt("book_id"));
                record.setBookTitle(rs.getString("book_title"));
                record.setAuthor(rs.getString("author"));
                record.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
                record.setDueDate(rs.getDate("due_date").toLocalDate());
                record.setStatus(rs.getString("status"));

                activeBorrows.add(record);
            }
        }

        return activeBorrows;
    }

    public static int getActiveBorrowCount(int studentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM borrow_records WHERE student_id = ? AND status = 'Active'";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return 0;
    }

    public static BorrowRecord getBorrowRecordById(int recordId) throws SQLException {
        String sql = "SELECT * FROM borrow_records WHERE record_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, recordId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                BorrowRecord record = new BorrowRecord();
                record.setRecordId(rs.getInt("record_id"));
                record.setStudentId(rs.getInt("student_id"));
                record.setBookId(rs.getInt("book_id"));
                record.setBookTitle(rs.getString("book_title"));
                record.setAuthor(rs.getString("author"));
                record.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
                record.setDueDate(rs.getDate("due_date").toLocalDate());

                Date returnDateSQL = rs.getDate("return_date");
                if (returnDateSQL != null) {
                    record.setReturnDate(returnDateSQL.toLocalDate());
                }

                record.setStatus(rs.getString("status"));
                record.setFine(rs.getDouble("fine"));

                return record;
            }
        }

        return null;
    }
}
