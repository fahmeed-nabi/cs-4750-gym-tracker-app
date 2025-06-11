package org.example.controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.database.*;
import org.example.models.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ClassManagerController {

    @FXML private TextField classNameField;
    @FXML private DatePicker startDatePicker, endDatePicker;
    @FXML private TextField startTimeField, endTimeField;
    @FXML private ComboBox<Gym> gymComboBox;
    @FXML private ComboBox<Instructor> instructorComboBox;
    @FXML private TextField availableSpotsField;
    @FXML private ListView<ClassOverview> classListView;

    private DBManager dbManager;
    private ClassService classService;
    private GymService gymService;
    private InstructorService instructorService;
    private ClassOverview selectedOverview;

    @FXML
    public void initialize() {
        try {
            dbManager = new DBManager();
            dbManager.connect();
            Connection conn = dbManager.getConnection();
            classService = new ClassService(conn);
            gymService = new GymService(conn);
            instructorService = new InstructorService(conn);

            loadGyms();
            loadInstructors();
            loadUpcomingClasses();

            classListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) populateForm(newVal);
            });
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Failed to connect or load data.");
        }
    }

    private void loadGyms() throws SQLException {
        gymComboBox.getItems().setAll(gymService.getAllGyms());
    }

    private void loadInstructors() throws SQLException {
        instructorComboBox.getItems().setAll(instructorService.getAllInstructors());
    }

    private void loadUpcomingClasses() throws SQLException {
        classListView.getItems().setAll(classService.getUpcomingClassOverviews());
    }

    private void populateForm(ClassOverview overview) {
        selectedOverview = overview;
        ClassSession cs = overview.getClassSession();

        classNameField.setText(cs.getName());
        startDatePicker.setValue(cs.getStartTime().toLocalDate());
        endDatePicker.setValue(cs.getEndTime().toLocalDate());
        startTimeField.setText(cs.getStartTime().toLocalTime().toString());
        endTimeField.setText(cs.getEndTime().toLocalTime().toString());
        availableSpotsField.setText(String.valueOf(cs.getAvailableSpots()));

        gymComboBox.getSelectionModel().select(gymComboBox.getItems().stream()
                .filter(g -> g.getGymId() == cs.getGymId()).findFirst().orElse(null));
        instructorComboBox.getSelectionModel().select(instructorComboBox.getItems().stream()
                .filter(i -> i.getInstructorId() == cs.getInstructorId()).findFirst().orElse(null));
    }

    @FXML
    private void handleCreateClass() {
        try {
            String name = classNameField.getText();
            LocalDateTime start = LocalDateTime.of(startDatePicker.getValue(), LocalTime.parse(startTimeField.getText()));
            LocalDateTime end = LocalDateTime.of(endDatePicker.getValue(), LocalTime.parse(endTimeField.getText()));
            Gym gym = gymComboBox.getValue();
            Instructor instructor = instructorComboBox.getValue();
            int availableSpots = Integer.parseInt(availableSpotsField.getText());

            boolean success = classService.addClass(name, start, end, gym.getGymId(), instructor.getInstructorId(), availableSpots);
            if (success) {
                showAlert("Class created.");
                loadUpcomingClasses();
                handleClearForm();
            } else {
                showAlert("Failed to create class.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Invalid input or error occurred.");
        }
    }

    @FXML
    private void handleUpdateClass() {
        if (selectedOverview == null) {
            showAlert("Select a class to update.");
            return;
        }

        try {
            String name = classNameField.getText();
            LocalDateTime start = LocalDateTime.of(startDatePicker.getValue(), LocalTime.parse(startTimeField.getText()));
            LocalDateTime end = LocalDateTime.of(endDatePicker.getValue(), LocalTime.parse(endTimeField.getText()));
            Gym gym = gymComboBox.getValue();
            Instructor instructor = instructorComboBox.getValue();

            boolean success = classService.updateClass(
                    selectedOverview.getClassSession().getClassId(),
                    name, start, end, gym.getGymId(), instructor.getInstructorId());

            if (success) {
                showAlert("Class updated.");
                loadUpcomingClasses();
                handleClearForm();
            } else {
                showAlert("Failed to update class.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Invalid input or error occurred.");
        }
    }

    @FXML
    private void handleDeleteClass() {
        if (selectedOverview == null) {
            showAlert("Select a class to delete.");
            return;
        }

        try {
            boolean success = classService.deleteClass(selectedOverview.getClassSession().getClassId());
            if (success) {
                showAlert("Class deleted.");
                loadUpcomingClasses();
                handleClearForm();
            } else {
                showAlert("Failed to delete class.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error occurred while deleting.");
        }
    }

    @FXML
    private void handleClearForm() {
        selectedOverview = null;
        classNameField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        startTimeField.clear();
        endTimeField.clear();
        availableSpotsField.clear();
        gymComboBox.getSelectionModel().clearSelection();
        instructorComboBox.getSelectionModel().clearSelection();
        classListView.getSelectionModel().clearSelection();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
