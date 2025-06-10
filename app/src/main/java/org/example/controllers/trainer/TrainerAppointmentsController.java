package org.example.controllers.trainer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.database.DBManager;
import org.example.database.TrainerService;
import org.example.models.TrainerAppointment;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class TrainerAppointmentsController implements Initializable {

    @FXML private TableView<TrainerAppointment> appointmentsTable;
    @FXML private TableColumn<TrainerAppointment, String> dateColumn;
    @FXML private TableColumn<TrainerAppointment, String> startTimeColumn;
    @FXML private TableColumn<TrainerAppointment, String> endTimeColumn;
    @FXML private TableColumn<TrainerAppointment, Integer> studentIdColumn;
    @FXML private TableColumn<TrainerAppointment, String> firstNameColumn;
    @FXML private TableColumn<TrainerAppointment, String> lastNameColumn;
    @FXML private TableColumn<TrainerAppointment, String> locationColumn;
    @FXML private TableColumn<TrainerAppointment, Void> actionColumn;

    private int trainerId;
    private TrainerService trainerService;

    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
        loadAppointments();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configure table columns
        dateColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDate().toString()));

        startTimeColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStartTime().toString()));

        endTimeColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEndTime().toString()));

        studentIdColumn.setCellValueFactory(cell -> cell.getValue().studentIdProperty().asObject());
        firstNameColumn.setCellValueFactory(cell -> cell.getValue().studentFirstNameProperty());
        lastNameColumn.setCellValueFactory(cell -> cell.getValue().studentLastNameProperty());
        locationColumn.setCellValueFactory(cell -> cell.getValue().locationNameProperty());

        configureActionColumn();
    }

    private void configureActionColumn() {
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final MenuButton optionsButton = new MenuButton("...");

            {
                MenuItem cancelItem = new MenuItem("Cancel");
                MenuItem rescheduleItem = new MenuItem("Reschedule");

                cancelItem.setOnAction(event -> handleCancel(getCurrentAppointment()));
                rescheduleItem.setOnAction(event -> handleReschedule(getCurrentAppointment()));

                optionsButton.getItems().addAll(cancelItem, rescheduleItem);
                optionsButton.setStyle("-fx-font-size: 11px; -fx-padding: 2 6;");
            }

            private TrainerAppointment getCurrentAppointment() {
                return getTableView().getItems().get(getIndex());
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(optionsButton);
                }
            }
        });
    }

    private void loadAppointments() {
        try {
            DBManager dbManager = new DBManager();
            dbManager.connect();
            Connection conn = dbManager.getConnection();
            trainerService = new TrainerService(conn);

            List<TrainerAppointment> appointments = trainerService.getTrainerAppointments(trainerId);
            ObservableList<TrainerAppointment> data = FXCollections.observableArrayList(appointments);
            appointmentsTable.setItems(data);
        } catch (SQLException e) {
            showError("Error loading appointments.");
            e.printStackTrace();
        }
    }

    private void handleCancel(TrainerAppointment appt) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to cancel this appointment?",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText("Cancel Appointment");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    boolean deleted = trainerService.deleteTrainerAppointment(appt.getAppointmentId());
                    if (deleted) {
                        showInfo("Appointment cancelled.");
                        loadAppointments();
                    } else {
                        showError("Failed to cancel appointment.");
                    }
                } catch (SQLException e) {
                    showError("Database error while canceling appointment.");
                    e.printStackTrace();
                }
            }
        });
    }

    private void handleReschedule(TrainerAppointment appt) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/trainer/reschedule-appointment.fxml"));
            Parent root = loader.load();

            org.example.controllers.trainer.RescheduleAppointment controller = loader.getController();
            controller.setAppointment(appt);

            Stage stage = new Stage();
            stage.setTitle("Reschedule Appointment");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            loadAppointments(); // Refresh after modal closes

        } catch (Exception e) {
            showError("Could not open reschedule window.");
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setHeaderText("Database Error");
        alert.showAndWait();
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) appointmentsTable.getScene().getWindow();
        stage.close();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setHeaderText("Success");
        alert.showAndWait();
    }

}
