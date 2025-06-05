package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ClassManager {

    @FXML private TextField classNameField;
    @FXML private ListView<String> classList;

    @FXML
    private void handleAdd() {
        String name = classNameField.getText().trim();
        if (!name.isEmpty()) {
            classList.getItems().add(name);
            classNameField.clear();
        }
    }

    @FXML
    private void handleDelete() {
        String selected = classList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            classList.getItems().remove(selected);
        }
    }
}
