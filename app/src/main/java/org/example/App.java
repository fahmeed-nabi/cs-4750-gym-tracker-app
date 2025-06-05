package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // Load the login screen FXML from the resources/ui folder
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/login-screen.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 600, 400);
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
        launch(); // Launch the JavaFX application
    }
}

