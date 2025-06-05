package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.models.Trainer;

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

    private final ObservableList<Trainer> trainerList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        specialtyColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getSpecialty()));
        availabilityColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAvailability()));

        trainerTable.setItems(trainerList);

        // TODO: Replace with database pull in the future
        trainerList.addAll(
            new Trainer(1, "Jordan Bell", "Strength Training", "Mon/Wed 2–5PM"),
            new Trainer(2, "Sam Chen", "Yoga", "Tues/Thurs 10–1PM")
        );
    }

    @FXML
    private void handleAddTrainer() {
        String name = nameField.getText().trim();
        String specialty = specialtyField.getText().trim();
        String availability = availabilityField.getText().trim();

        if (name.isEmpty() || specialty.isEmpty() || availability.isEmpty()) {
            showAlert("Input Error", "Please fill in all fields.");
            return;
        }

        // ID = 0 for now, assume auto-generated in future DB schema
        Trainer newTrainer = new Trainer(0, name, specialty, availability);
        trainerList.add(newTrainer);

        // TODO: Connect with database insert logic here
        // trainerDAO.insertTrainer(newTrainer);

        nameField.clear();
        specialtyField.clear();
        availabilityField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
