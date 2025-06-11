package org.example.controllers.trainer;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.example.database.DBManager;
import org.example.database.TrainerService;
import org.example.models.TrainerAvailability;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class UpdateAvailabilityController {

    @FXML private GridPane calendarGrid;

    private DBManager dbManager;
    private int trainerId;
    private TrainerService trainerService;

    private final String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    private final int startHour = 6;  // 6 AM
    private final int endHour = 22;  // 10 PM

    // Store availability blocks as Map<DayTimeString, Rectangle>
    private final Map<String, Rectangle> selectedBlocks = new HashMap<>();

    public void setTrainerId(int id) {
        this.trainerId = id;
        loadExistingAvailability();
    }

    @FXML
    public void initialize() {
        try {
            dbManager = new DBManager();
            dbManager.connect();
            trainerService = new TrainerService(dbManager.getConnection());
        } catch (SQLException e) {
            showError("Database connection error");
            return;
        }

        buildGrid();
    }

    private void buildGrid() {
        // Column headers
        for (int d = 0; d < days.length; d++) {
            Label label = new Label(days[d]);
            calendarGrid.add(label, d + 1, 0);
        }

        // Row headers and blocks
        for (int hour = startHour; hour < endHour; hour++) {
            Label timeLabel = new Label(String.format("%02d:00", hour));
            calendarGrid.add(timeLabel, 0, hour - startHour + 1);

            for (int day = 0; day < days.length; day++) {
                String key = days[day] + "_" + hour;
                Rectangle block = new Rectangle(70, 25);
                block.setFill(Color.LIGHTGRAY);
                block.setOnMouseClicked(e -> toggleBlock(key, block));
                calendarGrid.add(block, day + 1, hour - startHour + 1);
                selectedBlocks.put(key, block);
            }
        }
    }

    private void toggleBlock(String key, Rectangle block) {
        if (block.getFill().equals(Color.LIGHTGREEN)) {
            block.setFill(Color.LIGHTGRAY);
        } else {
            block.setFill(Color.LIGHTGREEN);
        }
    }

    private void loadExistingAvailability() {
        try {
            List<TrainerAvailability> list = trainerService.getAvailabilityByTrainer(trainerId);
            for (TrainerAvailability a : list) {
                int start = Integer.parseInt(a.getStartTime().substring(0, 2));
                int end = Integer.parseInt(a.getEndTime().substring(0, 2));
                for (int h = start; h < end; h++) {
                    String key = a.getDayOfWeek() + "_" + h;
                    Rectangle r = selectedBlocks.get(key);
                    if (r != null) r.setFill(Color.LIGHTGREEN);
                }
            }
        } catch (Exception e) {
            showError("Failed to load existing availability");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSave() {
        try {
            trainerService.clearAvailabilityForTrainer(trainerId);
            for (String key : selectedBlocks.keySet()) {
                Rectangle r = selectedBlocks.get(key);
                if (r.getFill().equals(Color.LIGHTGREEN)) {
                    String[] parts = key.split("_");
                    String day = parts[0];
                    int hour = Integer.parseInt(parts[1]);
                    trainerService.addAvailability(trainerId, day, hour + ":00", (hour + 1) + ":00");
                }
            }
            showInfo("Availability saved.");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to save availability");
        }
    }

    @FXML
    private void handleClose() {
        try {
            dbManager.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ((Stage) calendarGrid.getScene().getWindow()).close();
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }

    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }

    public void setStage(Stage stage) {
        stage.setOnCloseRequest(e -> {
            try {
                if (dbManager != null) dbManager.disconnect();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }
}
