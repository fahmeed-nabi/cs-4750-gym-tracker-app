package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.stage.StageStyle;
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
        openModal("trainer-appointments.fxml", "Your Appointments");
    }

    @FXML
    private void handleUpdateAvailability() {
        openModal("update-availability.fxml", "Update Availability");
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
    private void handleManageSpecialty() {
        openModal("trainer-specialty.fxml", "Manage Specialty");
    }

}
