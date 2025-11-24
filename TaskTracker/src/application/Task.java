package application;

import java.time.LocalDate;

public abstract class Task {
    protected String title;
    protected String description;
    protected boolean completed;
    protected LocalDate dueDate;

    public Task(String title, String description, LocalDate dueDate) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.completed = false;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public boolean isCompleted() { return completed; }
    public LocalDate getDueDate() { return dueDate; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String desc) { this.description = desc; }
    public void setDueDate(LocalDate date) { this.dueDate = date; }
    public void markCompleted() { this.completed = true; }

    public abstract String displayDetails();
}
