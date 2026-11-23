package com.librarymanagement.controller;

import com.librarymanagement.model.User;
import com.librarymanagement.service.UserService;
import com.librarymanagement.service.BookService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TeacherDashboardController {
    private User currentUser;
    private Stage stage;
    private Scene dashboardScene;
    private BookService bookService = new BookService();
    private UserService userService = new UserService();
    private BorderPane rootPane;
    private String currentPage = "dashboard"; // Track current page

    public TeacherDashboardController(User user, Stage stage) {
        this.currentUser = user;
        this.stage = stage;
    }

    public void showDashboard() {
        rootPane = new BorderPane();
        rootPane.setStyle("-fx-background-color: #f3f4f6;");

        // Create Sidebar
        VBox sidebar = createSidebar();
        rootPane.setLeft(sidebar);

        // Create Top Navigation Bar
        HBox topBar = createTopBar();
        rootPane.setTop(topBar);

        // Create Main Content Area (Dashboard)
        VBox mainContent = createDashboardContent();
        rootPane.setCenter(mainContent);

        dashboardScene = new Scene(rootPane, 1200, 700);
        stage.setScene(dashboardScene);
        stage.setTitle("Teacher Dashboard - Library Management System");
        stage.show();
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(24));
        sidebar.setPrefWidth(250);
        sidebar.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #1d4ed8, #3b82f6);" +
                        "-fx-border-color: #e5e7eb;");

        // Logo
        Label logo = new Label("📚 LMS");
        logo.setStyle("-fx-text-fill: white; -fx-font-size: 24; -fx-font-weight: bold;");

        // Navigation Items
        VBox navItems = new VBox(12);

        Button dashboardBtn = createNavButton("🏠 Dashboard", currentPage.equals("dashboard"));
        Button booksBtn = createNavButton("📖 Books Management", currentPage.equals("books"));
        Button studentsBtn = createNavButton("👥 Students", currentPage.equals("students"));
        Button profileBtn = createNavButton("👤 Profile", currentPage.equals("profile"));

        dashboardBtn.setOnAction(e -> navigateTo("dashboard"));
        booksBtn.setOnAction(e -> navigateTo("books"));
        studentsBtn.setOnAction(e -> navigateTo("students"));
        profileBtn.setOnAction(e -> navigateTo("profile"));

        navItems.getChildren().addAll(dashboardBtn, booksBtn, studentsBtn, profileBtn);

        VBox spacer = new VBox();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("🚪 Logout");
        logoutBtn.setPrefWidth(200);
        logoutBtn.setPrefHeight(40);
        logoutBtn.setStyle(
                "-fx-background-color: #dc2626;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 13;" +
                        "-fx-background-radius: 8;");
        logoutBtn.setOnAction(e -> handleLogout());

        sidebar.getChildren().addAll(logo, navItems, spacer, logoutBtn);
        return sidebar;
    }

    private Button createNavButton(String text, boolean isActive) {
        Button button = new Button(text);
        button.setPrefWidth(200);
        button.setPrefHeight(40);
        String style = isActive
                ? "-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;"
                : "-fx-background-color: transparent; -fx-text-fill: rgba(255,255,255,0.8); -fx-font-weight: bold; -fx-background-radius: 8;";
        button.setStyle(style);
        button.setOnMouseEntered(e -> {
            if (!isActive)
                button.setStyle(
                        "-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        });
        button.setOnMouseExited(e -> {
            if (!isActive)
                button.setStyle(
                        "-fx-background-color: transparent; -fx-text-fill: rgba(255,255,255,0.8); -fx-font-weight: bold; -fx-background-radius: 8;");
        });
        return button;
    }

    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(16, 24, 16, 24));
        topBar.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-width: 0 0 1 0;");

        Label title = new Label("Welcome back, " + currentUser.getName() + "!");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label dateLabel = new Label("📅 " + java.time.LocalDate.now());
        dateLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #6b7280;");

        topBar.getChildren().addAll(title, spacer, dateLabel);
        return topBar;
    }

    private VBox createDashboardContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(24));
        content.setStyle("-fx-background-color: #f3f4f6;");

        Label heading = new Label("Dashboard Overview");
        heading.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        // Stats Cards
        HBox statsBox = new HBox(20);
        statsBox.getChildren().addAll(
                createStatCard("📖 Total Books", "0", "#3b82f6"),
                createStatCard("👥 Total Students", "0", "#10b981"),
                createStatCard("📚 Available Books", "0", "#f59e0b"),
                createStatCard("📋 Active Tasks", "0", "#8b5cf6"));

        // Quick Actions
        Label actionHeading = new Label("Quick Actions");
        actionHeading
                .setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #1f2937; -fx-padding: 16 0 8 0;");

        HBox actionBox = new HBox(12);
        Button addBookBtn = createActionButton("➕ Add New Book", "#3b82f6");
        Button registerStudentBtn = createActionButton("➕ Register Student", "#10b981");

        addBookBtn.setOnAction(e -> navigateTo("books"));
        registerStudentBtn.setOnAction(e -> navigateTo("students"));

        actionBox.getChildren().addAll(addBookBtn, registerStudentBtn);

        content.getChildren().addAll(heading, statsBox, actionHeading, actionBox);
        return content;
    }

    private HBox createStatCard(String title, String value, String color) {
        HBox card = new HBox();
        card.setPadding(new Insets(20));
        card.setPrefWidth(250);
        card.setPrefHeight(120);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 4, 0, 0, 2);");

        VBox contentBox = new VBox(8);
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #6b7280;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        contentBox.getChildren().addAll(titleLabel, valueLabel);
        card.getChildren().add(contentBox);
        return card;
    }

    private Button createActionButton(String text, String color) {
        Button button = new Button(text);
        button.setPrefWidth(160);
        button.setPrefHeight(45);
        button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;");
        return button;
    }

    private void navigateTo(String page) {
        currentPage = page;

        // Update sidebar
        VBox sidebar = createSidebar();
        rootPane.setLeft(sidebar);

        // Update content based on page
        switch (page) {
            case "books":
                showBooksPage();
                break;
            case "students":
                showStudentsPage();
                break;
            case "profile":
                showProfilePage();
                break;
            default:
                VBox content = createDashboardContent();
                rootPane.setCenter(content);
                break;
        }
    }

    public void showBooksPage() {
        currentPage = "books";
        try {
            BookManagementController bookController = new BookManagementController(currentUser, stage, dashboardScene,
                    this);
            bookController.showBooksPage();
        } catch (Exception e) {
            showError("Error", "Unable to open books page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void showStudentsPage() {
        currentPage = "students";
        try {
            StudentManagementController studentController = new StudentManagementController(currentUser, stage,
                    dashboardScene, this);
            studentController.showStudentsPage();
        } catch (Exception e) {
            showError("Error", "Unable to open students page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void showProfilePage() {
        currentPage = "profile";
        try {
            ProfileController profileController = new ProfileController(currentUser, stage, dashboardScene, this);
            profileController.showProfilePage();
        } catch (Exception e) {
            showError("Error", "Unable to open profile page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleLogout() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("Do you want to logout from your account?");

        if (alert.showAndWait().get() == javafx.scene.control.ButtonType.OK) {
            stage.close();
            try {
                new com.librarymanagement.app.MainApp().start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void refreshDashboard() {
        currentPage = "dashboard";
        navigateTo("dashboard");
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
