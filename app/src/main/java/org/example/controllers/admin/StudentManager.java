package org.example.controllers.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.database.DBManager;
import org.example.database.UserService;
import org.example.models.Student;
import javafx.collections.transformation.FilteredList;

import java.sql.Connection;
import java.sql.SQLException;

public class StudentManager {
    @FXML private TextField searchField;

    private FilteredList<Student> filteredList;

    @FXML private TextField studentNameField;
    @FXML private TextField studentIdField;
    @FXML private TextField usernameField;

    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TableColumn<Student, String> idColumn;
    @FXML private TableColumn<Student, String> usernameColumn;

    private final ObservableList<Student> studentList = FXCollections.observableArrayList();
    private UserService userService;
    private DBManager dbManager;

    @FXML
   public void initialize() {
        try {
            dbManager = new DBManager();
            dbManager.connect();
            userService = new UserService(dbManager.getConnection());

            // Load students from DB
            studentList.clear();
            studentList.addAll(userService.getAllStudents());
    } catch (SQLException e) {
        e.printStackTrace();
        
    }
        // Configure table columns
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        idColumn.setCellValueFactory(cellData -> cellData.getValue().studentIdProperty());
        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());

        // Initialize filtered list
        filteredList = new FilteredList<>(studentList, p -> true);
        studentTable.setItems(filteredList);

        // Set up search listener
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String lower = newValue.toLowerCase().trim();
            filteredList.setPredicate(student -> {
                if (lower.isEmpty()) return true;
                return student.getName().toLowerCase().contains(lower) ||
                    student.getUsername().toLowerCase().contains(lower) ||
                    String.valueOf(student.getStudentId()).contains(lower);
        });
    });
}


    @FXML
    private void handleAdd() {
        String name = studentNameField.getText().trim();
        String idText = studentIdField.getText().trim();
        String email = usernameField.getText().trim();  // email instead of "username"

        if (!name.isEmpty() && !idText.isEmpty() && !email.isEmpty()) {
            try {
                int id = Integer.parseInt(idText);

                String[] parts = name.split(" ", 2);
                String firstName = parts[0];
                String lastName = parts.length > 1 ? parts[1] : "";

                boolean created = userService.createStudent(firstName, lastName, email, "changeme123");

                if (created) {
                    studentList.add(new Student(id, name, email, "Student")); // "Student" is default role
                } else {
                    System.err.println("Failed to insert student into database.");
                }

                studentNameField.clear();
                studentIdField.clear();
                usernameField.clear();

            } catch (NumberFormatException e) {
                System.err.println("Student ID must be an integer.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
private void handleDelete() {
    final boolean USE_EMAIL_TO_DELETE = false;  //  Change this to true to delete by email

    Student selected = studentTable.getSelectionModel().getSelectedItem();
    if (selected != null) {
        try {
            boolean deleted;

            if (USE_EMAIL_TO_DELETE) {
                deleted = userService.deleteStudentByEmail(selected.getUsername());
            } else {
               deleted = userService.deleteStudentById(selected.getStudentId());
            }

            if (deleted) {
                studentList.remove(selected);
            } else {
                System.err.println("Failed to delete student from database.");
            }

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
}

}
