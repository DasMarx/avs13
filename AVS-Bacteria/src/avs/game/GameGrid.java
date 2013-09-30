/**
 * 
 */

package avs.game;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author HZU
 */
public class GameGrid implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -399936942981838923L;

    private Cell[][] gameGrid;

    private ConcurrentHashMap<Cell,Boolean> cellsPossedByPlayerMap = new ConcurrentHashMap<Cell,Boolean>();
    private Set<Cell> cellsPossessedByPlayer = Collections.newSetFromMap(cellsPossedByPlayerMap);

    private ConcurrentHashMap<Cell,Boolean> cellsPossessedByAIMap = new ConcurrentHashMap<Cell,Boolean>();
    private Set<Cell> cellsPossessedByAI = Collections.newSetFromMap(cellsPossessedByAIMap);

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
    public HashSet<Cell> getCellsPossessedByPlayer() {
            return new HashSet<Cell>(cellsPossessedByPlayer);
    }

    public int getCellsPossessedByPlayerCount() {
            return cellsPossessedByPlayer.size();
    }

    /**
     * @return list of cells possessed by ai
     */
    public HashSet<Cell> getCellsPossessedByAI() {
            return new HashSet<Cell>(cellsPossessedByAI);
    }

    public int getCellsPossessedByAiCount() {
            return cellsPossessedByAI.size();
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
                if ((i == gridSize - 1) && (j == gridSize - 1)) {
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
        if (x < 0 || y < 0 || x > gridSize - 1 || y > gridSize - 1)
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
                g.gameGrid[i][j] = new Cell(i, j, gameGrid[i][j].getOwner(), gameGrid[i][j].getDirection());
            }
        }
        g.cellsPossessedByAI = getCellsPossessedByAI();
        g.cellsPossessedByPlayer = getCellsPossessedByPlayer();
        return g;
    }

    /**
     * @param c cell to be added
     */
    public void addCellPlayer(Cell c) {
            cellsPossessedByAI.remove(c);
            cellsPossessedByPlayer.add(c);
    }

    /**
     * @param c cell to be added
     */
    public void addCellAI(Cell c) {
            cellsPossessedByPlayer.remove(c);
            cellsPossessedByAI.add(c);
    }

    /**
     * @param x coordinate of the cell to be turned
     * @param y coordinate of the cell to be turned
     * @return list of changes, mutating
     */
    public LinkedList<CellChange> processChanges(int x, int y, boolean trackChanges) {
        LinkedList<CellChange> changes = new LinkedList<CellChange>();
        Cell target = getCell(x, y);
        target.turn();
        HashSet<Cell> processedCells = new HashSet<Cell>();
        processChanges(processedCells, target, target.getOwner(), changes, trackChanges);
        return changes;
    }

    /**
     * @param target the Cell
     * @return list of changes, mutating
     */
    public LinkedList<CellChange> processChanges(Cell target, boolean trackChanges) {
        return processChanges(target.getX(), target.getY(), trackChanges);
    }

    /**
     * @param cellChange
     * @return
     */
    public LinkedList<CellChange> processChanges(CellChange cellChange, boolean trackChanges) {
        return processChanges(cellChange.getX(), cellChange.getY(), trackChanges);
    }

    /**
     * @param origin cell edited before
     * @param target cell to be edited
     * @param rs counter for the recursive step
     * @param owner of the cells
     */
    private void processChanges(HashSet<Cell> processedCells, Cell target, int owner, LinkedList<CellChange> changes, boolean trackChanges) {

        if (processedCells.add(target)) {
            changeOwner(target, owner);
            if (trackChanges) {
                changes.add(produceCellChange(target));
            }

            Cell nextNeighbour = getCell(target.getX(), target.getY() - 1);
            if (nextNeighbour != null && ((nextNeighbour.getDirection() == Attributes.DOWN) || target.getDirection() == Attributes.UP)) {
                processChanges(processedCells, nextNeighbour, owner, changes, trackChanges);
            }
            nextNeighbour = getCell(target.getX() + 1, target.getY());
            if (nextNeighbour != null && ((nextNeighbour.getDirection() == Attributes.LEFT) || target.getDirection() == Attributes.RIGHT)) {
                processChanges(processedCells, nextNeighbour, owner, changes, trackChanges);
            }
            nextNeighbour = getCell(target.getX(), target.getY() + 1);
            if (nextNeighbour != null && ((nextNeighbour.getDirection() == Attributes.UP) || target.getDirection() == Attributes.DOWN)) {
                processChanges(processedCells, nextNeighbour, owner, changes, trackChanges);
            }
            nextNeighbour = getCell(target.getX() - 1, target.getY());
            if (nextNeighbour != null && ((nextNeighbour.getDirection() == Attributes.RIGHT) || target.getDirection() == Attributes.LEFT)) {
                processChanges(processedCells, nextNeighbour, owner, changes, trackChanges);
            }

        }

    }

    /**
     * @param target cell to be edited
     * @param owner to be set
     */
    private void changeOwner(Cell target, int owner) {
        getCell(target.getX(), target.getY()).setOwner(owner);
        if (owner == Attributes.PLAYER)
            addCellPlayer(target);
        else
            addCellAI(target);
    }

    public CellChange produceCellChange(Cell cell) {
        return new CellChange(cell);
    }

    public void consumeCellChange(CellChange cellChange) {
        Cell tmpCell = getCell(cellChange.getX(), cellChange.getY());
        tmpCell.setDirection(cellChange.getDirection());
        changeOwner(tmpCell, cellChange.getOwner());
    }

}
