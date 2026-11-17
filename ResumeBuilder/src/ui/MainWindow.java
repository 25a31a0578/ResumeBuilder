package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import export.PdfExporter;
import export.TxtExporter;
import layout.Layouter;
import model.Resume;
import model.ValidationException;
import template.ClassicPhotoTemplate;
import template.ClassicTablePhotoTemplate;
import template.ClassicTableTemplate;
import template.ClassicTemplate;
import template.ModernTemplate;
import template.Page;
import template.ResumeTemplate;

/**
 * The main window. Two screens (cards):
 *   "form"    : enter / edit details
 *   "preview" : choose a template, see the resume, save as TXT or PDF.
 * The data stays in the form, so going back to edit never loses anything.
 */
public class MainWindow extends JFrame {
    private final ResumeForm form = new ResumeForm();
    private final PreviewPanel preview = new PreviewPanel();
    private final ResumeTemplate[] templates = {
        new ClassicTemplate(), new ClassicPhotoTemplate(), new ClassicTableTemplate(),
        new ClassicTablePhotoTemplate(), new ModernTemplate()
    };
    private final JComboBox<ResumeTemplate> templateBox = new JComboBox<>(templates);
    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private Resume current;          // the Resume built the last time the user pressed "Generate Preview"

    public MainWindow() {
        super("Resume Builder");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(980, 760);
        setLocationRelativeTo(null);

        root.add(formScreen(), "form");
        root.add(previewScreen(), "preview");
        add(root);
        templateBox.addActionListener(e -> refreshPreview());
    }

    private JPanel formScreen() {
        JButton sample = new JButton("Fill Sample Data");
        sample.addActionListener(e -> form.fillSample());
        JButton generate = new JButton("Generate Preview");
        generate.addActionListener(e -> generatePreview());
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        bar.add(sample);
        bar.add(generate);

        JPanel p = new JPanel(new BorderLayout());
        p.add(form, BorderLayout.CENTER);
        p.add(bar, BorderLayout.SOUTH);
        return p;
    }

    private JPanel previewScreen() {
        JButton back = new JButton("< Back to Edit");
        back.addActionListener(e -> cards.show(root, "form"));
        JButton txt = new JButton("Save as TXT");
        txt.addActionListener(e -> save(false));
        JButton pdf = new JButton("Save as PDF");
        pdf.addActionListener(e -> save(true));

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        bar.add(back);
        bar.add(new JLabel("Template:"));
        bar.add(templateBox);
        bar.add(txt);
        bar.add(pdf);

        JScrollPane scroll = new JScrollPane(preview);
        scroll.getVerticalScrollBar().setUnitIncrement(24);
        JPanel p = new JPanel(new BorderLayout());
        p.add(bar, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    /** Reads the form, validates it, shows the preview. If something is wrong the user stays on the form. */
    private void generatePreview() {
        try {
            current = form.buildResume();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Please fix this", JOptionPane.WARNING_MESSAGE);
            return;
        }
        refreshPreview();
        cards.show(root, "preview");
    }

    /** Same Resume, (possibly) different template -> new layout. This is why switching templates needs no retyping. */
    private void refreshPreview() {
        if (current == null) {
            return;
        }
        ResumeTemplate template = (ResumeTemplate) templateBox.getSelectedItem();
        preview.setPages(new Layouter().layout(template.build(current)));
    }

    private void save(boolean pdf) {
        if (current == null) {
            return;
        }
        String ext = pdf ? "pdf" : "txt";
        String base = current.getPersonal().getName().replaceAll("[^A-Za-z0-9]+", "_").replaceAll("^_+|_+$", "");
        if (base.isEmpty()) {
            base = "My";
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save resume as " + ext.toUpperCase());
        chooser.setSelectedFile(new File(base + "_Resume." + ext));
        chooser.setFileFilter(new FileNameExtensionFilter(ext.toUpperCase() + " file", ext));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith("." + ext)) {
            file = new File(file.getParentFile(), file.getName() + "." + ext);
        }
        if (file.exists() && JOptionPane.showConfirmDialog(this, file.getName() + " already exists. Replace it?",
                "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            Page page = ((ResumeTemplate) templateBox.getSelectedItem()).build(current);
            if (pdf) {
                PdfExporter.export(new Layouter().layout(page), file);
            } else {
                TxtExporter.export(page, file);
            }
            JOptionPane.showMessageDialog(this, "Saved:\n" + file.getAbsolutePath(), "Done",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not save the file:\n" + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
