package org.example.controllers.trainer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.database.DBManager;
import org.example.database.TrainerService;
import org.example.models.TrainerSpecialty;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TrainerSpecialtyController {

    @FXML private ComboBox<String> specialtyComboBox;
    @FXML private TableView<TrainerSpecialty> specialtyTable;
    @FXML private TableColumn<TrainerSpecialty, String> specialtyColumn;

    private TrainerService trainerService;
    private int trainerId;
    private final ObservableList<TrainerSpecialty> specialtyList = FXCollections.observableArrayList();

    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
        loadSpecialties();
    }

    @FXML
    public void initialize() {
        specialtyColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getSpecialty()));
        specialtyTable.setItems(specialtyList);

        specialtyComboBox.setItems(FXCollections.observableArrayList(
                "Strength", "Cardio", "Aquatics", "Yoga", "Dance", "Flexibility", "Athletic Training"
        ));

        specialtyComboBox.setEditable(true);

        try {
            DBManager dbManager = new DBManager();
            dbManager.connect();
            trainerService = new TrainerService(dbManager.getConnection());
        } catch (SQLException e) {
            showError("Database connection failed.");
        }
    }

    private void loadSpecialties() {
        try {
            List<TrainerSpecialty> list = trainerService.getTrainerSpecialties(trainerId);
            specialtyList.setAll(list);
        } catch (SQLException e) {
            showError("Failed to load specialties.");
        }
    }

    @FXML
    private void handleAdd() {
        String selected = specialtyComboBox.getValue();
        if (selected == null || selected.isBlank()) {
            showError("Please select a specialty.");
            return;
        }

        try {
            boolean added = trainerService.addTrainerSpecialty(trainerId, selected);
            if (added) {
                loadSpecialties();
            } else {
                showError("Specialty already exists or failed to add.");
            }
        } catch (SQLException e) {
            showError("Error adding specialty.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRemove() {
        TrainerSpecialty selected = specialtyTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a specialty to remove.");
            return;
        }

        try {
            boolean removed = trainerService.removeTrainerSpecialty(trainerId, selected.getSpecialty());
            if (removed) {
                loadSpecialties();
            } else {
                showError("Could not remove specialty.");
            }
        } catch (SQLException e) {
            showError("Error removing specialty.");
            e.printStackTrace();
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setHeaderText("Error");
        alert.showAndWait();
    }
}
