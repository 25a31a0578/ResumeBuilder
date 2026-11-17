package layout;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * One drawing command (draw text / line / rectangle / image) at a position on an A4 page.
 * The Layouter creates these; the preview panel and the PDF writer both "replay" them,
 * so the preview and the PDF always look the same.
 * Coordinates are in points (1/72 inch), origin = top-left corner of the page.
 */
public class Cmd {
    public static final int TEXT = 0, LINE = 1, RECT = 2, IMAGE = 3;

    public int type;
    public double x, y, w, h, x2, y2, size, lineWidth = 1;
    public boolean bold, fill;
    public Color color = Color.BLACK;
    public String text;
    public BufferedImage image;

    /** (x, baselineY) is the left end of the text baseline. */
    public static Cmd text(String text, double x, double baselineY, double size, boolean bold, Color color) {
        Cmd c = new Cmd();
        c.type = TEXT; c.text = text; c.x = x; c.y = baselineY; c.size = size; c.bold = bold; c.color = color;
        return c;
    }

    public static Cmd line(double x1, double y1, double x2, double y2, double width, Color color) {
        Cmd c = new Cmd();
        c.type = LINE; c.x = x1; c.y = y1; c.x2 = x2; c.y2 = y2; c.lineWidth = width; c.color = color;
        return c;
    }

    public static Cmd rect(double x, double y, double w, double h, Color color, boolean fill, double lineWidth) {
        Cmd c = new Cmd();
        c.type = RECT; c.x = x; c.y = y; c.w = w; c.h = h; c.color = color; c.fill = fill; c.lineWidth = lineWidth;
        return c;
    }

    public static Cmd image(BufferedImage img, double x, double y, double w, double h) {
        Cmd c = new Cmd();
        c.type = IMAGE; c.image = img; c.x = x; c.y = y; c.w = w; c.h = h;
        return c;
    }
}
