package com.librarymanagement.service;

import com.librarymanagement.dao.UserDao;
import com.librarymanagement.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserDao userDao = new UserDao();

    public Optional<User> login(String registrationId, String password, String type) throws SQLException {
        return userDao.findActiveUserForLogin(registrationId, password, type);
    }

    public void registerUser(User user) throws SQLException {
        userDao.create(user);
    }

    public void updateUser(User user) throws SQLException {
        userDao.update(user);
    }

    public void deleteUser(int userId) throws SQLException {
        userDao.delete(userId);
    }

    public List<User> getAllStudents() throws SQLException {
        return userDao.findAllStudents();
    }

    public Optional<User> getUserById(int userId) throws SQLException {
        return userDao.findById(userId);
    }
}

