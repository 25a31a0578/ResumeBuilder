package template;

import java.util.ArrayList;
import java.util.List;

/**
 * The "design plan" a template produces: header + sections.
 * It contains only text that really exists, so nothing empty can ever reach the output.
 */
public class Page {
    public String name = "";
    public String subtitle = "";
    public final List<String> contactLines = new ArrayList<>();
    public String photoPath;            // null = no photo, so no photo space is reserved
    public boolean modern;              // true = banner header + coloured headings
    public final List<Section> sections = new ArrayList<>();
    public final List<Section> sidebar = new ArrayList<>();   // only used by the Modern template

    public void addMain(Section s) { add(sections, s); }
    public void addSide(Section s) { add(sidebar, s); }

    private static void add(List<Section> list, Section s) {
        if (s == null) {
            return;
        }
        s.trimTrailingGap();
        if (!s.isEmpty()) {
            list.add(s);
        }
    }
}
