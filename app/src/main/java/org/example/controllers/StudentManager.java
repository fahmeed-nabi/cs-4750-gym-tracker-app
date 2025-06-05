package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.models.Student;

public class StudentManager {

    @FXML private TextField studentNameField;
    @FXML private TextField studentIdField;
    @FXML private ComboBox<String> roleComboBox;

    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TableColumn<Student, String> idColumn;
    @FXML private TableColumn<Student, String> roleColumn;

    private ObservableList<Student> studentList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        idColumn.setCellValueFactory(cellData -> cellData.getValue().studentIdProperty());
        roleColumn.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
        studentTable.setItems(studentList);
    }

    @FXML
    private void handleAdd() {
        String name = studentNameField.getText().trim();
        String id = studentIdField.getText().trim();
        String role = roleComboBox.getValue();

        if (!name.isEmpty() && !id.isEmpty() && role != null) {
            studentList.add(new Student(name, id, role));
            studentNameField.clear();
            studentIdField.clear();
            roleComboBox.getSelectionModel().clearSelection();
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
