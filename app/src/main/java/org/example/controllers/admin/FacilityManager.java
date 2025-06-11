package org.example.controllers.admin;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.example.database.DBManager;
import org.example.database.FacilityService;
import org.example.database.GymService;
import org.example.models.Facility;
import org.example.models.Gym;

import java.sql.SQLException;

public class FacilityManager {

    @FXML private ComboBox<Gym> gymComboBox;
    @FXML private TableView<Facility> facilityTable;
    @FXML private TableColumn<Facility, String> nameColumn;
    @FXML private TableColumn<Facility, Integer> maxUsersColumn;

    @FXML private TextField nameField;
    @FXML private TextField maxUsersField;

    private DBManager dbManager;
    private FacilityService facilityService;
    private GymService gymService;
    private ObservableList<Facility> facilities = FXCollections.observableArrayList();

    private Gym selectedGym;

    public FacilityManager() throws SQLException {
        dbManager = new DBManager();
        dbManager.connect();
        this.facilityService = new FacilityService(dbManager.getConnection());
        this.gymService = new GymService(dbManager.getConnection());
    }

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getName()));
        maxUsersColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getMaxConcurrentUsers()));

        facilityTable.setItems(facilities);
        facilityTable.setOnMouseClicked(this::onRowClick);

        loadGyms();
    }

    private void loadGyms() {
        try {
            gymComboBox.setItems(FXCollections.observableArrayList(gymService.getAllGyms()));
            gymComboBox.setOnAction(e -> {
                selectedGym = gymComboBox.getValue();
                loadFacilities();
            });
        } catch (SQLException e) {
            showAlert("Error loading gyms: " + e.getMessage());
        }
    }

    private void loadFacilities() {
        try {
            if (selectedGym != null) {
                facilities.setAll(gymService.getFacilitiesByGym(selectedGym.getGymId()));
            }
        } catch (SQLException e) {
            showAlert("Error loading facilities: " + e.getMessage());
        }
    }

    @FXML
    private void onAdd() {
        if (!validateInputs()) return;
        try {
            boolean success = facilityService.createFacility(
                    nameField.getText(),
                    Integer.parseInt(maxUsersField.getText()),
                    selectedGym.getGymId()
            );
            if (success) {
                loadFacilities();
                clearForm();
            } else {
                showAlert("Failed to add facility.");
            }
        } catch (SQLException e) {
            showAlert("Error: " + e.getMessage());
        }
    }

    @FXML
    private void onUpdate() {
        Facility selected = facilityTable.getSelectionModel().getSelectedItem();
        if (selected == null || !validateInputs()) return;
        try {
            boolean success = facilityService.updateFacility(
                    selected.getFacilityId(),
                    nameField.getText(),
                    Integer.parseInt(maxUsersField.getText())
            );
            if (success) {
                loadFacilities();
                clearForm();
            } else {
                showAlert("Failed to update facility.");
            }
        } catch (SQLException e) {
            showAlert("Error: " + e.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        Facility selected = facilityTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        try {
            boolean success = facilityService.deleteFacility(selected.getFacilityId());
            if (success) {
                loadFacilities();
                clearForm();
            } else {
                showAlert("Failed to delete facility.");
            }
        } catch (SQLException e) {
            showAlert("Error: " + e.getMessage());
        }
    }

    @FXML
    private void onClear() {
        clearForm();
    }

    private void onRowClick(MouseEvent event) {
        Facility selected = facilityTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            nameField.setText(selected.getName());
            maxUsersField.setText(String.valueOf(selected.getMaxConcurrentUsers()));
        }
    }

    private boolean validateInputs() {
        if (nameField.getText().isBlank() || maxUsersField.getText().isBlank()) {
            showAlert("Name and Max Users must not be empty.");
            return false;
        }
        try {
            int value = Integer.parseInt(maxUsersField.getText());
            if (value <= 0) {
                showAlert("Max users must be greater than 0.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Max users must be a number.");
            return false;
        }
        return true;
    }

    private void clearForm() {
        nameField.clear();
        maxUsersField.clear();
        facilityTable.getSelectionModel().clearSelection();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }

    @FXML
    private void onRefresh() {
        loadFacilities();
    }
}
