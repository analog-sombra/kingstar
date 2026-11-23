package com.librarymanagement.service;

import com.librarymanagement.dao.BookDao;
import com.librarymanagement.model.Book;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class BookService {
    private final BookDao bookDao = new BookDao();

    public List<Book> getAllBooks() throws SQLException {
        return bookDao.findAll();
    }

    public int addBook(Book book) throws SQLException {
        return bookDao.create(book);
    }

    public void updateBook(Book book) throws SQLException {
        bookDao.update(book);
    }

    public void deleteBook(int bookId) throws SQLException {
        bookDao.delete(bookId);
    }

    public Optional<Book> getBookById(int bookId) throws SQLException {
        return bookDao.findById(bookId);
    }

    public List<Book> searchBooks(String query) throws SQLException {
        return bookDao.search(query);
    }
}

