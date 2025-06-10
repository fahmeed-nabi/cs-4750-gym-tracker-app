package org.example.controllers.admin;

import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.database.DBManager;
import org.example.database.GymService;
import org.example.models.Gym;

import java.sql.Connection;
import java.sql.SQLException;

public class GymManager {

    @FXML private TextField gymNameField;
    @FXML private TextField capacityField;
    @FXML private TableView<Gym> gymTable;
    @FXML private TableColumn<Gym, String> nameColumn;
    @FXML private TableColumn<Gym, Integer> capacityColumn;

    private DBManager dbManager;
    private GymService gymService;
    private ObservableList<Gym> gyms = FXCollections.observableArrayList();

    public void initialize() {
        try {
            dbManager = new DBManager();
            dbManager.connect();
            Connection conn = dbManager.getConnection();
            gymService = new GymService(conn);

            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            capacityColumn.setCellValueFactory(new PropertyValueFactory<>("maxOccupancy"));

            gymTable.setItems(gyms);
            gymTable.getSelectionModel().selectedItemProperty().addListener(
                    (obs, oldSel, newSel) -> populateForm(newSel));

            loadGyms();
        } catch (SQLException e) {
            showAlert("Error loading gyms: " + e.getMessage());
        }
    }

    private void loadGyms() throws SQLException {
        gyms.setAll(gymService.getAllGyms());
    }

    private void populateForm(Gym gym) {
        if (gym != null) {
            gymNameField.setText(gym.getName());
            capacityField.setText(String.valueOf(gym.getMaxOccupancy()));
        }
    }

    @FXML
    private void handleAdd() {
        String name = gymNameField.getText().trim();
        String capacityStr = capacityField.getText().trim();
        if (name.isEmpty() || capacityStr.isEmpty()) {
            showAlert("Please enter both name and capacity.");
            return;
        }

        try {
            int capacity = Integer.parseInt(capacityStr);
            gymService.createGym(name, capacity); // Ensure this method exists
            loadGyms();
            handleClear();
        } catch (Exception e) {
            showAlert("Failed to add gym: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        Gym selected = gymTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select a gym to update.");
            return;
        }

        try {
            String name = gymNameField.getText().trim();
            int capacity = Integer.parseInt(capacityField.getText().trim());

            gymService.updateGym(selected.getGymId(), name, capacity);
            loadGyms();
            handleClear();
        } catch (Exception e) {
            showAlert("Failed to update gym: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Gym selected = gymTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select a gym to delete.");
            return;
        }

        try {
            gymService.deleteGym(selected.getGymId());
            loadGyms();
            handleClear();
        } catch (Exception e) {
            showAlert("Failed to delete gym: " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        gymNameField.clear();
        capacityField.clear();
        gymTable.getSelectionModel().clearSelection();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
