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

    private LinkedList<Cell> cellsPossessedByPlayer;

    private LinkedList<Cell> cellsPossessedByAI;

    Random r;

    public LinkedList<Cell> getCellsPossessedByPlayer() {
        return cellsPossessedByPlayer;
    }

    public LinkedList<Cell> getCellsPossessedByAI() {
        return cellsPossessedByAI;
    }

    public void initialize() {
        r = new Random();
        EnumDirection direction = null;
        gameGrid = new Cell[29][29];
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                if ((i == 0) && (j == 0))
                    gameGrid[i][j] = new Cell(i, j, EnumOwner.PLAYER, EnumDirection.UP);
                if ((i == 29) && (j == 29))
                    gameGrid[i][j] = new Cell(i, j, EnumOwner.AI, EnumDirection.DOWN);
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

}
