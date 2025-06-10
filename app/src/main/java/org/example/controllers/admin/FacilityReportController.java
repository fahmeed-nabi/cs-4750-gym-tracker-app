package org.example.controllers.admin;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.example.database.DBManager;
import org.example.database.ReportService;

import java.net.URL;
import java.sql.Connection;
import java.util.*;
import java.util.ResourceBundle;

public class FacilityReportController implements Initializable {

    @FXML private TableView<Map.Entry<Integer, Integer>> usageTable;
    @FXML private TableColumn<Map.Entry<Integer, Integer>, Integer> hourColumn;
    @FXML private TableColumn<Map.Entry<Integer, Integer>, Integer> countColumn;

    @FXML private BarChart<String, Number> facilityBarChart;
    @FXML private CategoryAxis facilityXAxis;
    @FXML private NumberAxis facilityYAxis;
    @FXML private VBox occupancyContainer;

    private ReportService reportService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            DBManager dbManager = new DBManager();
            dbManager.connect();
            Connection conn = dbManager.getConnection();
            reportService = new ReportService(conn);

            // Table (Hourly Usage)
            hourColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getKey()).asObject());
            countColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getValue()).asObject());

            Map<Integer, Integer> usageData = reportService.getPeakFacilityUsageTimes();
            usageTable.getItems().setAll(usageData.entrySet().stream().toList());

            // Bar Chart (Top 5 Facilities)
            Map<String, String> popularFacilities = reportService.getMostPopularFacilitiesWithGym(5);
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Top 5 Facilities");

            for (Map.Entry<String, String> entry : popularFacilities.entrySet()) {
                String facility = entry.getKey();
                String gym = entry.getValue();
                String label = gym + " - " + facility;
                long count = usageData.entrySet().stream()
                        .mapToLong(Map.Entry::getValue)
                        .max()
                        .orElse(0);
                series.getData().add(new XYChart.Data<>(label, count)); // Use count from chart data (simplified)
            }

            facilityBarChart.getData().add(series);

            loadFacilityOccupancyRates();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadFacilityOccupancyRates() {
        try {
            Map<String, String> occupancyMap = reportService.getCurrentFacilityOccupancyRate();
            occupancyContainer.getChildren().clear();

            for (Map.Entry<String, String> entry : occupancyMap.entrySet()) {
                String name = entry.getKey();         // e.g. "Weight Room (Memorial Gym)"
                String status = entry.getValue();     // e.g. "12 users (60% occupied)"

                // Extract % value for progress bar
                double percent = 0;
                int openParen = status.indexOf('(');
                int percentIndex = status.indexOf('%');
                if (openParen != -1 && percentIndex != -1) {
                    try {
                        String numberStr = status.substring(openParen + 1, percentIndex).trim();
                        percent = Double.parseDouble(numberStr) / 100.0;
                    } catch (NumberFormatException ignored) {}
                }

                Label nameLabel = new Label(name);
                nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2e3b4e;");

                ProgressBar bar = new ProgressBar(percent);
                bar.setPrefWidth(300);
                bar.setStyle("-fx-accent: #3f8efc;");

                Label statusLabel = new Label(status);
                statusLabel.setStyle("-fx-text-fill: #555555;");

                VBox facilityBox = new VBox(5, nameLabel, bar, statusLabel);
                facilityBox.setStyle("-fx-background-color: #ffffff; -fx-padding: 15; -fx-border-color: #dddddd; -fx-border-radius: 5; -fx-background-radius: 5;");
                occupancyContainer.getChildren().add(facilityBox);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
