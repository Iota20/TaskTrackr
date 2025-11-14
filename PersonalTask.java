package application;

public class PersonalTask extends Task {
    private String category;

    public PersonalTask(String title, String description, String category) {
        super(title, description);
        this.category = category;
    }

    @Override
    public String displayDetails() {
        return "[Personal] " + getTitle() + " (" + category + ") - " +
               (isCompleted() ? "Done" : "Pending");
    }
}