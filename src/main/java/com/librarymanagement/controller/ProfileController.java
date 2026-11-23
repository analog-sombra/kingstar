package com.librarymanagement.controller;

import com.librarymanagement.model.User;
import com.librarymanagement.service.UserService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Optional;

public class ProfileController {
    private User currentUser;
    private Stage stage;
    private Scene dashboardScene;
    private TeacherDashboardController parentController;
    private UserService userService = new UserService();

    public ProfileController(User user, Stage stage, Scene dashboardScene, TeacherDashboardController parentController) {
        this.currentUser = user;
        this.stage = stage;
        this.dashboardScene = dashboardScene;
        this.parentController = parentController;
    }

    public void showProfilePage() {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24));
        mainContent.setStyle("-fx-background-color: #f3f4f6;");

        // Header Section
        HBox headerBox = createHeaderSection();
        mainContent.getChildren().add(headerBox);

        // Profile Content
        VBox profileContent = createProfileContent();
        ScrollPane scrollPane = new ScrollPane(profileContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
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

        Label title = new Label("👤 My Profile");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(backBtn, title, spacer);
        return header;
    }

    private VBox createProfileContent() {
        VBox mainBox = new VBox(20);
        mainBox.setAlignment(Pos.TOP_CENTER);

        // Profile Header Card
        VBox profileHeaderCard = createProfileHeaderCard();
        mainBox.getChildren().add(profileHeaderCard);

        // Profile Details Card
        VBox profileDetailsCard = createProfileDetailsCard();
        mainBox.getChildren().add(profileDetailsCard);

        // Change Password Card
        VBox changePasswordCard = createChangePasswordCard();
        mainBox.getChildren().add(changePasswordCard);

        return mainBox;
    }

    private VBox createProfileHeaderCard() {
        VBox card = new VBox(16);
        card.setPadding(new Insets(30));
        card.setPrefWidth(600);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 4, 0, 0, 2);");

        // Profile Avatar
        VBox avatarBox = new VBox();
        avatarBox.setAlignment(Pos.CENTER);
        Label avatarLabel = new Label("👨‍🏫");
        avatarLabel.setStyle("-fx-font-size: 64;");
        avatarBox.getChildren().add(avatarLabel);

        // User Info
        Label nameLabel = new Label(currentUser.getName());
        nameLabel.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        Label roleLabel = new Label("📚 Teacher / Instructor");
        roleLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #6b7280;");

        Label regIdLabel = new Label("Registration ID: " + currentUser.getRegistrationId());
        regIdLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #9ca3af;");

        Label statusLabel = new Label("Status: " + currentUser.getStatus());
        statusLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #10b981; -fx-font-weight: bold;");

        card.getChildren().addAll(avatarBox, nameLabel, roleLabel, regIdLabel, statusLabel);
        return card;
    }

    private VBox createProfileDetailsCard() {
        VBox card = new VBox(16);
        card.setPadding(new Insets(30));
        card.setPrefWidth(600);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 4, 0, 0, 2);");

        Label sectionTitle = new Label("Personal Information");
        sectionTitle.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        // Full Name
        VBox nameBox = createInfoField("Full Name", currentUser.getName());

        // Email
        VBox emailBox = createInfoField("Email Address", currentUser.getEmail());

        // Registration ID (read-only)
        VBox regIdBox = createInfoField("Registration ID", currentUser.getRegistrationId());

        // Account Type (read-only)
        VBox typeBox = createInfoField("Account Type", currentUser.getType());

        // Edit Button
        Button editBtn = new Button("✏️ Edit Profile");
        editBtn.setPrefWidth(150);
        editBtn.setPrefHeight(38);
        editBtn.setStyle(
                "-fx-background-color: #3b82f6;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 6;");
        editBtn.setOnAction(e -> showEditProfileDialog());

        HBox editBox = new HBox(editBtn);
        editBox.setAlignment(Pos.CENTER_RIGHT);
        editBox.setPadding(new Insets(12, 0, 0, 0));

        card.getChildren().addAll(sectionTitle, nameBox, emailBox, regIdBox, typeBox, editBox);
        return card;
    }

    private VBox createChangePasswordCard() {
        VBox card = new VBox(16);
        card.setPadding(new Insets(30));
        card.setPrefWidth(600);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 4, 0, 0, 2);");

        Label sectionTitle = new Label("Security");
        sectionTitle.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        Label securityDesc = new Label("Manage your account security and password settings.");
        securityDesc.setStyle("-fx-font-size: 12; -fx-text-fill: #6b7280;");
        securityDesc.setWrapText(true);

        Button changePasswordBtn = new Button("🔐 Change Password");
        changePasswordBtn.setPrefWidth(180);
        changePasswordBtn.setPrefHeight(38);
        changePasswordBtn.setStyle(
                "-fx-background-color: #8b5cf6;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 6;");
        changePasswordBtn.setOnAction(e -> showChangePasswordDialog());

        HBox buttonBox = new HBox(changePasswordBtn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(sectionTitle, securityDesc, buttonBox);
        return card;
    }

    private VBox createInfoField(String label, String value) {
        VBox field = new VBox(4);

        Label labelText = new Label(label);
        labelText.setStyle("-fx-font-size: 11; -fx-font-weight: bold; -fx-text-fill: #6b7280;");

        Label valueText = new Label(value);
        valueText.setStyle("-fx-font-size: 13; -fx-text-fill: #1f2937;");
        valueText.setWrapText(true);

        field.getChildren().addAll(labelText, valueText);
        return field;
    }

    private void showEditProfileDialog() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Edit Profile");
        dialog.setHeaderText("Update Your Profile Information");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(currentUser.getName());
        TextField emailField = new TextField(currentUser.getEmail());

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email Address:"), 0, 1);
        grid.add(emailField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<User> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (nameField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty()) {
                showError("Error", "Name and email cannot be empty!");
                return;
            }

            currentUser.setName(nameField.getText());
            currentUser.setEmail(emailField.getText());

            try {
                userService.updateUser(currentUser);
                showInfo("Success", "Profile updated successfully!");
                showProfilePage();
            } catch (SQLException e) {
                showError("Error", "Unable to update profile: " + e.getMessage());
            }
        }
    }

    private void showChangePasswordDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Update Your Password");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Enter your current password");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Enter new password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm new password");

        Label passwordHint = new Label(
                "Password must contain: uppercase, lowercase, number, special character, and be 8-16 characters long.");
        passwordHint.setStyle("-fx-font-size: 10; -fx-text-fill: #6b7280;");
        passwordHint.setWrapText(true);

        grid.add(new Label("Current Password:"), 0, 0);
        grid.add(currentPasswordField, 1, 0);
        grid.add(new Label("New Password:"), 0, 1);
        grid.add(newPasswordField, 1, 1);
        grid.add(new Label("Confirm Password:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);
        grid.add(passwordHint, 0, 3, 2, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<Void> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (currentPasswordField.getText().isEmpty() || newPasswordField.getText().isEmpty()) {
                showError("Error", "All fields are required!");
                return;
            }

            if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
                showError("Error", "New passwords do not match!");
                return;
            }

            try {
                // Verify current password
                Optional<User> user = userService.login(currentUser.getRegistrationId(), currentPasswordField.getText(), "TEACHER");
                if (user.isEmpty()) {
                    showError("Error", "Current password is incorrect!");
                    return;
                }

                // Update password
                currentUser.setPassword(newPasswordField.getText());
                userService.updateUser(currentUser);
                showInfo("Success", "Password changed successfully!");
            } catch (SQLException e) {
                showError("Error", "Unable to change password: " + e.getMessage());
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
