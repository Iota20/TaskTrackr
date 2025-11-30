package application;

import java.time.LocalDate;

public class WorkTask extends Task {
    private String project;

    // constructor with default status
    public WorkTask(String title, String description, LocalDate dueDate, String project) {
        super(title, description, dueDate);
        this.project = project;
    }

    // constructor with explicit status
    public WorkTask(String title, String description, LocalDate dueDate, TaskStatus status, String project) {
        super(title, description, dueDate, status);
        this.project = project;
    }

    public String getProject() { return project; }
    public void setProject(String project) { this.project = project; }

    @Override
    public String getType() { return "Work"; }

    @Override
    public String displayDetails() {
        return String.format(
            "[Work]\nTitle: %s\nProject: %s\nDue: %s\nStatus: %s",
            getTitle(), project == null ? "" : project, getDueDate(), getStatus()
        );
    }
}
