package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.models.Trainer;

public class TrainerManager {

    @FXML private TextField trainerNameField;
    @FXML private TextField specialtyField;
    @FXML private TextField availabilityField;

    @FXML private TableView<Trainer> trainerTable;
    @FXML private TableColumn<Trainer, String> nameColumn;
    @FXML private TableColumn<Trainer, String> specialtyColumn;
    @FXML private TableColumn<Trainer, String> availabilityColumn;

    private ObservableList<Trainer> trainerList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        specialtyColumn.setCellValueFactory(cellData -> cellData.getValue().specialtyProperty());
        availabilityColumn.setCellValueFactory(cellData -> cellData.getValue().availabilityProperty());
        trainerTable.setItems(trainerList);
    }

    @FXML
    private void handleAdd() {
        String name = trainerNameField.getText().trim();
        String specialty = specialtyField.getText().trim();
        String availability = availabilityField.getText().trim();

        if (!name.isEmpty() && !specialty.isEmpty() && !availability.isEmpty()) {
            trainerList.add(new Trainer(name, specialty, availability));
            trainerNameField.clear();
            specialtyField.clear();
            availabilityField.clear();
        }
    }

    @FXML
    private void handleDelete() {
        Trainer selected = trainerTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            trainerList.remove(selected);
        }
    }
}
