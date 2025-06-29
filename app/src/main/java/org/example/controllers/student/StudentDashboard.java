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

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.io.IOException;


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

    private void openScene(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student/" + fxml));

            Parent root = loader.load();

            // Get the current stage from any component in the current scene
            Stage stage = (Stage) gymOccupancyContainer.getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleManageClasses() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student/manage-classes.fxml"));
            Parent root = loader.load();

            ManageClassesMenuController controller = loader.getController();
            controller.setStudentEmail(studentEmail);

            Stage stage = (Stage) gymOccupancyContainer.getScene().getWindow();
            Scene scene = new Scene(root, 800, 700);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Manage Class Schedule");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleManageTrainerAppointments() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student/trainer-appointments.fxml"));
            Parent root = loader.load();

            TrainerAppointmentsMenuController controller = loader.getController();
            controller.setStudentEmail(studentEmail);

            Stage stage = (Stage) gymOccupancyContainer.getScene().getWindow();
            Scene scene = new Scene(root, 800, 700);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Manage Trainer Appointments");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCheckIn() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student/check-in.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the email
            StudentCheckinView controller = loader.getController();
            controller.setEmail(studentEmail);

            
            Stage stage = new Stage();
            stage.setTitle("Gym Check-In");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateInfo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student/update-info.fxml"));
            Parent root = loader.load();

            UpdateInfoController controller = loader.getController();
            controller.setCurrentEmail(studentEmail);

            Stage stage = new Stage();
            stage.setTitle("Update Your Info");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            dbManager.disconnect();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login-screen.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Login");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm());
            stage.setScene(scene);
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
        try {
            // Reconnect and fetch updated services
            dbManager = new DBManager();
            dbManager.connect();
            Connection conn = dbManager.getConnection();
            gymService = new GymService(conn);
            userService = new UserService(conn);

            // Re-load data
            loadGymOccupancy();

            // Re-display welcome message if necessary
            if (studentEmail != null) {
                String fullName = userService.getFullName("Student", studentEmail);
                if (fullName != null && !fullName.isBlank()) {
                    String firstName = fullName.split(" ")[0];
                    welcomeLabel.setText("Welcome, " + firstName + "!");
                }
            }

        } catch (SQLException e) {
            gymOccupancyContainer.getChildren().clear();
            gymOccupancyContainer.getChildren().add(new Label("Error refreshing dashboard."));
            e.printStackTrace();
        }
    }

}

