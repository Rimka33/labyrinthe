package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;


public class Maze {
    private Cell[][] grille;
    private int nombreLignes;
    private int nombreColonnes;
    private Cell depart;
    private Cell arrivee;

    /**
     * @param nombreLignes 
     * @param nombreColonnes 
     */
    public Maze(int nombreLignes, int nombreColonnes) {
        this.nombreLignes = nombreLignes;
        this.nombreColonnes = nombreColonnes;
        this.grille = new Cell[nombreLignes][nombreColonnes];
        initialiserLabyrintheVide();
    }

   
    private void initialiserLabyrintheVide() {
        for (int i = 0; i < nombreLignes; i++) {
            for (int j = 0; j < nombreColonnes; j++) {
                grille[i][j] = new Cell(i, j, false, false, false);
            }
        }
    }

   
    public void chargerDepuisFichier(String nomFichier) throws IOException {
        try (BufferedReader lecteur = new BufferedReader(new FileReader(nomFichier))) {
            String ligne;
            int numeroLigne = 0;
            while ((ligne = lecteur.readLine()) != null) {
                for (int colonne = 0; colonne < ligne.length(); colonne++) {
                    char caractere = ligne.charAt(colonne);
                    boolean estMur = caractere == '#';
                    boolean estDepart = caractere == 'S';
                    boolean estArrivee = caractere == 'E';
                    grille[numeroLigne][colonne] = new Cell(numeroLigne, colonne, estMur, estDepart, estArrivee);
                    
                    if (estDepart) depart = grille[numeroLigne][colonne];
                    if (estArrivee) arrivee = grille[numeroLigne][colonne];
                }
                numeroLigne++;
            }
        }
    }

    
    public void genererLabyrintheAleatoire() {
        Random aleatoire = new Random();
        
        for (int i = 0; i < nombreLignes; i++) {
            for (int j = 0; j < nombreColonnes; j++) {
                grille[i][j] = new Cell(i, j, true, false, false);
            }
        }

        List<Cell> murs = new ArrayList<>();
        int ligneDepart = 1 + aleatoire.nextInt(nombreLignes - 2);
        int colonneDepart = 1 + aleatoire.nextInt(nombreColonnes - 2);
        
        grille[ligneDepart][colonneDepart] = new Cell(ligneDepart, colonneDepart, false, false, false);
        ajouterMurs(ligneDepart, colonneDepart, murs);

        while (!murs.isEmpty()) {
            int indexAleatoire = aleatoire.nextInt(murs.size());
            Cell mur = murs.get(indexAleatoire);
            murs.remove(indexAleatoire);

            if (peutRetirerMur(mur)) {
                grille[mur.getLigne()][mur.getColonne()] = new Cell(mur.getLigne(), mur.getColonne(), false, false, false);
                ajouterMurs(mur.getLigne(), mur.getColonne(), murs);
            }
        }

        placerDepartEtArrivee();
    }

    
    private void placerDepartEtArrivee() {
        Random aleatoire = new Random();
        List<Cell> cellulesVides = new ArrayList<>();
        
        for (int i = 1; i < nombreLignes - 1; i++) {
            for (int j = 1; j < nombreColonnes - 1; j++) {
                if (!grille[i][j].estMur()) {
                    cellulesVides.add(grille[i][j]);
                }
            }
        }

        int indexDepart = aleatoire.nextInt(cellulesVides.size());
        depart = cellulesVides.get(indexDepart);
        grille[depart.getLigne()][depart.getColonne()] = new Cell(depart.getLigne(), depart.getColonne(), false, true, false);

        int distanceMax = 0;
        Cell meilleureArrivee = null;
        for (Cell cellule : cellulesVides) {
            if (cellule != depart) {
                int distance = Math.abs(cellule.getLigne() - depart.getLigne()) + 
                             Math.abs(cellule.getColonne() - depart.getColonne());
                if (distance > distanceMax) {
                    distanceMax = distance;
                    meilleureArrivee = cellule;
                }
            }
        }
        arrivee = meilleureArrivee;
        grille[arrivee.getLigne()][arrivee.getColonne()] = new Cell(arrivee.getLigne(), arrivee.getColonne(), false, false, true);
    }

    
    private void ajouterMurs(int ligne, int colonne, List<Cell> murs) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // haut, bas, gauche, droite
        for (int[] dir : directions) {
            int nouvelleLigne = ligne + dir[0];
            int nouvelleColonne = colonne + dir[1];
            if (nouvelleLigne > 0 && nouvelleLigne < nombreLignes - 1 && 
                nouvelleColonne > 0 && nouvelleColonne < nombreColonnes - 1) {
                Cell mur = grille[nouvelleLigne][nouvelleColonne];
                if (mur.estMur() && !murs.contains(mur)) {
                    murs.add(mur);
                }
            }
        }
    }

    private boolean peutRetirerMur(Cell mur) {
        int ligne = mur.getLigne();
        int colonne = mur.getColonne();
        int nombreChemins = 0;
        
        // Vérifier les cellules adjacentes
        if (ligne > 0 && !grille[ligne-1][colonne].estMur()) nombreChemins++;
        if (ligne < nombreLignes-1 && !grille[ligne+1][colonne].estMur()) nombreChemins++;
        if (colonne > 0 && !grille[ligne][colonne-1].estMur()) nombreChemins++;
        if (colonne < nombreColonnes-1 && !grille[ligne][colonne+1].estMur()) nombreChemins++;
        
        return nombreChemins == 1;
    }

    public Cell getDepart() { return depart; }
    public Cell getArrivee() { return arrivee; }
    public Cell getCellule(int ligne, int colonne) { return grille[ligne][colonne]; }
    public int getNombreLignes() { return nombreLignes; }
    public int getNombreColonnes() { return nombreColonnes; }

    @Override
    public String toString() {
        StringBuilder resultat = new StringBuilder();
        for (int i = 0; i < nombreLignes; i++) {
            for (int j = 0; j < nombreColonnes; j++) {
                resultat.append(grille[i][j].toString());
            }
            resultat.append("\n");
        }
        return resultat.toString();
    }
} 
