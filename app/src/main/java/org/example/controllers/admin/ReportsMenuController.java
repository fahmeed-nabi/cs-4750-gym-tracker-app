package org.example.controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class ReportsMenuController {

    @FXML
    private StackPane reportContentPane;

    public void handleTrainerReports() {
        loadReportView("trainer-report.fxml");
    }

    public void handleFacilityReports() {
        loadReportView("facility-report.fxml");
    }

    public void handleGymReports() {
        loadReportView("gym-report.fxml");
    }

    public void handleClassReports() {
        loadReportView("class-report.fxml");
    }

    private void loadReportView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/" + fxmlFile));
            Node view = loader.load();
            reportContentPane.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
