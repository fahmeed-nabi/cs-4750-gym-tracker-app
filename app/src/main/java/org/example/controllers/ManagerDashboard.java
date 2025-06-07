package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.database.DBManager;
import org.example.database.GymService;
import org.example.database.UserService;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;

public class ManagerDashboard implements Initializable {

    @FXML
    private VBox occupancyLabelContainer;

    private DBManager dbManager;
    private GymService gymService;
    private UserService userService;

    @FXML
    private Label welcomeLabel;

    private String managerEmail;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            dbManager = new DBManager();
            dbManager.connect();
            Connection connection = dbManager.getConnection();
            gymService = new GymService(connection);
            userService = new UserService(connection);
            updateOccupancyDisplay();
        } catch (SQLException e) {
            occupancyLabelContainer.getChildren().clear();
            occupancyLabelContainer.getChildren().add(new Label("Failed to connect to database."));
            e.printStackTrace();
        }
    }

    public void setManagerEmail(String email) {
        this.managerEmail = email;

        if (userService != null && welcomeLabel != null) {
            try {
                String fullName = userService.getFullName("Admin", email);
                if (fullName != null && !fullName.isBlank()) {
                    String firstName = fullName.split(" ")[0];
                    welcomeLabel.setText("Welcome, " + firstName + "!");
                } else {
                    welcomeLabel.setText("Welcome!");
                }
            } catch (SQLException e) {
                welcomeLabel.setText("Welcome!");
                e.printStackTrace();
            }
        }
    }

    private void updateOccupancyDisplay() {
        try {
            Map<String, Integer> rawCounts = gymService.getOccupancyForAllGyms();
            Map<String, Double> pctMap = gymService.getOccupancyPctForAllGyms();
            occupancyLabelContainer.getChildren().clear();

            if (rawCounts.isEmpty()) {
                occupancyLabelContainer.getChildren().add(new Label("No occupancy data available."));
            } else {
                for (String gym : rawCounts.keySet()) {
                    int count = rawCounts.getOrDefault(gym, 0);
                    double pct = pctMap.getOrDefault(gym, 0.0);
                    String display = String.format("%s: %d currently checked in (%.2f%%)", gym, count, pct);
                    occupancyLabelContainer.getChildren().add(new Label(display));
                }
            }
        } catch (SQLException e) {
            occupancyLabelContainer.getChildren().clear();
            occupancyLabelContainer.getChildren().add(new Label("Error loading occupancy data."));
            e.printStackTrace();
        }
    }

    private void openWindow(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/" + fxml));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void handleManageStudents() {
        openWindow("student-manager.fxml", "Manage Students");
    }

    @FXML private void handleManageGyms() {
        openWindow("gym-manager.fxml", "Manage Gyms");
    }

    @FXML private void handleManageFacilities() {
        openWindow("facility-manager.fxml", "Manage Facilities");
    }

    @FXML private void handleManageClasses() {
        openWindow("class-manager.fxml", "Manage Classes");
    }

    @FXML private void handleManageTrainers() {
        openWindow("trainer-manager.fxml", "Manage Trainers");
    }

    @FXML private void handleManageInstructors() {
        openWindow("instructor-manager.fxml", "Manage Instructors");
    }

    @FXML private void handleManageActivity() {
        openWindow("manager-activity-dashboard.fxml", "Check-Ins and Occupancy");
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login-screen.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();

            // Close current window
            Stage currentStage = (Stage) occupancyLabelContainer.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
