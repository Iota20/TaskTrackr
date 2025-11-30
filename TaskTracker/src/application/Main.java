package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class Main extends Application {

    private TaskManager manager = new TaskManager();
    private ListView<String> taskListView = new ListView<>();

    private ComboBox<String> filterTypeBox;
    private ComboBox<String> filterStatusBox;
    private ComboBox<String> sortBox;

    @Override
    public void start(Stage primaryStage) {
        // Load existing tasks
        manager.loadTasks();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // --- TOP: Filters & Sorting ---
        HBox top = new HBox(10);
        top.setPadding(new Insets(6));

        filterTypeBox = new ComboBox<>();
        filterTypeBox.getItems().addAll("All", "Work", "Personal");
        filterTypeBox.setValue("All");

        filterStatusBox = new ComboBox<>();
        filterStatusBox.getItems().addAll("All",
                "PENDING","IN_PROGRESS","ON_HOLD","CANCELLED","COMPLETED");
        filterStatusBox.setValue("All");

        sortBox = new ComboBox<>();
        sortBox.getItems().addAll("None", "Due Date", "Status", "Type");
        sortBox.setValue("None");

        filterTypeBox.setOnAction(e -> refreshList());
        filterStatusBox.setOnAction(e -> refreshList());
        sortBox.setOnAction(e -> refreshList());

        top.getChildren().addAll(new Label("Type:"), filterTypeBox,
                new Label("Status:"), filterStatusBox,
                new Label("Sort:"), sortBox);
        root.setTop(top);

        // --- CENTER: Task list (big) ---
        taskListView.setPrefWidth(700);
        taskListView.setPrefHeight(500);
        taskListView.setFocusTraversable(true);
        root.setCenter(taskListView);

        // --- LEFT: optional compact control panel (kept small) ---
        VBox left = new VBox(10);
        left.setPadding(new Insets(6));

        // small preview or extra controls could go here; keep minimal so center is large
        left.getChildren().add(new Label("Controls:"));
        root.setLeft(left);

        // --- BOTTOM: buttons (Add / Edit / Delete / Save / Load) ---
        HBox bottom = new HBox(10);
        bottom.setPadding(new Insets(8));

        Button addBtn = new Button("Add Task");
        Button editBtn = new Button("Edit Task");
        Button deleteBtn = new Button("Delete Task");
        Button saveBtn = new Button("Save");
        Button loadBtn = new Button("Load");

        addBtn.setOnAction(e -> showAddDialog());
        editBtn.setOnAction(e -> showEditDialog());
        deleteBtn.setOnAction(e -> {
            int sel = taskListView.getSelectionModel().getSelectedIndex();
            if (sel >= 0) {
                // map selected index in ListView back to item in filtered list
                List<Task> filtered = manager.getFilteredAndSortedTasks(
                        filterTypeBox.getValue(),
                        filterStatusBox.getValue().equals("All") ? null : filterStatusBox.getValue(),
                        sortBox.getValue()
                );
                if (sel < filtered.size()) {
                    Task toRemove = filtered.get(sel);
                    // remove from master list by identity
                    manager.getTasks().remove(toRemove);
                    manager.saveTasks();
                    refreshList();
                }
            }
        });

        saveBtn.setOnAction(e -> manager.saveTasks());
        loadBtn.setOnAction(e -> {
            manager.loadTasks();
            refreshList();
        });

        bottom.getChildren().addAll(addBtn, editBtn, deleteBtn, saveBtn, loadBtn);
        root.setBottom(bottom);

        // initial populate
        refreshList();

        Scene scene = new Scene(root, 900, 650);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Task Tracker");
        primaryStage.show();
    }

    private void refreshList() {
        taskListView.getItems().clear();
        String typeFilter = filterTypeBox == null ? "All" : filterTypeBox.getValue();
        String statusFilter = filterStatusBox == null ? null : (filterStatusBox.getValue().equals("All") ? null : filterStatusBox.getValue());
        String sortOpt = sortBox == null ? "None" : sortBox.getValue();

        List<Task> filtered = manager.getFilteredAndSortedTasks(typeFilter, statusFilter, sortOpt);
        for (Task t : filtered) {
            taskListView.getItems().add(t.displayDetails());
        }
    }

    // --- ADD DIALOG ---
    private void showAddDialog() {
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Add Task");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Work", "Personal");
        typeBox.setValue("Work");

        TextField titleField = new TextField();
        TextArea descField = new TextArea();
        DatePicker duePicker = new DatePicker(LocalDate.now());

        ComboBox<TaskStatus> statusBox = new ComboBox<>();
        statusBox.getItems().addAll(TaskStatus.values());
        statusBox.setValue(TaskStatus.PENDING);

        TextField extraField = new TextField();
        extraField.setPromptText("Project / Category");

        typeBox.setOnAction(e -> extraField.setPromptText(typeBox.getValue().equals("Work") ? "Project" : "Category"));

        GridPane grid = new GridPane();
        grid.setVgap(8);
        grid.setHgap(10);
        grid.setPadding(new Insets(10));

        grid.add(new Label("Type:"), 0, 0);
        grid.add(typeBox, 1, 0);
        grid.add(new Label("Title:"), 0, 1);
        grid.add(titleField, 1, 1);
        grid.add(new Label("Description:"), 0, 2);
        grid.add(descField, 1, 2);
        grid.add(new Label("Due Date:"), 0, 3);
        grid.add(duePicker, 1, 3);
        grid.add(new Label("Status:"), 0, 4);
        grid.add(statusBox, 1, 4);
        grid.add(new Label("Project/Category:"), 0, 5);
        grid.add(extraField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                String title = titleField.getText().trim();
                String desc = descField.getText().trim();
                LocalDate due = duePicker.getValue();
                TaskStatus st = statusBox.getValue();
                String extra = extraField.getText().trim();

                if (title.isEmpty() || due == null) return null;

                if ("Work".equals(typeBox.getValue())) {
                    WorkTask wt = new WorkTask(title, desc, due, st, extra);
                    return wt;
                } else {
                    PersonalTask pt = new PersonalTask(title, desc, due, st, extra);
                    return pt;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(t -> {
            manager.addTask(t);
            manager.saveTasks();
            refreshList();
        });
    }

    // --- EDIT DIALOG ---
    private void showEditDialog() {
        int selIndex = taskListView.getSelectionModel().getSelectedIndex();
        if (selIndex < 0) return;

        List<Task> filtered = manager.getFilteredAndSortedTasks(
                filterTypeBox.getValue(),
                filterStatusBox.getValue().equals("All") ? null : filterStatusBox.getValue(),
                sortBox.getValue()
        );

        if (selIndex >= filtered.size()) return;

        Task original = filtered.get(selIndex);
        // find original index in master list
        int originalIndex = manager.getTasks().indexOf(original);
        if (originalIndex < 0) return;

        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Edit Task");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Work", "Personal");
        typeBox.setValue(original instanceof WorkTask ? "Work" : "Personal");

        TextField titleField = new TextField(original.getTitle());
        TextArea descField = new TextArea(original.getDescription());
        DatePicker duePicker = new DatePicker(original.getDueDate());

        ComboBox<TaskStatus> statusBox = new ComboBox<>();
        statusBox.getItems().addAll(TaskStatus.values());
        statusBox.setValue(original.getStatus());

        TextField extraField = new TextField();
        if (original instanceof WorkTask) extraField.setText(((WorkTask) original).getProject());
        if (original instanceof PersonalTask) extraField.setText(((PersonalTask) original).getCategory());

        typeBox.setOnAction(e -> extraField.setPromptText(typeBox.getValue().equals("Work") ? "Project" : "Category"));

        GridPane grid = new GridPane();
        grid.setVgap(8);
        grid.setHgap(10);
        grid.setPadding(new Insets(10));

        grid.add(new Label("Type:"), 0, 0);
        grid.add(typeBox, 1, 0);
        grid.add(new Label("Title:"), 0, 1);
        grid.add(titleField, 1, 1);
        grid.add(new Label("Description:"), 0, 2);
        grid.add(descField, 1, 2);
        grid.add(new Label("Due Date:"), 0, 3);
        grid.add(duePicker, 1, 3);
        grid.add(new Label("Status:"), 0, 4);
        grid.add(statusBox, 1, 4);
        grid.add(new Label("Project/Category:"), 0, 5);
        grid.add(extraField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                String title = titleField.getText().trim();
                String desc = descField.getText().trim();
                LocalDate due = duePicker.getValue();
                TaskStatus st = statusBox.getValue();
                String extra = extraField.getText().trim();

                if (title.isEmpty() || due == null) return null;

                if ("Work".equals(typeBox.getValue())) {
                    return new WorkTask(title, desc, due, st, extra);
                } else {
                    return new PersonalTask(title, desc, due, st, extra);
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updated -> {
            manager.updateTask(originalIndex, updated);
            manager.saveTasks();
            refreshList();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
