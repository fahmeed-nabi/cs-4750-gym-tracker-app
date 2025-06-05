@FXML
private void handleLogin() {
    String username = usernameField.getText().trim();
    String password = passwordField.getText();
    String role = roleBox.getValue();

    if (username.isEmpty() || password.isEmpty() || role == null) {
        errorLabel.setText("All fields are required.");
        return;
    }

    // For now: allow anyone through, role determines view
    switch (role) {
        case "Student":
            navigateTo("student-dashboard.fxml", "Student Dashboard");
            break;
        case "Employee":
            navigateTo("employee-dashboard.fxml", "Employee Dashboard");
            break;
        case "Manager":
            navigateTo("manager-dashboard.fxml", "Manager Dashboard");
            break;
        default:
            errorLabel.setText("Unrecognized role.");
    }
}
private void navigateTo(String fxmlPath, String title) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/" + fxmlPath));
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    } catch (Exception e) {
        errorLabel.setText("Failed to load " + title);
        e.printStackTrace();
    }
}

