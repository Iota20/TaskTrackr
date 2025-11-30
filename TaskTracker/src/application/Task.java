package application;

import java.time.LocalDate;

public abstract class Task {
    private String title;
    private String description;
    private LocalDate dueDate;
    private TaskStatus status;

    public Task(String title, String description, LocalDate dueDate) {
        this(title, description, dueDate, TaskStatus.PENDING);
    }

    public Task(String title, String description, LocalDate dueDate, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDate getDueDate() { return dueDate; }
    public TaskStatus getStatus() { return status; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public abstract String getType();      // "Work" or "Personal"
    public abstract String displayDetails(); // multi-line display (Style B)
}
