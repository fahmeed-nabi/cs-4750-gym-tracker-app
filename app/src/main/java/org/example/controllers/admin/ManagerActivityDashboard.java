package org.example.controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.Map;

public class ManagerActivityDashboard {

    @FXML private TextField checkInUsernameField;
    @FXML private TextField checkOutUsernameField;

    @FXML private CheckBox weightLiftingBox;
    @FXML private CheckBox cardioBox;
    @FXML private CheckBox poolBox;
    @FXML private CheckBox sportsBox;
    @FXML private CheckBox classBox;

    @FXML private Label afcLabel;
    @FXML private Label memorialLabel;
    @FXML private Label northLabel;


    @FXML
    private void handleCheckIn() {
        String username = checkInUsernameField.getText().trim();
        if (username.isEmpty()) {
            showAlert("Check-In Error", "Please enter a username.");
            return;
        }

        StringBuilder activities = new StringBuilder();
        if (weightLiftingBox.isSelected()) activities.append("Weight Lifting, ");
        if (cardioBox.isSelected()) activities.append("Cardio, ");
        if (poolBox.isSelected()) activities.append("Pool, ");
        if (sportsBox.isSelected()) activities.append("Sports, ");
        if (classBox.isSelected()) activities.append("Class, ");

        if (activities.length() == 0) {
            showAlert("Check-In Error", "Please select at least one activity.");
            return;
        }

        // Remove trailing comma and log result (replace with DB call later)
        activities.setLength(activities.length() - 2);
        showAlert("Check-In Success", username + " checked in for: " + activities.toString());
    }

    @FXML
    private void handleCheckOut() {
        String username = checkOutUsernameField.getText().trim();
        if (username.isEmpty()) {
            showAlert("Check-Out Error", "Please enter a username.");
            return;
        }

        showAlert("Check-Out Success", username + " has been checked out.");
    }

    @FXML
    private void handleRefreshOccupancy() {
        // Stub values (you can query a database later)
        afcLabel.setText("AFC: 32 users");
        memorialLabel.setText("Memorial: 18 users");
        northLabel.setText("North Grounds: 25 users");
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
