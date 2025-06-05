package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class GymManager {

    @FXML private TextField gymNameField;
    @FXML private ListView<String> gymList;

    @FXML
    private void handleAdd() {
        String name = gymNameField.getText().trim();
        if (!name.isEmpty()) {
            gymList.getItems().add(name);
            gymNameField.clear();
        }
    }

    @FXML
    private void handleDelete() {
        String selected = gymList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            gymList.getItems().remove(selected);
        }
    }
}
