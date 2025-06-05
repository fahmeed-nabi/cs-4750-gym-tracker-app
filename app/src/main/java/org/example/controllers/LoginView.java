package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginView implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleBox;

    @FXML
    private Label errorLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //intetionally left blank, if we delete it though will need to touch up Initizlaizable above
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
                errorLabel.setText("Unrecognized role.");
                return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root, 800, 600);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Failed to load " + title);
        }
    }
}
