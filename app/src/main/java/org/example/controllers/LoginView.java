package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
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

    private Connection connection;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createButton.setOnAction(e -> showCreateAccount());
        try {
            DBManager dbManager = new DBManager();
            dbManager.connect();
            connection = dbManager.getConnection();
        } catch (SQLException e) {
            errorLabel.setText("Database connection failed.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String role = roleBox.getValue();

        if (username.isEmpty() || password.isEmpty() || role == null) {
            errorLabel.setText("All fields are required.");
            return;
        }

        if (!authenticate(username, password, role)) {
            errorLabel.setText("Invalid credentials.");
            return;
        }

        String fxmlPath;
        String title;

        switch (role) {
            case "Student":
                fxmlPath = "/org/example/views/StudentDashboard.fxml";
                title = "Student Dashboard";
                break;
            case "Trainer":
                fxmlPath = "/org/example/views/TrainerDashboard.fxml";
                title = "Trainer Dashboard";
                break;
            case "Manager":
                fxmlPath = "/org/example/views/ManagerDashboard.fxml";
                title = "Manager Dashboard";
                break;
            default:
                errorLabel.setText("Unknown role.");
                return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            errorLabel.setText("Could not load " + title);
            e.printStackTrace();
        }
    }

    private boolean authenticate(String username, String password, String role) {
        String table = switch (role) {
            case "Student" -> "Student";
            case "Trainer" -> "Trainer";
            case "Manager" -> "Admin";
            default -> null;
        };

        if (table == null) return false;

        String query = "SELECT * FROM " + table + " WHERE Email = ? AND Password = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
