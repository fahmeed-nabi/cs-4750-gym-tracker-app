package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.models.Instructor;

public class InstructorManager {

    @FXML
    private TextField nameField;

    @FXML
    private TextField certificationField;

    @FXML
    private TableView<Instructor> instructorTable;

    @FXML
    private TableColumn<Instructor, String> nameColumn;

    @FXML
    private TableColumn<Instructor, String> certColumn;

    private final ObservableList<Instructor> instructorList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Bind table columns to Instructor model properties
        nameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        certColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCertification()));

        instructorTable.setItems(instructorList);

        // TODO: Replace with database call in the future
        instructorList.addAll(
            new Instructor(1, "Alex Smith", "ACE Certified", ""),
            new Instructor(2, "Dana Kim", "CPR/First Aid", "")
        );
    }

    @FXML
    private void handleAddInstructor() {
        String name = nameField.getText().trim();
        String cert = certificationField.getText().trim();

        if (name.isEmpty() || cert.isEmpty()) {
            showAlert("Input Error", "Please enter both name and certification.");
            return;
        }

        // ID = 0, AssignedClass = "" until handled via database
        Instructor newInstructor = new Instructor(0, name, cert, "");

        instructorList.add(newInstructor); // UI only for now

        // TODO: Future integration:
        // instructorDAO.insertInstructor(newInstructor);

        nameField.clear();
        certificationField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
