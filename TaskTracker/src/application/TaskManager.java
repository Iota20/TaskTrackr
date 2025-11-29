package application;

import java.io.*;
import java.time.LocalDate;
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

    public void saveTasks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {

            for (Task t : tasks) {
                if (t instanceof WorkTask wt) {
                    writer.write("Work|" +
                                 wt.getTitle() + "|" +
                                 wt.getDescription() + "|" +
                                 wt.getDueDate() + "|" +
                                 wt.isCompleted() + "|" +
                                 wt.getProject());
                } 
                else if (t instanceof PersonalTask pt) {
                    writer.write("Personal|" +
                                 pt.getTitle() + "|" +
                                 pt.getDescription() + "|" +
                                 pt.getDueDate() + "|" +
                                 pt.isCompleted() + "|" +
                                 pt.getCategory());
                }
                writer.newLine();
                System.out.println("Saving: " + t.getTitle());
            }
            
        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
    }

    public void loadTasks() {
        tasks.clear();
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = reader.readLine()) != null) {

                String[] parts = line.split("\\|");
                if (parts.length < 6) continue;

                String type = parts[0];
                String title = parts[1];
                String desc = parts[2];
                LocalDate dueDate = LocalDate.parse(parts[3]);
                boolean completed = Boolean.parseBoolean(parts[4]);
                String extra = parts[5];   // project or category

                Task task;

                if (type.equals("Work")) {
                    task = new WorkTask(title, desc, dueDate, extra);
                } else {
                    task = new PersonalTask(title, desc, dueDate, extra);
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
