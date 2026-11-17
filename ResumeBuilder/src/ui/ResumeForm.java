package ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import model.Certification;
import model.Education;
import model.Experience;
import model.PersonalDetails;
import model.Project;
import model.Resume;
import model.Utils;
import model.ValidationException;
import model.Validator;

/**
 * The data-entry screen. It only collects text and, in buildResume(), converts it into a Resume object
 * after checking it. It knows nothing about templates, preview or saving (separation of responsibilities).
 */
public class ResumeForm extends JPanel {
    private final JTextField name = new JTextField(30);
    private final JTextField headline = new JTextField(30);
    private final JTextField phone = new JTextField(30);
    private final JTextField email = new JTextField(30);
    private final JTextField address = new JTextField(30);
    private final JTextField linkedin = new JTextField(30);
    private final JTextField github = new JTextField(30);
    private final JTextField leetcode = new JTextField(30);
    private final JTextField hackerrank = new JTextField(30);
    private final JTextField photo = new JTextField(30);

    private final JTextArea objective = new JTextArea(4, 30);
    private final JTextField programming = new JTextField(30);
    private final JTextField technical = new JTextField(30);
    private final JTextField otherSkills = new JTextField(30);

    private final JTextArea achievements = new JTextArea(4, 30);
    private final JTextField languages = new JTextField(30);
    private final JTextField hobbies = new JTextField(30);

    private final EntryListPanel educationPanel = new EntryListPanel("Education entries (add B.Tech, Intermediate, SSC ...)",
            new String[] {"Degree", "College/University", "Branch/Specialization", "CGPA/Percentage (8.14 or 82.9%)",
                "Start Year", "Graduation Year"}, new int[] {0, 1});
    private final EntryListPanel experiencePanel = new EntryListPanel("Experience (optional)",
            new String[] {"Role", "Company", "Duration", "Description"}, new int[] {0, 1});
    private final EntryListPanel projectPanel = new EntryListPanel("Projects (optional)",
            new String[] {"Project title", "Description"}, new int[] {0});
    private final EntryListPanel certificationPanel = new EntryListPanel("Certifications (optional)",
            new String[] {"Certification name", "Issued by"}, new int[] {0});

    public ResumeForm() {
        setLayout(new BorderLayout());
        photo.setEditable(false);
        objective.setLineWrap(true);
        objective.setWrapStyleWord(true);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("1. Personal", personalTab());
        tabs.addTab("2. Education", wrap(educationPanel));
        tabs.addTab("3. Objective & Skills", form(
                "Career objective", new JScrollPane(objective),
                "Programming languages", programming,
                "Technical skills", technical,
                "Other skills", otherSkills,
                "", new JLabel("Separate skills with commas, e.g. Java, Python, C")));
        JPanel work = new JPanel(new GridLayout(2, 1, 6, 6));
        work.add(experiencePanel);
        work.add(projectPanel);
        tabs.addTab("4. Experience & Projects", wrap(work));
        JPanel more = new JPanel(new BorderLayout(6, 6));
        more.add(certificationPanel, BorderLayout.CENTER);
        more.add(form("Achievements", new JScrollPane(achievements),
                "Languages known", languages,
                "Hobbies", hobbies,
                "", new JLabel("Achievements: one per line.  Languages / hobbies: separate with commas.")),
                BorderLayout.SOUTH);
        tabs.addTab("5. More", more);
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel personalTab() {
        JButton browse = new JButton("Browse...");
        browse.addActionListener(e -> choosePhoto());
        JButton clear = new JButton("Remove photo");
        clear.addActionListener(e -> photo.setText(""));
        JPanel photoRow = new JPanel(new BorderLayout(6, 0));
        photoRow.add(photo, BorderLayout.CENTER);
        JPanel photoButtons = new JPanel(new GridLayout(1, 2, 6, 0));
        photoButtons.add(browse);
        photoButtons.add(clear);
        photoRow.add(photoButtons, BorderLayout.EAST);

        return form(
                "Full name *", name,
                "Headline (e.g. Computer Science Engineering)", headline,
                "Phone", phone,
                "Email", email,
                "Address", address,
                "LinkedIn URL (optional)", linkedin,
                "GitHub URL (optional)", github,
                "LeetCode URL (optional)", leetcode,
                "HackerRank URL (optional)", hackerrank,
                "Profile photo (optional)", photoRow);
    }

    private void choosePhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Images (jpg, png, gif)", "jpg", "jpeg", "png", "gif"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            photo.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    // ------------------------------------------------------------------ form -> Resume

    /** Reads every field, validates it and returns a Resume. Throws ValidationException with a clear message. */
    public Resume buildResume() throws ValidationException {
        Resume r = new Resume();
        PersonalDetails d = r.getPersonal();
        d.setName(name.getText());
        d.setHeadline(headline.getText());
        d.setPhone(phone.getText());
        d.setEmail(email.getText());
        d.setAddress(address.getText());
        d.setLinkedin(linkedin.getText());
        d.setGithub(github.getText());
        d.setLeetcode(leetcode.getText());
        d.setHackerrank(hackerrank.getText());
        d.setPhotoPath(photo.getText());

        if (!Utils.has(d.getName())) {
            throw new ValidationException("Full name is required (tab 1).");
        }
        if (!Utils.has(d.getPhone()) && !Utils.has(d.getEmail())) {
            throw new ValidationException("Enter at least a phone number or an email (tab 1).");
        }
        if (Utils.has(d.getPhone()) && !Validator.isPhone(d.getPhone())) {
            throw new ValidationException("Phone number looks wrong. Use 10-13 digits, e.g. +91 98765 43210.");
        }
        if (Utils.has(d.getEmail()) && !Validator.isEmail(d.getEmail())) {
            throw new ValidationException("Email address looks wrong, e.g. name@example.com.");
        }
        checkLink("LinkedIn", d.getLinkedin(), "linkedin.com");
        checkLink("GitHub", d.getGithub(), "github.com");
        checkLink("LeetCode", d.getLeetcode(), "leetcode.com");
        checkLink("HackerRank", d.getHackerrank(), "hackerrank.com");
        checkPhoto(d.getPhotoPath());

        for (String[] e : educationPanel.getEntries()) {
            checkEducation(e);
            r.getEducation().add(new Education(e[0], e[1], e[2], e[3], e[4], e[5]));
        }
        if (r.getEducation().isEmpty()) {
            throw new ValidationException("Add at least one education entry (tab 2).");
        }

        r.setObjective(objective.getText());
        r.getProgrammingSkills().addAll(Utils.splitList(programming.getText()));
        r.getTechnicalSkills().addAll(Utils.splitList(technical.getText()));
        r.getOtherSkills().addAll(Utils.splitList(otherSkills.getText()));
        for (String[] e : experiencePanel.getEntries()) {
            r.getExperience().add(new Experience(e[0], e[1], e[2], e[3]));
        }
        for (String[] e : projectPanel.getEntries()) {
            r.getProjects().add(new Project(e[0], e[1]));
        }
        for (String[] e : certificationPanel.getEntries()) {
            r.getCertifications().add(new Certification(e[0], e[1]));
        }
        r.getAchievements().addAll(Utils.splitLines(achievements.getText()));
        r.getLanguages().addAll(Utils.splitList(languages.getText()));
        r.getHobbies().addAll(Utils.splitList(hobbies.getText()));
        return r;
    }

    private void checkLink(String label, String url, String domain) throws ValidationException {
        if (Utils.has(url) && !Validator.isUrl(url, domain)) {
            throw new ValidationException(label + " link looks wrong. It must contain \"" + domain
                    + "\" and no spaces. Leave it empty if you do not have one.");
        }
    }

    private void checkPhoto(String path) throws ValidationException {
        if (!Utils.has(path)) {
            return;
        }
        try {
            BufferedImage img = ImageIO.read(new File(path));
            if (img == null) {
                throw new ValidationException("The selected photo is not a valid image file.");
            }
        } catch (IOException ex) {
            throw new ValidationException("The selected photo could not be read: " + ex.getMessage());
        }
    }

    private void checkEducation(String[] e) throws ValidationException {
        String who = "Education \"" + e[0] + "\": ";
        if (Utils.has(e[3]) && !Validator.isScore(e[3])) {
            throw new ValidationException(who + "enter CGPA like 8.14 or percentage like 82.9%.");
        }
        if (Utils.has(e[4]) && !Validator.isYear(e[4])) {
            throw new ValidationException(who + "start year must be a 4-digit year, e.g. 2023.");
        }
        if (Utils.has(e[5]) && !Validator.isYear(e[5])) {
            throw new ValidationException(who + "graduation year must be a 4-digit year, e.g. 2027.");
        }
        if (Utils.has(e[4]) && Utils.has(e[5]) && Integer.parseInt(e[4]) > Integer.parseInt(e[5])) {
            throw new ValidationException(who + "start year cannot be after the graduation year.");
        }
    }

    // ------------------------------------------------------------------ sample data (for demo/testing)

    public void fillSample() {
        name.setText("Sample Student");
        headline.setText("Computer Science Engineering");
        phone.setText("+91 98765 43210");
        email.setText("sample.student@example.com");
        address.setText("Vijayawada, Andhra Pradesh");
        linkedin.setText("https://linkedin.com/in/sample-student");
        github.setText("https://github.com/sample-student");
        leetcode.setText("");
        hackerrank.setText("");
        objective.setText("Motivated computer science student looking for an internship to apply Java and "
                + "problem-solving skills on real projects.");
        programming.setText("Java, Python, C");
        technical.setText("SQL, HTML, CSS, Git, Data Structures");
        otherSkills.setText("Communication, Problem Solving, Teamwork");
        educationPanel.setEntries(Arrays.asList(
                new String[] {"B.Tech", "Pragati Engineering College", "Computer Science Engineering", "8.14", "2025", "2029"},
                new String[] {"Intermediate", "KMR Junior College", "MPC", "82.9%", "2023", "2025"},
                new String[] {"SSC", "AP Model School", "", "85.17%", "", "2023"}));
        projectPanel.setEntries(Arrays.asList(
                new String[] {"Resume Builder", "Java Swing desktop application that creates resumes in 5 templates and exports TXT/PDF."},
                new String[] {"Student Management System", "Console application using OOP, collections and file handling."}));
        achievements.setText("Solved 100+ problems on coding platforms\nParticipated in college hackathon");
        languages.setText("English, Telugu, Hindi");
        hobbies.setText("Reading, Chess");
    }

    // ------------------------------------------------------------------ layout helpers

    private static JPanel wrap(JComponent c) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(c, BorderLayout.CENTER);
        return p;
    }

    /** Builds a two-column form: label on the left, input on the right. Arguments: label, component, label, component ... */
    private static JPanel form(Object... items) {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8);
        g.anchor = GridBagConstraints.NORTHWEST;
        int row = 0;
        for (int i = 0; i < items.length; i += 2) {
            g.gridy = row++;
            g.gridx = 0;
            g.weightx = 0;
            g.fill = GridBagConstraints.NONE;
            p.add(new JLabel((String) items[i]), g);
            g.gridx = 1;
            g.weightx = 1;
            g.fill = GridBagConstraints.HORIZONTAL;
            p.add((JComponent) items[i + 1], g);
        }
        g.gridy = row;
        g.gridx = 0;
        g.gridwidth = 2;
        g.weighty = 1;
        g.fill = GridBagConstraints.BOTH;
        p.add(new JPanel(), g);       // invisible filler pushes everything to the top
        return p;
    }
}
