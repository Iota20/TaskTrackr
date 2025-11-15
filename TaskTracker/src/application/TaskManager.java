package application;

import java.io.*;
import java.util.ArrayList;

public class TaskManager {
    private ArrayList<Task> tasks = new ArrayList<>();
    private final String FILE_PATH = "tasks.txt";

    public void addTask(Task task) {
        tasks.add(task);
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    // 💾 Save tasks to file
    public void saveTasks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Task t : tasks) {
                String type = (t instanceof WorkTask) ? "Work" : "Personal";
                writer.write(type + "|" + t.getTitle() + "|" + t.getDescription() + "|" + t.isCompleted());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
    }

    // 📂 Load tasks from file
    public void loadTasks() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 4) continue;

                String type = parts[0];
                String title = parts[1];
                String desc = parts[2];
                boolean completed = Boolean.parseBoolean(parts[3]);

                Task task;
                if (type.equals("Work")) {
                    task = new WorkTask(title, desc, "Project X");
                } else {
                    task = new PersonalTask(title, desc, "General");
                }
                if (completed) task.markCompleted();
                tasks.add(task);
            }
        } catch (IOException e) {
            System.out.println("Error loading tasks: " + e.getMessage());
        }
    }

    public void markTaskCompleted(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.get(index).markCompleted();
    }
}
}
