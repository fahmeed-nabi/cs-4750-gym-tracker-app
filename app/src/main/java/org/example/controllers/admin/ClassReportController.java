package org.example.controllers.admin;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.example.database.DBManager;
import org.example.database.ReportService;

import java.net.URL;
import java.sql.Connection;
import java.util.Map;
import java.util.ResourceBundle;

public class ClassReportController implements Initializable {

    @FXML private BarChart<String, Number> classBarChart;
    @FXML private CategoryAxis classXAxis;
    @FXML private NumberAxis classYAxis;

    private ReportService reportService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            DBManager dbManager = new DBManager();
            dbManager.connect();
            Connection conn = dbManager.getConnection();
            reportService = new ReportService(conn);

            Map<String, Integer> classCounts = reportService.getClassCountPerGym();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Classes per Gym");

            for (Map.Entry<String, Integer> entry : classCounts.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }

            classBarChart.getData().add(series);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
