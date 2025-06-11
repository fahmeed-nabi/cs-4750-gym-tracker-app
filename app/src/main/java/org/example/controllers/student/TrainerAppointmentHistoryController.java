package org.example.controllers.student;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import org.example.database.TrainerService;
import org.example.database.UserService;
import org.example.models.TrainerAppointment;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

public class TrainerAppointmentHistoryController {

    @FXML private TableView<TrainerAppointment> historyTable;
    @FXML private TableColumn<TrainerAppointment, LocalDate> dateColumn;
    @FXML private TableColumn<TrainerAppointment, LocalTime> startTimeColumn;
    @FXML private TableColumn<TrainerAppointment, LocalTime> endTimeColumn;
    @FXML private TableColumn<TrainerAppointment, String> trainerNameColumn;
    @FXML private TableColumn<TrainerAppointment, String> locationColumn;

    private TrainerService trainerService;
    private UserService userService;
    private int studentId;
    private String studentEmail;

    public void setDependencies(TrainerService trainerService, UserService userService, int studentId, String studentEmail) {
        this.trainerService = trainerService;
        this.userService = userService;
        this.studentId = studentId;
        this.studentEmail = studentEmail;
        loadAppointmentHistory();
    }

    private void loadAppointmentHistory() {
        try {
            List<TrainerAppointment> history = trainerService.getAppointmentHistoryForStudent(studentEmail);
            historyTable.getItems().setAll(history);

            dateColumn.setCellValueFactory(data -> data.getValue().dateProperty());
            startTimeColumn.setCellValueFactory(data -> data.getValue().startTimeProperty());
            endTimeColumn.setCellValueFactory(data -> data.getValue().endTimeProperty());
            trainerNameColumn.setCellValueFactory(data ->
                    new SimpleStringProperty(data.getValue().getStudentFirstName() + " " + data.getValue().getStudentLastName()));
            locationColumn.setCellValueFactory(data -> data.getValue().locationNameProperty());
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
}
