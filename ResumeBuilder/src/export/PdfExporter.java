package export;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import javax.imageio.ImageIO;
import layout.Cmd;
import layout.Layouter;

/**
 * Writes a PDF file by hand (no external library needed).
 * A PDF is a list of numbered objects (catalog, pages, fonts, image, page contents)
 * followed by a cross-reference table ("xref") that stores where each object starts.
 */
public final class PdfExporter {
    private static final Charset WIN_ANSI = Charset.forName("windows-1252");

    private PdfExporter() { }

    public static void export(List<List<Cmd>> pages, File file) throws IOException {
        Files.write(file.toPath(), build(pages));
    }

    public static byte[] build(List<List<Cmd>> pages) throws IOException {
        BufferedImage image = findImage(pages);
        byte[] jpeg = image == null ? null : toJpeg(image);
        int n = pages.size();
        int firstPageObj = jpeg != null ? 6 : 5;          // objects 1-4 (and 5 = image) come first
        int lastObj = firstPageObj + 2 * n - 1;
        long[] offset = new long[lastObj + 1];
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        put(out, "%PDF-1.4\n");
        object(out, offset, 1, "<< /Type /Catalog /Pages 2 0 R >>");

        StringBuilder kids = new StringBuilder();
        for (int i = 0; i < n; i++) {
            kids.append(firstPageObj + 2 * i + 1).append(" 0 R ");
        }
        object(out, offset, 2, "<< /Type /Pages /Kids [ " + kids + "] /Count " + n + " >>");
        object(out, offset, 3, "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>");
        object(out, offset, 4, "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold /Encoding /WinAnsiEncoding >>");
        if (jpeg != null) {
            stream(out, offset, 5, "/Type /XObject /Subtype /Image /Width " + image.getWidth()
                    + " /Height " + image.getHeight()
                    + " /ColorSpace /DeviceRGB /BitsPerComponent 8 /Filter /DCTDecode", jpeg);
        }

        String resources = "/Resources << /Font << /F1 3 0 R /F2 4 0 R >>"
                + (jpeg != null ? " /XObject << /Im1 5 0 R >>" : "") + " >>";
        for (int i = 0; i < n; i++) {
            int contentObj = firstPageObj + 2 * i;
            stream(out, offset, contentObj, "", content(pages.get(i)));
            object(out, offset, contentObj + 1, "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] "
                    + resources + " /Contents " + contentObj + " 0 R >>");
        }

        long xrefStart = out.size();
        put(out, "xref\n0 " + (lastObj + 1) + "\n");
        put(out, "0000000000 65535 f \n");
        for (int i = 1; i <= lastObj; i++) {
            put(out, String.format(Locale.US, "%010d 00000 n \n", offset[i]));
        }
        put(out, "trailer\n<< /Size " + (lastObj + 1) + " /Root 1 0 R >>\nstartxref\n" + xrefStart + "\n%%EOF\n");
        return out.toByteArray();
    }

    // ---------------------------------------------------------------- page content

    private static byte[] content(List<Cmd> cmds) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        double ph = Layouter.PH;
        for (Cmd c : cmds) {
            switch (c.type) {
                case Cmd.TEXT:
                    put(b, "BT /" + (c.bold ? "F2" : "F1") + " " + num(c.size) + " Tf " + rgb(c.color, false)
                            + " " + num(c.x) + " " + num(ph - c.y) + " Td (");
                    b.write(escape(c.text.getBytes(WIN_ANSI)));
                    put(b, ") Tj ET\n");
                    break;
                case Cmd.LINE:
                    put(b, rgb(c.color, true) + " " + num(c.lineWidth) + " w " + num(c.x) + " " + num(ph - c.y)
                            + " m " + num(c.x2) + " " + num(ph - c.y2) + " l S\n");
                    break;
                case Cmd.RECT:
                    if (c.fill) {
                        put(b, rgb(c.color, false) + " " + num(c.x) + " " + num(ph - c.y - c.h) + " "
                                + num(c.w) + " " + num(c.h) + " re f\n");
                    } else {
                        put(b, rgb(c.color, true) + " " + num(c.lineWidth) + " w " + num(c.x) + " "
                                + num(ph - c.y - c.h) + " " + num(c.w) + " " + num(c.h) + " re S\n");
                    }
                    break;
                case Cmd.IMAGE:
                    put(b, "q " + num(c.w) + " 0 0 " + num(c.h) + " " + num(c.x) + " " + num(ph - c.y - c.h)
                            + " cm /Im1 Do Q\n");
                    break;
                default:
                    break;
            }
        }
        return b.toByteArray();
    }

    /** Inside a PDF text string, ( ) and \ must be escaped with a backslash. */
    private static byte[] escape(byte[] raw) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        for (byte x : raw) {
            if (x == '(' || x == ')' || x == '\\') {
                b.write('\\');
            }
            b.write(x);
        }
        return b.toByteArray();
    }

    // ---------------------------------------------------------------- low level helpers

    private static void object(ByteArrayOutputStream out, long[] offset, int id, String body) throws IOException {
        offset[id] = out.size();
        put(out, id + " 0 obj\n" + body + "\nendobj\n");
    }

    private static void stream(ByteArrayOutputStream out, long[] offset, int id, String dict, byte[] data)
            throws IOException {
        offset[id] = out.size();
        put(out, id + " 0 obj\n<< " + dict + " /Length " + data.length + " >>\nstream\n");
        out.write(data);
        put(out, "\nendstream\nendobj\n");
    }

    private static void put(ByteArrayOutputStream out, String s) throws IOException {
        out.write(s.getBytes(StandardCharsets.ISO_8859_1));
    }

    private static String num(double v) {
        return String.format(Locale.US, "%.2f", v);
    }

    private static String rgb(Color c, boolean stroke) {
        return String.format(Locale.US, "%.3f %.3f %.3f %s",
                c.getRed() / 255.0, c.getGreen() / 255.0, c.getBlue() / 255.0, stroke ? "RG" : "rg");
    }

    private static BufferedImage findImage(List<List<Cmd>> pages) {
        for (List<Cmd> page : pages) {
            for (Cmd c : page) {
                if (c.type == Cmd.IMAGE) {
                    return c.image;
                }
            }
        }
        return null;
    }

    private static byte[] toJpeg(BufferedImage img) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        if (!ImageIO.write(img, "jpg", b)) {
            throw new IOException("Could not encode the photo as JPEG.");
        }
        return b.toByteArray();
    }
}
