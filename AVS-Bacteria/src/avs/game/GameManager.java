
package avs.game;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import avs.ai.AICore;
import avs.hazelcast.HazelcastWorker;
import avs.ui.UserInterface;

/**
 * @author HZU
 */

public class GameManager {

    private final UserInterface userInterface;

    // KI zur Berechnung der Spielzüge des Computers
    private final AICore aiCore;

    // Das Spielfeld
    private GameGrid gameGrid = new GameGrid();

    // Zeigt an, der wievielte Zug momentan läuft
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

    private HazelcastWorker myHazelcastWorker = new HazelcastWorker();

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
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        Thread.currentThread().setName("AVS:GameManager Thread");
        lastTime = System.currentTimeMillis();

        while (running) {
            // Akkumulator befüllen
            currentTime = System.currentTimeMillis();

            if ((currentTime - lastTime) > timeDelta) {
                timeAccumulator = (currentTime - lastTime);
                lastTime = currentTime;

            }

            while (timeAccumulator > timeDelta) {
                timeAccumulator -= timeDelta;
                timeRunning += timeDelta;
            }
            
            userInterface.setWork(aiCore.getWork());
            userInterface.setWorkDone(aiCore.getWorkDone());
            userInterface.setStats(myHazelcastWorker.getInstance().getExecutorService("default").getLocalExecutorStats());
            userInterface.setMemberStats(myHazelcastWorker.getInstance().getCluster().getMembers());
            
            if (!allChanges.isEmpty()) {
                LinkedList<CellChange> changes = gameGrid.processChanges(allChanges.removeFirst(),true);
                userInterface.updateGrid(changes);
                if (gameGrid.getCellsPossessedByAiCount() == 0 || gameGrid.getCellsPossessedByPlayerCount() == 0) {
                    aiCore.setRunning(false);
                }
                locked = false;
            }

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
        myHazelcastWorker.getInstance().getLifecycleService().shutdown();

    }


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
        if (locked) return false;
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

    /**
     * This method will return the current turn
     * @return
     */
    public int getTurn() {
        return turn.get();
    }

    public boolean isAIsTurn() {
        if (locked) return false;
        return !isPlayersTurn();
    }

    public HazelcastWorker getHazelCastWorker() {
        return myHazelcastWorker ;
    }
}
