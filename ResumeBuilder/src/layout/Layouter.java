package layout;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import model.Utils;
import template.Line;
import template.Page;
import template.Section;

/**
 * The layout engine: takes a Page (what to show) and decides WHERE everything goes on A4 pages.
 * It handles text wrapping, tables, the photo and automatic page breaks.
 * Output = a list of pages, each page = a list of drawing commands (Cmd).
 */
public class Layouter {
    public static final double PW = 595, PH = 842, M = 40;     // A4 page size and margin in points
    private static final double BODY = 10.5, LEAD = 1.3, SIDE_W = 150;
    private static final Color INK = new Color(30, 30, 30);
    private static final Color GREY = new Color(85, 85, 85);
    private static final Color ACCENT = new Color(26, 60, 110);
    private static final Color TABLE_HEAD = new Color(230, 230, 230);
    private static final Color SIDE_BG = new Color(238, 242, 248);

    private List<List<Cmd>> pages;
    private List<Cmd> cur;          // commands of the page we are drawing on
    private double y;               // current vertical position (grows downwards)
    private double colX, colW;      // left edge and width of the column we are filling
    private boolean sideMode;       // true while drawing the Modern sidebar (no page breaks there)
    private boolean modern;
    private double bannerBottom;

    public List<List<Cmd>> layout(Page p) {
        pages = new ArrayList<>();
        modern = p.modern;
        sideMode = false;
        newPage();
        BufferedImage photo = loadPhoto(p.photoPath);

        if (p.modern) {
            bannerHeader(p, photo);
        } else {
            classicHeader(p, photo);
        }

        if (p.sidebar.isEmpty()) {
            setColumn(M, PW - 2 * M);
            drawAll(p.sections);
        } else {
            double top = y;
            cur.add(Cmd.rect(0, bannerBottom, M + SIDE_W + 14, PH - bannerBottom, SIDE_BG, true, 0));
            sideMode = true;
            setColumn(M, SIDE_W);
            drawAll(p.sidebar);
            sideMode = false;
            y = top;
            double mainX = M + SIDE_W + 28;
            setColumn(mainX, PW - M - mainX);
            drawAll(p.sections);
        }
        return pages;
    }

    // ---------------------------------------------------------------- headers

    private void classicHeader(Page p, BufferedImage photo) {
        double fullW = PW - 2 * M;
        double[] box = photo != null ? fitBox(photo, 85, 105) : new double[] {0, 0};
        double textW = photo != null ? fullW - box[0] - 15 : fullW;
        boolean center = photo == null;
        double top = y;

        for (String l : wrap(p.name.toUpperCase(), true, 24, textW)) {
            textLine(l, align(l, true, 24, textW, center), 24, true, INK);
        }
        if (Utils.has(p.subtitle)) {
            for (String l : wrap(p.subtitle, false, 12, textW)) {
                textLine(l, align(l, false, 12, textW, center), 12, false, GREY);
            }
        }
        y += 3;
        for (String line : p.contactLines) {
            for (String l : wrap(line, false, 10, textW)) {
                textLine(l, align(l, false, 10, textW, center), 10, false, INK);
            }
        }
        if (photo != null) {
            cur.add(Cmd.image(photo, PW - M - box[0], top, box[0], box[1]));
            y = Math.max(y, top + box[1]);
        }
        y += 4;
        cur.add(Cmd.line(M, y, PW - M, y, 1.2, INK));
        y += 8;
    }

    private void bannerHeader(Page p, BufferedImage photo) {
        double fullW = PW - 2 * M;
        double[] box = photo != null ? fitBox(photo, 70, 88) : new double[] {0, 0};
        double textW = photo != null ? fullW - box[0] - 15 : fullW;
        List<String> nameLines = wrap(p.name.toUpperCase(), true, 26, textW);
        List<String> subLines = Utils.has(p.subtitle) ? wrap(p.subtitle, false, 12, textW) : new ArrayList<String>();
        double textH = nameLines.size() * 26 * LEAD + subLines.size() * 12 * LEAD;
        double bannerH = Math.max(textH + 44, photo != null ? box[1] + 30 : 0);

        cur.add(Cmd.rect(0, 0, PW, bannerH, ACCENT, true, 0));
        y = (bannerH - textH) / 2;
        for (String l : nameLines) {
            textLine(l, M, 26, true, Color.WHITE);
        }
        for (String l : subLines) {
            textLine(l, M, 12, false, new Color(215, 225, 240));
        }
        if (photo != null) {
            cur.add(Cmd.image(photo, PW - M - box[0], (bannerH - box[1]) / 2, box[0], box[1]));
        }
        bannerBottom = bannerH;
        y = bannerH + 16;
    }

    // ---------------------------------------------------------------- sections

    private void drawAll(List<Section> list) {
        for (Section s : list) {
            drawSection(s);
        }
    }

    private void drawSection(Section s) {
        double headSize = modern ? 11 : 12;
        Color headColor = modern ? ACCENT : INK;
        ensure(headSize * LEAD + 8 + BODY * LEAD * 2);
        y += 6;
        textLine(s.title, colX, headSize, true, headColor);
        y -= 2;
        cur.add(Cmd.line(colX, y, colX + colW, y, 0.8, headColor));
        y += 5;
        if (s.isTable()) {
            drawTable(s);
        } else {
            for (Line l : s.lines) {
                drawLine(l);
            }
        }
    }

    private void drawLine(Line l) {
        if (l.style == Line.GAP) {
            y += 4;
            return;
        }
        boolean bold = l.style == Line.BOLD;
        boolean bullet = l.style == Line.BULLET;
        double indent = bullet ? 12 : 0;
        List<String> rows = wrap(l.text, bold, BODY, colW - indent);
        if (bold) {
            ensure(BODY * LEAD * 2);        // keep a bold title together with the line below it
        }
        for (int i = 0; i < rows.size(); i++) {
            ensure(BODY * LEAD);
            if (bullet && i == 0) {
                cur.add(Cmd.text("\u2022", colX + 2, y + BODY * 0.85, BODY, false, INK));
            }
            textLine(rows.get(i), colX + indent, BODY, bold, INK);
        }
    }

    private void drawTable(Section s) {
        int n = s.tableHeader.length;
        double sum = 0;
        for (double w : s.tableWeights) {
            sum += w;
        }
        double[] cw = new double[n];
        for (int i = 0; i < n; i++) {
            cw[i] = colW * s.tableWeights[i] / sum;
        }
        drawRow(s.tableHeader, cw, true, TABLE_HEAD);
        for (String[] row : s.tableRows) {
            drawRow(row, cw, false, null);
        }
        y += 2;
    }

    private void drawRow(String[] cells, double[] cw, boolean bold, Color bg) {
        double size = 10, pad = 4, lh = size * LEAD;
        List<List<String>> wrapped = new ArrayList<>();
        int maxLines = 1;
        for (int i = 0; i < cells.length; i++) {
            List<String> w = wrap(cells[i], bold, size, cw[i] - 2 * pad);
            wrapped.add(w);
            maxLines = Math.max(maxLines, w.size());
        }
        double rowH = maxLines * lh + 2 * pad - 2;
        ensure(rowH);
        double x = colX;
        for (int i = 0; i < cells.length; i++) {
            if (bg != null) {
                cur.add(Cmd.rect(x, y, cw[i], rowH, bg, true, 0));
            }
            cur.add(Cmd.rect(x, y, cw[i], rowH, INK, false, 0.6));
            double ty = y + pad;
            for (String l : wrapped.get(i)) {
                cur.add(Cmd.text(l, x + pad, ty + size * 0.85, size, bold, INK));
                ty += lh;
            }
            x += cw[i];
        }
        y += rowH;
    }

    // ---------------------------------------------------------------- helpers

    private void newPage() {
        cur = new ArrayList<>();
        pages.add(cur);
        y = M;
    }

    private void setColumn(double x, double w) {
        colX = x;
        colW = w;
    }

    /** If the next block does not fit on this page, continue on a new page (full width). */
    private void ensure(double height) {
        if (!sideMode && y + height > PH - M) {
            newPage();
            setColumn(M, PW - 2 * M);
        }
    }

    /** Draws one line of text at the current y and moves y down by one line height. */
    private void textLine(String s, double x, double size, boolean bold, Color c) {
        cur.add(Cmd.text(s, x, y + size * 0.85, size, bold, c));
        y += size * LEAD;
    }

    private double align(String s, boolean bold, double size, double areaW, boolean center) {
        return center ? M + (areaW - Metrics.width(s, bold, size)) / 2 : M;
    }

    /** Splits text into lines that fit into 'width' points. Very long words (URLs) are cut. */
    static List<String> wrap(String text, boolean bold, double size, double width) {
        List<String> out = new ArrayList<>();
        for (String para : text.split("\n")) {
            String line = "";
            for (String word : para.split(" ")) {
                if (word.isEmpty()) {
                    continue;
                }
                while (Metrics.width(word, bold, size) > width) {
                    if (!line.isEmpty()) {
                        out.add(line);
                        line = "";
                    }
                    int cut = charsThatFit(word, bold, size, width);
                    out.add(word.substring(0, cut));
                    word = word.substring(cut);
                }
                if (line.isEmpty()) {
                    line = word;
                } else if (Metrics.width(line + " " + word, bold, size) <= width) {
                    line = line + " " + word;
                } else {
                    out.add(line);
                    line = word;
                }
            }
            if (!line.isEmpty()) {
                out.add(line);
            }
        }
        if (out.isEmpty()) {
            out.add("");
        }
        return out;
    }

    private static int charsThatFit(String word, boolean bold, double size, double width) {
        int n = 0;
        double w = 0;
        for (int i = 0; i < word.length(); i++) {
            w += Metrics.charWidth(word.charAt(i), bold) * size / 1000.0;
            if (w > width && n > 0) {
                break;
            }
            n++;
        }
        return Math.max(n, 1);
    }

    /** Scales an image to fit inside maxW x maxH keeping its proportions. */
    private static double[] fitBox(BufferedImage img, double maxW, double maxH) {
        double k = Math.min(maxW / img.getWidth(), maxH / img.getHeight());
        return new double[] {img.getWidth() * k, img.getHeight() * k};
    }

    /** Loads the photo, shrinks it to at most 500 px and converts it to plain RGB (keeps the PDF small). */
    private static BufferedImage loadPhoto(String path) {
        if (!Utils.has(path)) {
            return null;
        }
        try {
            BufferedImage src = ImageIO.read(new File(path));
            if (src == null) {
                return null;
            }
            double k = Math.min(1.0, 500.0 / Math.max(src.getWidth(), src.getHeight()));
            int w = Math.max(1, (int) Math.round(src.getWidth() * k));
            int h = Math.max(1, (int) Math.round(src.getHeight() * k));
            BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = out.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, w, h);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(src, 0, 0, w, h, null);
            g.dispose();
            return out;
        } catch (IOException e) {
            return null;
        }
    }
}
