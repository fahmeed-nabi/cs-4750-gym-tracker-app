package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.models.Student;

public class StudentManager {

    @FXML private TextField studentNameField;
    @FXML private TextField studentIdField;
    @FXML private TextField usernameField;
    @FXML private ComboBox<String> roleComboBox;

    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TableColumn<Student, String> idColumn;
    @FXML private TableColumn<Student, String> usernameColumn;
    @FXML private TableColumn<Student, String> roleColumn;

    private final ObservableList<Student> studentList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        idColumn.setCellValueFactory(cellData -> cellData.getValue().studentIdProperty());
        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        roleColumn.setCellValueFactory(cellData -> cellData.getValue().roleProperty());

        studentTable.setItems(studentList);
    }

    @FXML
    private void handleAdd() {
        String name = studentNameField.getText().trim();
        String idText = studentIdField.getText().trim();
        String username = usernameField.getText().trim();
        String role = roleComboBox.getValue();

        if (!name.isEmpty() && !idText.isEmpty() && !username.isEmpty() && role != null) {
            try {
                int id = Integer.parseInt(idText);
                studentList.add(new Student(id, name, username, role));

                studentNameField.clear();
                studentIdField.clear();
                usernameField.clear();
                roleComboBox.getSelectionModel().clearSelection();
            } catch (NumberFormatException e) {
                System.err.println("Student ID must be an integer.");
            }
        }
    }

    @FXML
    private void handleDelete() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            studentList.remove(selected);
        }
    }
}
