
package avs.game;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import avs.ai.AICore;
import avs.ui.UserInterface;

/**
 * @author HZU
 * @param <E>
 */

public class GameManager<E> {

    private final UserInterface userInterface;

    // KI zur Berechnung der Spielzüge des Computers
    private final AICore aiCore;

    // Das Spielfeld
    private GameGrid gameGrid = new GameGrid();

    // Zeigt an, der wievielte Zug momentan läuft
    private  AtomicInteger turn = new AtomicInteger();

//    private LinkedList<LinkedList<CellChanges>> allChanges = new LinkedList<LinkedList<CellChanges>>();

    /**
     * Initializes a new {@link GameManager}.
     * 
     * @param userInterface of the game
     * @param aiCore of the game
     */
    public GameManager(UserInterface userInterface, AICore aiCore) {
        this.userInterface = userInterface;
        this.aiCore = aiCore;
        gameGrid.initialize();
        this.userInterface.initialize(this);
        this.aiCore.initialize(this);
        this.userInterface.setGameGrid(gameGrid);
        this.aiCore.setGameGrid(gameGrid);
        this.aiCore.run();
    }

    public void run() {

    }

    //
    // /**
    // * @param c cell to check
    // * @return list of the neighbours pointing at this cell
    // */
    // private LinkedList<Cell> checkNeighbour(Cell c) {
    // LinkedList<Cell> neighbours = new LinkedList<Cell>();
    // Cell nextNeighbour = gameGrid.getCell(c.getX(), c.getY() - 1);
    // if (nextNeighbour != null) {
    // if (nextNeighbour.getDirection() == EnumDirection.DOWN)
    // neighbours.add(nextNeighbour);
    // }
    // nextNeighbour = null;
    // nextNeighbour = gameGrid.getCell(c.getX() + 1, c.getY());
    // if (nextNeighbour != null) {
    // if (nextNeighbour.getDirection() == EnumDirection.LEFT)
    // neighbours.add(nextNeighbour);
    // }
    // nextNeighbour = null;
    // nextNeighbour = gameGrid.getCell(c.getX(), c.getY() + 1);
    // if (nextNeighbour != null) {
    // if (nextNeighbour.getDirection() == EnumDirection.UP)
    // neighbours.add(nextNeighbour);
    // }
    // nextNeighbour = null;
    // nextNeighbour = gameGrid.getCell(c.getX() - 1, c.getY());
    // if (nextNeighbour != null) {
    // if (nextNeighbour.getDirection() == EnumDirection.RIGHT)
    // neighbours.add(nextNeighbour);
    // }
    //
    // return neighbours;
    // }

    /**
     * @param x coordinate of the cell to be turned
     * @param y coordinate of the cell to be turned
     * @return list of changes, mutating
     */
    public LinkedList<CellChanges> processChanges(int x, int y) {
        LinkedList<CellChanges> changes = new LinkedList<CellChanges>();
        Cell target = null;
        synchronized (gameGrid) {
            target = gameGrid.getCell(x, y);
            target.turn();
        }
        
//        Cell nextNeighbour = gameGrid.getCell(x, y - 1);
        HashSet<Cell> processedCells = new HashSet<Cell>();
        processChanges(processedCells, target, target.getOwner(), changes);
//        processedCells.add(target);
//        if (nextNeighbour != null && ((nextNeighbour.getDirection() == Attributes.DOWN) || target.getDirection() == Attributes.UP)) {
//            processChanges(processedCells, nextNeighbour, target.getOwner(), changes);
//        }
//        nextNeighbour = gameGrid.getCell(x + 1, y);
//        if (nextNeighbour != null && ((nextNeighbour.getDirection() == Attributes.LEFT) || target.getDirection() == Attributes.RIGHT)) {
//            processChanges(processedCells, nextNeighbour, target.getOwner(), changes);
//        }
//        nextNeighbour = gameGrid.getCell(x, y + 1);
//        if (nextNeighbour != null && ((nextNeighbour.getDirection() == Attributes.UP) || target.getDirection() == Attributes.DOWN)) {
//            processChanges(processedCells, nextNeighbour, target.getOwner(), changes);
//        }
//        nextNeighbour = gameGrid.getCell(x - 1, y);
//        if (nextNeighbour != null && ((nextNeighbour.getDirection() == Attributes.RIGHT) || target.getDirection() == Attributes.LEFT)) {
//            processChanges(processedCells, nextNeighbour, target.getOwner(), changes);
//        }
        return changes;
    }

    /**
     * @param origin cell edited before
     * @param target cell to be edited
     * @param rs counter for the recursive step
     * @param owner of the cells
     */
    private void processChanges(HashSet<Cell> processedCells, Cell target, int owner, LinkedList<CellChanges> changes) {

        if(processedCells.add(target)){
            Cell nextNeighbour = gameGrid.getCell(target.getX(), target.getY() - 1);
            if (nextNeighbour != null && ((nextNeighbour.getDirection() == Attributes.DOWN) || target.getDirection() == Attributes.UP)) {
                processChanges(processedCells, nextNeighbour, owner, changes);
            }
            nextNeighbour = gameGrid.getCell(target.getX() + 1, target.getY());
            if (nextNeighbour != null && ((nextNeighbour.getDirection() == Attributes.LEFT) || target.getDirection() == Attributes.RIGHT)) {
                processChanges(processedCells, nextNeighbour, owner, changes);
            }
            nextNeighbour = gameGrid.getCell(target.getX(), target.getY() + 1);
            if (nextNeighbour != null && ((nextNeighbour.getDirection() == Attributes.UP) || target.getDirection() == Attributes.DOWN)) {
                processChanges(processedCells, nextNeighbour, owner, changes);
            }
            nextNeighbour = gameGrid.getCell(target.getX() - 1, target.getY());
            if (nextNeighbour != null && ((nextNeighbour.getDirection() == Attributes.RIGHT) || target.getDirection() == Attributes.LEFT)) {
                processChanges(processedCells, nextNeighbour, owner, changes);
            }

            changeOwner(target, owner);
            changes.add(new CellChanges(target));
        }
        
    }

    /**
     * @param target cell to be edited
     * @param owner to be set
     */
    private void changeOwner(Cell target, int owner) {
        gameGrid.getCell(target.getX(), target.getY()).setOwner(owner);
        if (owner == Attributes.PLAYER)
            gameGrid.addCellPlayer(target);
        else
            gameGrid.addCellAI(target);
    }

    /**
     * @param x
     * @param y
     * @param owner
     * @return true if turn is allowed, false if turn is forbidden
     */
    private boolean checkTurn(int x, int y, int owner) {
        return gameGrid.getCell(x, y).getOwner() == owner;
    }

    /**
     * @param x
     * @param y
     * @param owner
     * @return true = valid move, false invalid move
     */
    public boolean chooseCell(int x, int y, int owner) {
            if (checkTurn(x, y, owner)) {
                LinkedList<CellChanges> changes = processChanges(x, y);
//                allChanges.add(changes);
//                userInterface.updateGrid(changes);
//                aiCore.updateGrid(changes);
                turn.incrementAndGet();

                return true;
            }
        return false;
    }

    /**
     * @return true = player turn, false = ai turn
     */
    public boolean isPlayersTurn() {
        return (turn.get() % 2) == 0;
    }

    /**
     * @return the Game Grid
     */
    public GameGrid getGrid() {
        synchronized (gameGrid) {
            GameGrid g = gameGrid;
            return g;
        }
    }

//    /**
//     * @return last change
//     */
//    public LinkedList<CellChanges> getChanges() {
//        synchronized (allChanges) {
//            return allChanges.getLast();
//        }
//    }
//    
//    /**
//     * @param the turn whos changes are requested
//     * @return the changes of the specified turn
//     */
//    public LinkedList<CellChanges> getChanges(int turn){
//        synchronized (allChanges){
//            return allChanges.get(turn);
//        }
//    }
}
