package model;

import java.util.ArrayList;
import java.util.List;

/**
 * The ONE object that holds all resume data.
 * Templates only read from it; they never keep their own copy.
 */
public class Resume {
    private PersonalDetails personal = new PersonalDetails();
    private String objective = "";

    private final List<Education> education = new ArrayList<>();
    private final List<String> programmingSkills = new ArrayList<>();
    private final List<String> technicalSkills = new ArrayList<>();
    private final List<String> otherSkills = new ArrayList<>();
    private final List<Experience> experience = new ArrayList<>();
    private final List<Project> projects = new ArrayList<>();
    private final List<Certification> certifications = new ArrayList<>();
    private final List<String> achievements = new ArrayList<>();
    private final List<String> languages = new ArrayList<>();
    private final List<String> hobbies = new ArrayList<>();

    public PersonalDetails getPersonal() { return personal; }
    public void setPersonal(PersonalDetails personal) { this.personal = personal; }
    public String getObjective() { return objective; }
    public void setObjective(String objective) { this.objective = Utils.clean(objective); }

    public List<Education> getEducation() { return education; }
    public List<String> getProgrammingSkills() { return programmingSkills; }
    public List<String> getTechnicalSkills() { return technicalSkills; }
    public List<String> getOtherSkills() { return otherSkills; }
    public List<Experience> getExperience() { return experience; }
    public List<Project> getProjects() { return projects; }
    public List<Certification> getCertifications() { return certifications; }
    public List<String> getAchievements() { return achievements; }
    public List<String> getLanguages() { return languages; }
    public List<String> getHobbies() { return hobbies; }
}
