package application;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TaskManager {

    private ArrayList<Task> tasks = new ArrayList<>();
    private final String FILE_PATH = "tasks.txt";

    public void addTask(Task t) { tasks.add(t); }
    public ArrayList<Task> getTasks() { return tasks; }

    public void deleteTask(int index) {
        if (index >= 0 && index < tasks.size()) tasks.remove(index);
    }

    public void updateTask(int index, Task task) {
        if (index >= 0 && index < tasks.size()) {
            tasks.set(index, task);
        }
    }

    public void sortByDate() {
        tasks.sort(Comparator.comparing(Task::getDueDate));
    }

    public void sortByStatus() {
        tasks.sort(Comparator.comparingInt(t -> t.getStatus().ordinal()));
    }

    public void sortByType() {
        tasks.sort(Comparator.comparing(Task::getType));
    }

    /**
     * Returns a new list of tasks filtered by type/status and optionally sorted per sortOption.
     * filterType: "All", "Work", "Personal"
     * filterStatus: null or one of TaskStatus.name()
     * sortOption: "None", "Due Date", "Status", "Type"
     */
    public List<Task> getFilteredAndSortedTasks(String filterType, String filterStatus, String sortOption) {
        List<Task> filtered = tasks.stream()
            .filter(t -> {
                boolean typeMatches = filterType == null || filterType.equals("All") || t.getType().equals(filterType);
                boolean statusMatches = (filterStatus == null || filterStatus.equals("All")) ||
                        t.getStatus().name().equals(filterStatus);
                return typeMatches && statusMatches;
            })
            .collect(Collectors.toList());

        switch (sortOption == null ? "None" : sortOption) {
            case "Due Date" -> filtered.sort(Comparator.comparing(Task::getDueDate));
            case "Status" -> filtered.sort(Comparator.comparingInt(t -> t.getStatus().ordinal()));
            case "Type" -> filtered.sort(Comparator.comparing(Task::getType));
            default -> {} // no sort
        }

        return filtered;
    }

    // SAVE TASKS (simple ; separated format)
    public void saveTasks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Task t : tasks) {
                if (t instanceof WorkTask wt) {
                    writer.write(String.join(";",
                            "WORK",
                            escape(wt.getTitle()),
                            escape(wt.getDescription()),
                            wt.getDueDate().toString(),
                            wt.getStatus().name(),
                            escape(wt.getProject() == null ? "" : wt.getProject())
                    ));
                } else if (t instanceof PersonalTask pt) {
                    writer.write(String.join(";",
                            "PERSONAL",
                            escape(pt.getTitle()),
                            escape(pt.getDescription()),
                            pt.getDueDate().toString(),
                            pt.getStatus().name(),
                            escape(pt.getCategory() == null ? "" : pt.getCategory())
                    ));
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving tasks: " + e.getMessage());
        }
    }

    // LOAD TASKS
    public void loadTasks() {
        tasks.clear();
        File f = new File(FILE_PATH);
        if (!f.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // simple split; we escaped fields with backslash for semicolons if needed
                String[] parts = splitEscaped(line, ';');
                if (parts.length < 6) continue;

                String type = parts[0];
                String title = unescape(parts[1]);
                String desc = unescape(parts[2]);
                LocalDate due = LocalDate.parse(parts[3]);
                TaskStatus status = TaskStatus.valueOf(parts[4]);
                String extra = unescape(parts[5]);

                if ("WORK".equals(type)) {
                    tasks.add(new WorkTask(title, desc, due, status, extra));
                } else {
                    tasks.add(new PersonalTask(title, desc, due, status, extra));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading tasks: " + e.getMessage());
        }
    }

    // --- small helpers for escaping semicolons (rudimentary) ---
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace(";", "\\;");
    }

    private String unescape(String s) {
        if (s == null) return "";
        StringBuilder out = new StringBuilder();
        boolean esc = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (esc) {
                out.append(c);
                esc = false;
            } else {
                if (c == '\\') esc = true;
                else out.append(c);
            }
        }
        return out.toString();
    }

    // splits on unescaped separator
    private String[] splitEscaped(String line, char sep) {
        ArrayList<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean esc = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (esc) {
                current.append(c);
                esc = false;
            } else {
                if (c == '\\') {
                    esc = true;
                } else if (c == sep) {
                    parts.add(current.toString());
                    current.setLength(0);
                } else {
                    current.append(c);
                }
            }
        }
        parts.add(current.toString());
        return parts.toArray(new String[0]);
    }
}
