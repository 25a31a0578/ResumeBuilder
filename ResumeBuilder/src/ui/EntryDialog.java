package ui;

import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** A pop-up window with one text box per label. Used to add/edit one entry (an education, a project ...). */
public final class EntryDialog {
    private EntryDialog() { }

    /** Returns the typed values, or null if the user pressed Cancel. Fields marked required cannot be empty. */
    public static String[] show(Component parent, String title, String[] labels, int[] required, String[] initial) {
        int n = labels.length;
        JTextField[] fields = new JTextField[n];
        JPanel panel = new JPanel(new GridLayout(n, 2, 8, 8));
        for (int i = 0; i < n; i++) {
            panel.add(new JLabel(labels[i] + (isRequired(required, i) ? " *" : "")));
            fields[i] = new JTextField(initial != null ? initial[i] : "", 28);
            panel.add(fields[i]);
        }
        while (true) {
            int choice = JOptionPane.showConfirmDialog(parent, panel, title,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (choice != JOptionPane.OK_OPTION) {
                return null;
            }
            String[] result = new String[n];
            boolean ok = true;
            for (int i = 0; i < n && ok; i++) {
                result[i] = fields[i].getText().trim();
                if (isRequired(required, i) && result[i].isEmpty()) {
                    JOptionPane.showMessageDialog(parent, labels[i] + " is required.", "Missing information",
                            JOptionPane.WARNING_MESSAGE);
                    ok = false;
                }
            }
            if (ok) {
                return result;
            }
        }
    }

    private static boolean isRequired(int[] required, int index) {
        for (int r : required) {
            if (r == index) {
                return true;
            }
        }
        return false;
    }
}
