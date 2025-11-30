package application;

import java.time.LocalDate;

public class PersonalTask extends Task {
    private String category;

    // constructor with default status
    public PersonalTask(String title, String description, LocalDate dueDate, String category) {
        super(title, description, dueDate);
        this.category = category;
    }

    // constructor with explicit status
    public PersonalTask(String title, String description, LocalDate dueDate, TaskStatus status, String category) {
        super(title, description, dueDate, status);
        this.category = category;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    @Override
    public String getType() { return "Personal"; }

    @Override
    public String displayDetails() {
        return String.format(
            "[Personal]\nTitle: %s\nCategory: %s\nDue: %s\nStatus: %s",
            getTitle(), category == null ? "" : category, getDueDate(), getStatus()
        );
    }
}
