package model;

public class Project {
    private final String title;
    private final String description;

    public Project(String title, String description) {
        this.title = Utils.clean(title);
        this.description = Utils.clean(description);
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
}
