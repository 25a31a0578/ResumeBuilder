package layout;

/**
 * Character widths of the standard PDF fonts Helvetica and Helvetica-Bold (units per 1000 em).
 * We need them to know where a line must wrap, because a PDF does not wrap text by itself.
 * Index 0 = the space character (ASCII 32) ... index 94 = '~' (ASCII 126).
 */
public final class Metrics {
    private Metrics() { }

    private static final int[] REGULAR = {
        278, 278, 355, 556, 556, 889, 667, 191, 333, 333, 389, 584, 278, 333, 278, 278,
        556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 278, 278, 584, 584, 584, 556,
        1015, 667, 667, 722, 722, 667, 611, 778, 722, 278, 500, 667, 556, 833, 722, 778,
        667, 778, 722, 667, 611, 722, 667, 944, 667, 667, 611, 278, 278, 278, 469, 556,
        333, 556, 556, 500, 556, 556, 278, 556, 556, 222, 222, 500, 222, 833, 556, 556,
        556, 556, 333, 500, 278, 556, 500, 722, 500, 500, 500, 334, 260, 334, 584
    };

    private static final int[] BOLD = {
        278, 333, 474, 556, 556, 889, 722, 238, 333, 333, 389, 584, 278, 333, 278, 278,
        556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 333, 333, 584, 584, 584, 611,
        975, 722, 722, 722, 722, 667, 611, 778, 722, 278, 556, 722, 611, 833, 722, 778,
        667, 778, 722, 667, 611, 722, 667, 944, 667, 667, 611, 333, 278, 333, 584, 556,
        333, 556, 611, 556, 611, 556, 333, 611, 611, 278, 278, 556, 278, 889, 611, 611,
        611, 611, 389, 556, 333, 611, 556, 778, 556, 556, 500, 389, 280, 389, 584
    };

    public static double charWidth(char c, boolean bold) {
        if (c >= 32 && c <= 126) {
            return (bold ? BOLD : REGULAR)[c - 32];
        }
        if (c == '\u2013') return 556;      // en dash
        if (c == '\u2014') return 1000;     // em dash
        if (c == '\u2022') return 350;      // bullet
        return 556;                         // any other character: average width
    }

    public static double width(String s, boolean bold, double size) {
        double total = 0;
        for (int i = 0; i < s.length(); i++) {
            total += charWidth(s.charAt(i), bold);
        }
        return total * size / 1000.0;
    }
}
