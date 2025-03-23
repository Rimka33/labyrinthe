package algorithm;

import model.Cell;
import model.Maze;
import java.util.*;

public class DFS implements MazeSolver {
    private int nombreEtapes;
    private List<Cell> cellulesExplorees;

    public DFS() {
        this.nombreEtapes = 0;
        this.cellulesExplorees = new ArrayList<>();
    }

    @Override
    public List<Cell> solve(Maze labyrinthe) {
        nombreEtapes = 0;
        cellulesExplorees.clear();
        
        Cell depart = labyrinthe.getDepart();
        Cell arrivee = labyrinthe.getArrivee();
        
        Stack<Cell> pile = new Stack<>();
        Map<Cell, Cell> parents = new HashMap<>();
        
        pile.push(depart);
        parents.put(depart, null);
        cellulesExplorees.add(depart);
        
        while (!pile.isEmpty()) {
            Cell celluleCourante = pile.pop();
            nombreEtapes++;
            
            if (celluleCourante.equals(arrivee)) {
                return reconstruireChemin(celluleCourante, parents);
            }
            
            List<Cell> voisins = trouverVoisinsValides(celluleCourante, labyrinthe);
            for (Cell voisin : voisins) {
                if (!parents.containsKey(voisin)) {
                    pile.push(voisin);
                    parents.put(voisin, celluleCourante);
                    cellulesExplorees.add(voisin);
                }
            }
        }
        
        return new ArrayList<>();
    }

    private List<Cell> reconstruireChemin(Cell arrivee, Map<Cell, Cell> parents) {
        List<Cell> chemin = new ArrayList<>();
        Cell celluleCourante = arrivee;
        
        while (celluleCourante != null) {
            chemin.add(0, celluleCourante);
            celluleCourante = parents.get(celluleCourante);
        }
        
        return chemin;
    }

    private List<Cell> trouverVoisinsValides(Cell cellule, Maze labyrinthe) {
        List<Cell> voisins = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        
        for (int[] direction : directions) {
            int nouvelleLigne = cellule.getLigne() + direction[0];
            int nouvelleColonne = cellule.getColonne() + direction[1];
            
            if (nouvelleLigne >= 0 && nouvelleLigne < labyrinthe.getNombreLignes() && 
                nouvelleColonne >= 0 && nouvelleColonne < labyrinthe.getNombreColonnes()) {
                Cell voisin = labyrinthe.getCellule(nouvelleLigne, nouvelleColonne);
                if (!voisin.estMur()) {
                    voisins.add(voisin);
                }
            }
        }
        
        return voisins;
    }

    @Override
    public String getName() {
        return "DFS (Parcours en Profondeur)";
    }

    @Override
    public int getSteps() {
        return nombreEtapes;
    }

    @Override
    public List<Cell> getExploredCells() {
        return cellulesExplorees;
    }
} 