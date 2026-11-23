package com.librarymanagement.controller;

import com.librarymanagement.model.Book;
import com.librarymanagement.model.User;
import com.librarymanagement.service.BookService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookManagementController {
    private User currentUser;
    private Stage stage;
    private Scene dashboardScene;
    private TeacherDashboardController parentController;
    private BookService bookService = new BookService();
    private TableView<Book> booksTable;
    private List<Book> books = new ArrayList<>();

    public BookManagementController(User user, Stage stage, Scene dashboardScene, TeacherDashboardController parentController) {
        this.currentUser = user;
        this.stage = stage;
        this.dashboardScene = dashboardScene;
        this.parentController = parentController;
    }

    public void showBooksPage() {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24));
        mainContent.setStyle("-fx-background-color: #f3f4f6;");

        // Header Section
        HBox headerBox = createHeaderSection();
        mainContent.getChildren().add(headerBox);

        // Filter and Action Section
        HBox filterBox = createFilterSection();
        mainContent.getChildren().add(filterBox);

        // Books Table
        booksTable = createBooksTable();
        ScrollPane scrollPane = new ScrollPane(booksTable);
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

        Label title = new Label("📖 Books Management");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBookBtn = new Button("➕ Add New Book");
        addBookBtn.setPrefHeight(38);
        addBookBtn.setStyle(
                "-fx-background-color: #3b82f6;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 6;");
        addBookBtn.setOnAction(e -> showAddBookDialog());

        header.getChildren().addAll(backBtn, title, spacer, addBookBtn);
        return header;
    }

    private HBox createFilterSection() {
        HBox filterBox = new HBox(12);
        filterBox.setPadding(new Insets(12));
        filterBox.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-width: 1; -fx-background-radius: 8;");

        Label searchLabel = new Label("Search:");
        searchLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151;");

        TextField searchField = new TextField();
        searchField.setPromptText("Search by title, author, or ISBN...");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-padding: 8; -fx-border-radius: 4;");

        ComboBox<String> genreFilter = new ComboBox<>();
        genreFilter.getItems().add("All Genres");
        genreFilter.getItems().addAll(com.librarymanagement.util.Genres.getAllGenres());
        genreFilter.setValue("All Genres");
        genreFilter.setPrefWidth(150);

        Button resetBtn = new Button("🔄 Reset");
        resetBtn.setStyle(
                "-fx-background-color: #6b7280;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 12;" +
                        "-fx-background-radius: 4;");
        resetBtn.setOnAction(e -> {
            searchField.clear();
            genreFilter.setValue("All Genres");
            booksTable.getItems().clear();
            booksTable.getItems().addAll(books);
        });

        Button searchBtn = new Button("🔍 Search");
        searchBtn.setStyle(
                "-fx-background-color: #3b82f6;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 12;" +
                        "-fx-background-radius: 4;");
        searchBtn.setOnAction(e -> performBookSearch(searchField.getText(), genreFilter.getValue()));

        // Allow Enter key in search field to trigger search
        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                performBookSearch(searchField.getText(), genreFilter.getValue());
            }
        });

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        filterBox.getChildren().addAll(searchLabel, searchField, genreFilter, searchBtn, resetBtn, spacer);
        return filterBox;
    }

    private void performBookSearch(String searchText, String genre) {
        try {
            booksTable.getItems().clear();
            
            List<Book> filteredBooks = new ArrayList<>(books);
            
            // Filter by search text (title, author, ISBN)
            if (searchText != null && !searchText.trim().isEmpty()) {
                String search = searchText.toLowerCase().trim();
                filteredBooks = filteredBooks.stream()
                        .filter(b -> b.getTitle().toLowerCase().contains(search) ||
                                b.getAuthor().toLowerCase().contains(search) ||
                                (b.getIsbn() != null && b.getIsbn().toLowerCase().contains(search)))
                        .toList();
            }
            
            // Filter by genre
            if (genre != null && !genre.equals("All Genres")) {
                final String selectedGenre = genre;
                filteredBooks = filteredBooks.stream()
                        .filter(b -> b.getCategory().equals(selectedGenre))
                        .toList();
            }
            
            booksTable.getItems().addAll(filteredBooks);
        } catch (Exception e) {
            showError("Search Error", "Error during search: " + e.getMessage());
        }
    }

    private TableView<Book> createBooksTable() {
        TableView<Book> table = new TableView<>();
        table.setPrefHeight(400);
        table.setStyle("-fx-font-size: 11;");

        TableColumn<Book, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(40);

        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.setPrefWidth(200);

        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorColumn.setPrefWidth(150);

        TableColumn<Book, String> isbnColumn = new TableColumn<>("ISBN");
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        isbnColumn.setPrefWidth(120);

        TableColumn<Book, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryColumn.setPrefWidth(100);

        TableColumn<Book, Integer> totalCopiesColumn = new TableColumn<>("Total Copies");
        totalCopiesColumn.setCellValueFactory(new PropertyValueFactory<>("totalCopies"));
        totalCopiesColumn.setPrefWidth(100);

        TableColumn<Book, Integer> availableColumn = new TableColumn<>("Available");
        availableColumn.setCellValueFactory(new PropertyValueFactory<>("availableCopies"));
        availableColumn.setPrefWidth(80);

        TableColumn<Book, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setPrefWidth(120);
        actionColumn.setCellFactory(param -> new TableCell<Book, Void>() {
            private final Button editBtn = new Button("✏️ Edit");
            private final Button deleteBtn = new Button("🗑️ Delete");

            {
                editBtn.setPrefWidth(50);
                editBtn.setStyle("-fx-font-size: 10; -fx-padding: 4;");
                deleteBtn.setPrefWidth(50);
                deleteBtn.setStyle("-fx-font-size: 10; -fx-padding: 4; -fx-text-fill: white; -fx-background-color: #dc2626;");

                editBtn.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    showEditBookDialog(book);
                });

                deleteBtn.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(book);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox actions = new HBox(4, editBtn, deleteBtn);
                    setGraphic(actions);
                }
            }
        });

        table.getColumns().addAll(idColumn, titleColumn, authorColumn, isbnColumn, categoryColumn, totalCopiesColumn, availableColumn, actionColumn);

        // Assign to instance variable for later use
        booksTable = table;
        
        // Load data AFTER assigning to instance variable
        loadBooksData();

        return table;
    }

    private void loadBooksData() {
        try {
            books = bookService.getAllBooks();
            booksTable.getItems().addAll(books);
        } catch (Exception e) {
            showError("Error", "Unable to load books: " + e.getMessage());
        }
    }

    private void showAddBookDialog() {
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Add New Book");
        dialog.setHeaderText("Enter Book Details");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField titleField = new TextField();
        titleField.setPromptText("Enter book title");

        TextField authorField = new TextField();
        authorField.setPromptText("Enter author name");

        TextField isbnField = new TextField();
        isbnField.setPromptText("Enter ISBN");

        TextField descField = new TextField();
        descField.setPromptText("Enter description");
        descField.setPrefWidth(300);

        ComboBox<String> genreCombo = new ComboBox<>();
        genreCombo.getItems().addAll(com.librarymanagement.util.Genres.getAllGenres());
        genreCombo.setPromptText("Select a genre");
        genreCombo.setPrefWidth(200);

        javafx.scene.control.DatePicker releaseDatePicker = new javafx.scene.control.DatePicker();
        releaseDatePicker.setPromptText("Select release date");
        releaseDatePicker.setPrefWidth(200);

        Spinner<Integer> totalCopiesSpinner = new Spinner<>(1, 100, 5);
        Spinner<Integer> availableCopiesSpinner = new Spinner<>(1, 100, 5);

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Author:"), 0, 1);
        grid.add(authorField, 1, 1);
        grid.add(new Label("ISBN:"), 0, 2);
        grid.add(isbnField, 1, 2);
        grid.add(new Label("Description:"), 0, 3);
        grid.add(descField, 1, 3);
        grid.add(new Label("Genre:"), 0, 4);
        grid.add(genreCombo, 1, 4);
        grid.add(new Label("Release Date:"), 0, 5);
        grid.add(releaseDatePicker, 1, 5);
        grid.add(new Label("Total Copies:"), 0, 6);
        grid.add(totalCopiesSpinner, 1, 6);
        grid.add(new Label("Available Copies:"), 0, 7);
        grid.add(availableCopiesSpinner, 1, 7);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<Book> result = dialog.showAndWait();
        if (result.isPresent()) {
            // Comprehensive validation
            String validationError = validateBookFields(
                titleField.getText(),
                authorField.getText(),
                isbnField.getText(),
                descField.getText(),
                genreCombo.getValue(),
                releaseDatePicker.getValue(),
                totalCopiesSpinner.getValue(),
                availableCopiesSpinner.getValue()
            );
            
            if (validationError != null) {
                showError("Validation Error", validationError);
                return;
            }
            
            Book newBook = new Book();
            newBook.setTitle(titleField.getText().trim());
            newBook.setAuthor(authorField.getText().trim());
            newBook.setIsbn(isbnField.getText().trim());
            newBook.setDescription(descField.getText().trim());
            newBook.setCategory(genreCombo.getValue());
            newBook.setReleaseDate(releaseDatePicker.getValue().toString());
            newBook.setTotalCopies(totalCopiesSpinner.getValue());
            newBook.setAvailableCopies(availableCopiesSpinner.getValue());

            try {
                bookService.addBook(newBook);
                showInfo("Success", "Book added successfully!");
                booksTable.getItems().clear();
                loadBooksData();
            } catch (Exception e) {
                showError("Error", "Unable to add book: " + e.getMessage());
            }
        }
    }

    private void showEditBookDialog(Book book) {
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Edit Book");
        dialog.setHeaderText("Edit Book Details");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField titleField = new TextField(book.getTitle());
        TextField authorField = new TextField(book.getAuthor());
        TextField isbnField = new TextField(book.getIsbn());
        
        ComboBox<String> genreCombo = new ComboBox<>();
        genreCombo.getItems().addAll(com.librarymanagement.util.Genres.getAllGenres());
        genreCombo.setValue(book.getCategory());
        genreCombo.setPrefWidth(200);

        javafx.scene.control.DatePicker releaseDatePicker = new javafx.scene.control.DatePicker();
        if (book.getReleaseDate() != null && !book.getReleaseDate().isEmpty()) {
            try {
                releaseDatePicker.setValue(java.time.LocalDate.parse(book.getReleaseDate()));
            } catch (Exception e) {
                releaseDatePicker.setValue(java.time.LocalDate.now());
            }
        }
        releaseDatePicker.setPrefWidth(200);

        Spinner<Integer> totalCopiesSpinner = new Spinner<>(1, 100, book.getTotalCopies());
        Spinner<Integer> availableCopiesSpinner = new Spinner<>(1, 100, book.getAvailableCopies());

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Author:"), 0, 1);
        grid.add(authorField, 1, 1);
        grid.add(new Label("ISBN:"), 0, 2);
        grid.add(isbnField, 1, 2);
        grid.add(new Label("Genre:"), 0, 3);
        grid.add(genreCombo, 1, 3);
        grid.add(new Label("Release Date:"), 0, 4);
        grid.add(releaseDatePicker, 1, 4);
        grid.add(new Label("Total Copies:"), 0, 5);
        grid.add(totalCopiesSpinner, 1, 5);
        grid.add(new Label("Available Copies:"), 0, 6);
        grid.add(availableCopiesSpinner, 1, 6);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<Book> result = dialog.showAndWait();
        if (result.isPresent()) {
            // Comprehensive validation
            String validationError = validateBookFields(
                titleField.getText(),
                authorField.getText(),
                isbnField.getText(),
                "",
                genreCombo.getValue(),
                releaseDatePicker.getValue(),
                totalCopiesSpinner.getValue(),
                availableCopiesSpinner.getValue()
            );
            
            if (validationError != null) {
                showError("Validation Error", validationError);
                return;
            }
            
            book.setTitle(titleField.getText().trim());
            book.setAuthor(authorField.getText().trim());
            book.setIsbn(isbnField.getText().trim());
            book.setCategory(genreCombo.getValue());
            book.setReleaseDate(releaseDatePicker.getValue().toString());
            book.setTotalCopies(totalCopiesSpinner.getValue());
            book.setAvailableCopies(availableCopiesSpinner.getValue());

            try {
                bookService.updateBook(book);
                showInfo("Success", "Book updated successfully!");
                booksTable.getItems().clear();
                loadBooksData();
            } catch (Exception e) {
                showError("Error", "Unable to update book: " + e.getMessage());
            }
        }
    }

    private String validateBookFields(String title, String author, String isbn, String description, 
                                     String genre, java.time.LocalDate releaseDate, 
                                     Integer totalCopies, Integer availableCopies) {
        // Title validation
        if (title == null || title.trim().isEmpty()) {
            return "Title is required and cannot be empty";
        }
        if (title.trim().length() < 2) {
            return "Title must be at least 2 characters long";
        }
        if (title.trim().length() > 100) {
            return "Title cannot exceed 100 characters";
        }
        
        // Author validation
        if (author == null || author.trim().isEmpty()) {
            return "Author name is required and cannot be empty";
        }
        if (author.trim().length() < 2) {
            return "Author name must be at least 2 characters long";
        }
        if (author.trim().length() > 50) {
            return "Author name cannot exceed 50 characters";
        }
        
        // ISBN validation
        if (isbn != null && !isbn.trim().isEmpty()) {
            String isbnClean = isbn.trim().replaceAll("-", "");
            if (!isbnClean.matches("\\d{10}(\\d{3})?")) {
                return "ISBN must be 10 or 13 digits (format: 123-456-789-0 or 978-0-123-45678-9)";
            }
        }
        
        // Genre validation
        if (genre == null || genre.isEmpty()) {
            return "Genre must be selected";
        }
        
        // Release Date validation
        if (releaseDate == null) {
            return "Release Date is required";
        }
        if (releaseDate.isAfter(java.time.LocalDate.now())) {
            return "Release Date cannot be in the future";
        }
        if (releaseDate.isBefore(java.time.LocalDate.of(1000, 1, 1))) {
            return "Release Date cannot be before year 1000";
        }
        
        // Copies validation
        if (totalCopies == null || totalCopies < 1) {
            return "Total Copies must be at least 1";
        }
        if (availableCopies == null || availableCopies < 1) {
            return "Available Copies must be at least 1";
        }
        if (availableCopies > totalCopies) {
            return "Available Copies cannot be more than Total Copies";
        }
        
        return null; // All validations passed
    }

    private void showDeleteConfirmation(Book book) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Delete Book?");
        alert.setContentText("Are you sure you want to delete '" + book.getTitle() + "'?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                bookService.deleteBook(book.getId());
                showInfo("Success", "Book deleted successfully!");
                booksTable.getItems().clear();
                loadBooksData();
            } catch (Exception e) {
                showError("Error", "Unable to delete book: " + e.getMessage());
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
