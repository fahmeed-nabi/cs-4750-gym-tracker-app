package org.example.controllers.admin;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.database.DBManager;
import org.example.database.InstructorService;
import org.example.models.Instructor;

import java.sql.SQLException;

public class InstructorManager {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField certificationField;
    @FXML private TextField focusAreaField;

    @FXML private TableView<Instructor> instructorTable;
    @FXML private TableColumn<Instructor, String> firstNameColumn;
    @FXML private TableColumn<Instructor, String> lastNameColumn;
    @FXML private TableColumn<Instructor, String> emailColumn;
    @FXML private TableColumn<Instructor, String> certColumn;
    @FXML private TableColumn<Instructor, String> focusColumn;

    private final ObservableList<Instructor> instructorList = FXCollections.observableArrayList();
    private DBManager dbManager;
    private InstructorService instructorService;

    @FXML
    private void initialize() {
        try {
            dbManager = new DBManager();
            dbManager.connect();
            instructorService = new InstructorService(dbManager.getConnection());

            instructorList.addAll(instructorService.getAllInstructors());
        } catch (SQLException e) {
            showAlert("Database Error", "Could not connect to database.");
            e.printStackTrace();
        }

        firstNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFirstName()));
        lastNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLastName()));
        emailColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        certColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCertification()));
        focusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFocusArea()));

        instructorTable.setItems(instructorList);

        instructorTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                populateFields(newSel);
            }
        });
    }

    private void populateFields(Instructor instructor) {
        firstNameField.setText(instructor.getFirstName());
        lastNameField.setText(instructor.getLastName());
        emailField.setText(instructor.getEmail());
        certificationField.setText(instructor.getCertification());
        focusAreaField.setText(instructor.getFocusArea());
    }

    @FXML
    private void handleAddInstructor() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String certification = certificationField.getText().trim();
        String focusArea = focusAreaField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                || certification.isEmpty() || focusArea.isEmpty()) {
            showAlert("Input Error", "Please fill in all fields.");
            return;
        }

        try {
            boolean success = instructorService.addInstructor(firstName, lastName, email, certification, focusArea);
            if (success) {
                instructorList.clear();
                instructorList.addAll(instructorService.getAllInstructors());
                clearFields();
            } else {
                showAlert("Insert Failed", "Could not add instructor.");
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to add instructor.");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDeleteInstructor(ActionEvent actionEvent) {
        Instructor selected = instructorTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("No Selection", "Please select an instructor to delete.");
            return;
        }

        try {
            boolean deleted = instructorService.deleteInstructor(selected.getInstructorId());

            if (deleted) {
                dbManager.commit();
                instructorList.remove(selected);
                clearFields();
                showAlert("Success", "Instructor deleted successfully.");
            } else {
                showAlert("Delete Failed", "Could not delete instructor from the database.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while deleting the instructor.");
            try {
                dbManager.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    public void handleUpdateInstructor() {
        Instructor selected = instructorTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("No Selection", "Please select an instructor to update.");
            return;
        }

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String certification = certificationField.getText().trim();
        String focusArea = focusAreaField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                || certification.isEmpty() || focusArea.isEmpty()) {
            showAlert("Input Error", "Please fill in all fields.");
            return;
        }

        try {
            boolean success = instructorService.updateInstructor(
                    selected.getInstructorId(), firstName, lastName, email, certification, focusArea
            );

            if (success) {
                instructorList.clear();
                instructorList.addAll(instructorService.getAllInstructors());
                clearFields();
                showAlert("Update Successful", "Instructor updated successfully.");
            } else {
                showAlert("Update Failed", "Could not update instructor.");
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to update instructor.");
            e.printStackTrace();
        }
    }

    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        certificationField.clear();
        focusAreaField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
