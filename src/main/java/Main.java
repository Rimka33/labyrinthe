import ui.MazeGUI;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            MazeGUI gui = new MazeGUI();
            gui.setVisible(true);
        });
    }
} 