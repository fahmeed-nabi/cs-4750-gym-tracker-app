package org.example.controllers.trainer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.stage.StageStyle;
import org.example.controllers.trainer.TrainerAppointmentsController;
import org.example.database.DBManager;
import org.example.database.UserService;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class TrainerDashboard implements Initializable {

    @FXML
    private Label welcomeLabel;

    private DBManager dbManager;
    private UserService userService;
    private String trainerEmail;
    private int trainerId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            dbManager = new DBManager();
            dbManager.connect();
            Connection conn = dbManager.getConnection();
            userService = new UserService(conn);
        } catch (SQLException e) {
            welcomeLabel.setText("Welcome!");
            e.printStackTrace();
        }
    }

    public void setTrainerEmail(String email) {
        this.trainerEmail = email;

        if (userService != null && welcomeLabel != null) {
            try {
                String fullName = userService.getFullName("Trainer", trainerEmail);
                trainerId = userService.getUserId("Trainer", trainerEmail);

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

    private void openModal(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxml));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewAppointments() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/trainer/trainer-appointments.fxml"));
            Parent root = loader.load();

            TrainerAppointmentsController controller = loader.getController();
            controller.setTrainerId(trainerId);

            Stage stage = new Stage();
            stage.setTitle("Your Appointments");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleUpdateAvailability() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/trainer/update-availability.fxml"));
            Parent root = loader.load();

            org.example.controllers.trainer.UpdateAvailabilityController controller = loader.getController();
            controller.setTrainerId(trainerId);

            Stage stage = new Stage();
            stage.setTitle("Update Availability");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            ((Stage) welcomeLabel.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleManageSpecialties() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/trainer/trainer-specialty.fxml"));
            Parent root = loader.load();

            TrainerSpecialtyController controller = loader.getController();
            controller.setTrainerId(trainerId);

            Stage stage = new Stage();
            stage.setTitle("Manage Specialty");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewHistory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/trainer/trainer-appointment-history.fxml"));
            Parent root = loader.load();

            AppointmentHistoryController controller = loader.getController();
            controller.setTrainerId(trainerId); // assumes you have this field set

            Stage stage = new Stage();
            stage.setTitle("Appointment History");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
