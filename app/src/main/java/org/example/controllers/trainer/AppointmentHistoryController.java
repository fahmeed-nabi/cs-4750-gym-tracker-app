package org.example.controllers.trainer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.database.DBManager;
import org.example.database.TrainerService;
import org.example.models.TrainerAppointment;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class AppointmentHistoryController implements Initializable {

    @FXML private TableView<TrainerAppointment> historyTable;
    @FXML private TableColumn<TrainerAppointment, String> dateColumn;
    @FXML private TableColumn<TrainerAppointment, String> startTimeColumn;
    @FXML private TableColumn<TrainerAppointment, String> endTimeColumn;
    @FXML private TableColumn<TrainerAppointment, Integer> studentIdColumn;
    @FXML private TableColumn<TrainerAppointment, String> firstNameColumn;
    @FXML private TableColumn<TrainerAppointment, String> lastNameColumn;
    @FXML private TableColumn<TrainerAppointment, String> locationColumn;

    private int trainerId;
    private TrainerService trainerService;
    private DBManager dbManager;

    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
        loadHistory();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dateColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDate().toString()));
        startTimeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStartTime().toString()));
        endTimeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEndTime().toString()));
        studentIdColumn.setCellValueFactory(cell -> cell.getValue().studentIdProperty().asObject());
        firstNameColumn.setCellValueFactory(cell -> cell.getValue().studentFirstNameProperty());
        lastNameColumn.setCellValueFactory(cell -> cell.getValue().studentLastNameProperty());
        locationColumn.setCellValueFactory(cell -> cell.getValue().locationNameProperty());
    }

    private void loadHistory() {
        try {
            dbManager = new DBManager();
            dbManager.connect();
            Connection conn = dbManager.getConnection();
            trainerService = new TrainerService(conn);

            List<TrainerAppointment> pastAppointments = trainerService.getPastAppointments(trainerId);
            ObservableList<TrainerAppointment> data = FXCollections.observableArrayList(pastAppointments);
            historyTable.setItems(data);
        } catch (SQLException e) {
            showError("Error loading appointment history.");
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
        try {
            dbManager.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Stage stage = (Stage) historyTable.getScene().getWindow();
        stage.close();
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
