package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ManagerDashboard {

    private void openWindow(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/" + fxml));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Modal window
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

}
