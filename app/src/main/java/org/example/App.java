package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // Corrected FXML path
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login-screen.fxml"));


            Parent root = loader.load();

            Scene scene = new Scene(root, 600, 400);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm());
            stage.setTitle("UVA Gym Tracker - Login");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            System.err.println("Failed to load login screen:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}

