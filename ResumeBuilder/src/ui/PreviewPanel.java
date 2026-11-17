package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import layout.Cmd;
import layout.Layouter;

/** Shows the laid-out resume pages on screen by replaying the drawing commands (same ones the PDF uses). */
public class PreviewPanel extends JPanel {
    private static final double SCALE = 1.25;
    private static final int GAP = 20;
    private List<List<Cmd>> pages = new ArrayList<>();

    public PreviewPanel() {
        setBackground(new Color(120, 124, 130));
    }

    public void setPages(List<List<Cmd>> pages) {
        this.pages = pages;
        revalidate();
        repaint();
    }

    private int pageWidth() { return (int) Math.round(Layouter.PW * SCALE); }

    private int pageHeight() { return (int) Math.round(Layouter.PH * SCALE); }

    @Override
    public Dimension getPreferredSize() {
        int n = Math.max(1, pages.size());
        return new Dimension(pageWidth() + 2 * GAP, GAP + n * (pageHeight() + GAP));
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int offX = Math.max(GAP, (getWidth() - pageWidth()) / 2);
        for (int i = 0; i < pages.size(); i++) {
            Graphics2D pg = (Graphics2D) g.create();
            pg.translate(offX, GAP + i * (pageHeight() + GAP));
            pg.setColor(Color.WHITE);
            pg.fillRect(0, 0, pageWidth(), pageHeight());
            pg.clipRect(0, 0, pageWidth(), pageHeight());
            pg.scale(SCALE, SCALE);
            for (Cmd c : pages.get(i)) {
                draw(pg, c);
            }
            pg.dispose();
        }
        g.dispose();
    }

    private static void draw(Graphics2D g, Cmd c) {
        g.setColor(c.color);
        switch (c.type) {
            case Cmd.TEXT:
                g.setFont(new Font(Font.SANS_SERIF, c.bold ? Font.BOLD : Font.PLAIN, 10).deriveFont((float) c.size));
                g.drawString(c.text, (float) c.x, (float) c.y);
                break;
            case Cmd.LINE:
                g.setStroke(new BasicStroke((float) c.lineWidth));
                g.draw(new Line2D.Double(c.x, c.y, c.x2, c.y2));
                break;
            case Cmd.RECT:
                if (c.fill) {
                    g.fill(new Rectangle2D.Double(c.x, c.y, c.w, c.h));
                } else {
                    g.setStroke(new BasicStroke((float) c.lineWidth));
                    g.draw(new Rectangle2D.Double(c.x, c.y, c.w, c.h));
                }
                break;
            case Cmd.IMAGE:
                g.drawImage(c.image, (int) Math.round(c.x), (int) Math.round(c.y),
                        (int) Math.round(c.w), (int) Math.round(c.h), null);
                break;
            default:
                break;
        }
    }
}
