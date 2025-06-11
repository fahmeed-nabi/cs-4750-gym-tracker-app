package org.example.controllers.student;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.database.DBManager;
import org.example.database.TrainerService;
import org.example.database.UserService;
import org.example.models.TrainerAppointment;

import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

public class UpcomingAppointmentsController {

    @FXML private TableView<TrainerAppointment> appointmentsTable;
    @FXML private TableColumn<TrainerAppointment, LocalDate> dateColumn;
    @FXML private TableColumn<TrainerAppointment, LocalTime> startTimeColumn;
    @FXML private TableColumn<TrainerAppointment, LocalTime> endTimeColumn;
    @FXML private TableColumn<TrainerAppointment, String> trainerNameColumn;
    @FXML private TableColumn<TrainerAppointment, String> locationColumn;
    @FXML private TableColumn<TrainerAppointment, Void> actionColumn;

    private TrainerService trainerService;
    private UserService userService;
    private int studentId;
    private String studentEmail;

    public void setDependencies(TrainerService trainerService, UserService userService, int studentId, String studentEmail) {
        this.trainerService = trainerService;
        this.userService = userService;
        this.studentId = studentId;
        this.studentEmail = studentEmail;
        loadAppointments();
    }

    private void loadAppointments() {
        try {
            List<TrainerAppointment> upcoming = trainerService.getUpcomingAppointmentsForStudent(studentEmail);
            appointmentsTable.getItems().setAll(upcoming);

            dateColumn.setCellValueFactory(data -> data.getValue().dateProperty());
            startTimeColumn.setCellValueFactory(data -> data.getValue().startTimeProperty());
            endTimeColumn.setCellValueFactory(data -> data.getValue().endTimeProperty());
            trainerNameColumn.setCellValueFactory(data ->
                    new SimpleStringProperty(data.getValue().getStudentFirstName() + " " + data.getValue().getStudentLastName()));
            locationColumn.setCellValueFactory(data -> data.getValue().locationNameProperty());
            addCancelButtonToTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student/trainer-appointments.fxml"));
            Parent root = loader.load();

            TrainerAppointmentsMenuController controller = loader.getController();
            controller.setStudentEmail(studentEmail);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Manage Trainer Appointments");
            Scene scene = new Scene(root, 800, 700);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addCancelButtonToTable() {
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button cancelButton = new Button("Cancel");

            {
                cancelButton.setOnAction(event -> {
                    TrainerAppointment appointment = getTableView().getItems().get(getIndex());

                    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmation.setTitle("Cancel Appointment");
                    confirmation.setHeaderText(null);
                    confirmation.setContentText("Are you sure you want to cancel this appointment with " +
                            appointment.getStudentFirstName() + " " + appointment.getStudentLastName() + " on " +
                            appointment.getDate() + "?");

                    confirmation.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            try {
                                boolean success = trainerService.deleteTrainerAppointment(appointment.getAppointmentId());
                                if (success) {
                                    appointmentsTable.getItems().remove(appointment);
                                } else {
                                    showError("Failed to cancel appointment.");
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                                showError("Database error while canceling.");
                            }
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(cancelButton);
                }
            }
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
