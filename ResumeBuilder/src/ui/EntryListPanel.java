package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/**
 * A reusable list with Add / Edit / Remove / Up / Down buttons.
 * The same class is used for Education, Experience, Projects and Certifications;
 * only the labels differ (so we write the code once).
 */
public class EntryListPanel extends JPanel {
    private final String[] labels;
    private final int[] required;
    private final String title;
    private final DefaultListModel<String[]> model = new DefaultListModel<>();
    private final JList<String[]> list = new JList<>(model);

    public EntryListPanel(String title, String[] labels, int[] required) {
        this.title = title;
        this.labels = labels;
        this.required = required;
        setLayout(new BorderLayout(6, 6));
        setBorder(BorderFactory.createTitledBorder(title));

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object value, int index,
                                                          boolean selected, boolean focus) {
                return super.getListCellRendererComponent(l, summary((String[]) value), index, selected, focus);
            }
        });
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    edit();
                }
            }
        });
        add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        buttons.add(button("Add", this::add));
        buttons.add(button("Edit", this::edit));
        buttons.add(button("Remove", this::remove));
        buttons.add(button("Move Up", () -> move(-1)));
        buttons.add(button("Move Down", () -> move(1)));
        add(buttons, BorderLayout.SOUTH);
    }

    private JButton button(String text, Runnable action) {
        JButton b = new JButton(text);
        b.addActionListener(e -> action.run());
        return b;
    }

    private void add() {
        String[] values = EntryDialog.show(this, "Add - " + title, labels, required, null);
        if (values != null) {
            model.addElement(values);
        }
    }

    private void edit() {
        int i = list.getSelectedIndex();
        if (i < 0) {
            return;
        }
        String[] values = EntryDialog.show(this, "Edit - " + title, labels, required, model.get(i));
        if (values != null) {
            model.set(i, values);
        }
    }

    private void remove() {
        int i = list.getSelectedIndex();
        if (i >= 0) {
            model.remove(i);
        }
    }

    private void move(int direction) {
        int i = list.getSelectedIndex();
        int j = i + direction;
        if (i < 0 || j < 0 || j >= model.size()) {
            return;
        }
        String[] a = model.get(i);
        model.set(i, model.get(j));
        model.set(j, a);
        list.setSelectedIndex(j);
    }

    private static String summary(String[] values) {
        List<String> parts = new ArrayList<>();
        for (String v : values) {
            if (v != null && !v.trim().isEmpty()) {
                parts.add(v.trim());
            }
        }
        return String.join("  |  ", parts);
    }

    public List<String[]> getEntries() {
        List<String[]> out = new ArrayList<>();
        for (int i = 0; i < model.size(); i++) {
            out.add(model.get(i));
        }
        return out;
    }

    public void setEntries(List<String[]> entries) {
        model.clear();
        for (String[] e : entries) {
            model.addElement(e);
        }
    }
}
