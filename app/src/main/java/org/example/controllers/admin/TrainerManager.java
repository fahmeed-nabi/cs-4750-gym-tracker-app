package org.example.controllers.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.database.DBManager;
import org.example.database.TrainerService;
import org.example.models.Trainer;

import java.sql.Connection;
import java.sql.SQLException;

public class TrainerManager {

    @FXML
    private TextField nameField;

    @FXML
    private TextField specialtyField;

    @FXML
    private TextField availabilityField;

    @FXML
    private TableView<Trainer> trainerTable;

    @FXML
    private TableColumn<Trainer, String> nameColumn;

    @FXML
    private TableColumn<Trainer, String> specialtyColumn;

    @FXML
    private TableColumn<Trainer, String> availabilityColumn;

    private ObservableList<Trainer> trainerList = FXCollections.observableArrayList();
    private TrainerService trainerService;

    @FXML
    private void initialize() {
        try {
            DBManager dbManager = new DBManager();
            dbManager.connect();
            trainerService = new TrainerService(dbManager.getConnection());
            trainerList.addAll(trainerService.getAllTrainers());
    } catch (SQLException e) {
        showAlert("Database Error", "Could not load trainers: " + e.getMessage());
    }

    
}


    @FXML
    private void handleAdd() {
        String name = nameField.getText().trim();
        String specialty = specialtyField.getText().trim();
        String availability = availabilityField.getText().trim();

        if (name.isEmpty() || specialty.isEmpty() || availability.isEmpty()) {
            showAlert("Input Error", "Please fill in all fields.");
            return;
        }

        // This assumes future enhancement to persist to DB
        Trainer newTrainer = new Trainer(0, name, specialty, availability);
        trainerList.add(newTrainer);

        // Clear input fields
        nameField.clear();
        specialtyField.clear();
        availabilityField.clear();
    }

    @FXML
    private void handleDelete() {
        Trainer selected = trainerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Error", "Please select a trainer to delete.");
            return;
    }

        trainerList.remove(selected);

    // Optional: delete from DB (if implemented)
     //trainerService.deleteTrainerById(selected.getTrainerId());
}


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
