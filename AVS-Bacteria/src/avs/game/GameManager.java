
package avs.game;

import java.util.LinkedList;
import avs.ai.AICore;
import avs.ui.UserInterface;

/**
 * @author HZU
 */

public class GameManager {

    private final UserInterface userInterface;

    private final AICore aiCore;

    private GameGrid gameGrid = new GameGrid();

    private int turn = 0;

    private LinkedList<LinkedList<CellChanges>> allChanges = new LinkedList<LinkedList<CellChanges>>();

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
        this.userInterface.setGameGrid(gameGrid.getCopy());
        this.aiCore.setGameGrid(gameGrid.getCopy());
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

        Cell nextNeighbour = gameGrid.getCell(x, y - 1);
        if (nextNeighbour != null && ((nextNeighbour.getDirection() == EnumDirection.DOWN) || target.getDirection() == EnumDirection.UP)) {
            processChanges(target, nextNeighbour, 0, target.getOwner(), changes);
        }
        nextNeighbour = gameGrid.getCell(x + 1, y);
        if (nextNeighbour != null && ((nextNeighbour.getDirection() == EnumDirection.LEFT) || target.getDirection() == EnumDirection.RIGHT)) {
            processChanges(target, nextNeighbour, 0, target.getOwner(), changes);
        }
        nextNeighbour = gameGrid.getCell(x, y + 1);
        if (nextNeighbour != null && ((nextNeighbour.getDirection() == EnumDirection.UP) || target.getDirection() == EnumDirection.DOWN)) {
            processChanges(target, nextNeighbour, 0, target.getOwner(), changes);
        }
        nextNeighbour = gameGrid.getCell(x - 1, y);
        if (nextNeighbour != null && ((nextNeighbour.getDirection() == EnumDirection.RIGHT) || target.getDirection() == EnumDirection.LEFT)) {
            processChanges(target, nextNeighbour, 0, target.getOwner(), changes);
        }
        return changes;
    }

    /**
     * @param origin cell edited before
     * @param target cell to be edited
     * @param rs counter for the recursive step
     * @param owner of the cells
     */
    private void processChanges(Cell origin, Cell target, int rs, EnumOwner owner, LinkedList<CellChanges> changes) {

        Cell nextNeighbour = gameGrid.getCell(target.getX(), target.getY() - 1);
        if (nextNeighbour != origin && nextNeighbour != null && ((nextNeighbour.getDirection() == EnumDirection.DOWN) || target.getDirection() == EnumDirection.UP)) {
            processChanges(target, nextNeighbour, rs + 1, owner, changes);
        }
        nextNeighbour = gameGrid.getCell(target.getX() + 1, target.getY());
        if (nextNeighbour != origin && nextNeighbour != null && ((nextNeighbour.getDirection() == EnumDirection.LEFT) || target.getDirection() == EnumDirection.RIGHT)) {
            processChanges(target, nextNeighbour, rs + 1, owner, changes);
        }
        nextNeighbour = gameGrid.getCell(target.getX(), target.getY() + 1);
        if (nextNeighbour != origin && nextNeighbour != null && ((nextNeighbour.getDirection() == EnumDirection.UP) || target.getDirection() == EnumDirection.DOWN)) {
            processChanges(target, nextNeighbour, rs + 1, owner, changes);
        }
        nextNeighbour = gameGrid.getCell(target.getX() - 1, target.getY());
        if (nextNeighbour != origin && nextNeighbour != null && ((nextNeighbour.getDirection() == EnumDirection.RIGHT) || target.getDirection() == EnumDirection.LEFT)) {
            processChanges(target, nextNeighbour, rs + 1, owner, changes);
        }

        changeOwner(target, owner);
        changes.add(new CellChanges(target, rs));
    }

    /**
     * @param target cell to be edited
     * @param owner to be set
     */
    private void changeOwner(Cell target, EnumOwner owner) {
        gameGrid.removeCell(target);
        gameGrid.getCell(target.getX(), target.getY()).setOwner(owner);
        if (owner == EnumOwner.PLAYER)
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
    private boolean checkTurn(int x, int y, EnumOwner owner) {
        Cell c1 = new Cell(x, y, owner, EnumDirection.UP);
        Cell c2 = gameGrid.getCell(x, y);
        return c1.getOwner() == c2.getOwner();
    }

    /**
     * @param x
     * @param y
     * @param owner
     * @return list of changes
     */
    public LinkedList<CellChanges> chooseCell(int x, int y, EnumOwner owner) {
        if ((isPlayersTurn() && (owner == EnumOwner.PLAYER)) || (!isPlayersTurn() && (owner == EnumOwner.AI))) {
            if (checkTurn(x, y, owner)) {
                LinkedList<CellChanges> changes = processChanges(x, y);
                allChanges.add(changes);

                return changes;

            }
        }
        return null;
    }

    /**
     * @return true = player turn, false = ai turn
     */
    public boolean isPlayersTurn() {
        boolean playersTurn = false;
        if ((turn % 2) == 0)
            playersTurn = true;
        return playersTurn;
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

    /**
     * @return all changes
     */
    public LinkedList<CellChanges> getChanges() {
        synchronized (allChanges) {
            return allChanges.getLast();
        }
    }
}
