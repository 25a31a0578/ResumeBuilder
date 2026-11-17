package export;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import template.Line;
import template.Page;
import template.Section;

/** Saves the resume as a plain text file. */
public final class TxtExporter {
    private TxtExporter() { }

    public static void export(Page page, File file) throws IOException {
        Files.write(file.toPath(), render(page).getBytes(StandardCharsets.UTF_8));
    }

    /** Builds the text (separate from saving, so it is easy to test). */
    public static String render(Page p) {
        StringBuilder sb = new StringBuilder();
        sb.append(p.name.toUpperCase()).append("\n");
        if (!p.subtitle.isEmpty()) {
            sb.append(p.subtitle).append("\n");
        }
        for (String c : p.contactLines) {
            sb.append(c).append("\n");
        }
        sb.append("\n");
        for (Section s : p.sidebar) {
            appendSection(sb, s);
        }
        for (Section s : p.sections) {
            appendSection(sb, s);
        }
        return sb.toString().trim() + "\n";
    }

    private static void appendSection(StringBuilder sb, Section s) {
        sb.append(s.title).append("\n");
        for (int i = 0; i < s.title.length(); i++) {
            sb.append('-');
        }
        sb.append("\n");
        if (s.isTable()) {
            appendTable(sb, s);
        } else {
            for (Line l : s.lines) {
                if (l.style == Line.GAP) {
                    sb.append("\n");
                } else if (l.style == Line.BULLET) {
                    sb.append("- ").append(l.text).append("\n");
                } else {
                    sb.append(l.text).append("\n");
                }
            }
        }
        sb.append("\n");
    }

    private static void appendTable(StringBuilder sb, Section s) {
        int n = s.tableHeader.length;
        int[] width = new int[n];
        List<String[]> all = new ArrayList<>();
        all.add(s.tableHeader);
        all.addAll(s.tableRows);
        for (String[] row : all) {
            for (int i = 0; i < n; i++) {
                width[i] = Math.max(width[i], row[i].length());
            }
        }
        for (int r = 0; r < all.size(); r++) {
            StringBuilder line = new StringBuilder();
            for (int i = 0; i < n; i++) {
                line.append(String.format("%-" + width[i] + "s", all.get(r)[i]));
                if (i < n - 1) {
                    line.append(" | ");
                }
            }
            sb.append(line.toString().replaceAll("\\s+$", "")).append("\n");
            if (r == 0) {
                for (int i = 0; i < n; i++) {
                    for (int k = 0; k < width[i]; k++) {
                        sb.append('-');
                    }
                    if (i < n - 1) {
                        sb.append("-+-");
                    }
                }
                sb.append("\n");
            }
        }
    }
}
