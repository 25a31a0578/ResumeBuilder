package model;

/** One education entry (B.Tech, Intermediate, SSC ...). */
public class Education {
    private final String degree;
    private final String institution;
    private final String branch;
    private final String score;
    private final String startYear;
    private final String endYear;

    public Education(String degree, String institution, String branch,
                     String score, String startYear, String endYear) {
        this.degree = Utils.clean(degree);
        this.institution = Utils.clean(institution);
        this.branch = Utils.clean(branch);
        this.score = Utils.clean(score);
        this.startYear = Utils.clean(startYear);
        this.endYear = Utils.clean(endYear);
    }

    public String getDegree() { return degree; }
    public String getInstitution() { return institution; }
    public String getBranch() { return branch; }
    public String getScore() { return score; }

    /** "2025-2029", "2023" or "2025-Present"; empty if no year was given. */
    public String getPeriod() {
        if (Utils.has(startYear) && Utils.has(endYear)) return startYear + "\u2013" + endYear;
        if (Utils.has(endYear)) return endYear;
        if (Utils.has(startYear)) return startYear + "\u2013Present";
        return "";
    }

    /** "CGPA: 8.14" or "Percentage: 82.9%"; empty if no score was given. */
    public String getScoreText() {
        if (!Utils.has(score)) return "";
        return (score.contains("%") ? "Percentage: " : "CGPA: ") + score;
    }
}
