package application;

public class WorkTask extends Task {
    private String project;

    public WorkTask(String title, String description, String project) {
        super(title, description);
        this.project = project;
    }

    @Override
    public String displayDetails() {
        return "[Work] " + getTitle() + " (" + project + ") - " +
               (isCompleted() ? "Done" : "Pending");
    }
}
