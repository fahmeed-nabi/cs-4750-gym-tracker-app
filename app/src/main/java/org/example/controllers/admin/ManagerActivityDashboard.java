package org.example.controllers.admin;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import org.example.database.DBManager;
import org.example.database.GymService;
import org.example.database.ReportService;
import java.util.Map;
import javafx.scene.layout.VBox;
import java.util.LinkedHashMap;



import java.sql.SQLException;

public class ManagerActivityDashboard {
    @FXML private VBox occupancyLabelContainer;

    @FXML private TextField checkInUsernameField;
    @FXML private TextField checkOutUsernameField;
    @FXML private VBox gymFacilityPaneContainer;

    @FXML private CheckBox weightLiftingBox;
    @FXML private CheckBox cardioBox;
    @FXML private CheckBox poolBox;
    @FXML private CheckBox sportsBox;
    @FXML private CheckBox classBox;

    @FXML private Label afcLabel;
    @FXML private Label memorialLabel;
    @FXML private Label northLabel;

    private DBManager dbManager;
    private GymService gymService;
    private ReportService reportService;

    @FXML
    private void initialize() {
        try {
            dbManager = new DBManager();
            dbManager.connect();
            gymService = new GymService(dbManager.getConnection());

            updateOccupancyDisplay(); // populate on load
            reportService = new ReportService(dbManager.getConnection());
            loadGymFacilityOccupancyPanels();

        } catch (SQLException e) {
            showAlert("Database Error", "Could not connect to database.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCheckIn() {
        String username = checkInUsernameField.getText().trim();
        if (username.isEmpty()) {
            showAlert("Check-In Error", "Please enter a username.");
            return;
        }

        StringBuilder activities = new StringBuilder();
        if (weightLiftingBox.isSelected()) activities.append("Weight Lifting, ");
        if (cardioBox.isSelected()) activities.append("Cardio, ");
        if (poolBox.isSelected()) activities.append("Pool, ");
        if (sportsBox.isSelected()) activities.append("Sports, ");
        if (classBox.isSelected()) activities.append("Class, ");

        if (activities.length() == 0) {
            showAlert("Check-In Error", "Please select at least one activity.");
            return;
        }

        activities.setLength(activities.length() - 2); // Trim trailing comma
        showAlert("Check-In Success", username + " checked in for: " + activities.toString());

        // TODO: Insert check-in data into DB via activityService
    }

    @FXML
    private void handleCheckOut() {
        String username = checkOutUsernameField.getText().trim();

        if (username.isEmpty()) {
            showAlert("No Input", "Please enter a student username to check out.");
            return;
    }

        try {
            int studentId = gymService.getStudentIdByUsername(username);  

            if (studentId == -1) {
                showAlert("User Not Found", "No student found with username: " + username);
                return;
        }

        boolean checkedOut = gymService.checkOutStudent(studentId);

        if (checkedOut) {
            dbManager.commit();
            showAlert("Success", username + " has been checked out.");
            checkOutUsernameField.clear();
        } else {
            showAlert("Check-Out Failed", "Could not check out student. They may not be checked in.");
        }

    } catch (SQLException e) {
        e.printStackTrace();
        showAlert("Database Error", "An error occurred during check-out.");
        try {
            dbManager.rollback();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}


    @FXML
    private void handleRefreshOccupancy() {
        updateOccupancyDisplay();
    }
    private void updateOccupancyDisplay() {
        try {
            Map<String, Integer> rawCounts = gymService.getOccupancyForAllGyms();
            Map<String, Double> pctMap = gymService.getOccupancyPctForAllGyms();
            occupancyLabelContainer.getChildren().clear();

        if (rawCounts.isEmpty()) {
            occupancyLabelContainer.getChildren().add(new Label("No occupancy data available."));
        } else {
            for (String gym : rawCounts.keySet()) {
                int count = rawCounts.getOrDefault(gym, 0);
                double pct = pctMap.getOrDefault(gym, 0.0);
                String display = String.format("%s: %d currently checked in (%.2f%% full)", gym, count, pct);
                occupancyLabelContainer.getChildren().add(new Label(display));
            }
        }
    } catch (SQLException e) {
        occupancyLabelContainer.getChildren().clear();
        occupancyLabelContainer.getChildren().add(new Label("Error loading occupancy data."));
        e.printStackTrace();
    }
}
    private void loadGymFacilityOccupancyPanels() {
        gymFacilityPaneContainer.getChildren().clear();

    try {
        Map<String, String> flatMap = reportService.getCurrentFacilityOccupancyRate();

        // Group by gym name
        Map<String, Map<String, String>> grouped = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : flatMap.entrySet()) {
            String fullLabel = entry.getKey(); // e.g. "Weight Room (AFC)"
            String status = entry.getValue();  // e.g. "12 users (60% occupied)"

            int open = fullLabel.lastIndexOf('(');
            int close = fullLabel.lastIndexOf(')');
            if (open != -1 && close != -1 && close > open) {
                String facilityName = fullLabel.substring(0, open).trim();
                String gymName = fullLabel.substring(open + 1, close).trim();

                grouped.computeIfAbsent(gymName, k -> new LinkedHashMap<>())
                       .put(facilityName, status);
            }
        }

        // Render panels
        for (Map.Entry<String, Map<String, String>> gymEntry : grouped.entrySet()) {
            String gymName = gymEntry.getKey();
            Map<String, String> facilities = gymEntry.getValue();

            VBox gymBox = new VBox(10);
            gymBox.setPadding(new Insets(10));
            gymBox.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ccc; -fx-border-radius: 5; -fx-background-radius: 5;");

            for (Map.Entry<String, String> facilityEntry : facilities.entrySet()) {
                String facility = facilityEntry.getKey();
                String status = facilityEntry.getValue();

                double percent = 0;
                int open = status.indexOf('(');
                int pctIndex = status.indexOf('%');
                if (open != -1 && pctIndex != -1) {
                    try {
                        String number = status.substring(open + 1, pctIndex).trim();
                        percent = Double.parseDouble(number) / 100.0;
                    } catch (NumberFormatException ignored) {}
                }

                Label nameLabel = new Label(facility);
                nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2e3b4e;");

                ProgressBar progressBar = new ProgressBar(percent);
                progressBar.setPrefWidth(300);
                progressBar.setStyle("-fx-accent: #3f8efc;");

                Label statusLabel = new Label(status);
                statusLabel.setStyle("-fx-text-fill: #555555;");

                VBox entryBox = new VBox(5, nameLabel, progressBar, statusLabel);
                gymBox.getChildren().add(entryBox);
            }

            TitledPane gymPane = new TitledPane(gymName, gymBox);
            gymPane.setExpanded(false);
            gymFacilityPaneContainer.getChildren().add(gymPane);
        }

    } catch (SQLException e) {
        e.printStackTrace();
        gymFacilityPaneContainer.getChildren().add(new Label("Error loading facility usage data."));
    }
}



    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
