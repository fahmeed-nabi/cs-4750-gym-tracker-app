package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.controllers.admin.ManagerDashboard;
import org.example.controllers.student.StudentDashboard;
import org.example.controllers.trainer.TrainerDashboard;
import org.example.database.AuthService;
import org.example.database.DBManager;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class LoginView implements Initializable {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleBox;
    @FXML private Label errorLabel;
    @FXML private Button createButton;

    // Classes for database querying
    private DBManager dbManager;
    private AuthService authService;  // for authenticate methods

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createButton.setOnAction(e -> showCreateAccount());
        try {
            dbManager = new DBManager();
            dbManager.connect();
            authService = new AuthService(dbManager.getConnection());
        } catch (SQLException e) {
            errorLabel.setText("Database connection failed.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogin() {
        String email = usernameField.getText().trim();
        String password = passwordField.getText();
        String role = roleBox.getValue();

        if (email.isEmpty() || password.isEmpty() || role == null) {
            errorLabel.setText("All fields are required.");
            return;
        }

        boolean authenticated = switch (role) {
            case "Student" -> authenticateStudent(email, password);
            case "Trainer" -> authenticateTrainer(email, password);
            case "Manager" -> authenticateAdmin(email, password);
            default -> false;
        };

        if (!authenticated) {
            errorLabel.setText("Invalid credentials.");
            return;
        }

        loadDashboard(role);
    }

    private boolean authenticateStudent(String email, String password) {
        try {
            return authService.authenticateStudent(email, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean authenticateTrainer(String email, String password) {
        try {
            return authService.authenticateTrainer(email, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean authenticateAdmin(String email, String password) {
        try {
            return authService.authenticateAdmin(email, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void loadDashboard(String role) {
        String fxmlPath;
        String title;

        switch (role) {
            case "Student" -> {
                fxmlPath = "/fxml/student/student-dashboard.fxml";
                title = "Student Dashboard";
            }
            case "Trainer" -> {
                fxmlPath = "/fxml/trainer/trainer-dashboard.fxml";
                title = "Trainer Dashboard";
            }
            case "Manager" -> {
                fxmlPath = "/fxml/admin/manager-dashboard.fxml";
                title = "Manager Dashboard";
            }
            default -> {
                errorLabel.setText("Unknown role");
                return;
            }
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            if (role.equals("Student")) {
                StudentDashboard controller = loader.getController();
                controller.setStudentEmail(usernameField.getText().trim()); // pass the logged-in student email
            }
            else if (role.equals("Manager")) {
                ManagerDashboard controller = loader.getController();
                controller.setManagerEmail(usernameField.getText().trim());
            }
            else {
                TrainerDashboard controller = loader.getController();
                controller.setTrainerEmail(usernameField.getText().trim());
            }


            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root, 800, 700));
            stage.show();
        } catch (IOException e) {
            errorLabel.setText("Could not load " + title);
            e.printStackTrace();
        }
    }

    public void showCreateAccount() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/create-account.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Create Account");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

