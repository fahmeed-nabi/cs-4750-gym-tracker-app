package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.database.DBManager;

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

    private final DBManager dbManager = new DBManager();

    @FXML
    public void initialize() {
        roleBox.getItems().addAll("Student", "Trainer", "Manager");
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

        try {
            dbManager.connect();
            Connection connection = dbManager.getConnection();

            String sql = switch (role) {
                case "Student" -> "INSERT INTO Student (StudentID, FirstName, LastName, Email, Password) VALUES (?, ?, ?, ?, ?)";
                case "Trainer" -> "INSERT INTO Trainer (TrainerID, FirstName, LastName, Email, Password) VALUES (?, ?, ?, ?, ?)";
                case "Manager" -> "INSERT INTO Admin (FirstName, LastName, Email, Password) VALUES (?, ?, ?, ?)";
                default -> null;
            };

            if (sql == null) {
                messageLabel.setText("Invalid role selected.");
                return;
            }

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                if (!role.equals("Manager")) {
                    pstmt.setInt(1, (int) (Math.random() * 1000000)); // Generate ID
                    pstmt.setString(2, firstName);
                    pstmt.setString(3, lastName);
                    pstmt.setString(4, email);
                    pstmt.setString(5, password);
                } else {
                    pstmt.setString(1, firstName);
                    pstmt.setString(2, lastName);
                    pstmt.setString(3, email);
                    pstmt.setString(4, password);
                }

                pstmt.executeUpdate();
                dbManager.commit();
                dbManager.disconnect();
                showLogin();
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            messageLabel.setText("Account creation failed.");
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
