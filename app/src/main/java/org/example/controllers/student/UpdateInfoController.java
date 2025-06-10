package org.example.controllers.student;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.database.DBManager;
import org.example.database.UserService;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UpdateInfoController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private String currentEmail;
    private UserService userService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            DBManager db = new DBManager();
            db.connect();
            Connection conn = db.getConnection();
            userService = new UserService(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setCurrentEmail(String email) {
        this.currentEmail = email;
        this.emailField.setText(email);
    }

    @FXML
    private void handleUpdate() {
        String newName = nameField.getText().trim();
        String newEmail = emailField.getText().trim();
        String newPassword = passwordField.getText().trim();

        if (newName.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty()) {
            statusLabel.setText("All fields are required.");
            return;
        }

        try {
            boolean success = userService.updateStudentInfo(currentEmail, newName, newEmail, newPassword);
            if (success) {
                statusLabel.setText("Information updated successfully.");
                currentEmail = newEmail;
            } else {
                statusLabel.setText("Update failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Database error.");
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
