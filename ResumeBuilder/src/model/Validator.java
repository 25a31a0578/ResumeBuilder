package model;

/** All input rules in one place. Each method answers: "is this text valid?" */
public final class Validator {
    private Validator() { }

    public static boolean isEmail(String s) {
        return s.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
    }

    public static boolean isPhone(String s) {
        if (!s.matches("[+0-9 ()-]+")) {
            return false;
        }
        int digits = s.replaceAll("[^0-9]", "").length();
        return digits >= 10 && digits <= 13;
    }

    /** A link must have no spaces and must contain the expected site, e.g. "github.com". */
    public static boolean isUrl(String s, String domain) {
        return !s.matches(".*\\s.*") && s.toLowerCase().contains(domain);
    }

    public static boolean isYear(String s) {
        if (!s.matches("\\d{4}")) {
            return false;
        }
        int y = Integer.parseInt(s);
        return y >= 1950 && y <= 2100;
    }

    /** 8.14  or  82.9%  */
    public static boolean isScore(String s) {
        return s.matches("\\d{1,3}(\\.\\d{1,2})?\\s*%?");
    }
}
