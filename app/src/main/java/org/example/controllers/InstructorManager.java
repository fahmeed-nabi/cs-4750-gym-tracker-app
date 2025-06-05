package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.models.Instructor;

public class InstructorManager {

    @FXML private TextField instructorNameField;
    @FXML private TextField certificationField;

    @FXML private TableView<Instructor> instructorTable;
    @FXML private TableColumn<Instructor, String> nameColumn;
    @FXML private TableColumn<Instructor, String> certColumn;

    private ObservableList<Instructor> instructorList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        certColumn.setCellValueFactory(cellData -> cellData.getValue().certificationProperty());
        instructorTable.setItems(instructorList);
    }

    @FXML
    private void handleAdd() {
        String name = instructorNameField.getText().trim();
        String cert = certificationField.getText().trim();

        if (!name.isEmpty() && !cert.isEmpty()) {
            instructorList.add(new Instructor(name, cert));
            instructorNameField.clear();
            certificationField.clear();
        }
    }

    @FXML
    private void handleDelete() {
        Instructor selected = instructorTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            instructorList.remove(selected);
        }
    }
}
