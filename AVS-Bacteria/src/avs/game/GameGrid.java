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
import static avs.game.Constants.*;

/**
 * @author HZU
 */
public class GameGrid implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -399936942981838923L;

    private final Cell[][] gameGrid;

    private final HashSet<Cell> cellsPossessedByPlayer;

    private final HashSet<Cell> cellsPossessedByAI;

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
        cellsPossessedByAI = new HashSet<Cell>();
        cellsPossessedByPlayer = new HashSet<Cell>();
        
    }

    public GameGrid(final GameGrid gameGrid2) {
        gameGrid = new Cell[gridSize][gridSize];
        for (int i = 0; i < gameGrid2.gridSize; i++) {
            for (int j = 0; j < gameGrid2.gridSize; j++) {
                // g.gameGrid[i][j] = new Cell(i, j, gameGrid[i][j].getOwner(), gameGrid[i][j].getDirection());
                gameGrid[i][j] = new Cell(gameGrid2.gameGrid[i][j]);
            }
        }
        cellsPossessedByAI = gameGrid2.getCellsPossessedByAI();
        cellsPossessedByPlayer = gameGrid2.getCellsPossessedByPlayer();
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
            return cellsPossessedByAI.size() - cellsPossessedByPlayer.size() * 9000;
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
    public Cell getCell(final int x, final int y) {
        if (x < 0 || y < 0 || x > gridSize - 1 || y > gridSize - 1)
            return null;
        return gameGrid[x][y];
    }

    /**
     * @return copy of the GameGrid
     */
    public GameGrid getCopy() {
        return new GameGrid(this);
    }

    /**
     * @param c cell to be added
     */
    public void addCellPlayer(final Cell c) {
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
    public void addCellAI(final Cell c) {
        write.lock();
        try {
            cellsPossessedByPlayer.remove(c);
            cellsPossessedByAI.add(c);
            getCell(c.getX(), c.getY()).setOwner(Attributes.AI);
        } finally {
            write.unlock();
        }
    }

    public void removeOwner(final Cell c, final int owner) {
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
    public LinkedList<CellChange> processChanges(final int x, final int y, final boolean saveChanges) {
        LinkedList<CellChange> changes = new LinkedList<CellChange>();
        Cell target = getCell(x, y);

        HashSet<Cell> processedCells = new HashSet<Cell>();
        if (saveChanges) {
            target.turn();
            changes.add(produceCellChange(target));
            processChanges(processedCells, target, target.getOwner(), changes, saveChanges);
        } else {
            changes.add(produceCellChange(target));
            target.turn();
            processChanges(processedCells, target, target.getOwner(), changes, saveChanges);
        }

        return changes;
    }

    /**
     * @param target the Cell
     * @return list of changes, mutating
     */
    public LinkedList<CellChange> processChanges(final Cell target, final boolean saveChanges) {
        return processChanges(target.getX(), target.getY(), saveChanges);
    }

    /**
     * @param cellChange
     * @return
     */
    public LinkedList<CellChange> processChanges(final CellChange cellChange, final boolean saveChanges) {
        return processChanges(cellChange.getX(), cellChange.getY(), saveChanges);
    }

    /**
     * @param processedCells
     * @param target
     * @param owner
     * @param changes
     * @param saveChanges
     */
    private void processChanges(final HashSet<Cell> processedCells, final Cell target, final int owner, final LinkedList<CellChange> changes, final boolean saveChanges) {

        if (processedCells.add(target)) {
            if (saveChanges) {
                changeOwner(target, owner);
                changes.add(produceCellChange(target));
            } else {
                changes.add(produceCellChange(target));
                changeOwner(target, owner);
            }

            createMoreWork(processedCells, target, owner, changes, saveChanges);

        }
    }

    /**
     * @param processedCells
     * @param target
     * @param owner
     * @param changes
     * @param saveChanges
     */
    private void createMoreWork(final HashSet<Cell> processedCells, final Cell target, final int owner, final LinkedList<CellChange> changes, final boolean saveChanges) {
        Cell nextNeighbour = getCell(target.getX(), target.getY() - 1);
        if (nextNeighbour != null && compareOwnerForDifference(target, nextNeighbour) && ((nextNeighbour.getDirection() == Attributes.DOWN) || target.getDirection() == Attributes.UP)) {
            processChanges(processedCells, nextNeighbour, owner, changes, saveChanges);
        }
        nextNeighbour = getCell(target.getX() + 1, target.getY());
        if (nextNeighbour != null && compareOwnerForDifference(target, nextNeighbour) && ((nextNeighbour.getDirection() == Attributes.LEFT) || target.getDirection() == Attributes.RIGHT)) {
            processChanges(processedCells, nextNeighbour, owner, changes, saveChanges);
        }
        nextNeighbour = getCell(target.getX(), target.getY() + 1);
        if (nextNeighbour != null && compareOwnerForDifference(target, nextNeighbour) && ((nextNeighbour.getDirection() == Attributes.UP) || target.getDirection() == Attributes.DOWN)) {
            processChanges(processedCells, nextNeighbour, owner, changes, saveChanges);
        }
        nextNeighbour = getCell(target.getX() - 1, target.getY());
        if (nextNeighbour != null && compareOwnerForDifference(target, nextNeighbour) && ((nextNeighbour.getDirection() == Attributes.RIGHT) || target.getDirection() == Attributes.LEFT)) {
            processChanges(processedCells, nextNeighbour, owner, changes, saveChanges);
        }
    }

    /**
     * @param cell
     * @param otherCell
     * @return
     */
    private boolean compareOwnerForDifference(final Cell cell, Cell otherCell) {
        if (USE_OPTIMIZATION_2) {
            return cell.getOwner() != otherCell.getOwner();
        } return true;
    }

    /**
     * @param target cell to be edited
     * @param owner to be set
     */
    private boolean changeOwner(final Cell target, final int owner) {
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

    public CellChange produceCellChange(final Cell cell) {
        return new CellChange(cell);
    }

    public void consumeCellChange(final CellChange cellChange) {
        Cell tmpCell = getCell(cellChange.getX(), cellChange.getY());
        tmpCell.setDirection(cellChange.getDirection());
        changeOwner(tmpCell, cellChange.getOwner());
    }

}
