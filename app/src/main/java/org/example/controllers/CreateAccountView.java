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

        try {
            switch (role) {
                case "Student" -> success = userService.createStudent(firstName, lastName, email, password);
                case "Trainer" -> success = userService.createTrainer(firstName, lastName, email, password);
                case "Manager" -> success = userService.createAdmin(firstName, lastName, email, password);
                default -> {
                    messageLabel.setText("Invalid role selected.");
                    return;
                }
            }

            if (success) {
                dbManager.commit();
                dbManager.disconnect();
                showLogin();
            } else {
                messageLabel.setText("Account creation failed.");
            }

        } catch (SQLException | IOException e) {
            messageLabel.setText("Account creation failed.");
            e.printStackTrace();
            try {
                dbManager.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void showLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/resources/fxml/login-screen.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 600, 400);
        Stage stage = (Stage) messageLabel.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }

}
