package org.example.controllers.student;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;
import org.example.database.*;
import org.example.models.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class BookTrainerController {

    @FXML private ComboBox<Trainer> trainerComboBox;
    @FXML private ComboBox<Gym> gymComboBox;
    @FXML private GridPane availabilityGrid;

    private DBManager dbManager;
    private TrainerService trainerService;
    private UserService userService;
    private GymService gymService;

    private int studentId;
    private String studentEmail;
    private final ToggleGroup toggleGroup = new ToggleGroup();

    public void setStudentEmail(String email) {
        this.studentEmail = email;
    }

    public void setDependencies(TrainerService trainerService, GymService gymService, UserService userService,
                                int studentId, String studentEmail) {
        this.trainerService = trainerService;
        this.gymService = gymService;
        this.userService = userService;
        this.studentId = studentId;
        this.studentEmail = studentEmail;

        loadTrainers();
        loadGyms();
    }

    public void initializeServices() {
        try {
            dbManager = new DBManager();
            dbManager.connect();

            trainerService = new TrainerService(dbManager.getConnection());
            userService = new UserService(dbManager.getConnection());
            gymService = new GymService(dbManager.getConnection());

            studentId = userService.getUserId("Student", studentEmail);
            if (studentId == -1) throw new IllegalStateException("Student not found: " + studentEmail);

            loadTrainers();
            loadGyms();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error initializing services.");
        }
    }

    private void loadTrainers() {
        try {
            List<Trainer> trainers = trainerService.getAllTrainers();
            trainerComboBox.getItems().addAll(trainers);
            trainerComboBox.setOnAction(e -> loadAvailability(trainerComboBox.getValue()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadGyms() {
        try {
            List<Gym> gyms = gymService.getAllGyms();
            gymComboBox.getItems().addAll(gyms);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAvailability(Trainer trainer) {
        availabilityGrid.getChildren().clear();
        try {
            List<TrainerAvailability> availabilities = trainerService.getAvailabilityByTrainer(trainer.getTrainerId());
            Map<String, List<TrainerAvailability>> grouped = new LinkedHashMap<>();
            for (String day : List.of("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")) {
                grouped.put(day, new ArrayList<>());
            }
            for (TrainerAvailability a : availabilities) {
                grouped.get(a.getDayOfWeek()).add(a);
            }

            int col = 1;
            for (String day : grouped.keySet()) {
                availabilityGrid.add(new Label(day), col++, 0);
            }

            int row = 1;
            for (int hour = 6; hour <= 20; hour++) {
                availabilityGrid.add(new Label(String.format("%02d:00", hour)), 0, row);
                col = 1;
                for (String day : grouped.keySet()) {
                    int finalHour = hour;
                    boolean slotAvailable = grouped.get(day).stream().anyMatch(a ->
                            finalHour >= LocalTime.parse(a.getStartTime()).getHour() &&
                                    finalHour < LocalTime.parse(a.getEndTime()).getHour());
                    if (slotAvailable) {
                        RadioButton rb = new RadioButton();
                        rb.setToggleGroup(toggleGroup);
                        rb.setUserData(Map.of("day", day, "hour", hour, "trainer", trainer));
                        availabilityGrid.add(rb, col, row);
                    }
                    col++;
                }
                row++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBook() {
        Toggle selected = toggleGroup.getSelectedToggle();
        Gym selectedGym = gymComboBox.getValue();

        if (selected == null || selectedGym == null) {
            showAlert("Please select both a time slot and a gym.");
            return;
        }

        Map<String, Object> data = (Map<String, Object>) selected.getUserData();
        String day = (String) data.get("day");
        int hour = (int) data.get("hour");
        Trainer trainer = (Trainer) data.get("trainer");

        LocalDate nextDate = getNextDate(day);
        LocalTime start = LocalTime.of(hour, 0);
        LocalTime end = start.plusHours(1);

        try {
            boolean booked = trainerService.bookTrainerAppointment(
                    studentId, trainer.getTrainerId(), selectedGym.getGymId(), nextDate, start, end);
            showAlert(booked ? "Appointment booked!" : "Failed to book appointment.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database error while booking.");
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student/trainer-appointments.fxml"));
            Parent root = loader.load();

            TrainerAppointmentsMenuController controller = loader.getController();
            controller.setStudentEmail(studentEmail);

            Stage stage = (Stage) trainerComboBox.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 700));
            stage.setTitle("Manage Trainer Appointments");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private LocalDate getNextDate(String dayAbbreviation) {
        Map<String, java.time.DayOfWeek> map = Map.of(
                "Mon", java.time.DayOfWeek.MONDAY,
                "Tue", java.time.DayOfWeek.TUESDAY,
                "Wed", java.time.DayOfWeek.WEDNESDAY,
                "Thu", java.time.DayOfWeek.THURSDAY,
                "Fri", java.time.DayOfWeek.FRIDAY,
                "Sat", java.time.DayOfWeek.SATURDAY,
                "Sun", java.time.DayOfWeek.SUNDAY
        );
        LocalDate today = LocalDate.now();
        int daysUntil = (map.get(dayAbbreviation).getValue() - today.getDayOfWeek().getValue() + 7) % 7;
        return today.plusDays(daysUntil == 0 ? 7 : daysUntil);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
