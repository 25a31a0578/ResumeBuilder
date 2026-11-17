import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import ui.MainWindow;

/** Program entry point: starts the Swing window on the Swing thread. */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // if the system look is not available, Swing's default look is used
            }
            new MainWindow().setVisible(true);
        });
    }
}
