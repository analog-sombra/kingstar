package com.librarymanagement.model;

import java.time.LocalDate;

public class BorrowRecord {
    private int recordId;
    private int studentId;
    private int bookId;
    private String bookTitle;
    private String author;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status;
    private double fine;

    public BorrowRecord() {}

    public BorrowRecord(int studentId, int bookId, String bookTitle, String author,
                       LocalDate borrowDate, LocalDate dueDate, String status) {
        this.studentId = studentId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.author = author;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = null;
        this.status = status;
        this.fine = 0.0;
    }

    // Getters and Setters
    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getFine() { return fine; }
    public void setFine(double fine) { this.fine = fine; }
}
