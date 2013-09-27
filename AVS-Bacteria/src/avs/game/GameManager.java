
package avs.game;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import avs.ai.AICore;
import avs.ui.UserInterface;

/**
 * @author HZU
 */

public class GameManager {

    private final UserInterface userInterface;

    // KI zur Berechnung der Spielz�ge des Computers
    private final AICore aiCore;

    // Das Spielfeld
    private GameGrid gameGrid = new GameGrid();

    // Zeigt an, der wievielte Zug momentan l�uft
    private AtomicInteger turn = new AtomicInteger();

    public boolean running = true;

    long lastTime = 0;

    long currentTime = System.currentTimeMillis();

    long timeAccumulator = 0;

    final long timeDelta = 30;

    long timeRunning = 0;

    long sleepTime = 0;

    private boolean locked = false;
    
    private LinkedList<CellChange> allChanges = new LinkedList<CellChange>();

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
        this.aiCore.setGameGrid(gameGrid);
        new Thread(aiCore).start();
    }

    public void run() {
        lastTime = System.currentTimeMillis();

        while (running) {
            // Akkumulator bef�llen
            currentTime = System.currentTimeMillis();

            if ((currentTime - lastTime) > timeDelta) {
                timeAccumulator = (currentTime - lastTime);
                lastTime = currentTime;

            }

            while (timeAccumulator > timeDelta) {
                timeAccumulator -= timeDelta;
                timeRunning += timeDelta;
            }
            if (!allChanges.isEmpty()) {
                LinkedList<CellChange> changes = gameGrid.processChanges(allChanges.removeFirst());
                userInterface.updateGrid(changes);
                locked = false;
            }

            // TODO do the work

            sleepTime = lastTime - currentTime + timeDelta;
            if (sleepTime > 0) {
                try {
                    // System.out.println(sleepTime);
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

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
     * @param x
     * @param y
     * @param owner
     * @return true if turn is allowed, false if turn is forbidden
     */
    private boolean checkTurnAllowed(int x, int y, int owner) {
        return gameGrid.getCell(x, y).getOwner() == owner;
    }

    /**
     * @param x
     * @param y
     * @param owner
     * @return true = valid move, false invalid move
     */
    public boolean chooseCell(int x, int y, int owner) {
        if (!locked) {
            if (checkTurnAllowed(x, y, owner)) {
                locked = true;
                turn.incrementAndGet();
                allChanges.add(new CellChange(gameGrid.getCell(x, y), owner));
                return true;
            }
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
            GameGrid g = gameGrid.getCopy();
            return g;
        }
    }

    // /**
    // * @return last change
    // */
    // public LinkedList<CellChanges> getChanges() {
    // synchronized (allChanges) {
    // return allChanges.getLast();
    // }
    // }
    //
    // /**
    // * @param the turn whos changes are requested
    // * @return the changes of the specified turn
    // */
    // public LinkedList<CellChanges> getChanges(int turn){
    // synchronized (allChanges){
    // return allChanges.get(turn);
    // }
    // }
}
