
package avs.ai;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import avs.game.Attributes;
import avs.game.Cell;
import avs.game.GameGrid;
import avs.game.GameManager;
import avs.hazelcast.HazelcastWorker;
import avs.hazelcast.WorkLoadReturn;
import com.hazelcast.core.IExecutorService;

public class AICore implements Runnable {

    private GameManager gm;

    private boolean running = true;

    // private LinkedList<Future<WorkLoadReturn>> futureQueue = new LinkedList<Future<WorkLoadReturn>>();

    private GameGrid grid;

    private HazelcastWorker myWorker;

    private IExecutorService executorService;

    private boolean ProducerStillRunning = false;

    public void initialize(GameManager gm) {
        this.setGm(gm);
        setMyWorker(new HazelcastWorker());
    }

    public void setGameGrid(GameGrid grid) {
        this.setGrid(grid);

    }

    public void run() {
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        setExecutorService(getMyWorker().getInstance().getExecutorService("default"));
        while (isRunning()) {
            if (getGm().isAIsTurn()) {

                System.out.println("=== new Round ====");
                long startTime = System.currentTimeMillis();

                // Creating shared object
                BlockingQueue<WorkLoadReturn> futureQueue = new LinkedBlockingQueue<WorkLoadReturn>();
                AtomicInteger concurrentExecution = new AtomicInteger(0);
                // Creating Producer and Consumer Thread
                Thread prodThread = new Thread(new Producer(futureQueue, this,concurrentExecution));

                ProducerStillRunning = true;
                int THREAD_COUNT = 6;

                Consumer[] consumerArray = new Consumer[THREAD_COUNT];
                Thread[] consumerThreadArray = new Thread[THREAD_COUNT];
                for (int i = 0; i < THREAD_COUNT; i++) {
                    consumerArray[i] = new Consumer(futureQueue,this,concurrentExecution);
                    consumerThreadArray[i] = new Thread(consumerArray[i]);
                }

                // Starting producer and Consumer thread
                prodThread.start();
                for (int i = 0; i < THREAD_COUNT; i++) {
                    consumerThreadArray[i].start();
                }

                try {
                    prodThread.join();
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                ProducerStillRunning = false;
                for (int i = 0; i < THREAD_COUNT; i++) {
                    try {
                        consumerThreadArray[i].join();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                long endTime = System.currentTimeMillis();
                long calcTime = endTime - startTime;
                int calc = 0;
                for (int i = 0; i < THREAD_COUNT; i++) {
                    calc += consumerArray[i].getCounter();
                }
                long calcPerSec = (calc / calcTime);

                WorkLoadReturn bestReturned = null;
                for (int i = 0; i < THREAD_COUNT; i++) {
                    if (null != consumerArray[i].getInternalReturn()) {
                        if (null == bestReturned) {
                            bestReturned = consumerArray[i].getInternalReturn();
                        }
                        if (bestReturned.getRating() < consumerArray[i].getInternalReturn().getRating()) {
                            bestReturned = consumerArray[i].getInternalReturn();
                        }
                    }
                }
                
                for (int i = 0; i < THREAD_COUNT; i++) {
                    System.out.print(consumerArray[i].getCounter() + " ");
                }
                System.out.println("");
                System.out.println("futureQueue " + futureQueue.size() + " concurrentExecution " + concurrentExecution.get());

                System.out.println("done " + calc + " calculations in " + (calcTime) + " ms which is " + calcPerSec + " calc/ms");
                if (bestReturned == null) {
                    LinkedList<Cell> ownedCells = new LinkedList<Cell>(grid.getCellsPossessedByAI());
                    int size = ownedCells.size();
                    int chosenCell = new Random().nextInt(size);
                    int nextTurnX = ownedCells.get(chosenCell).getX();
                    int nextTurnY = ownedCells.get(chosenCell).getY();
                    gm.chooseCell(nextTurnX, nextTurnY, Attributes.AI);
                } else {
                    System.out.println("Best choice is: " + bestReturned.getInitialX() + ":" + bestReturned.getInitialY());
                    gm.chooseCell(bestReturned.getInitialX(), bestReturned.getInitialY(), Attributes.AI);
                }

            } else {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            // collect and merge result trees
            // get best result
            // execute turn
            // discard obsolete nodes
            // get grid from leaves
            // get cells from grids

        }

    }

    public boolean ProducerStillRunning() {
        return ProducerStillRunning;
    }

    public GameGrid getGrid() {
        return grid;
    }

    public void setGrid(GameGrid grid) {
        this.grid = grid;
    }

    public IExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(IExecutorService executorService) {
        this.executorService = executorService;
    }

    public GameManager getGm() {
        return gm;
    }

    public void setGm(GameManager gm) {
        this.gm = gm;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public HazelcastWorker getMyWorker() {
        return myWorker;
    }

    public void setMyWorker(HazelcastWorker myWorker) {
        this.myWorker = myWorker;
    }
}
