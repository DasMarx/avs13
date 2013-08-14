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

    Random r;

    /**
     * Initializes a new {@link GameGrid}.
     */
    public GameGrid() {
        gameGrid = new Cell[30][30];
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
        EnumDirection direction = null;
        gameGrid = new Cell[30][30];
        for (int i = 0; i < gameGrid.length; i++) {
            for (int j = 0; j < gameGrid.length; j++) {
                if ((i == 0) && (j == 0)) {
                    gameGrid[i][j] = new Cell(i, j, EnumOwner.PLAYER, EnumDirection.UP);
                    cellsPossessedByPlayer.add(getCell(i, j));
                }
                if ((i == 29) && (j == 29)) {
                    gameGrid[i][j] = new Cell(i, j, EnumOwner.AI, EnumDirection.DOWN);
                    cellsPossessedByAI.add(getCell(i, j));
                }
                switch (r.nextInt(4)) {
                case 0:
                    direction = EnumDirection.UP;
                    break;
                case 1:
                    direction = EnumDirection.RIGHT;
                    break;
                case 2:
                    direction = EnumDirection.DOWN;
                    break;
                case 3:
                    direction = EnumDirection.LEFT;
                    break;
                }
                gameGrid[i][j] = new Cell(i, j, EnumOwner.NEUTRAL, direction);
            }
        }

    }

    /**
     * @return length & height of the grid
     */
    public int getLength() {
        return gameGrid.length;
    }

    /**
     * @param x coordinate
     * @param y coordinate
     * @return the cell
     */
    public Cell getCell(int x, int y) {
        if (x < 0 || y < 0 || x > 29 || y > 29)
            return null;
        return gameGrid[x][y];
    }

    /**
     * @return copy of the GameGrid
     */
    public GameGrid getCopy() {
        GameGrid g = new GameGrid();
        for (int i = 0; i < gameGrid.length; i++) {
            for (int j = 0; j < gameGrid.length; j++) {
                g.gameGrid[i][j] = gameGrid[i][j];
            }
        }
        g.cellsPossessedByAI = cellsPossessedByAI;
        g.cellsPossessedByPlayer = cellsPossessedByPlayer;
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
