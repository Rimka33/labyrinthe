package model;


public class Cell {
    private int ligne;  
    private int colonne;  
    
    private boolean estMur;  
    private boolean estDepart;  
    private boolean estArrivee;  
    private boolean estChemin;  

    /**
     * @param ligne 
     * @param colonne 
     * @param estMur 
     * @param estDepart 
     * @param estArrivee 
     */
    public Cell(int ligne, int colonne, boolean estMur, boolean estDepart, boolean estArrivee) {
        this.ligne = ligne;
        this.colonne = colonne;
        this.estMur = estMur;
        this.estDepart = estDepart;
        this.estArrivee = estArrivee;
        this.estChemin = false;
    }

    public int getLigne() { return ligne; }
    public int getColonne() { return colonne; }
    public boolean estMur() { return estMur; }
    public boolean estDepart() { return estDepart; }
    public boolean estArrivee() { return estArrivee; }
    public boolean estChemin() { return estChemin; }

    public void marquerCommeChemin(boolean estChemin) {
        this.estChemin = estChemin;
    }

    
    @Override
    public String toString() {
        if (estMur) return "#";
        if (estDepart) return "S";
        if (estArrivee) return "E";
        if (estChemin) return "+";
        return "=";
    }

    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cell autre = (Cell) obj;
        return ligne == autre.ligne && colonne == autre.colonne;
    }

   
    @Override
    public int hashCode() {
        return 31 * ligne + colonne;
    }
} 