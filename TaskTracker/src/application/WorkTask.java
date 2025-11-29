package application;

import java.time.LocalDate;

public class WorkTask extends Task {
    private String project;

    public WorkTask(String title, String description, LocalDate dueDate, String project) {
        super(title, description, dueDate);
        this.project = project;
    }

    public String getProject() { return project; }
    public void setProject(String project) { this.project = project; }

    @Override
    public String displayDetails() {
        return "[Work] " + title +
               " | " + project +
               " | Due: " + dueDate +
               " | Completed: " + completed;
    }
}
