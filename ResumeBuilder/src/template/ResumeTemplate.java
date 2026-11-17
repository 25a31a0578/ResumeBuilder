package template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.Certification;
import model.Education;
import model.Experience;
import model.PersonalDetails;
import model.Project;
import model.Resume;
import model.Utils;

/**
 * Abstract parent of all templates (abstraction + inheritance).
 * It contains the shared "building blocks"; each child decides which blocks to use and in what order.
 * Every block returns an EMPTY section when there is no data, and Page.add() drops empty sections.
 */
public abstract class ResumeTemplate {

    public abstract String getName();

    /** Turns the resume data into a Page (the layout plan). Each template implements this differently. */
    public abstract Page build(Resume r);

    @Override
    public String toString() {
        return getName();
    }

    protected Page newPage(Resume r, boolean withPhoto) {
        Page p = new Page();
        PersonalDetails d = r.getPersonal();
        p.name = d.getName();
        p.subtitle = d.getHeadline();
        if (withPhoto && Utils.has(d.getPhotoPath())) {
            p.photoPath = d.getPhotoPath();
        }
        return p;
    }

    /** Line 1: phone | email | address.  Line 2: only the links that were entered. */
    protected void addContactLines(Page p, PersonalDetails d) {
        List<String> contact = new ArrayList<>();
        if (Utils.has(d.getPhone())) contact.add(d.getPhone());
        if (Utils.has(d.getEmail())) contact.add(d.getEmail());
        if (Utils.has(d.getAddress())) contact.add(d.getAddress());
        if (!contact.isEmpty()) {
            p.contactLines.add(String.join(" | ", contact));
        }
        List<String> links = new ArrayList<>();
        for (Map.Entry<String, String> e : d.getLinks().entrySet()) {
            links.add(e.getKey() + ": " + shortUrl(e.getValue()));
        }
        if (!links.isEmpty()) {
            p.contactLines.add(String.join(" | ", links));
        }
    }

    protected static String shortUrl(String url) {
        String s = url.replaceFirst("(?i)^https?://", "");
        return s.endsWith("/") ? s.substring(0, s.length() - 1) : s;
    }

    protected Section objectiveSection(Resume r, String title) {
        Section s = new Section(title);
        if (Utils.has(r.getObjective())) {
            s.plain(r.getObjective());
        }
        return s;
    }

    protected Section educationSection(Resume r, boolean asTable) {
        Section s = new Section("EDUCATION");
        List<Education> list = r.getEducation();
        if (asTable) {
            List<String[]> rows = new ArrayList<>();
            for (Education e : list) {
                String degree = e.getDegree() + (Utils.has(e.getBranch()) ? " (" + e.getBranch() + ")" : "");
                rows.add(new String[] {degree, e.getInstitution(), e.getPeriod(), e.getScore()});
            }
            s.setTable(new String[] {"Degree", "Institution", "Year", "CGPA/Percentage"}, rows,
                    new double[] {0.30, 0.34, 0.16, 0.20});
            return s;
        }
        for (int i = 0; i < list.size(); i++) {
            Education e = list.get(i);
            String head = e.getDegree();
            if (Utils.has(e.getBranch())) {
                head += " \u2013 " + e.getBranch();
            }
            s.bold(head);
            s.plain(e.getInstitution());
            List<String> info = new ArrayList<>();
            if (Utils.has(e.getScoreText())) info.add(e.getScoreText());
            if (Utils.has(e.getPeriod())) info.add(e.getPeriod());
            if (!info.isEmpty()) {
                s.plain(String.join(" | ", info));
            }
            if (i < list.size() - 1) {
                s.gap();
            }
        }
        return s;
    }

    /** compact = label on its own line (used in the narrow Modern sidebar). */
    protected Section skillsSection(Resume r, boolean compact) {
        Section s = new Section("SKILLS");
        skillGroup(s, "Programming Languages", r.getProgrammingSkills(), compact);
        skillGroup(s, "Technical Skills", r.getTechnicalSkills(), compact);
        skillGroup(s, "Other Skills", r.getOtherSkills(), compact);
        return s;
    }

    private void skillGroup(Section s, String label, List<String> items, boolean compact) {
        if (items.isEmpty()) {
            return;
        }
        String joined = String.join(", ", items);
        if (compact) {
            s.bold(label).plain(joined).gap();
        } else {
            s.plain(label + ": " + joined);
        }
    }

    protected Section experienceSection(Resume r) {
        Section s = new Section("EXPERIENCE");
        List<Experience> list = r.getExperience();
        for (int i = 0; i < list.size(); i++) {
            Experience e = list.get(i);
            s.bold(e.getRole() + (Utils.has(e.getCompany()) ? " \u2013 " + e.getCompany() : ""));
            if (Utils.has(e.getDuration())) s.plain(e.getDuration());
            if (Utils.has(e.getDescription())) s.plain(e.getDescription());
            if (i < list.size() - 1) s.gap();
        }
        return s;
    }

    protected Section projectSection(Resume r) {
        Section s = new Section("PROJECTS");
        List<Project> list = r.getProjects();
        for (int i = 0; i < list.size(); i++) {
            Project p = list.get(i);
            s.bold(p.getTitle());
            if (Utils.has(p.getDescription())) s.plain(p.getDescription());
            if (i < list.size() - 1) s.gap();
        }
        return s;
    }

    protected Section certificationSection(Resume r) {
        Section s = new Section("CERTIFICATIONS");
        for (Certification c : r.getCertifications()) {
            s.bullet(c.getName() + (Utils.has(c.getIssuer()) ? " \u2013 " + c.getIssuer() : ""));
        }
        return s;
    }

    protected Section achievementSection(Resume r) {
        Section s = new Section("ACHIEVEMENTS");
        for (String a : r.getAchievements()) {
            s.bullet(a);
        }
        return s;
    }

    protected Section languageSection(Resume r) {
        Section s = new Section("LANGUAGES");
        if (!r.getLanguages().isEmpty()) {
            s.plain(String.join(", ", r.getLanguages()));
        }
        return s;
    }

    protected Section hobbySection(Resume r) {
        Section s = new Section("HOBBIES");
        if (!r.getHobbies().isEmpty()) {
            s.plain(String.join(", ", r.getHobbies()));
        }
        return s;
    }
}
