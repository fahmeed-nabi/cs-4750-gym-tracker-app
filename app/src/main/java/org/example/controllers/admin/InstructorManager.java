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

                firstNameField.clear();
                lastNameField.clear();
                emailField.clear();
                certificationField.clear();
                focusAreaField.clear();
            } else {
                showAlert("Insert Failed", "Could not add instructor.");
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to add instructor.");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    

}
