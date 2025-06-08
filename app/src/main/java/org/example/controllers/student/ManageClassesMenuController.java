package org.example.controllers.student;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;

public class ManageClassesMenuController {

    private Stage getStage(ActionEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

    private void loadScene(String fxmlFile, String title, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student/" + fxmlFile));
            Parent root = loader.load();
            Scene newScene = new Scene(root, 800, 700);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(newScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewUpcomingClasses(ActionEvent event) {
        loadScene("manage-upcoming-classes.fxml", "Upcoming Classes", event);
    }

    @FXML
    private void handleMyRegisteredClasses(ActionEvent event) {
        loadScene("registered-classes.fxml", "My Registered Classes", event);
    }

    @FXML
    private void handleClassHistory(ActionEvent event) {
        loadScene("class-history.fxml", "Class Attendance History", event);
    }

    @FXML
    private void handleBackToDashboard(ActionEvent event) {
        loadScene("student-dashboard.fxml", "Student Dashboard", event);
    }
}
