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

public class TrainerReportController implements Initializable {

    @FXML private BarChart<String, Number> appointmentChart;
    @FXML private CategoryAxis appointmentXAxis;
    @FXML private NumberAxis appointmentYAxis;

    private ReportService reportService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            DBManager dbManager = new DBManager();
            dbManager.connect();
            Connection conn = dbManager.getConnection();
            reportService = new ReportService(conn);

            Map<String, Integer> appointmentData = reportService.getTrainerAppointmentsPerGym();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Trainer Appointments");

            for (Map.Entry<String, Integer> entry : appointmentData.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }

            appointmentChart.getData().add(series);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
