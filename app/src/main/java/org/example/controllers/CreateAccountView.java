package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.database.DBManager;
import org.example.database.UserService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateAccountView {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleBox;
    @FXML private Label messageLabel;

    private DBManager dbManager;
    private UserService userService;

    @FXML
    public void initialize() {
        roleBox.getItems().addAll("Student", "Trainer", "Manager");

        try {
            dbManager = new DBManager();
            dbManager.connect();
            userService = new UserService(dbManager.getConnection());
        } catch (SQLException e) {
            messageLabel.setText("Failed to connect to database.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCreateAccount() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String role = roleBox.getValue();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            messageLabel.setText("Please fill all fields.");
            return;
        }

        boolean success = false;
        String table = switch (role) {
            case "Student" -> "Student";
            case "Trainer" -> "Trainer";
            case "Manager" -> "Admin";
            default -> null;
        };

        if (table == null) {
            messageLabel.setText("Invalid role selected.");
            return;
        }

        try {
            if (userService.emailExists(table, email)) {
                messageLabel.setText("An account with this email already exists.");
                return;
            }

            switch (role) {
                case "Student" -> success = userService.createStudent(firstName, lastName, email, password);
                case "Trainer" -> success = userService.createTrainer(firstName, lastName, email, password);
                case "Manager" -> success = userService.createAdmin(firstName, lastName, email, password);
            }

            if (success) {
                dbManager.commit();
                dbManager.disconnect();
                showSuccessAndClose("Account created successfully!");
            } else {
                messageLabel.setText("Account creation failed.");
            }

        } catch (SQLException e) {
            messageLabel.setText("Account creation failed.");
            e.printStackTrace();
            try {
                dbManager.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void showSuccessAndClose(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

        Stage stage = (Stage) messageLabel.getScene().getWindow();
        stage.close();
    }

}
