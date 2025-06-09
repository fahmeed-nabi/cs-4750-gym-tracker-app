package org.example.controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.database.DBManager;
// import org.example.services.ActivityService; // Uncomment and implement if needed

import java.sql.SQLException;

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

    private DBManager dbManager;
    private GymService gymService; // Optional: implement this if needed

    @FXML
    private void initialize() {
        try {
            dbManager = new DBManager();
            dbManager.connect();
            gymService = new GymService(dbManager.getConnection());

            handleRefreshOccupancy(); // Load initial occupancy stats

        } catch (SQLException e) {
            showAlert("Database Error", "Could not connect to database.");
            e.printStackTrace();
        }
    }

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

        activities.setLength(activities.length() - 2); // Trim trailing comma
        showAlert("Check-In Success", username + " checked in for: " + activities.toString());

        // TODO: Insert check-in data into DB via activityService
    }

    @FXML
    private void handleCheckOut() {
        String username = checkOutUsernameField.getText().trim();

        if (username.isEmpty()) {
            showAlert("No Input", "Please enter a student username to check out.");
            return;
    }

        try {
            int studentId = gymService.getStudentIdByUsername(username);  

            if (studentId == -1) {
                showAlert("User Not Found", "No student found with username: " + username);
                return;
        }

        boolean checkedOut = gymService.checkOutStudent(studentId);

        if (checkedOut) {
            dbManager.commit();
            showAlert("Success", username + " has been checked out.");
            checkOutUsernameField.clear();
        } else {
            showAlert("Check-Out Failed", "Could not check out student. They may not be checked in.");
        }

    } catch (SQLException e) {
        e.printStackTrace();
        showAlert("Database Error", "An error occurred during check-out.");
        try {
            dbManager.rollback();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}


    @FXML
    private void handleRefreshOccupancy() {
        // TODO: Replace with DB query in future
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
