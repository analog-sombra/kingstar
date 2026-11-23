package com.librarymanagement.controller;

import com.librarymanagement.model.User;
import com.librarymanagement.model.BorrowRecord;
import com.librarymanagement.model.Book;
import com.librarymanagement.dao.BorrowRecordDAO;
import com.librarymanagement.dao.BookDao;
import com.librarymanagement.app.MainApp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StudentDashboardController {
    private User student;
    private Stage stage;
    private String currentPage = "Dashboard";
    private VBox mainContent;
    private final Map<String, Button> sidebarButtons = new HashMap<>();

    private static final String SIDEBAR_BTN_BASE_STYLE =
            "-fx-padding: 10;" +
            "-fx-font-size: 13;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-border: none;";

    private static final String SIDEBAR_BTN_INACTIVE_STYLE =
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #374151;" +
            SIDEBAR_BTN_BASE_STYLE;

    private static final String SIDEBAR_BTN_HOVER_STYLE =
            "-fx-background-color: #f3f4f6;" +
            "-fx-text-fill: #374151;" +
            SIDEBAR_BTN_BASE_STYLE;

    private static final String SIDEBAR_BTN_ACTIVE_STYLE =
            "-fx-background-color: #4f46e5;" +
            "-fx-text-fill: white;" +
            SIDEBAR_BTN_BASE_STYLE;

    public StudentDashboardController(User student, Stage stage) {
        this.student = student;
        this.stage = stage;
        initializeDashboard();
    }

    private void initializeDashboard() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f8fafc;");

        // Top Navigation Bar
        root.setTop(createTopBar());

        // Left Sidebar
        root.setLeft(createSidebar());

        // Main Content Area
        mainContent = new VBox();
        mainContent.setStyle("-fx-background-color: #f8fafc;");
        mainContent.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #f8fafc;");
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1200, 700);
        stage.setScene(scene);
        stage.setTitle("Student Dashboard - " + student.getName());
        stage.show();

        // Load initial page
        navigateTo("Dashboard");
    }

    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(12, 20, 12, 20));
        topBar.setStyle(
                "-fx-background-color: linear-gradient(to right, #4f46e5, #06b6d4);" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 4);");

        Label title = new Label("📚 Student Library Portal");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label welcome = new Label("Welcome, " + student.getName());
        welcome.setStyle("-fx-text-fill: white; -fx-font-size: 14;");

        topBar.getChildren().addAll(title, spacer, welcome);
        topBar.setAlignment(Pos.CENTER_LEFT);

        return topBar;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(12);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #e2e8f0;" +
                        "-fx-border-width: 0 1 0 0;");
        sidebar.setPrefWidth(200);

        Label navigation = new Label("NAVIGATION");
        navigation.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #6b7280;");

        Button dashboardBtn = createSidebarButton("📊 Dashboard", "Dashboard", true);
        Button booksBtn = createSidebarButton("📖 Books", "Books", true);
        Button historyBtn = createSidebarButton("📋 History", "History", true);
        Button profileBtn = createSidebarButton("👤 Profile", "Profile", true);
        Button logoutBtn = createSidebarButton("🚪 Logout", "Logout", false);

        logoutBtn.setStyle(
                "-fx-background-color: #fee2e2;" +
                        "-fx-text-fill: #dc2626;" +
                        "-fx-padding: 10;" +
                        "-fx-font-size: 13;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-border: none;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(navigation, dashboardBtn, booksBtn, historyBtn, profileBtn,
                spacer, logoutBtn);

        return sidebar;
    }

    private Button createSidebarButton(String text, String page, boolean selectable) {
        Button btn = new Button(text);
        btn.setPrefWidth(180);
        btn.setStyle(SIDEBAR_BTN_INACTIVE_STYLE);

        if (selectable) {
            sidebarButtons.put(page, btn);
            btn.setOnMouseEntered(e -> {
                if (!currentPage.equals(page)) {
                    btn.setStyle(SIDEBAR_BTN_HOVER_STYLE);
                }
            });

            btn.setOnMouseExited(e -> {
                if (!currentPage.equals(page)) {
                    btn.setStyle(SIDEBAR_BTN_INACTIVE_STYLE);
                }
            });
        }

        btn.setOnAction(e -> navigateTo(page));

        return btn;
    }

    private void navigateTo(String page) {
        currentPage = page;
        updateSidebarSelection();

        mainContent.getChildren().clear();

        switch (page) {
            case "Dashboard":
                mainContent.getChildren().add(createDashboardView());
                break;
            case "Books":
                mainContent.getChildren().add(createBooksView());
                break;
            case "History":
                mainContent.getChildren().add(createHistoryView());
                break;
            case "Profile":
                mainContent.getChildren().add(createProfileView());
                break;
            case "Logout":
                handleLogout();
                break;
        }
    }

    private void updateSidebarSelection() {
        for (Map.Entry<String, Button> entry : sidebarButtons.entrySet()) {
            entry.getValue().setStyle(entry.getKey().equals(currentPage) ? SIDEBAR_BTN_ACTIVE_STYLE : SIDEBAR_BTN_INACTIVE_STYLE);
        }
    }

    private VBox createDashboardView() {
        VBox dashboard = new VBox(20);
        dashboard.setPadding(new Insets(0));

        Label title = new Label("📊 Dashboard");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        // Student Info Card
        VBox infoCard = createInfoCard();

        // Active Borrows Section
        VBox borrowsSection = createActiveBorrowsSection();

        dashboard.getChildren().addAll(title, infoCard, borrowsSection);

        return dashboard;
    }

    private VBox createInfoCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #e2e8f0;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;");

        Label cardTitle = new Label("Student Information");
        cardTitle.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);

        addInfoRow(grid, 0, "Name:", student.getName());
        addInfoRow(grid, 1, "Registration ID:", student.getRegistrationId());
        addInfoRow(grid, 2, "Email:", student.getEmail());

        try {
            int activeBorrows = BorrowRecordDAO.getActiveBorrowCount(student.getId());
            addInfoRow(grid, 3, "Active Borrows:", activeBorrows + "/4");
        } catch (SQLException e) {
            addInfoRow(grid, 3, "Active Borrows:", "Error loading");
        }

        card.getChildren().addAll(cardTitle, new Separator(), grid);

        return card;
    }

    private void addInfoRow(GridPane grid, int row, String label, Object value) {
        Label labelNode = new Label(label);
        labelNode.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #6b7280;");

        Label valueNode = new Label(String.valueOf(value));
        valueNode.setStyle("-fx-font-size: 13; -fx-text-fill: #1f2937;");

        grid.add(labelNode, 0, row);
        grid.add(valueNode, 1, row);
    }

    private VBox createActiveBorrowsSection() {
        VBox section = new VBox(12);
        section.setPadding(new Insets(20));
        section.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #e2e8f0;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;");

        Label title = new Label("📚 Currently Borrowed Books");
        title.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        try {
            List<BorrowRecord> activeBorrows = BorrowRecordDAO.getStudentActiveBorrows(student.getId());

            if (activeBorrows.isEmpty()) {
                Label noBooks = new Label("You haven't borrowed any books yet.");
                noBooks.setStyle("-fx-font-size: 13; -fx-text-fill: #6b7280;");
                section.getChildren().addAll(title, new Separator(), noBooks);
            } else {
                TableView<BorrowRecord> borrowTable = new TableView<>();
                borrowTable.setPrefHeight(300);

                TableColumn<BorrowRecord, String> titleCol = new TableColumn<>("Book Title");
                titleCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBookTitle()));

                TableColumn<BorrowRecord, String> authorCol = new TableColumn<>("Author");
                authorCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAuthor()));

                TableColumn<BorrowRecord, String> borrowCol = new TableColumn<>("Borrowed");
                borrowCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBorrowDate().toString()));

                TableColumn<BorrowRecord, String> dueCol = new TableColumn<>("Due Date");
                dueCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDueDate().toString()));

                TableColumn<BorrowRecord, Void> returnCol = new TableColumn<>("Return");
                returnCol.setCellFactory(param -> new TableCell<BorrowRecord, Void>() {
                    private final Button returnBtn = new Button("Return");

                    {
                        returnBtn.setStyle("-fx-padding: 6 12; -fx-font-size: 12;");
                        returnBtn.setOnAction(event -> handleReturnBook(getTableView().getItems().get(getIndex())));
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : returnBtn);
                    }
                });

                borrowTable.getColumns().addAll(titleCol, authorCol, borrowCol, dueCol, returnCol);
                borrowTable.getItems().addAll(activeBorrows);

                section.getChildren().addAll(title, new Separator(), borrowTable);
            }
        } catch (SQLException e) {
            Label errorMsg = new Label("Error loading active borrows: " + e.getMessage());
            errorMsg.setStyle("-fx-text-fill: #dc2626;");
            section.getChildren().addAll(title, new Separator(), errorMsg);
        }

        return section;
    }

    private VBox createBooksView() {
        VBox booksView = new VBox(20);
        booksView.setPadding(new Insets(0));

        Label title = new Label("📖 Browse Books");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #1f2937;");
        TextField searchField = new TextField();
        searchField.setPromptText("Search by title, author, genre, or ISBN");
        searchField.setStyle("-fx-padding: 10; -fx-background-radius: 8;");
        searchField.setMaxWidth(420);

        try {
            TableView<com.librarymanagement.model.Book> booksTable = new TableView<>();
            booksTable.setPrefHeight(500);

            TableColumn<com.librarymanagement.model.Book, String> titleCol = new TableColumn<>("Title");
            titleCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitle()));
            titleCol.setPrefWidth(200);

            TableColumn<com.librarymanagement.model.Book, String> authorCol = new TableColumn<>("Author");
            authorCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAuthor()));

            TableColumn<com.librarymanagement.model.Book, String> genreCol = new TableColumn<>("Genre");
            genreCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getGenre()));

            TableColumn<com.librarymanagement.model.Book, Integer> copiesCol = new TableColumn<>("Available");
            copiesCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getAvailableCopies()));

            TableColumn<com.librarymanagement.model.Book, Void> borrowCol = new TableColumn<>("Action");
            borrowCol.setCellFactory(param -> new TableCell<com.librarymanagement.model.Book, Void>() {
                private final Button borrowBtn = new Button("Borrow");

                {
                    borrowBtn.setStyle("-fx-padding: 6 12; -fx-font-size: 12;");
                    borrowBtn.setOnAction(event -> handleBorrowBook(getTableView().getItems().get(getIndex())));
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        com.librarymanagement.model.Book book = getTableView().getItems().get(getIndex());
                        if (book.getAvailableCopies() > 0) {
                            borrowBtn.setDisable(false);
                        } else {
                            borrowBtn.setDisable(true);
                        }
                        setGraphic(borrowBtn);
                    }
                }
            });

            booksTable.getColumns().addAll(titleCol, authorCol, genreCol, copiesCol, borrowCol);

            List<com.librarymanagement.model.Book> books = new BookDao().findAll();
            ObservableList<Book> booksData = FXCollections.observableArrayList(books);
            FilteredList<Book> filteredBooks = new FilteredList<>(booksData, book -> true);

            searchField.textProperty().addListener((obs, oldValue, newValue) -> {
                String query = newValue == null ? "" : newValue.trim().toLowerCase(Locale.ROOT);
                filteredBooks.setPredicate(book -> matchesBookSearch(book, query));
            });

            SortedList<Book> sortedBooks = new SortedList<>(filteredBooks);
            sortedBooks.comparatorProperty().bind(booksTable.comparatorProperty());
            booksTable.setItems(sortedBooks);

            booksView.getChildren().addAll(title, searchField, booksTable);
        } catch (SQLException e) {
            Label errorMsg = new Label("Error loading books: " + e.getMessage());
            errorMsg.setStyle("-fx-text-fill: #dc2626;");
            booksView.getChildren().addAll(title, errorMsg);
        }

        return booksView;
    }

    private VBox createHistoryView() {
        VBox historyView = new VBox(20);
        historyView.setPadding(new Insets(0));

        Label title = new Label("📋 Borrowing History");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #1f2937;");
        TextField searchField = new TextField();
        searchField.setPromptText("Search by book, author, status, or date");
        searchField.setStyle("-fx-padding: 10; -fx-background-radius: 8;");
        searchField.setMaxWidth(420);

        try {
            List<BorrowRecord> history = BorrowRecordDAO.getStudentBorrowHistory(student.getId());
            TableView<BorrowRecord> historyTable = new TableView<>();
            historyTable.setPrefHeight(500);

            TableColumn<BorrowRecord, String> titleCol = new TableColumn<>("Book Title");
            titleCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBookTitle()));
            titleCol.setPrefWidth(200);

            TableColumn<BorrowRecord, String> authorCol = new TableColumn<>("Author");
            authorCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAuthor()));

            TableColumn<BorrowRecord, String> borrowCol = new TableColumn<>("Borrow Date");
            borrowCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBorrowDate().toString()));

            TableColumn<BorrowRecord, String> dueCol = new TableColumn<>("Due Date");
            dueCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDueDate().toString()));

            TableColumn<BorrowRecord, String> returnCol = new TableColumn<>("Returned");
            returnCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getReturnDate() != null ? cellData.getValue().getReturnDate().toString() : "Not returned"));

            TableColumn<BorrowRecord, String> statusCol = new TableColumn<>("Status");
            statusCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));

            TableColumn<BorrowRecord, Double> fineCol = new TableColumn<>("Fine (₹)");
            fineCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getFine()));

            historyTable.getColumns().addAll(titleCol, authorCol, borrowCol, dueCol, returnCol, statusCol, fineCol);

            ObservableList<BorrowRecord> historyData = FXCollections.observableArrayList(history);
            FilteredList<BorrowRecord> filteredHistory = new FilteredList<>(historyData, record -> true);
            searchField.textProperty().addListener((obs, oldValue, newValue) -> {
                String query = newValue == null ? "" : newValue.trim().toLowerCase(Locale.ROOT);
                filteredHistory.setPredicate(record -> matchesHistorySearch(record, query));
            });

            SortedList<BorrowRecord> sortedHistory = new SortedList<>(filteredHistory);
            sortedHistory.comparatorProperty().bind(historyTable.comparatorProperty());
            historyTable.setItems(sortedHistory);

            historyView.getChildren().addAll(title, searchField, historyTable);

            if (history.isEmpty()) {
                Label noHistory = new Label("No borrowing history yet.");
                noHistory.setStyle("-fx-font-size: 13; -fx-text-fill: #6b7280;");
                historyView.getChildren().add(noHistory);
            }
        } catch (SQLException e) {
            Label errorMsg = new Label("Error loading history: " + e.getMessage());
            errorMsg.setStyle("-fx-text-fill: #dc2626;");
            historyView.getChildren().addAll(title, errorMsg);
        }

        return historyView;
    }

    private boolean matchesBookSearch(Book book, String query) {
        if (query.isEmpty()) {
            return true;
        }

        return containsIgnoreCase(book.getTitle(), query)
                || containsIgnoreCase(book.getAuthor(), query)
                || containsIgnoreCase(book.getGenre(), query)
                || containsIgnoreCase(book.getIsbn(), query)
                || String.valueOf(book.getAvailableCopies()).contains(query);
    }

    private boolean matchesHistorySearch(BorrowRecord record, String query) {
        if (query.isEmpty()) {
            return true;
        }

        String returnDate = record.getReturnDate() == null ? "not returned" : record.getReturnDate().toString();
        return containsIgnoreCase(record.getBookTitle(), query)
                || containsIgnoreCase(record.getAuthor(), query)
                || containsIgnoreCase(record.getStatus(), query)
                || containsIgnoreCase(record.getBorrowDate().toString(), query)
                || containsIgnoreCase(record.getDueDate().toString(), query)
                || containsIgnoreCase(returnDate, query)
                || containsIgnoreCase(String.format(Locale.ROOT, "%.2f", record.getFine()), query);
    }

    private boolean containsIgnoreCase(String value, String query) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(query);
    }

    private VBox createProfileView() {
        VBox profileView = new VBox(20);
        profileView.setPadding(new Insets(0));

        Label title = new Label("👤 My Profile");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        VBox profileCard = new VBox(15);
        profileCard.setPadding(new Insets(20));
        profileCard.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #e2e8f0;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;");
        profileCard.setPrefWidth(500);

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);

        Label nameLabel = new Label("Full Name");
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #6b7280;");
        TextField nameField = new TextField(student.getName());
        nameField.setStyle("-fx-padding: 8;");
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);

        Label regLabel = new Label("Registration ID");
        regLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #6b7280;");
        TextField regField = new TextField(student.getRegistrationId());
        regField.setEditable(false);
        regField.setStyle("-fx-padding: 8; -fx-text-fill: #9ca3af;");
        grid.add(regLabel, 0, 1);
        grid.add(regField, 1, 1);

        Label emailLabel = new Label("Email");
        emailLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #6b7280;");
        TextField emailField = new TextField(student.getEmail());
        emailField.setStyle("-fx-padding: 8;");
        grid.add(emailLabel, 0, 2);
        grid.add(emailField, 1, 2);

        Label phoneLabel = new Label("Phone");
        phoneLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #6b7280;");
        TextField phoneField = new TextField("");
        phoneField.setPromptText("Enter phone number");
        phoneField.setStyle("-fx-padding: 8;");
        grid.add(phoneLabel, 0, 3);
        grid.add(phoneField, 1, 3);

        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(15, 0, 0, 0));

        Button updateBtn = new Button("Update Profile");
        updateBtn.setStyle(
                "-fx-background-color: #4f46e5;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 10 20;" +
                        "-fx-font-size: 13;" +
                        "-fx-background-radius: 6;");
        updateBtn.setOnAction(e -> showAlert(Alert.AlertType.INFORMATION, "Success", "Profile update feature coming soon!"));

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle(
                "-fx-background-color: #e5e7eb;" +
                        "-fx-text-fill: #374151;" +
                        "-fx-padding: 10 20;" +
                        "-fx-font-size: 13;" +
                        "-fx-background-radius: 6;");
        cancelBtn.setOnAction(e -> navigateTo("Dashboard"));

        buttonBox.getChildren().addAll(updateBtn, cancelBtn);

        profileCard.getChildren().addAll(
                new Label("Update Your Information"),
                new Separator(),
                grid,
                buttonBox
        );

        profileView.getChildren().addAll(title, profileCard);

        return profileView;
    }

    private void handleBorrowBook(com.librarymanagement.model.Book book) {
        try {
            int activeBorrows = BorrowRecordDAO.getActiveBorrowCount(student.getId());
            if (activeBorrows >= 4) {
                showAlert(Alert.AlertType.WARNING, "Borrow Limit", "You can only borrow maximum 4 books at a time!");
                return;
            }

            if (book.getAvailableCopies() <= 0) {
                showAlert(Alert.AlertType.WARNING, "Not Available", "This book is not available for borrowing.");
                return;
            }

            // Create borrow record
            java.time.LocalDate borrowDate = java.time.LocalDate.now();
            java.time.LocalDate dueDate = borrowDate.plusDays(14); // Default 14 days

            BorrowRecord record = new BorrowRecord(
                    student.getId(),
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    borrowDate,
                    dueDate,
                    "Active"
            );

            BorrowRecordDAO.createBorrowRecord(record);

            // Update book availability
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            new BookDao().update(book);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Book borrowed successfully!");
            navigateTo("Dashboard");

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to borrow book: " + e.getMessage());
        }
    }

    private void handleReturnBook(BorrowRecord record) {
        try {
            java.time.LocalDate returnDate = java.time.LocalDate.now();
            double fine = 0.0;

            // Calculate fine if overdue
            if (returnDate.isAfter(record.getDueDate())) {
                long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(record.getDueDate(), returnDate);
                fine = daysOverdue * 7.0; // 7 rupees per day fine
            }

            BorrowRecordDAO.returnBook(record.getRecordId(), returnDate, fine);

            // Update book availability
            java.util.Optional<com.librarymanagement.model.Book> bookOpt = new BookDao().findById(record.getBookId());
            if (bookOpt.isPresent()) {
                com.librarymanagement.model.Book book = bookOpt.get();
                book.setAvailableCopies(book.getAvailableCopies() + 1);
                new BookDao().update(book);
            }

            String fineMsg = fine > 0 ? "\nFine charged: ₹" + String.format("%.2f", fine) : "";
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book returned successfully!" + fineMsg);
            navigateTo("Dashboard");

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to return book: " + e.getMessage());
        }
    }

    private void handleLogout() {
        try {
            Stage loginStage = new Stage();
            MainApp.showLoginPage(loginStage);
            stage.close();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Logout failed: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
