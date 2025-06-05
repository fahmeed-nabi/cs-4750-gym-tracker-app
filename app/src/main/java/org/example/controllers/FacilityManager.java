package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class FacilityManager {

    @FXML private TextField facilityNameField;
    @FXML private ListView<String> facilityList;

    @FXML
    private void handleAdd() {
        String name = facilityNameField.getText().trim();
        if (!name.isEmpty()) {
            facilityList.getItems().add(name);
            facilityNameField.clear();
        }
    }

    @FXML
    private void handleDelete() {
        String selected = facilityList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            facilityList.getItems().remove(selected);
        }
    }
}
