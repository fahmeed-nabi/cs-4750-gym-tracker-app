package org.example.controllers.trainer;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.database.DBManager;
import org.example.database.TrainerService;
import org.example.models.TrainerAppointment;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class RescheduleAppointment {

    @FXML private Label infoLabel;
    @FXML private DatePicker datePicker;
    @FXML private TextField startField;
    @FXML private TextField endField;

    private TrainerAppointment originalAppointment;
    private DBManager dbManager;
    private TrainerService trainerService;

    public void setAppointment(TrainerAppointment appt) {
        this.originalAppointment = appt;
        infoLabel.setText("Rescheduling appointment with Student ID: " + appt.getStudentId());

        datePicker.setValue(appt.getDate());
        startField.setText(appt.getStartTime().toString());
        endField.setText(appt.getEndTime().toString());

        try {
            dbManager = new DBManager();
            dbManager.connect();
            Connection conn = dbManager.getConnection();
            trainerService = new TrainerService(conn);
        } catch (Exception e) {
            showError("Database error.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSave() {
        try {
            LocalDate newDate = datePicker.getValue();
            LocalTime newStart = LocalTime.parse(startField.getText());
            LocalTime newEnd = LocalTime.parse(endField.getText());

            boolean success = trainerService.updateTrainerAppointment(
                    originalAppointment.getAppointmentId(),
                    originalAppointment.getStudentId(),
                    originalAppointment.getTrainerId(),
                    newDate, newStart, newEnd
            );

            if (success) {
                showInfo("Appointment updated.");
                closeWindow();
            } else {
                showError("Time conflict or trainer not available.");
            }
        } catch (Exception e) {
            showError("Invalid time format or error updating.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        try {
            dbManager.disconnect();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) infoLabel.getScene().getWindow();
        stage.close();
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }

    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }

    public void setStage(Stage stage) {
        stage.setOnCloseRequest(e -> {
            try {
                if (dbManager != null) dbManager.disconnect();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }
}
