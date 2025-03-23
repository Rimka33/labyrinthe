package algorithm;

import model.Cell;
import model.Maze;
import java.util.List;

public interface MazeSolver {
    /**
     * Résout le labyrinthe et retourne le chemin trouvé
     * @param labyrinthe Le labyrinthe à résoudre
     * @return La liste des cellules formant le chemin de la solution
     */
    List<Cell> solve(Maze labyrinthe);

    /**
     * Retourne le nom de l'algorithme
     * @return Le nom de l'algorithme
     */
    String getName();

    /**
     * Retourne le nombre d'étapes effectuées pour trouver la solution
     * @return Le nombre d'étapes
     */
    int getSteps();

    /**
     * Retourne la liste des cellules explorées pendant la recherche
     * @return La liste des cellules explorées
     */
    List<Cell> getExploredCells();
} 