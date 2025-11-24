package application;

import java.time.LocalDate;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Main extends Application {
    private TaskManager manager = new TaskManager();
    private ListView<String> taskList = new ListView<>();
    private ChoiceBox<String> filterBox = new ChoiceBox<>();
    

    @Override
    public void start(Stage stage) {
        // Load existing tasks on startup
        manager.loadTasks();
        refreshList();

        Label titleLbl = new Label("Task Title:");
        TextField titleField = new TextField();
        Label descLbl = new Label("Description:");
        TextField descField = new TextField();
        Label dueLbl = new Label("Due Date:");
        DatePicker dueDatePicker = new DatePicker();

        ChoiceBox<String> typeBox = new ChoiceBox<>();
        typeBox.getItems().addAll("Work", "Personal");
        typeBox.setValue("Work");

        Button addBtn = new Button("Add Task");
        Button saveBtn = new Button("Save Tasks");
        Button loadBtn = new Button("Reload Tasks");
        Button completeBtn = new Button("Mark as Complete");
        Button deleteBtn = new Button("Delete Task");
        Button editBtn = new Button("Edit Task");

        filterBox.getItems().addAll("All", "Completed", "Pending");
        filterBox.setValue("All");
        filterBox.setOnAction(e -> refreshList());

        // Add new task
        addBtn.setOnAction(e -> {
            String title = titleField.getText().trim();
            String desc = descField.getText().trim();
            LocalDate due = dueDatePicker.getValue();
            String type = typeBox.getValue();

            if (title.isEmpty() || due == null) return;

            Task newTask = type.equals("Work") ?
                new WorkTask(title, desc, due, "Project X") :
                new PersonalTask(title, desc, due, "General");

            manager.addTask(newTask);
            refreshList();

            titleField.clear();
            descField.clear();
            dueDatePicker.setValue(null);
        });

        saveBtn.setOnAction(e -> manager.saveTasks());

        loadBtn.setOnAction(e -> {
            manager.getTasks().clear();
            manager.loadTasks();
            refreshList();
        });

        deleteBtn.setOnAction(e -> {
            int index = taskList.getSelectionModel().getSelectedIndex();
            if (index >= 0) {
                manager.getTasks().remove(index);
                refreshList();
            }
        });

        completeBtn.setOnAction(e -> {
            int index = taskList.getSelectionModel().getSelectedIndex();

            if (index >= 0) {
                manager.markTaskCompleted(index);
                refreshList();     // refresh UI
            }
        });

        editBtn.setOnAction(e -> {
            int index = taskList.getSelectionModel().getSelectedIndex();
            if (index >= 0) {
                Task task = manager.getTasks().get(index);
                showEditWindow(task);
            }
        });
        
        HBox buttons = new HBox(10, addBtn, editBtn, completeBtn, deleteBtn, saveBtn, loadBtn);
        HBox filterRow = new HBox(10, new Label("Filter:"), filterBox);

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 15;");
        layout.getChildren().addAll(titleLbl, titleField, descLbl, descField, dueLbl, typeBox,
                buttons, filterRow, taskList);

        Scene scene = new Scene(layout, 400, 450);
        stage.setScene(scene);
        stage.setTitle("Task Manager OOP");
        stage.show();
    }

    private void refreshList() {
        taskList.getItems().clear();
        String filter = filterBox.getValue();
        if (filter == null) filter = "All";

        for (Task t : manager.getTasks()) {
            boolean show = false;

            switch (filter) {
                case "All":
                    show = true;
                    break;

                case "Completed":
                    show = t.isCompleted();
                    break;

                case "Pending":
                    show = !t.isCompleted();
                    break;
            }

            if (show)
                taskList.getItems().add(t.displayDetails());
        }
    }

    private void showEditWindow(Task task) {
        Stage editStage = new Stage();
        editStage.setTitle("Edit Task");

        TextField titleField = new TextField(task.title);
        TextField descField = new TextField(task.description);
        DatePicker datePicker = new DatePicker(task.getDueDate());

        Button saveBtn = new Button("Save");

        saveBtn.setOnAction(e -> {
            task.setTitle(titleField.getText());
            task.setDescription(descField.getText());
            task.setDueDate(datePicker.getValue());

            refreshList();
            editStage.close();
        });

        VBox layout = new VBox(10, new Label("Title"), titleField,
                new Label("Description"), descField,
                new Label("Due Date"), datePicker,
                saveBtn);

        layout.setStyle("-fx-padding: 15;");

        editStage.setScene(new Scene(layout, 300, 250));
        editStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
