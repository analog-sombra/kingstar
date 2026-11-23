package com.librarymanagement.app;

import com.librarymanagement.model.User;
import com.librarymanagement.service.UserService;
import com.librarymanagement.util.PasswordValidator;
import com.librarymanagement.util.DatabaseMigration;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Optional;

public class MainApp extends Application {
    private final UserService userService = new UserService();
    private String selectedType = "STUDENT";

    @Override
    public void start(Stage stage) {
        // Run database migrations first
        System.out.println("🔍 Checking and running database migrations...");
        DatabaseMigration.runMigrations();
        System.out.println("✅ Database ready!\n");

        StackPane root = new StackPane();
        
        HBox card = createLoginCard();
        card.setMaxWidth(940);
        card.setPrefHeight(520);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 16;" +
                        "-fx-effect: dropshadow(gaussian, rgba(31,41,55,0.25), 28, 0.2, 0, 12);");


                        
        root.setPadding(new Insets(20));
        setBackground(root);
        root.getChildren().add(card);

        Scene scene = new Scene(root, 1024, 640);
        stage.setTitle("Library Management System");
        stage.setScene(scene);
        stage.show();
    }

    private HBox createLoginCard() {
        VBox leftPane = new VBox(18);
        leftPane.setPadding(new Insets(36, 40, 36, 40));
        leftPane.setPrefWidth(440);

        Label brandLabel = new Label("LIBRARY MANAGEMENT");
        brandLabel.setStyle("-fx-text-fill: #1d4ed8; -fx-font-size: 20; -fx-font-weight: bold;");

        Label title = new Label("Login Member Area");
        title.setStyle("-fx-text-fill: #1f2937; -fx-font-size: 32; -fx-font-weight: 600;");

        Label subtitle = new Label("Select role and sign in");
        subtitle.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 13;");

        HBox typeButtons = createLoginTypeSection();
        VBox formSection = createFormSection();

        leftPane.getChildren().addAll(brandLabel, title, subtitle, typeButtons, formSection);

        StackPane rightPane = new StackPane();
        rightPane.setPrefWidth(500);
        rightPane.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #312e81, #2563eb);" +
                        "-fx-background-radius: 0 16 16 0;");
        applyIllustrationBackground(rightPane);

        Label rightText = new Label("Smart Library\nLogin Portal");
        rightText.setStyle("-fx-text-fill: rgba(255,255,255,0.85); -fx-font-size: 28; -fx-font-weight: bold;");
        StackPane.setAlignment(rightText, Pos.TOP_LEFT);
        StackPane.setMargin(rightText, new Insets(24));
        rightPane.getChildren().add(rightText);

        HBox card = new HBox(leftPane, rightPane);
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        return card;
    }

    private HBox createLoginTypeSection() {
        Button studentButton = new Button("Student Login");
        Button teacherButton = new Button("Teacher Login");
        studentButton.setPrefWidth(170);
        teacherButton.setPrefWidth(170);
        studentButton.setPrefHeight(38);
        teacherButton.setPrefHeight(38);

        setSelectedStyle(studentButton, teacherButton, true);

        studentButton.setOnAction(event -> {
            selectedType = "STUDENT";
            setSelectedStyle(studentButton, teacherButton, true);
        });

        teacherButton.setOnAction(event -> {
            selectedType = "TEACHER";
            setSelectedStyle(studentButton, teacherButton, false);
        });

        HBox box = new HBox(10, studentButton, teacherButton);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private VBox createFormSection() {
        Label registrationLabel = new Label("Registration ID");
        registrationLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #111827;");
        TextField registrationField = new TextField();
        registrationField.setPromptText("e.g. STU001");
        registrationField.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: transparent transparent #cbd5e1 transparent;" +
                        "-fx-border-width: 0 0 1.2 0;" +
                        "-fx-padding: 6 4 6 4;");

        Label passwordLabel = new Label("Password");
        passwordLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #111827;");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter secure password");
        passwordField.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: transparent transparent #cbd5e1 transparent;" +
                        "-fx-border-width: 0 0 1.2 0;" +
                        "-fx-padding: 6 4 6 4;" +
                        "-fx-font-size: 14;");

        // Password visibility toggle
        TextField visiblePasswordField = new TextField();
        visiblePasswordField.setPromptText("Enter secure password");
        visiblePasswordField.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: transparent transparent #cbd5e1 transparent;" +
                        "-fx-border-width: 0 0 1.2 0;" +
                        "-fx-padding: 6 4 6 4;" +
                        "-fx-font-size: 14;");

        // Bind the two password fields together
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            visiblePasswordField.setText(newVal);
        });
        visiblePasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
            passwordField.setText(newVal);
        });

        Button togglePasswordBtn = new Button("👁️");
        togglePasswordBtn.setPrefWidth(28);
        togglePasswordBtn.setPrefHeight(28);
        togglePasswordBtn.setStyle(
                "-fx-font-size: 14;" +
                "-fx-padding: 0;" +
                "-fx-border: none;" +
                "-fx-background-color: transparent;" +
                "-fx-cursor: hand;");
        
        // Use StackPane to overlay fields properly
        StackPane passwordFieldStack = new StackPane();
        passwordFieldStack.setPrefHeight(35);
        passwordFieldStack.getChildren().addAll(passwordField, visiblePasswordField);
        StackPane.setAlignment(visiblePasswordField, Pos.CENTER_LEFT);
        StackPane.setAlignment(passwordField, Pos.CENTER_LEFT);
        visiblePasswordField.setVisible(false);
        visiblePasswordField.setManaged(false);
        
        togglePasswordBtn.setOnAction(e -> {
            boolean isPasswordFieldVisible = passwordField.isVisible();
            passwordField.setVisible(!isPasswordFieldVisible);
            passwordField.setManaged(isPasswordFieldVisible);
            visiblePasswordField.setVisible(isPasswordFieldVisible);
            visiblePasswordField.setManaged(isPasswordFieldVisible);
            togglePasswordBtn.setText(isPasswordFieldVisible ? "👁️" : "🙈");
        });

        HBox passwordBox = new HBox(8);
        passwordBox.setStyle("-fx-alignment: center-left;");
        HBox.setHgrow(passwordFieldStack, Priority.ALWAYS);
        passwordBox.getChildren().addAll(passwordFieldStack, togglePasswordBtn);

        Label passwordRule = new Label(
                "Password format: uppercase, lowercase, number, special character, length 8-16.");
        passwordRule.setTextFill(Color.web("#374151"));
        passwordRule.setWrapText(true);
        passwordRule.setStyle("-fx-font-size: 11;");

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.web("#dc2626"));
        errorLabel.setWrapText(true);
        errorLabel.setStyle("-fx-font-size: 12;");

        Label forgotPassword = new Label("Forgot Password?");
        forgotPassword.setStyle("-fx-text-fill: #0ea5e9; -fx-font-size: 11; -fx-font-weight: bold;");
        forgotPassword.setAlignment(Pos.CENTER_RIGHT);
        forgotPassword.setMaxWidth(Double.MAX_VALUE);

        Button submitButton = new Button("Login");
        submitButton.setPrefWidth(120);
        submitButton.setPrefHeight(36);
        submitButton.setStyle(
                "-fx-background-color: linear-gradient(to right, #4f46e5, #06b6d4);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;");
        submitButton.setOnAction(event -> handleLogin(registrationField, passwordField, errorLabel));

        HBox submitRow = new HBox(submitButton);
        submitRow.setAlignment(Pos.CENTER);
        submitRow.setPadding(new Insets(12, 0, 0, 0));

        VBox form = new VBox(8,
                registrationLabel,
                registrationField,
                passwordLabel,
                passwordBox,
                forgotPassword,
                passwordRule,
                errorLabel,
                submitRow);
        form.setAlignment(Pos.TOP_LEFT);
        form.setPadding(new Insets(6, 0, 0, 0));
        return form;
    }

    private void handleLogin(TextField registrationField, PasswordField passwordField, Label errorLabel) {
        errorLabel.setText("");

        String registrationId = registrationField.getText() == null ? "" : registrationField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText().trim();

        if (registrationId.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Registration ID and Password are required.");
            return;
        }

        if (!PasswordValidator.isValid(password)) {
            errorLabel.setText(
                    "Password format is invalid. It must contain uppercase, lowercase, number, special character, and be 8-16 characters.");
            return;
        }

        try {
            Optional<User> user = userService.login(registrationId, password, selectedType);
            if (user.isEmpty()) {
                errorLabel.setText("Invalid credentials, wrong login type, or inactive user.");
                return;
            }

            showInfo("Login Successful", "Welcome " + user.get().getName() + " (" + selectedType + ")");
            registrationField.clear();
            passwordField.clear();
            
            // Open dashboard based on user type
            if ("TEACHER".equals(selectedType)) {
                Stage dashboardStage = new Stage();
                com.librarymanagement.controller.TeacherDashboardController dashboardController = 
                    new com.librarymanagement.controller.TeacherDashboardController(user.get(), dashboardStage);
                dashboardController.showDashboard();
                // Close login window
                javafx.stage.Stage loginStage = (javafx.stage.Stage) registrationField.getScene().getWindow();
                loginStage.close();
            } else if ("STUDENT".equals(selectedType)) {
                Stage dashboardStage = new Stage();
                new com.librarymanagement.controller.StudentDashboardController(user.get(), dashboardStage);
                // Close login window
                javafx.stage.Stage loginStage = (javafx.stage.Stage) registrationField.getScene().getWindow();
                loginStage.close();
            }
        } catch (SQLException ex) {
            showError("Database Error", "Unable to process login. Please check DB connection.\n\n" + ex.getMessage());
        }
    }

    private void setSelectedStyle(Button studentButton, Button teacherButton, boolean studentSelected) {
        String active = "-fx-background-color: #1d4ed8; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;";
        String inactive = "-fx-background-color: #e5e7eb; -fx-text-fill: #111827; -fx-font-weight: bold; -fx-background-radius: 8;";
        studentButton.setStyle(studentSelected ? active : inactive);
        teacherButton.setStyle(studentSelected ? inactive : active);
    }

    private void setBackground(StackPane root) {
        String backgroundResource = "/images/background/login-background.jpg";
        Optional<javafx.scene.image.Image> backgroundImage = loadImage(backgroundResource);
        if (backgroundImage.isEmpty()) {
            root.setStyle("-fx-background-color: linear-gradient(to bottom right, #dbeafe, #f8fafc);");
            return;
        }

        BackgroundImage image = new BackgroundImage(
                backgroundImage.get(),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(1, 1, true, true, false, true));
        root.setBackground(new Background(image));
    }

    private void applyIllustrationBackground(StackPane panel) {
        String illustrationPath = "/images/background/login-illustration.png";
        Optional<javafx.scene.image.Image> illustrationImage = loadImage(illustrationPath);
        if (illustrationImage.isEmpty()) {
            return;
        }

        ImageView illustrationView = new ImageView(illustrationImage.get());
        illustrationView.setPreserveRatio(true);
        illustrationView.setSmooth(true);
        illustrationView.fitWidthProperty().bind(panel.widthProperty().multiply(0.72));
        illustrationView.fitHeightProperty().bind(panel.heightProperty().multiply(0.9));
        panel.getChildren().add(0, illustrationView);
    }

    private Optional<javafx.scene.image.Image> loadImage(String resourcePath) {
        Optional<String> imageUrl = resolveImageUrl(resourcePath);
        if (imageUrl.isEmpty()) {
            return Optional.empty();
        }

        javafx.scene.image.Image image = new javafx.scene.image.Image(imageUrl.get(), false);
        if (image.isError()) {
            System.err.println("Failed to load image: " + resourcePath + " | " + image.getException());
            return Optional.empty();
        }
        return Optional.of(image);
    }

    private Optional<javafx.scene.image.Image> loadImage(
            String resourcePath,
            double width,
            double height,
            boolean preserveRatio,
            boolean smooth) {
        Optional<String> imageUrl = resolveImageUrl(resourcePath);
        if (imageUrl.isEmpty()) {
            return Optional.empty();
        }

        javafx.scene.image.Image image = new javafx.scene.image.Image(
                imageUrl.get(),
                width,
                height,
                preserveRatio,
                smooth,
                false);
        if (image.isError()) {
            System.err.println("Failed to load image: " + resourcePath + " | " + image.getException());
            return Optional.empty();
        }
        return Optional.of(image);
    }

    private Optional<String> resolveImageUrl(String resourcePath) {
        var classpathUrl = MainApp.class.getResource(resourcePath);
        if (classpathUrl != null) {
            return Optional.of(classpathUrl.toExternalForm());
        }

        String relativePath = resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
        Path sourceResourcesPath = Paths.get("src", "main", "resources", relativePath);
        if (Files.exists(sourceResourcesPath)) {
            return Optional.of(sourceResourcesPath.toUri().toString());
        }

        Path compiledResourcesPath = Paths.get("out", "resources", relativePath);
        if (Files.exists(compiledResourcesPath)) {
            return Optional.of(compiledResourcesPath.toUri().toString());
        }

        System.err.println("Image resource not found: " + resourcePath);
        return Optional.empty();
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

    public static void showLoginPage(Stage stage) {
        MainApp mainApp = new MainApp();
        mainApp.start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
