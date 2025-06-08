package org.example.controllers.student;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.stage.StageStyle;
import org.example.database.DBManager;
import org.example.database.GymService;
import org.example.database.UserService;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;

public class StudentDashboard implements Initializable {

    @FXML
    private VBox gymOccupancyContainer;

    @FXML
    private Label welcomeLabel;
    @FXML private BarChart<String, Number> occupancyChart;
    @FXML private CategoryAxis gymXAxis;
    @FXML private NumberAxis occupancyYAxis;

    private DBManager dbManager;
    private GymService gymService;
    private UserService userService;

    private String studentEmail;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            dbManager = new DBManager();
            dbManager.connect();
            Connection conn = dbManager.getConnection();
            gymService = new GymService(conn);
            userService = new UserService(conn);

            loadGymOccupancy();

        } catch (SQLException e) {
            welcomeLabel.setText("Welcome!");
            e.printStackTrace();
        }
    }

    public void setStudentEmail(String email) {
        this.studentEmail = email;

        if (userService != null && welcomeLabel != null) {
            try {
                String fullName = userService.getFullName("Student", email);
                if (fullName != null && !fullName.isBlank()) {
                    String firstName = fullName.split(" ")[0]; // assumes "First Last"
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

    private void loadGymOccupancy() {
        try {
            Map<String, Integer> rawCounts = gymService.getOccupancyForAllGyms();
            Map<String, Double> pctMap = gymService.getOccupancyPctForAllGyms();
            gymOccupancyContainer.getChildren().clear();

            for (String gym : rawCounts.keySet()) {
                int count = rawCounts.get(gym);
                double pct = pctMap.getOrDefault(gym, 0.0);
                String label = String.format("%s: %d people checked in (%.2f%%)", gym, count, pct);
                gymOccupancyContainer.getChildren().add(new Label(label));
            }

            loadOccupancyChart(pctMap);

        } catch (SQLException e) {
            gymOccupancyContainer.getChildren().clear();
            gymOccupancyContainer.getChildren().add(new Label("Error loading occupancy."));
            e.printStackTrace();
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
    private void handleManageClasses() {
        openModal("class-schedule.fxml", "Manage Class Schedule");
    }

    @FXML
    private void handleManageTrainerAppointments() {
        openModal("trainer-appointments.fxml", "Manage Trainer Appointments");
    }

    @FXML
    private void handleCheckIn() {
        openModal("check-in.fxml", "Gym Check-In");
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
            ((Stage) gymOccupancyContainer.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadOccupancyChart(Map<String, Double> gymOccupancyMap) {
        gymXAxis.setLabel("Gym");
        occupancyYAxis.setLabel("Occupancy (%)");
        occupancyYAxis.setAutoRanging(false);
        occupancyYAxis.setLowerBound(0);
        occupancyYAxis.setUpperBound(100);
        occupancyYAxis.setTickUnit(10);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Current Occupancy");

        for (Map.Entry<String, Double> entry : gymOccupancyMap.entrySet()) {
            String gymName = entry.getKey();
            double percent = entry.getValue();
            series.getData().add(new XYChart.Data<>(gymName, percent));
        }

        occupancyChart.getData().clear();
        occupancyChart.getData().add(series);
    }

    @FXML
    private void handleRefreshDashboard() {
        loadGymOccupancy();
    }
}
