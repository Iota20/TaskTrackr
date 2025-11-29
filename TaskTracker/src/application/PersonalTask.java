package application;

import java.time.LocalDate;

public class PersonalTask extends Task {
    private String category;

    public PersonalTask(String title, String description, LocalDate dueDate, String category) {
        super(title, description, dueDate);
        this.category = category;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    @Override
    public String displayDetails() {
        return "[Personal] " + title +
               " | " + category +
               " | Due: " + dueDate +
               " | Completed: " + completed;
    }
}
