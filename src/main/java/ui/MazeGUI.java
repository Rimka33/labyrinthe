package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import java.util.List;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.awt.Font;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import algorithm.MazeSolver;
import model.Cell;
import model.Maze;
import algorithm.BFS;
import algorithm.DFS;

public class MazeGUI extends JFrame {
    private Maze maze;
    private JPanel mazePanel;
    private List<Cell> solution;
    private final int CELL_SIZE = 30;
    private MazeSolver[] solvers;
    private JComboBox<String> algorithmComboBox;
    private JButton solveButton;
    private JButton generateButton;
    private JLabel stepsLabel;
    private JLabel timeLabel;
    private JStatusBar statusBar;
    private int mazeSize = 15;
    private boolean showBorders = true;
    private int animationSpeed = 100;
    private Color pathColor = Color.GREEN;
    private Color wallColor = Color.BLACK;
    private Color startColor = Color.BLUE;
    private Color endColor = Color.RED;
    private List<Cell> currentPath;
    private Timer animationTimer;
    private int currentStep;
    private List<Cell> exploredCells;
    private boolean isSearching;

    public MazeGUI() {
        solvers = new MazeSolver[]{new BFS(), new DFS()};
        currentPath = new ArrayList<>();
        exploredCells = new ArrayList<>();
        isSearching = false;
        setupUI();
    }

    private void setupUI() {
        setTitle("Résolution de Labyrinthe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createMenuBar();

        mazePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (maze != null) {
                    drawMaze(g);
                }
            }
        };
        
        JPanel controlPanel = new JPanel();
        algorithmComboBox = new JComboBox<>(new String[]{"BFS", "DFS"});
        solveButton = new JButton("Résoudre");
        generateButton = new JButton("Nouveau");
        stepsLabel = new JLabel("Étapes: 0");
        timeLabel = new JLabel("Temps: 0ms");
        
        solveButton.addActionListener(e -> solveMaze(solvers[algorithmComboBox.getSelectedIndex()]));
        generateButton.addActionListener(e -> generateNewMaze());
        
        controlPanel.add(new JLabel("Algorithme:"));
        controlPanel.add(algorithmComboBox);
        controlPanel.add(solveButton);
        controlPanel.add(generateButton);
        controlPanel.add(stepsLabel);
        controlPanel.add(timeLabel);

        statusBar = new JStatusBar();
        
        setLayout(new BorderLayout());
        add(mazePanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        add(statusBar, BorderLayout.NORTH);
        
        setupKeyboardShortcuts();
        generateNewMaze();
        pack();
        setLocationRelativeTo(null);
    }

    private void generateNewMaze() {
        maze = new Maze(mazeSize, mazeSize);
        do {
            maze.genererLabyrintheAleatoire();
            BFS solver = new BFS();
            List<Cell> testSolution = solver.solve(maze);
            if (testSolution != null && !testSolution.isEmpty()) {
                break;
            }
        } while (true);
        
        solution = null;
        currentPath.clear();
        exploredCells.clear();
        resizeMazePanel();
        statusBar.setMessage("Nouveau labyrinthe généré");
        repaint();
    }

    private void solveMaze(MazeSolver solver) {
        currentPath.clear();
        isSearching = false;
        currentStep = 0;
        statusBar.setMessage("Recherche de la solution...");
        solution = null;
        repaint();
        
        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            solution = solver.solve(maze);
            long endTime = System.currentTimeMillis();
            
            SwingUtilities.invokeLater(() -> {
                if (animationTimer != null) {
                    animationTimer.stop();
                }
                
                animationTimer = new Timer(100, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (currentStep < solution.size()) {
                            currentPath.add(solution.get(currentStep));
                            statusBar.setMessage("Affichage du chemin: " + (currentStep + 1) + "/" + solution.size());
                            currentStep++;
                            repaint();
                        } else {
                            ((Timer)e.getSource()).stop();
                            stepsLabel.setText("Étapes: " + solver.getSteps());
                            timeLabel.setText("Temps: " + (endTime - startTime) + "ms");
                            statusBar.setMessage("Solution trouvée avec " + solver.getName());
                        }
                    }
                });
                
                animationTimer.start();
            });
        }).start();
    }

    private void resizeMazePanel() {
        mazePanel.setPreferredSize(new Dimension(
            maze.getNombreColonnes() * CELL_SIZE,
            maze.getNombreLignes() * CELL_SIZE
        ));
        pack();
    }

    private void drawMaze(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        for (int row = 0; row < maze.getNombreLignes(); row++) {
            for (int col = 0; col < maze.getNombreColonnes(); col++) {
                Cell cell = maze.getCellule(row, col);
                int x = col * CELL_SIZE;
                int y = row * CELL_SIZE;

                if (cell.estMur()) {
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                }

                if (currentPath.contains(cell)) {
                    g2d.setColor(new Color(50, 205, 50));
                    g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                    
                    int index = currentPath.indexOf(cell);
                    if (index < currentPath.size() - 1) {
                        Cell nextCell = currentPath.get(index + 1);
                        
                        drawArrow(g2d, 
                            x + CELL_SIZE/2, y + CELL_SIZE/2,
                            x + CELL_SIZE/2 + (nextCell.getColonne() - cell.getColonne()) * CELL_SIZE,
                            y + CELL_SIZE/2 + (nextCell.getLigne() - cell.getLigne()) * CELL_SIZE);
                    }
                }

                if (cell.estDepart()) {
                    g2d.setColor(startColor);
                    g2d.fillOval(x + 5, y + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                } else if (cell.estArrivee()) {
                    g2d.setColor(endColor);
                    g2d.fillOval(x + 5, y + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                }

                g2d.setColor(Color.GRAY);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRect(x, y, CELL_SIZE, CELL_SIZE);
            }
        }

        if (isSearching) {
            g2d.setColor(new Color(0, 0, 0, 180));
            String message = "Recherche en cours...";
            g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 16.0f));
            int messageWidth = g2d.getFontMetrics().stringWidth(message);
            g2d.drawString(message, 
                (maze.getNombreColonnes() * CELL_SIZE - messageWidth) / 2,
                maze.getNombreLignes() * CELL_SIZE / 2);
        }
    }

    private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        g2d.setColor(new Color(0, 100, 0, 200));
        g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        g2d.drawLine(x1, y1, x2, y2);
        
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int arrowSize = 12;
        
        int x3 = (int)(x2 - arrowSize * Math.cos(angle - Math.PI/6));
        int y3 = (int)(y2 - arrowSize * Math.sin(angle - Math.PI/6));
        int x4 = (int)(x2 - arrowSize * Math.cos(angle + Math.PI/6));
        int y4 = (int)(y2 - arrowSize * Math.sin(angle + Math.PI/6));
        
        g2d.drawLine(x2, y2, x3, y3);
        g2d.drawLine(x2, y2, x4, y4);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Fichier");
        JMenuItem loadItem = new JMenuItem("Charger");
        JMenuItem saveItem = new JMenuItem("Sauvegarder");
        JMenuItem exitItem = new JMenuItem("Quitter");
        
        loadItem.addActionListener(e -> loadMazeFromFile());
        saveItem.addActionListener(e -> saveMazeToFile());
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(loadItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu optionsMenu = new JMenu("Options");
        JMenuItem sizeItem = new JMenuItem("Taille du labyrinthe");
        JMenuItem speedItem = new JMenuItem("Vitesse d'animation");
        JMenuItem colorsItem = new JMenuItem("Couleurs");
        JCheckBoxMenuItem bordersItem = new JCheckBoxMenuItem("Afficher les bordures", showBorders);
        
        sizeItem.addActionListener(e -> changeMazeSize());
        speedItem.addActionListener(e -> changeAnimationSpeed());
        colorsItem.addActionListener(e -> changeColors());
        bordersItem.addActionListener(e -> {
            showBorders = bordersItem.isSelected();
            mazePanel.repaint();
        });
        
        optionsMenu.add(sizeItem);
        optionsMenu.add(speedItem);
        optionsMenu.add(colorsItem);
        optionsMenu.addSeparator();
        optionsMenu.add(bordersItem);

        menuBar.add(fileMenu);
        menuBar.add(optionsMenu);
        setJMenuBar(menuBar);
    }

    private void setupKeyboardShortcuts() {
        solveButton.setMnemonic('R');
        solveButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke("control R"), "solve");
        solveButton.getActionMap().put("solve", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { 
                solveMaze(solvers[algorithmComboBox.getSelectedIndex()]); 
            }
        });

        generateButton.setMnemonic('N');
        generateButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke("control N"), "generate");
        generateButton.getActionMap().put("generate", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { generateNewMaze(); }
        });
    }

    private void loadMazeFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers texte", "txt"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                maze = new Maze(mazeSize, mazeSize);
                maze.chargerDepuisFichier(fileChooser.getSelectedFile().getPath());
                solution = null;
                currentPath.clear();
                exploredCells.clear();
                resizeMazePanel();
                statusBar.setMessage("Labyrinthe chargé depuis " + fileChooser.getSelectedFile().getName());
                repaint();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors du chargement du fichier: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveMazeToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers labyrinthe (*.txt)", "txt"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = fileChooser.getSelectedFile().getPath();
                if (!filePath.endsWith(".txt")) {
                    filePath += ".txt";
                }
                
                java.io.FileWriter writer = new java.io.FileWriter(filePath);
                writer.write(maze.toString());
                writer.close();
                
                statusBar.setMessage("Labyrinthe sauvegardé avec succès");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la sauvegarde du fichier", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void changeMazeSize() {
        String input = JOptionPane.showInputDialog(this, 
            "Entrez la taille du labyrinthe (5-50):", 
            String.valueOf(mazeSize));
        
        try {
            int newSize = Integer.parseInt(input);
            if (newSize >= 5 && newSize <= 50) {
                mazeSize = newSize;
                maze = new Maze(mazeSize, mazeSize);
                maze.genererLabyrintheAleatoire();
                mazePanel.repaint();
                statusBar.setMessage("Taille du labyrinthe modifiée: " + mazeSize);
            } else {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un nombre valide entre 5 et 50", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changeAnimationSpeed() {
        String input = JOptionPane.showInputDialog(this, 
            "Entrez la vitesse d'animation en ms (0-1000):", 
            String.valueOf(animationSpeed));
        
        try {
            int newSpeed = Integer.parseInt(input);
            if (newSpeed >= 0 && newSpeed <= 1000) {
                animationSpeed = newSpeed;
                statusBar.setMessage("Vitesse d'animation modifiée: " + animationSpeed + "ms");
            } else {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un nombre valide entre 0 et 1000", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changeColors() {
        JDialog dialog = new JDialog(this, "Personnalisation des couleurs", true);
        dialog.setLayout(new GridLayout(4, 2, 5, 5));
        
        JButton pathButton = new JButton("Chemin");
        JButton wallButton = new JButton("Mur");
        JButton startButton = new JButton("Départ");
        JButton endButton = new JButton("Arrivée");
        
        pathButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(dialog, "Couleur du chemin", pathColor);
            if (newColor != null) {
                pathColor = newColor;
                mazePanel.repaint();
            }
        });
        
        wallButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(dialog, "Couleur des murs", wallColor);
            if (newColor != null) {
                wallColor = newColor;
                mazePanel.repaint();
            }
        });
        
        startButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(dialog, "Couleur du départ", startColor);
            if (newColor != null) {
                startColor = newColor;
                mazePanel.repaint();
            }
        });
        
        endButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(dialog, "Couleur de l'arrivée", endColor);
            if (newColor != null) {
                endColor = newColor;
                mazePanel.repaint();
            }
        });
        
        dialog.add(new JLabel("Chemin:"));
        dialog.add(pathButton);
        dialog.add(new JLabel("Mur:"));
        dialog.add(wallButton);
        dialog.add(new JLabel("Départ:"));
        dialog.add(startButton);
        dialog.add(new JLabel("Arrivée:"));
        dialog.add(endButton);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private class MazePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int cellSize = Math.min(getWidth() / maze.getNombreColonnes(), getHeight() / maze.getNombreLignes());
            int startX = (getWidth() - maze.getNombreColonnes() * cellSize) / 2;
            int startY = (getHeight() - maze.getNombreLignes() * cellSize) / 2;

            for (int i = 0; i < maze.getNombreLignes(); i++) {
                for (int j = 0; j < maze.getNombreColonnes(); j++) {
                    Cell cell = maze.getCellule(i, j);
                    int x = startX + j * cellSize;
                    int y = startY + i * cellSize;

                    if (cell.estMur()) {
                        g2d.setColor(wallColor);
                        g2d.fillRect(x, y, cellSize, cellSize);
                    } else if (cell.estChemin()) {
                        g2d.setColor(pathColor);
                        g2d.fillRect(x, y, cellSize, cellSize);
                    } else if (cell.estDepart()) {
                        g2d.setColor(startColor);
                        g2d.fillRect(x, y, cellSize, cellSize);
                    } else if (cell.estArrivee()) {
                        g2d.setColor(endColor);
                        g2d.fillRect(x, y, cellSize, cellSize);
                    } else {
                        g2d.setColor(Color.WHITE);
                        g2d.fillRect(x, y, cellSize, cellSize);
                    }

                    if (showBorders) {
                        g2d.setColor(Color.GRAY);
                        g2d.drawRect(x, y, cellSize, cellSize);
                    }
                }
            }
        }
    }

    private class JStatusBar extends JPanel {
        private JLabel messageLabel;

        public JStatusBar() {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEtchedBorder());
            messageLabel = new JLabel("Prêt");
            add(messageLabel, BorderLayout.WEST);
        }

        public void setMessage(String message) {
            messageLabel.setText(message);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MazeGUI().setVisible(true);
        });
    }
} 