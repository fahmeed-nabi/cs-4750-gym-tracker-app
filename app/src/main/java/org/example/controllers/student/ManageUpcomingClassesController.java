package org.example.controllers.student;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.example.database.ClassService;
import org.example.database.UserService;
import org.example.models.ClassOverview;
import org.example.models.ClassSession;
import org.example.models.Instructor;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ManageUpcomingClassesController {

    @FXML private TableView<ClassOverview> classTable;
    @FXML private TableColumn<ClassOverview, String> nameColumn;
    @FXML private TableColumn<ClassOverview, String> timeColumn;
    @FXML private TableColumn<ClassOverview, String> gymColumn;
    @FXML private TableColumn<ClassOverview, String> instructorColumn;
    @FXML private TableColumn<ClassOverview, String> emailColumn;
    @FXML private TableColumn<ClassOverview, Void> actionColumn;
    @FXML private TableColumn<ClassOverview, String> spotsColumn;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, HH:mm");

    private ClassService classService;
    private UserService userService;
    private int currentStudentId;
    private String studentEmail;

    public void setDependencies(ClassService classService, UserService userService, int studentId, String studentEmail) {
        this.classService = classService;
        this.userService = userService;
        this.currentStudentId = studentId;
        this.studentEmail = studentEmail;
        loadData();
    }

    private void loadData() {
        setupColumns();
        addRegisterButtons();
        try {
            List<ClassOverview> upcomingClasses = classService.getUpcomingClassOverviews();
            classTable.getItems().setAll(upcomingClasses);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load classes.");
            e.printStackTrace();
        }
    }

    private void setupColumns() {
        nameColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getClassSession().getName()));

        timeColumn.setCellValueFactory(data -> {
            ClassSession s = data.getValue().getClassSession();
            String time = s.getStartTime().format(dtf) + " - " + s.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            return new ReadOnlyStringWrapper(time);
        });

        gymColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getGymName()));

        instructorColumn.setCellValueFactory(data -> {
            Instructor i = data.getValue().getInstructor();
            return new ReadOnlyStringWrapper(i.getFirstName() + " " + i.getLastName());
        });

        emailColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getInstructor().getEmail()));

        spotsColumn.setCellValueFactory(data -> {
            int remaining = data.getValue().getClassSession().getAvailableSpots();
            return new ReadOnlyStringWrapper(String.valueOf(remaining));
        });
    }

    private void addRegisterButtons() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Register");

            {
                btn.setOnAction(event -> {
                    ClassOverview overview = getTableView().getItems().get(getIndex());
                    int classId = overview.getClassSession().getClassId();
                    int spots = overview.getClassSession().getAvailableSpots();
                    if (spots <= 0) {
                        showAlert(Alert.AlertType.WARNING, "Class Full", "This class is full and cannot accept more students.");
                        return;
                    }

                    try {
                        boolean success = classService.registerStudentForClass(currentStudentId, classId);
                        if (success) {
                            showAlert(Alert.AlertType.INFORMATION, "Success", "Registered successfully!");
                            overview.getClassSession().setAvailableSpots(spots - 1); // update in UI
                            classTable.refresh();
                        } else {
                            showAlert(Alert.AlertType.WARNING, "Not Eligible", "Already registered, time conflict, or class expired.");
                        }
                    } catch (SQLException e) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Registration failed.");
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

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student/manage-classes.fxml"));
            Parent root = loader.load();

            ManageClassesMenuController controller = loader.getController();
            controller.setStudentEmail(studentEmail);

            Stage stage = (Stage) classTable.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 600));
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

}
