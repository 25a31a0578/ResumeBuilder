package template;

import java.util.ArrayList;
import java.util.List;

/** A titled block of the resume (EDUCATION, SKILLS ...). Holds either lines or a table. */
public class Section {
    public final String title;
    public final List<Line> lines = new ArrayList<>();
    public String[] tableHeader;
    public List<String[]> tableRows;
    public double[] tableWeights;

    public Section(String title) {
        this.title = title;
    }

    public Section plain(String text) { lines.add(new Line(text, Line.PLAIN)); return this; }
    public Section bold(String text) { lines.add(new Line(text, Line.BOLD)); return this; }
    public Section bullet(String text) { lines.add(new Line(text, Line.BULLET)); return this; }
    public Section gap() { lines.add(new Line("", Line.GAP)); return this; }

    public boolean isTable() {
        return tableRows != null && !tableRows.isEmpty();
    }

    /** A section with nothing to show is simply not added to the page (empty-section rule). */
    public boolean isEmpty() {
        return lines.isEmpty() && !isTable();
    }

    public void trimTrailingGap() {
        while (!lines.isEmpty() && lines.get(lines.size() - 1).style == Line.GAP) {
            lines.remove(lines.size() - 1);
        }
    }

    /** Stores a table but removes any column in which every cell is empty. */
    public void setTable(String[] header, List<String[]> rows, double[] weights) {
        List<Integer> keep = new ArrayList<>();
        for (int c = 0; c < header.length; c++) {
            for (String[] row : rows) {
                if (row[c] != null && !row[c].trim().isEmpty()) {
                    keep.add(c);
                    break;
                }
            }
        }
        tableHeader = new String[keep.size()];
        tableWeights = new double[keep.size()];
        tableRows = new ArrayList<>();
        for (int i = 0; i < keep.size(); i++) {
            tableHeader[i] = header[keep.get(i)];
            tableWeights[i] = weights[keep.get(i)];
        }
        for (String[] row : rows) {
            String[] cells = new String[keep.size()];
            for (int i = 0; i < keep.size(); i++) {
                cells[i] = row[keep.get(i)] == null ? "" : row[keep.get(i)];
            }
            tableRows.add(cells);
        }
    }
}
