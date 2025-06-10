package org.example.controllers.student;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.database.ClassService;
import org.example.database.UserService;
import org.example.models.ClassOverview;
import org.example.models.ClassSession;
import org.example.models.Instructor;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class RegisteredClassesController {

    @FXML private TableView<ClassOverview> classTable;
    @FXML private TableColumn<ClassOverview, String> nameColumn;
    @FXML private TableColumn<ClassOverview, String> timeColumn;
    @FXML private TableColumn<ClassOverview, String> gymColumn;
    @FXML private TableColumn<ClassOverview, String> instructorColumn;
    @FXML private TableColumn<ClassOverview, String> emailColumn;
    @FXML private TableColumn<ClassOverview, Void> actionColumn;

    private ClassService classService;
    private UserService userService;
    private int studentId;
    private String studentEmail;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, HH:mm");

    public void setDependencies(ClassService classService, UserService userService, int studentId, String studentEmail) {
        this.classService = classService;
        this.userService = userService;
        this.studentId = studentId;
        this.studentEmail = studentEmail;
        loadData();
    }

    private void loadData() {
        setupColumns();
        addUnregisterButtons();
        try {
            List<ClassOverview> registered = classService.getRegisteredClassOverviews(studentId);
            classTable.getItems().setAll(registered);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load registered classes.");
            e.printStackTrace();
        }
    }

    private void setupColumns() {
        nameColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getClassSession().getName()));
        timeColumn.setCellValueFactory(data -> {
            ClassSession s = data.getValue().getClassSession();
            return new ReadOnlyStringWrapper(s.getStartTime().format(dtf) + " - " + s.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        });
        gymColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getGymName()));
        instructorColumn.setCellValueFactory(data -> {
            Instructor i = data.getValue().getInstructor();
            return new ReadOnlyStringWrapper(i.getFirstName() + " " + i.getLastName());
        });
        emailColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getInstructor().getEmail()));
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student/manage-classes.fxml"));
            Parent root = loader.load();

            ManageClassesMenuController controller = loader.getController();
            controller.setStudentEmail(studentEmail);

            Stage stage = (Stage) classTable.getScene().getWindow();
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Manage Classes");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void addUnregisterButtons() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Unregister");

            {
                btn.setOnAction(event -> {
                    ClassOverview overview = getTableView().getItems().get(getIndex());
                    int classId = overview.getClassSession().getClassId();
                    try {
                        boolean success = classService.unregisterStudentFromClass(studentId, classId);
                        if (success) {
                            showAlert(Alert.AlertType.INFORMATION, "Unregistered", "You have been unregistered.");
                            classTable.getItems().remove(overview);
                        } else {
                            showAlert(Alert.AlertType.WARNING, "Failed", "Unregistration failed.");
                        }
                    } catch (SQLException e) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Database error occurred.");
                        e.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

}
