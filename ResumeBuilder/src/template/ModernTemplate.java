package template;

import java.util.Map;
import model.PersonalDetails;
import model.Resume;
import model.Utils;

/**
 * Template 5: coloured banner, two columns.
 * Left sidebar = contact, skills, languages, hobbies. Right column = profile, education, experience ...
 */
public class ModernTemplate extends ResumeTemplate {

    @Override
    public String getName() { return "5 - Modern"; }

    @Override
    public Page build(Resume r) {
        Page p = newPage(r, true);
        p.modern = true;
        PersonalDetails d = r.getPersonal();

        Section contact = new Section("CONTACT");
        addItem(contact, "Phone", d.getPhone());
        addItem(contact, "Email", d.getEmail());
        addItem(contact, "Address", d.getAddress());
        for (Map.Entry<String, String> e : d.getLinks().entrySet()) {
            addItem(contact, e.getKey(), shortUrl(e.getValue()));
        }
        p.addSide(contact);
        p.addSide(skillsSection(r, true));
        p.addSide(languageSection(r));
        p.addSide(hobbySection(r));

        p.addMain(objectiveSection(r, "PROFILE"));
        p.addMain(educationSection(r, false));
        p.addMain(experienceSection(r));
        p.addMain(projectSection(r));
        p.addMain(certificationSection(r));
        p.addMain(achievementSection(r));
        return p;
    }

    private void addItem(Section s, String label, String value) {
        if (Utils.has(value)) {
            s.bold(label).plain(value).gap();
        }
    }
}
