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
        Label titleLbl = new Label("Task Title:");
        TextField titleField = new TextField();
        Label descLbl = new Label("Description:");
        TextField descField = new TextField();

        ChoiceBox<String> typeBox = new ChoiceBox<>();
        typeBox.getItems().addAll("Work", "Personal");
        typeBox.setValue("Work");

        Button addBtn = new Button("Add Task");
        addBtn.setOnAction(e -> {
            String title = titleField.getText();
            String desc = descField.getText();
            String type = typeBox.getValue();

            Task newTask;
            if (type.equals("Work"))
                newTask = new WorkTask(title, desc, "Project X");
            else
                newTask = new PersonalTask(title, desc, "General");

            manager.addTask(newTask);
            taskList.getItems().add(newTask.displayDetails());

            titleField.clear();
            descField.clear();
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 15;");
        layout.getChildren().addAll(titleLbl, titleField, descLbl, descField, typeBox, addBtn, taskList);

        Scene scene = new Scene(layout, 350, 400);
        stage.setScene(scene);
        stage.setTitle("OOP Task Manager");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
