package template;

import model.Resume;

/**
 * Template 1. Also the parent of templates 2-4: they only switch two "hooks"
 * (usesPhoto / usesTable) and reuse everything else (inheritance + method overriding).
 */
public class ClassicTemplate extends ResumeTemplate {

    protected boolean usesPhoto() { return false; }

    protected boolean usesTable() { return false; }

    @Override
    public String getName() { return "1 - Classic"; }

    @Override
    public Page build(Resume r) {
        Page p = newPage(r, usesPhoto());
        addContactLines(p, r.getPersonal());
        p.addMain(objectiveSection(r, "CAREER OBJECTIVE"));
        p.addMain(educationSection(r, usesTable()));
        p.addMain(skillsSection(r, false));
        p.addMain(experienceSection(r));
        p.addMain(projectSection(r));
        p.addMain(certificationSection(r));
        p.addMain(achievementSection(r));
        p.addMain(languageSection(r));
        p.addMain(hobbySection(r));
        return p;
    }
}
