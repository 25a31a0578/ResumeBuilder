package model;

public class Experience {
    private final String role;
    private final String company;
    private final String duration;
    private final String description;

    public Experience(String role, String company, String duration, String description) {
        this.role = Utils.clean(role);
        this.company = Utils.clean(company);
        this.duration = Utils.clean(duration);
        this.description = Utils.clean(description);
    }

    public String getRole() { return role; }
    public String getCompany() { return company; }
    public String getDuration() { return duration; }
    public String getDescription() { return description; }
}
