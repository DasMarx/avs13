/**
 * 
 */

package avs.game;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author HZU
 */
public class GameGrid implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -399936942981838923L;

    private final Cell[][] gameGrid;

    private HashSet<Cell> cellsPossessedByPlayer = new HashSet<Cell>();

    private HashSet<Cell> cellsPossessedByAI = new HashSet<Cell>();

    private final int gridSize = 30;

    Random r;

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final Lock read = readWriteLock.readLock();

    private final Lock write = readWriteLock.writeLock();

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
        read.lock();
        try {
            return new HashSet<Cell>(cellsPossessedByPlayer);
        } finally {
            read.unlock();
        }
    }

    public int getCellsPossessedByPlayerCount() {
        read.lock();
        try {
            return cellsPossessedByPlayer.size();
        } finally {
            read.unlock();
        }
    }

    public int getRating() {
        read.lock();
        try {
            return cellsPossessedByAI.size() - cellsPossessedByPlayer.size() * 100;
        } finally {
            read.unlock();
        }
    }

    /**
     * @return list of cells possessed by ai
     */
    public HashSet<Cell> getCellsPossessedByAI() {
        read.lock();
        try {
            return new HashSet<Cell>(cellsPossessedByAI);
        } finally {
            read.unlock();
        }
    }

    public int getCellsPossessedByAiCount() {
        read.lock();
        try {
            return cellsPossessedByAI.size();
        } finally {
            read.unlock();
        }
    }

    /**
     * initializes the grid
     */
    public void initialize() {
        r = new Random();
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
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                // g.gameGrid[i][j] = new Cell(i, j, gameGrid[i][j].getOwner(), gameGrid[i][j].getDirection());
                g.gameGrid[i][j] = new Cell(gameGrid[i][j]);
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
        write.lock();
        try {
            cellsPossessedByAI.remove(c);
            cellsPossessedByPlayer.add(c);
            getCell(c.getX(), c.getY()).setOwner(Attributes.PLAYER);
        } finally {
            write.unlock();
        }
    }

    /**
     * @param c cell to be added
     */
    public void addCellAI(Cell c) {
        write.lock();
        try {
            cellsPossessedByPlayer.remove(c);
            cellsPossessedByAI.add(c);
            getCell(c.getX(), c.getY()).setOwner(Attributes.AI);
        } finally {
            write.unlock();
        }
    }

    public void removeOwner(Cell c, int owner) {
        write.lock();
        try {
            cellsPossessedByPlayer.remove(c);
            cellsPossessedByAI.remove(c);
            getCell(c.getX(), c.getY()).setOwner(owner);
        } finally {
            write.unlock();
        }
    }

    /**
     * @param x coordinate of the cell to be turned
     * @param y coordinate of the cell to be turned
     * @return list of changes, mutating
     */
    public LinkedList<CellChange> processChanges(int x, int y, boolean afterChanges) {
        LinkedList<CellChange> changes = new LinkedList<CellChange>();
        Cell target = getCell(x, y);

        HashSet<Cell> processedCells = new HashSet<Cell>();
        if (afterChanges) {
            target.turn();
            changes.add(produceCellChange(target));
            processChangesAndSaveStateAfterProcessing(processedCells, target, target.getOwner(), changes);
        } else {
            changes.add(produceCellChange(target));
            target.turn();
            processChangesAndSaveStateBeforeProcessing(processedCells, target, target.getOwner(), changes);
        }

        return changes;
    }

    /**
     * @param target the Cell
     * @return list of changes, mutating
     */
    public LinkedList<CellChange> processChanges(Cell target, boolean afterChanges) {
        return processChanges(target.getX(), target.getY(), afterChanges);
    }

    /**
     * @param cellChange
     * @return
     */
    public LinkedList<CellChange> processChanges(CellChange cellChange, boolean afterChanges) {
        return processChanges(cellChange.getX(), cellChange.getY(), afterChanges);
    }

    /**
     * @param origin cell edited before
     * @param target cell to be edited
     * @param rs counter for the recursive step
     * @param owner of the cells
     */
    private void processChangesAndSaveStateAfterProcessing(HashSet<Cell> processedCells, Cell target, int owner, LinkedList<CellChange> changes) {

        if (processedCells.add(target)) {
            if (changeOwner(target, owner)) {
                changes.add(produceCellChange(target));
            }

            Cell nextNeighbour = getCell(target.getX(), target.getY() - 1);
            if (nextNeighbour != null && ((nextNeighbour.getDirection() == Attributes.DOWN) || target.getDirection() == Attributes.UP)) {
                processChangesAndSaveStateAfterProcessing(processedCells, nextNeighbour, owner, changes);
            }
            nextNeighbour = getCell(target.getX() + 1, target.getY());
            if (nextNeighbour != null && ((nextNeighbour.getDirection() == Attributes.LEFT) || target.getDirection() == Attributes.RIGHT)) {
                processChangesAndSaveStateAfterProcessing(processedCells, nextNeighbour, owner, changes);
            }
            nextNeighbour = getCell(target.getX(), target.getY() + 1);
            if (nextNeighbour != null && ((nextNeighbour.getDirection() == Attributes.UP) || target.getDirection() == Attributes.DOWN)) {
                processChangesAndSaveStateAfterProcessing(processedCells, nextNeighbour, owner, changes);
            }
            nextNeighbour = getCell(target.getX() - 1, target.getY());
            if (nextNeighbour != null && ((nextNeighbour.getDirection() == Attributes.RIGHT) || target.getDirection() == Attributes.LEFT)) {
                processChangesAndSaveStateAfterProcessing(processedCells, nextNeighbour, owner, changes);
            }

        }

    }

    /**
     * @param origin cell edited before
     * @param target cell to be edited
     * @param rs counter for the recursive step
     * @param owner of the cells
     */
    private void processChangesAndSaveStateBeforeProcessing(HashSet<Cell> processedCells, Cell target, int owner, LinkedList<CellChange> changes) {

        if (processedCells.add(target)) {
            final CellChange beforeChange = produceCellChange(target);
            if (changeOwner(target, owner)) {
                changes.add(beforeChange);
            }

            Cell nextNeighbour = getCell(target.getX(), target.getY() - 1);
            if (nextNeighbour != null && ((nextNeighbour.getDirection() == Attributes.DOWN) || target.getDirection() == Attributes.UP)) {
                processChangesAndSaveStateBeforeProcessing(processedCells, nextNeighbour, owner, changes);
            }
            nextNeighbour = getCell(target.getX() + 1, target.getY());
            if (nextNeighbour != null && ((nextNeighbour.getDirection() == Attributes.LEFT) || target.getDirection() == Attributes.RIGHT)) {
                processChangesAndSaveStateBeforeProcessing(processedCells, nextNeighbour, owner, changes);
            }
            nextNeighbour = getCell(target.getX(), target.getY() + 1);
            if (nextNeighbour != null && ((nextNeighbour.getDirection() == Attributes.UP) || target.getDirection() == Attributes.DOWN)) {
                processChangesAndSaveStateBeforeProcessing(processedCells, nextNeighbour, owner, changes);
            }
            nextNeighbour = getCell(target.getX() - 1, target.getY());
            if (nextNeighbour != null && ((nextNeighbour.getDirection() == Attributes.RIGHT) || target.getDirection() == Attributes.LEFT)) {
                processChangesAndSaveStateBeforeProcessing(processedCells, nextNeighbour, owner, changes);
            }

        }
    }

    /**
     * @param target cell to be edited
     * @param owner to be set
     */
    private boolean changeOwner(Cell target, int owner) {
        if (target.getOwner() == owner) {
            return false;
        }
        switch (owner) {
        case Attributes.PLAYER:
            addCellPlayer(target);
            break;
        case Attributes.AI:
            addCellAI(target);
            break;
        case Attributes.HOVER:
        default:
            removeOwner(target, owner);
        }
        return true;
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
