package org.example.controllers.admin;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.example.database.DBManager;
import org.example.database.ReportService;

import java.net.URL;
import java.sql.Connection;
import java.util.Map;
import java.util.ResourceBundle;

public class GymReportController implements Initializable {

    @FXML private BarChart<String, Number> checkInBarChart;
    @FXML private CategoryAxis checkInXAxis;
    @FXML private NumberAxis checkInYAxis;

    @FXML private TableView<Map.Entry<String, Double>> durationTable;
    @FXML private TableColumn<Map.Entry<String, Double>, String> gymNameColumn;
    @FXML private TableColumn<Map.Entry<String, Double>, Number> avgDurationColumn;

    private ReportService reportService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            DBManager dbManager = new DBManager();
            dbManager.connect();
            Connection conn = dbManager.getConnection();
            reportService = new ReportService(conn);

            // ✅ Load today's check-in stats
            Map<String, Integer> todayCheckIns = reportService.getTodayCheckInsPerGym();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Today’s Check-ins");

            for (Map.Entry<String, Integer> entry : todayCheckIns.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
            checkInBarChart.getData().add(series);

            gymNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getKey()));

            avgDurationColumn.setCellValueFactory(data ->
                    new javafx.beans.property.SimpleObjectProperty<Number>(data.getValue().getValue())
            );

            avgDurationColumn.setCellFactory(column -> new TableCell<>() {
                private final ProgressBar progressBar = new ProgressBar();
                private final Label label = new Label();
                private final HBox container = new HBox(10, progressBar, label);

                {
                    progressBar.setMaxWidth(200);
                    container.setAlignment(Pos.CENTER_LEFT);
                }

                @Override
                protected void updateItem(Number duration, boolean empty) {
                    super.updateItem(duration, empty);
                    if (empty || duration == null) {
                        setGraphic(null);
                    } else {
                        double maxDuration = 120.0; // Reference scale
                        double percent = Math.min(duration.doubleValue() / maxDuration, 1.0);
                        progressBar.setProgress(percent);
                        label.setText(String.format("%.0f min", duration.doubleValue()));
                        setGraphic(container);
                    }
                }
            });

            Map<String, Double> avgDurations = reportService.getAverageCheckInDurationPerGym();
            durationTable.getItems().setAll(avgDurations.entrySet());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
