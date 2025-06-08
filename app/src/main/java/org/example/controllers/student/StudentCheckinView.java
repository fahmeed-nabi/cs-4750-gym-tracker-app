package org.example.controllers.student;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class StudentCheckinView {

    @FXML
    private Label emailLabel;

    /**
     * Sets the student email to be displayed.
     * This is called after loading the FXML.
     */
    public void setEmail(String email) {
        if (emailLabel != null) {
            emailLabel.setText("Email: " + email);
        }
    }
}
