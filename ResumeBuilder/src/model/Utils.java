package model;

import java.util.ArrayList;
import java.util.List;

/** Small helper methods used everywhere. "has(...)" is the heart of the empty-field rule. */
public final class Utils {
    private Utils() { }

    /** true only if the text contains something other than spaces. */
    public static boolean has(String s) {
        return s != null && !s.trim().isEmpty();
    }

    public static String clean(String s) {
        return s == null ? "" : s.trim();
    }

    /** "Java, Python" or one-per-line text -> list of trimmed, non-empty items. */
    public static List<String> splitList(String text) {
        return split(text, "[,\\r\\n]+");
    }

    /** Only splits on new lines (used for achievements, which may contain commas). */
    public static List<String> splitLines(String text) {
        return split(text, "[\\r\\n]+");
    }

    private static List<String> split(String text, String regex) {
        List<String> out = new ArrayList<>();
        if (text == null) {
            return out;
        }
        for (String part : text.split(regex)) {
            if (has(part)) {
                out.add(part.trim());
            }
        }
        return out;
    }
}
