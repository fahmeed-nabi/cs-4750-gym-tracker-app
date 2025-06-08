package org.example.controllers.trainer;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.stage.StageStyle;
import org.example.controllers.trainer.TrainerAppointmentsController;
import org.example.database.DBManager;
import org.example.database.TrainerService;
import org.example.database.UserService;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TrainerDashboard implements Initializable {

    @FXML
    private Label welcomeLabel;

    private DBManager dbManager;
    private UserService userService;
    private TrainerService trainerService;
    private String trainerEmail;
    private int trainerId;

    @FXML private Label specialtyLabel;
    @FXML private Label sessionCountLabel;
    @FXML private LineChart<String, Number> appointmentChart;
    @FXML private CategoryAxis appointmentXAxis;

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

        loadDashboardDetails();
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

    private void loadDashboardDetails() {
        try {
            trainerService = new TrainerService(dbManager.getConnection());

            // Load specialties
            var specialties = trainerService.getTrainerSpecialties(trainerId);
            String specialtyList = specialties.stream()
                    .map(s -> s.getSpecialty())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("None");
            specialtyLabel.setText("Specialty: " + specialtyList);

            // Load upcoming sessions
            int upcomingCount = trainerService.getTrainerAppointments(trainerId).size();
            sessionCountLabel.setText("You have " + upcomingCount + " upcoming session" + (upcomingCount == 1 ? "" : "s"));

            List<String> days = List.of("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
            appointmentXAxis.setCategories(FXCollections.observableArrayList(days));

            // Load chart
            Map<String, Long> sessionsPerDay = trainerService.getTrainerAppointmentsForCurrentWeek(trainerId).stream()
                    .collect(Collectors.groupingBy(
                            appt -> formatDayOfWeek(appt.getDate().getDayOfWeek()),
                            Collectors.counting()
                    ));

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Appointments");

            for (String day : days) {
                long count = sessionsPerDay.getOrDefault(day, 0L);
                series.getData().add(new XYChart.Data<>(day, count));
            }

            appointmentChart.getData().clear();
            appointmentChart.layout(); // Force axis/layout reset before adding series

            Platform.runLater(() -> {
                appointmentChart.getData().add(series);
            });

        } catch (SQLException e) {
            e.printStackTrace();
            specialtyLabel.setText("Specialty: Error");
            sessionCountLabel.setText("Error loading session count.");
        }
    }

    private String formatDayOfWeek(java.time.DayOfWeek day) {
        return switch (day) {
            case MONDAY -> "Mon";
            case TUESDAY -> "Tue";
            case WEDNESDAY -> "Wed";
            case THURSDAY -> "Thu";
            case FRIDAY -> "Fri";
            case SATURDAY -> "Sat";
            case SUNDAY -> "Sun";
        };
    }

    @FXML
    private void handleRefreshDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/trainer/trainer-dashboard.fxml"));
            Parent newRoot = loader.load();

            TrainerDashboard controller = loader.getController();
            controller.setTrainerEmail(trainerEmail); // re-set current trainer

            // Replace only the root content of the current scene
            Scene currentScene = welcomeLabel.getScene();
            currentScene.setRoot(newRoot);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
