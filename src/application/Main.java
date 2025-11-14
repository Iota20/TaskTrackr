package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Main extends Application {
    private TaskManager manager = new TaskManager();
    private ListView<String> taskList = new ListView<>();

    @Override
    public void start(Stage stage) {
        // Load existing tasks on startup
        manager.loadTasks();
        refreshList();

        Label titleLbl = new Label("Task Title:");
        TextField titleField = new TextField();
        Label descLbl = new Label("Description:");
        TextField descField = new TextField();

        ChoiceBox<String> typeBox = new ChoiceBox<>();
        typeBox.getItems().addAll("Work", "Personal");
        typeBox.setValue("Work");

        Button addBtn = new Button("Add Task");
        Button saveBtn = new Button("Save Tasks");
        Button loadBtn = new Button("Reload Tasks");

        // Add new task
        addBtn.setOnAction(e -> {
            String title = titleField.getText().trim();
            String desc = descField.getText().trim();
            String type = typeBox.getValue();

            if (title.isEmpty()) return;

            Task newTask = type.equals("Work") ?
                new WorkTask(title, desc, "Project X") :
                new PersonalTask(title, desc, "General");

            manager.addTask(newTask);
            refreshList();

            titleField.clear();
            descField.clear();
        });

        // Save and load actions
        saveBtn.setOnAction(e -> manager.saveTasks());
        loadBtn.setOnAction(e -> {
            manager.getTasks().clear();
            manager.loadTasks();
            refreshList();
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 15;");
        layout.getChildren().addAll(titleLbl, titleField, descLbl, descField, typeBox,
                new HBox(10, addBtn, saveBtn, loadBtn), taskList);

        Scene scene = new Scene(layout, 400, 450);
        stage.setScene(scene);
        stage.setTitle("Task Manager OOP + Save/Load");
        stage.show();
    }

    private void refreshList() {
        taskList.getItems().clear();
        for (Task t : manager.getTasks()) {
            taskList.getItems().add(t.displayDetails());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
