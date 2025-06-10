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
        loadReportView("TrainerReportView.fxml");
    }

    public void handleFacilityReports() {
        loadReportView("FacilityReportView.fxml");
    }

    public void handleGymReports() {
        loadReportView("GymReportView.fxml");
    }

    public void handleClassReports() {
        loadReportView("ClassReportView.fxml");
    }

    private void loadReportView(String fxmlFile) {
        try {
            Node reportView = FXMLLoader.load(getClass().getResource(fxmlFile));
            reportContentPane.getChildren().setAll(reportView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
