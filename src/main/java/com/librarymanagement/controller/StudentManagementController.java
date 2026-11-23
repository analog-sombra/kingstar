package com.librarymanagement.controller;

import com.librarymanagement.model.User;
import com.librarymanagement.service.UserService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentManagementController {
    private User currentUser;
    private Stage stage;
    private Scene dashboardScene;
    private TeacherDashboardController parentController;
    private UserService userService = new UserService();
    private TableView<User> studentsTable;
    private List<User> students = new ArrayList<>();

    public StudentManagementController(User user, Stage stage, Scene dashboardScene, TeacherDashboardController parentController) {
        this.currentUser = user;
        this.stage = stage;
        this.dashboardScene = dashboardScene;
        this.parentController = parentController;
    }

    public void showStudentsPage() {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24));
        mainContent.setStyle("-fx-background-color: #f3f4f6;");

        // Header Section
        HBox headerBox = createHeaderSection();
        mainContent.getChildren().add(headerBox);

        // Filter and Action Section
        HBox filterBox = createFilterSection();
        mainContent.getChildren().add(filterBox);

        // Students Table
        studentsTable = createStudentsTable();
        ScrollPane scrollPane = new ScrollPane(studentsTable);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        mainContent.getChildren().add(scrollPane);

        // Update dashboard scene
        javafx.scene.layout.BorderPane root = (javafx.scene.layout.BorderPane) dashboardScene.getRoot();
        root.setCenter(mainContent);
    }

    private HBox createHeaderSection() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(10);

        Button backBtn = new Button("← Back to Dashboard");
        backBtn.setStyle(
                "-fx-background-color: #e5e7eb;" +
                        "-fx-text-fill: #1f2937;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 12;" +
                        "-fx-background-radius: 6;");
        backBtn.setOnAction(e -> parentController.refreshDashboard());

        Label title = new Label("👥 Students Management");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button registerBtn = new Button("➕ Register New Student");
        registerBtn.setPrefHeight(38);
        registerBtn.setStyle(
                "-fx-background-color: #10b981;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 6;");
        registerBtn.setOnAction(e -> showRegisterStudentDialog());

        header.getChildren().addAll(backBtn, title, spacer, registerBtn);
        return header;
    }

    private HBox createFilterSection() {
        HBox filterBox = new HBox(12);
        filterBox.setPadding(new Insets(12));
        filterBox.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-width: 1; -fx-background-radius: 8;");

        Label searchLabel = new Label("Search:");
        searchLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151;");

        TextField searchField = new TextField();
        searchField.setPromptText("Search by name, email, or registration ID...");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-padding: 8; -fx-border-radius: 4;");

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All Status", "Active", "Inactive", "Suspended");
        statusFilter.setValue("All Status");
        statusFilter.setPrefWidth(150);

        Button resetBtn = new Button("🔄 Reset");
        resetBtn.setStyle(
                "-fx-background-color: #6b7280;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 12;" +
                        "-fx-background-radius: 4;");
        resetBtn.setOnAction(e -> {
            searchField.clear();
            statusFilter.setValue("All Status");
            studentsTable.getItems().clear();
            studentsTable.getItems().addAll(students);
        });

        Button searchBtn = new Button("🔍 Search");
        searchBtn.setStyle(
                "-fx-background-color: #10b981;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 12;" +
                        "-fx-background-radius: 4;");
        searchBtn.setOnAction(e -> performStudentSearch(searchField.getText(), statusFilter.getValue()));

        // Allow Enter key in search field to trigger search
        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                performStudentSearch(searchField.getText(), statusFilter.getValue());
            }
        });

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        filterBox.getChildren().addAll(searchLabel, searchField, statusFilter, searchBtn, resetBtn, spacer);
        return filterBox;
    }

    private void performStudentSearch(String searchText, String status) {
        try {
            studentsTable.getItems().clear();
            
            List<User> filteredStudents = new ArrayList<>(students);
            
            // Filter by search text (name, email, registration ID)
            if (searchText != null && !searchText.trim().isEmpty()) {
                String search = searchText.toLowerCase().trim();
                filteredStudents = filteredStudents.stream()
                        .filter(u -> u.getName().toLowerCase().contains(search) ||
                                u.getEmail().toLowerCase().contains(search) ||
                                (u.getRegistrationId() != null && u.getRegistrationId().toLowerCase().contains(search)))
                        .toList();
            }
            
            // Filter by status (if applicable based on your User model)
            if (status != null && !status.equals("All Status")) {
                // Add status filtering if your User model has a status field
                // For now, this is a placeholder for future enhancement
            }
            
            studentsTable.getItems().addAll(filteredStudents);
        } catch (Exception e) {
            showError("Search Error", "Error during search: " + e.getMessage());
        }
    }

    private TableView<User> createStudentsTable() {
        TableView<User> table = new TableView<>();
        table.setPrefHeight(400);
        table.setStyle("-fx-font-size: 11;");

        TableColumn<User, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(40);

        TableColumn<User, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(150);

        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailColumn.setPrefWidth(200);

        TableColumn<User, String> regIdColumn = new TableColumn<>("Registration ID");
        regIdColumn.setCellValueFactory(new PropertyValueFactory<>("registrationId"));
        regIdColumn.setPrefWidth(120);

        TableColumn<User, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeColumn.setPrefWidth(80);

        TableColumn<User, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setPrefWidth(100);

        TableColumn<User, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setPrefWidth(150);
        actionColumn.setCellFactory(param -> new TableCell<User, Void>() {
            private final Button viewBtn = new Button("👁️ View");
            private final Button editBtn = new Button("✏️ Edit");
            private final Button deleteBtn = new Button("🗑️ Delete");

            {
                viewBtn.setPrefWidth(45);
                viewBtn.setStyle("-fx-font-size: 10; -fx-padding: 4;");
                editBtn.setPrefWidth(45);
                editBtn.setStyle("-fx-font-size: 10; -fx-padding: 4;");
                deleteBtn.setPrefWidth(50);
                deleteBtn.setStyle("-fx-font-size: 10; -fx-padding: 4; -fx-text-fill: white; -fx-background-color: #dc2626;");

                viewBtn.setOnAction(event -> {
                    User student = getTableView().getItems().get(getIndex());
                    showStudentDetails(student);
                });

                editBtn.setOnAction(event -> {
                    User student = getTableView().getItems().get(getIndex());
                    showEditStudentDialog(student);
                });

                deleteBtn.setOnAction(event -> {
                    User student = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(student);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox actions = new HBox(3, viewBtn, editBtn, deleteBtn);
                    setGraphic(actions);
                }
            }
        });

        table.getColumns().addAll(idColumn, nameColumn, emailColumn, regIdColumn, typeColumn, statusColumn, actionColumn);

        // Assign to instance variable
        studentsTable = table;
        
        // Load student data AFTER assigning
        loadStudentsData();

        return table;
    }

    private void loadStudentsData() {
        try {
            students = userService.getAllStudents();
            studentsTable.getItems().addAll(students);
        } catch (SQLException e) {
            showError("Error", "Unable to load students: " + e.getMessage());
        }
    }

    private void showRegisterStudentDialog() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Register New Student");
        dialog.setHeaderText("Enter Student Details");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("Enter student full name");

        TextField emailField = new TextField();
        emailField.setPromptText("Enter student email");

        TextField regIdField = new TextField();
        regIdField.setPromptText("Enter registration ID (e.g., STU001)");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm password");

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Active", "Inactive", "Suspended");
        statusCombo.setValue("Active");

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Registration ID:"), 0, 2);
        grid.add(regIdField, 1, 2);
        grid.add(new Label("Password:"), 0, 3);
        grid.add(passwordField, 1, 3);
        grid.add(new Label("Confirm Password:"), 0, 4);
        grid.add(confirmPasswordField, 1, 4);
        grid.add(new Label("Status:"), 0, 5);
        grid.add(statusCombo, 1, 5);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<User> result = dialog.showAndWait();
        if (result.isPresent()) {
            // Comprehensive validation
            String validationError = validateStudentFields(
                nameField.getText(),
                emailField.getText(),
                regIdField.getText(),
                passwordField.getText(),
                confirmPasswordField.getText()
            );
            
            if (validationError != null) {
                showError("Validation Error", validationError);
                return;
            }

            User newStudent = new User();
            newStudent.setName(nameField.getText().trim());
            newStudent.setEmail(emailField.getText().trim());
            newStudent.setRegistrationId(regIdField.getText().trim());
            newStudent.setPassword(passwordField.getText());
            newStudent.setType("STUDENT");
            newStudent.setStatus(statusCombo.getValue());

            try {
                userService.registerUser(newStudent);
                showInfo("Success", "Student registered successfully!");
                studentsTable.getItems().clear();
                loadStudentsData();
            } catch (SQLException e) {
                showError("Error", "Unable to register student: " + e.getMessage());
            }
        }
    }

    private String validateStudentFields(String name, String email, String registrationId, 
                                        String password, String confirmPassword) {
        // Name validation
        if (name == null || name.trim().isEmpty()) {
            return "Full Name is required and cannot be empty";
        }
        if (name.trim().length() < 3) {
            return "Full Name must be at least 3 characters long";
        }
        if (name.trim().length() > 50) {
            return "Full Name cannot exceed 50 characters";
        }
        if (!name.matches("[a-zA-Z\\s]+")) {
            return "Full Name can only contain letters and spaces";
        }
        
        // Email validation
        if (email == null || email.trim().isEmpty()) {
            return "Email is required and cannot be empty";
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!email.trim().matches(emailRegex)) {
            return "Please enter a valid email address";
        }
        if (email.trim().length() > 100) {
            return "Email cannot exceed 100 characters";
        }
        
        // Registration ID validation
        if (registrationId == null || registrationId.trim().isEmpty()) {
            return "Registration ID is required and cannot be empty";
        }
        if (registrationId.trim().length() < 4) {
            return "Registration ID must be at least 4 characters";
        }
        if (registrationId.trim().length() > 20) {
            return "Registration ID cannot exceed 20 characters";
        }
        
        // Password validation
        if (password == null || password.isEmpty()) {
            return "Password is required";
        }
        if (password.length() < 8 || password.length() > 16) {
            return "Password must be between 8 and 16 characters";
        }
        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$")) {
            return "Password must contain: uppercase, lowercase, number, and special character (@$!%*?&)";
        }
        
        // Password confirmation
        if (!password.equals(confirmPassword)) {
            return "Passwords do not match";
        }
        
        return null; // All validations passed
    }

    private void showStudentDetails(User student) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Student Details");
        alert.setHeaderText(student.getName());

        String details = "Name: " + student.getName() + "\n" +
                "Email: " + student.getEmail() + "\n" +
                "Registration ID: " + student.getRegistrationId() + "\n" +
                "Type: " + student.getType() + "\n" +
                "Status: " + student.getStatus();

        alert.setContentText(details);
        alert.showAndWait();
    }

    private void showEditStudentDialog(User student) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Edit Student");
        dialog.setHeaderText("Edit Student Details");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(student.getName());
        TextField emailField = new TextField(student.getEmail());
        TextField regIdField = new TextField(student.getRegistrationId());

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Active", "Inactive", "Suspended");
        statusCombo.setValue(student.getStatus());

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Registration ID:"), 0, 2);
        grid.add(regIdField, 1, 2);
        grid.add(new Label("Status:"), 0, 3);
        grid.add(statusCombo, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<User> result = dialog.showAndWait();
        if (result.isPresent()) {
            // Validation for edit (no password in edit mode)
            String validationError = validateEditStudentFields(
                nameField.getText(),
                emailField.getText(),
                regIdField.getText()
            );
            
            if (validationError != null) {
                showError("Validation Error", validationError);
                return;
            }
            
            student.setName(nameField.getText().trim());
            student.setEmail(emailField.getText().trim());
            student.setRegistrationId(regIdField.getText().trim());
            student.setStatus(statusCombo.getValue());

            try {
                userService.updateUser(student);
                showInfo("Success", "Student updated successfully!");
                studentsTable.getItems().clear();
                loadStudentsData();
            } catch (SQLException e) {
                showError("Error", "Unable to update student: " + e.getMessage());
            }
        }
    }

    private String validateEditStudentFields(String name, String email, String registrationId) {
        // Name validation
        if (name == null || name.trim().isEmpty()) {
            return "Full Name is required and cannot be empty";
        }
        if (name.trim().length() < 3) {
            return "Full Name must be at least 3 characters long";
        }
        if (name.trim().length() > 50) {
            return "Full Name cannot exceed 50 characters";
        }
        if (!name.matches("[a-zA-Z\\s]+")) {
            return "Full Name can only contain letters and spaces";
        }
        
        // Email validation
        if (email == null || email.trim().isEmpty()) {
            return "Email is required and cannot be empty";
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!email.trim().matches(emailRegex)) {
            return "Please enter a valid email address";
        }
        if (email.trim().length() > 100) {
            return "Email cannot exceed 100 characters";
        }
        
        // Registration ID validation
        if (registrationId == null || registrationId.trim().isEmpty()) {
            return "Registration ID is required and cannot be empty";
        }
        if (registrationId.trim().length() < 4) {
            return "Registration ID must be at least 4 characters";
        }
        if (registrationId.trim().length() > 20) {
            return "Registration ID cannot exceed 20 characters";
        }
        
        return null; // All validations passed
    }

    private void showDeleteConfirmation(User student) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Delete Student?");
        alert.setContentText("Are you sure you want to delete '" + student.getName() + "'? This action cannot be undone.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                userService.deleteUser(student.getId());
                showInfo("Success", "Student deleted successfully!");
                studentsTable.getItems().clear();
                loadStudentsData();
            } catch (SQLException e) {
                showError("Error", "Unable to delete student: " + e.getMessage());
            }
        }
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
