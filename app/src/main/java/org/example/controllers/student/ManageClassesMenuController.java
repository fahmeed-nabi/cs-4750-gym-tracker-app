package org.example.controllers.student;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.example.database.ClassService;
import org.example.database.DBManager;
import org.example.database.UserService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class ManageClassesMenuController {

    private String studentEmail;

    public void setStudentEmail(String email) {
        this.studentEmail = email;
    }

    private Stage getStage(ActionEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

    private void loadScene(String fxmlFile, String title, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student/" + fxmlFile));
            Parent root = loader.load();
            Scene newScene = new Scene(root, 1200, 700); // widened

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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student/manage-upcoming-classes.fxml"));
            Parent root = loader.load();

            DBManager dbManager = new DBManager();
            dbManager.connect();
            UserService userService = new UserService(dbManager.getConnection());
            ClassService classService = new ClassService(dbManager.getConnection());

            Integer studentId = userService.getUserId("Student", studentEmail);
            if (studentId == null) {
                throw new IllegalStateException("Student not found for email: " + studentEmail);
            }

            ManageUpcomingClassesController controller = loader.getController();
            controller.setDependencies(classService, userService, studentId, studentEmail);

            Scene newScene = new Scene(root, 1100, 700);
            newScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm());
            Stage stage = getStage(event);
            stage.setTitle("Upcoming Classes");
            stage.setScene(newScene);
            stage.show();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMyRegisteredClasses(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student/registered-classes.fxml"));
            Parent root = loader.load();

            DBManager dbManager = new DBManager();
            dbManager.connect();
            UserService userService = new UserService(dbManager.getConnection());
            ClassService classService = new ClassService(dbManager.getConnection());

            Integer studentId = userService.getUserId("Student", studentEmail);
            if (studentId == null) {
                throw new IllegalStateException("Student not found for email: " + studentEmail);
            }

            RegisteredClassesController controller = loader.getController();
            controller.setDependencies(classService, userService, studentId, studentEmail);

            Scene newScene = new Scene(root, 1000, 650);
            newScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm());
            Stage stage = getStage(event);
            stage.setTitle("My Registered Classes");
            stage.setScene(newScene);
            stage.show();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClassHistory(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student/class-history.fxml"));
            Parent root = loader.load();

            DBManager dbManager = new DBManager();
            dbManager.connect();
            UserService userService = new UserService(dbManager.getConnection());
            ClassService classService = new ClassService(dbManager.getConnection());

            Integer studentId = userService.getUserId("Student", studentEmail);
            if (studentId == null) {
                throw new IllegalStateException("Student not found for email: " + studentEmail);
            }

            ClassHistoryController controller = loader.getController();
            controller.setDependencies(classService, userService, studentId, studentEmail);

            Scene newScene = new Scene(root, 1000, 600);
            newScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Class Attendance History");
            stage.setScene(newScene);
            stage.show();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleBackToDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student/student-dashboard.fxml"));
            Parent root = loader.load();

            StudentDashboard controller = loader.getController();
            controller.setStudentEmail(studentEmail);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Student Dashboard");
            Scene scene = new Scene(root, 800, 700);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
