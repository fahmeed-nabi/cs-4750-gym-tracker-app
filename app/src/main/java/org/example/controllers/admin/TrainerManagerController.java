package org.example.controllers.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.database.DBManager;
import org.example.database.TrainerService;
import org.example.models.Trainer;
import org.example.models.TrainerSpecialty;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TrainerManagerController {

    @FXML private ListView<Trainer> trainerListView;
    @FXML private ListView<String> specialtiesListView;

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField addSpecialtyField;
    @FXML private TextField updateSpecialtyField;

    private DBManager dbManager;
    private TrainerService trainerService;
    private Trainer selectedTrainer;

    @FXML
    public void initialize() {
        try {
            dbManager = new DBManager();
            dbManager.connect();
            Connection conn = dbManager.getConnection();
            trainerService = new TrainerService(conn);

            loadTrainers();
            trainerListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) populateForm(newVal);
            });
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error initializing trainer manager.");
        }
    }

    private void loadTrainers() throws SQLException {
        List<Trainer> trainers = trainerService.getAllTrainers();
        trainerListView.setItems(FXCollections.observableArrayList(trainers));
    }

    private void populateForm(Trainer trainer) {
        selectedTrainer = trainer;
        try {
            List<TrainerSpecialty> specialties = trainerService.getTrainerSpecialties(trainer.getTrainerId());
            ObservableList<String> specList = FXCollections.observableArrayList();
            for (TrainerSpecialty ts : specialties) {
                specList.add(ts.getSpecialty());
            }
            specialtiesListView.setItems(specList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error loading trainer specialties.");
        }
    }

    @FXML
    private void handleAddTrainer() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert("Please fill out all fields to add a trainer.");
            return;
        }

        try {
            boolean success = trainerService.addTrainer(firstName, lastName, email, password);
            if (success) {
                showAlert("Trainer added successfully.");
                loadTrainers();
                clearAddTrainerForm();
            } else {
                showAlert("Failed to add trainer.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database error while adding trainer.");
        }
    }

    private void clearAddTrainerForm() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        passwordField.clear();
    }

    @FXML
    private void handleDeleteTrainer() {
        if (selectedTrainer == null) {
            showAlert("Select a trainer to delete.");
            return;
        }

        try {
            if (trainerService.deleteTrainer(selectedTrainer.getTrainerId())) {
                showAlert("Trainer deleted.");
                loadTrainers();
                specialtiesListView.getItems().clear();
            } else {
                showAlert("Failed to delete trainer.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database error while deleting trainer.");
        }
    }

    @FXML
    private void handleAddSpecialty() {
        if (selectedTrainer == null) {
            showAlert("Please select a trainer first.");
            return;
        }

        String specialty = addSpecialtyField.getText().trim();
        if (specialty.isEmpty()) {
            showAlert("Enter a specialty to add.");
            return;
        }

        try {
            if (trainerService.addTrainerSpecialty(selectedTrainer.getTrainerId(), specialty)) {
                populateForm(selectedTrainer);
                addSpecialtyField.clear();
            } else {
                showAlert("Failed to add specialty.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database error while adding specialty.");
        }
    }

    @FXML
    private void handleRemoveSpecialty() {
        if (selectedTrainer == null) return;
        String selected = specialtiesListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            if (trainerService.removeTrainerSpecialty(selectedTrainer.getTrainerId(), selected)) {
                populateForm(selectedTrainer);
            } else {
                showAlert("Failed to remove specialty.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database error while removing specialty.");
        }
    }

    @FXML
    private void handleUpdateSpecialty() {
        if (selectedTrainer == null) return;
        String selected = specialtiesListView.getSelectionModel().getSelectedItem();
        String newSpecialty = updateSpecialtyField.getText().trim();

        if (selected == null || newSpecialty.isEmpty()) {
            showAlert("Select a specialty and enter a new value.");
            return;
        }

        try {
            if (trainerService.updateTrainerSpecialty(selectedTrainer.getTrainerId(), selected, newSpecialty)) {
                populateForm(selectedTrainer);
                updateSpecialtyField.clear();
            } else {
                showAlert("Failed to update specialty.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database error while updating specialty.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
