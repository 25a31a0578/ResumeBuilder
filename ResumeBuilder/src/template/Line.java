package template;

/** One line of resume text plus how it should look (plain, bold, bullet or a small gap). */
public class Line {
    public static final int PLAIN = 0, BOLD = 1, BULLET = 2, GAP = 3;

    public final String text;
    public final int style;

    public Line(String text, int style) {
        this.text = text;
        this.style = style;
    }
}
