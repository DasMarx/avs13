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

    private int[][][] intGameGrid;
    
//    private Cell[][] gameGrid;

//    private ConcurrentHashMap<Cell, Boolean> cellsPossedByPlayerMap = new ConcurrentHashMap<Cell, Boolean>();

//    private Set<Cell> cellsPossessedByPlayer = Collections.newSetFromMap(cellsPossedByPlayerMap);
    private HashSet<Cell> cellsPossessedByPlayer = new HashSet<Cell>();

//    private ConcurrentHashMap<Cell, Boolean> cellsPossessedByAIMap = new ConcurrentHashMap<Cell, Boolean>();

//    private Set<Cell> cellsPossessedByAI = Collections.newSetFromMap(cellsPossessedByAIMap);
    private HashSet<Cell> cellsPossessedByAI = new HashSet<Cell>();

    private static final int gridSize = 30;

    Random r;

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final Lock read = readWriteLock.readLock();

    private final Lock write = readWriteLock.writeLock();

    /**
     * Initializes a new {@link GameGrid}.
     */
    public GameGrid() {
        intGameGrid = new int[gridSize][gridSize][2];
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
//        gameGrid = new Cell[gridSize][gridSize];
//        for (int i = 0; i < gameGrid.length; i++) {
//            for (int j = 0; j < gameGrid.length; j++) {
//                gameGrid[i][j] = new Cell(i, j, Attributes.NEUTRAL, r.nextInt(4));
//                if ((i == 0) && (j == 0)) {
//                    gameGrid[i][j] = new Cell(i, j, Attributes.PLAYER, Attributes.UP);
//                    cellsPossessedByPlayer.add(getCell(i, j));
//                }
//                if ((i == gridSize - 1) && (j == gridSize - 1)) {
//                    gameGrid[i][j] = new Cell(i, j, Attributes.AI, Attributes.DOWN);
//                    cellsPossessedByAI.add(getCell(i, j));
//                }
//            }
//        }
        intGameGrid = new int[gridSize][gridSize][2];
        for (int i = 0; i < intGameGrid.length; i++) {
            for (int j = 0; j < intGameGrid.length; j++) {
                setOwnerForCell(i, j, Attributes.NEUTRAL);
                setDirectionForCell(i, j, r.nextInt(4));
//                    new Cell(i, j, Attributes.NEUTRAL, r.nextInt(4));
                if ((i == 0) && (j == 0)) {
                    setOwnerForCell(i, j, Attributes.PLAYER);
                    setDirectionForCell(i, j, Attributes.UP);
                    cellsPossessedByPlayer.add(getCell(i, j));
                }
                if ((i == gridSize - 1) && (j == gridSize - 1)) {
                    setOwnerForCell(i, j, Attributes.AI);
                    setDirectionForCell(i, j,  Attributes.DOWN);
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
        return new Cell(x,y);
    }

    /**
     * @return copy of the GameGrid
     */
    public GameGrid getCopy() {
        GameGrid g = new GameGrid();
        for (int x = 0; x < intGameGrid.length; x++) {
            for (int y = 0; y < intGameGrid.length; y++) {
                g.setDirectionForCell(x, y, getDirectionForCell(x, y));
                g.setOwnerForCell(x, y, getOwnerForCell(x, y));
//                g.intGameGrid[x][y] = intGameGrid[x][y].clone();
            }
        }
//        g.intGameGrid = intGameGrid.clone();
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
        } finally {
            write.unlock();
        }
    }

    /**
     * @param x coordinate of the cell to be turned
     * @param y coordinate of the cell to be turned
     * @return list of changes, mutating
     */
    public LinkedList<CellChange> processChanges(int x, int y, boolean trackChanges) {
        LinkedList<CellChange> changes = new LinkedList<CellChange>();
        Cell target = getCell(x, y);
        turn(x,y);
//        target.turn();
        HashSet<Cell> processedCells = new HashSet<Cell>();
        processChanges(processedCells, target, getOwnerForCell(x, y), changes, trackChanges);
        return changes;
    }

    private void turn(int x, int y) {
        intGameGrid[x][y][1] = (intGameGrid[x][y][1] + 1 ) % 4;
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
            if (nextNeighbour != null && ((getDirectionForCell(nextNeighbour.getX(), nextNeighbour.getY()) == Attributes.DOWN) || getDirectionForCell(target.getX(), target.getY()) == Attributes.UP)) {
                processChanges(processedCells, nextNeighbour, owner, changes, trackChanges);
            }
            nextNeighbour = getCell(target.getX() + 1, target.getY());
            if (nextNeighbour != null && ((getDirectionForCell(nextNeighbour.getX(), nextNeighbour.getY()) == Attributes.LEFT) || getDirectionForCell(target.getX(), target.getY()) == Attributes.RIGHT)) {
                processChanges(processedCells, nextNeighbour, owner, changes, trackChanges);
            }
            nextNeighbour = getCell(target.getX(), target.getY() + 1);
            if (nextNeighbour != null && ((getDirectionForCell(nextNeighbour.getX(), nextNeighbour.getY()) == Attributes.UP) || getDirectionForCell(target.getX(), target.getY()) == Attributes.DOWN)) {
                processChanges(processedCells, nextNeighbour, owner, changes, trackChanges);
            }
            nextNeighbour = getCell(target.getX() - 1, target.getY());
            if (nextNeighbour != null && ((getDirectionForCell(nextNeighbour.getX(), nextNeighbour.getY()) == Attributes.RIGHT) || getDirectionForCell(target.getX(), target.getY()) == Attributes.LEFT)) {
                processChanges(processedCells, nextNeighbour, owner, changes, trackChanges);
            }

        }

    }

    /**
     * @param target cell to be edited
     * @param owner to be set
     */
    private void changeOwner(Cell target, int owner) {
        intGameGrid[target.getX()][target.getY()][0] = owner;
        if (owner == Attributes.PLAYER)
            addCellPlayer(target);
        else
            addCellAI(target);
    }

    public CellChange produceCellChange(Cell cell) {
        return new CellChange(cell,getOwnerForCell(cell.getX(),cell.getY()),getDirectionForCell(cell.getX(),cell.getY()));
    }

    public void consumeCellChange(CellChange cellChange) {
        Cell tmpCell = getCell(cellChange.getX(), cellChange.getY());
        intGameGrid[cellChange.getX()][cellChange.getY()][1] = cellChange.getDirection();
        changeOwner(tmpCell, cellChange.getOwner());
    }
    
    public void setOwnerForCell(int x, int y,int owner) {
        intGameGrid[x][y][0] = owner;
    }
    
    public void setDirectionForCell(int x, int y,int direction) {
        intGameGrid[x][y][1] = direction;
    }

    /**
     * this method will return the owner of a field;
     * @param x
     * @param y
     * @return the owner
     */
    public int getOwnerForCell(int x, int y) {
        return intGameGrid[x][y][0];
    }
    
    /**
     * this method will return the direction of a field;
     * @param x
     * @param y
     * @return the direction
     */
    public int getDirectionForCell(int x, int y) {
        return intGameGrid[x][y][1];
    }
    
}
