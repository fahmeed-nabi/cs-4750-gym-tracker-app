package org.example.controllers.student;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.example.database.DBManager;
import org.example.database.TrainerService;
import org.example.database.UserService;

import java.io.IOException;
import java.sql.SQLException;

public class TrainerAppointmentsMenuController {

    private String studentEmail;

    public void setStudentEmail(String email) {
        this.studentEmail = email;
    }

    private Stage getStage(ActionEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

    @FXML
    private void handleViewUpcomingAppointments(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student/upcoming-trainer-appointments.fxml"));
            Parent root = loader.load();

            DBManager dbManager = new DBManager();
            dbManager.connect();
            TrainerService trainerService = new TrainerService(dbManager.getConnection());
            UserService userService = new UserService(dbManager.getConnection());

            Integer studentId = userService.getUserId("Student", studentEmail);
            if (studentId == null) throw new IllegalStateException("Student not found for email: " + studentEmail);

            UpcomingAppointmentsController controller = loader.getController();
            controller.setDependencies(trainerService, userService, studentId, studentEmail);

            Stage stage = getStage(event);
            stage.setTitle("Upcoming Trainer Appointments");
            stage.setScene(new Scene(root, 1000, 650));
            stage.show();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewAppointmentHistory(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student/trainer-appointment-history.fxml"));
            Parent root = loader.load();

            DBManager dbManager = new DBManager();
            dbManager.connect();
            TrainerService trainerService = new TrainerService(dbManager.getConnection());
            UserService userService = new UserService(dbManager.getConnection());

            Integer studentId = userService.getUserId("Student", studentEmail);
            if (studentId == null) throw new IllegalStateException("Student not found for email: " + studentEmail);

            TrainerAppointmentHistoryController controller = loader.getController();
            controller.setDependencies(trainerService, userService, studentId, studentEmail);

            Stage stage = getStage(event);
            stage.setTitle("Trainer Appointment History");
            stage.setScene(new Scene(root, 1000, 650));
            stage.show();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBookTrainer(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student/book-trainer.fxml"));
            Parent root = loader.load();

            BookTrainerController controller = loader.getController();
            controller.setStudentEmail(studentEmail);

            Stage stage = getStage(event);
            stage.setTitle("Book a Trainer");
            stage.setScene(new Scene(root, 900, 650));
            stage.show();
        } catch (IOException e) {
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

            Stage stage = getStage(event);
            stage.setTitle("Student Dashboard");
            stage.setScene(new Scene(root, 800, 700));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
