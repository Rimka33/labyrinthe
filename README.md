# Résolution de Labyrinthe - Algorithmes BFS et DFS

Ce projet implémente une application Java permettant de visualiser et comparer deux algorithmes de résolution de labyrinthe : le parcours en largeur (BFS) et le parcours en profondeur (DFS).

## Fonctionnalités

- Génération aléatoire de labyrinthes
- Visualisation du chemin de résolution avec des flèches directionnelles
- Comparaison des performances entre BFS et DFS
- Interface graphique intuitive
- Personnalisation des couleurs
- Sauvegarde et chargement de labyrinthes
- Raccourcis clavier

## Structure du Projet

Le projet est organisé en plusieurs packages :

### Package `model`
- `Cell.java` : Représente une cellule du labyrinthe
- `Maze.java` : Gère la structure du labyrinthe

### Package `algorithm`
- `MazeSolver.java` : Interface définissant les méthodes de résolution
- `BFS.java` : Implémentation de l'algorithme de parcours en largeur
- `DFS.java` : Implémentation de l'algorithme de parcours en profondeur
- `MazeAnalyzer.java` : Analyse des performances des algorithmes

### Package `ui`
- `MazeGUI.java` : Interface graphique principale

## Algorithmes Implémentés

### BFS (Breadth-First Search)
- Parcourt le labyrinthe niveau par niveau
- Garantit le chemin le plus court
- Complexité : O(V + E) où V = nombre de cellules, E = nombre de passages

### DFS (Depth-First Search)
- Explore le labyrinthe en profondeur
- Ne garantit pas le chemin le plus court
- Complexité : O(V + E)

## Installation et Exécution

1. Cloner le repository :
```bash
git clone [URL_DU_REPO]
```

2. Compiler le projet :
```bash
javac src/main/java/**/*.java
```

3. Exécuter l'application :
```bash
java -cp src/main/java Main
```

## Utilisation

1. **Générer un nouveau labyrinthe** : Bouton "Nouveau" ou Ctrl+N
2. **Résoudre le labyrinthe** : 
   - Sélectionner l'algorithme (BFS ou DFS)
   - Cliquer sur "Résoudre" ou Ctrl+R
3. **Personnaliser** :
   - Menu Options > Taille du labyrinthe
   - Menu Options > Couleurs
   - Menu Options > Vitesse d'animation

