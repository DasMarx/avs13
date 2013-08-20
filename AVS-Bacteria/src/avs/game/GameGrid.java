/**
 * 
 */

package avs.game;

import java.util.LinkedList;
import java.util.Random;

/**
 * @author HZU
 */
public class GameGrid {

    private Cell[][] gameGrid;

    private LinkedList<Cell> cellsPossessedByPlayer = new LinkedList<Cell>();

    private LinkedList<Cell> cellsPossessedByAI = new LinkedList<Cell>();
    
    private static final int gridSize = 30;

    Random r;

    /**
     * Initializes a new {@link GameGrid}.
     */
    public GameGrid() {
        gameGrid = new Cell[gridSize][gridSize];
    }

    /**
     * @return list of cells possessed by player
     */
    public LinkedList<Cell> getCellsPossessedByPlayer() {
        return cellsPossessedByPlayer;
    }

    /**
     * @return list of cells possessed by ai
     */
    public LinkedList<Cell> getCellsPossessedByAI() {
        return cellsPossessedByAI;
    }

    /**
     * initializes the grid
     */
    public void initialize() {
        r = new Random();
        gameGrid = new Cell[gridSize][gridSize];
        for (int i = 0; i < gameGrid.length; i++) {
            for (int j = 0; j < gameGrid.length; j++) {
                gameGrid[i][j] = new Cell(i, j, Attributes.NEUTRAL, r.nextInt(4));
                if ((i == 0) && (j == 0)) {
                    gameGrid[i][j] = new Cell(i, j, Attributes.PLAYER, Attributes.UP);
                    cellsPossessedByPlayer.add(getCell(i, j));
                }
                if ((i == gridSize-1) && (j == gridSize-1)) {
                    gameGrid[i][j] = new Cell(i, j, Attributes.AI, Attributes.DOWN);
                    cellsPossessedByAI.add(getCell(i, j));
                }
            }
        }

    }

    /**
     * @return length & height of the grid
     */
    public int getLength() {
        return gridSize;
    }

    /**
     * @param x coordinate
     * @param y coordinate
     * @return the cell
     */
    public Cell getCell(int x, int y) {
        if (x < 0 || y < 0 || x > gridSize-1 || y > gridSize-1)
            return null;
        return gameGrid[x][y];
    }

    /**
     * @return copy of the GameGrid
     */
    public GameGrid getCopy() {
        GameGrid g = new GameGrid();
//        for (int i = 0; i < gameGrid.length; i++) {
//            for (int j = 0; j < gameGrid.length; j++) {
//                g.gameGrid[i][j] = gameGrid[i][j];
//            }
//        }
//        for (int i = 0; i < cellsPossessedByAI.size(); i++) {
//            g.cellsPossessedByAI.add(cellsPossessedByAI.get(i));
//        }
//        for (int i = 0; i < cellsPossessedByPlayer.size(); i++) {
//            g.cellsPossessedByPlayer.add(cellsPossessedByPlayer.get(i));
//        }
        
        g.gameGrid = gameGrid.clone();
        g.cellsPossessedByAI = new LinkedList<Cell>(cellsPossessedByAI);
        g.cellsPossessedByPlayer = new LinkedList<Cell>(cellsPossessedByPlayer);
        return g;
    }

    /**
     * @param c cell to be added
     */
    public void addCellPlayer(Cell c) {
        cellsPossessedByPlayer.add(c);
    }

    /**
     * @param c cell to be added
     */
    public void addCellAI(Cell c) {
        cellsPossessedByAI.add(c);
    }

    /**
     * @param c cell to be removed
     */
    public void removeCell(Cell c) {
        cellsPossessedByAI.remove(c);
        cellsPossessedByPlayer.remove(c);
    }

}
