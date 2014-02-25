
package avs.ai;

import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import avs.game.Attributes;
import avs.game.Cell;
import avs.game.GameGrid;
import avs.game.GameManager;
import avs.hazelcast.HazelcastInstanceImpl;
import avs.hazelcast.WorkLoadReturn;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.Member;
import static avs.game.Constants.*;

public class AICore implements Runnable {

    private GameManager gm;

    private boolean running = true;

    // private LinkedList<Future<WorkLoadReturn>> futureQueue = new LinkedList<Future<WorkLoadReturn>>();

    private GameGrid grid;

    private IExecutorService executorService;

    private boolean ProducerStillRunning = false;

    public void initialize(GameManager gm) {
        this.setGm(gm);
    }

    public void setGameGrid(GameGrid grid) {
        this.setGrid(grid);
    }

    private int work = 0, workDone = 0;

    @SuppressWarnings("unused")
    public void run() {
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        setExecutorService(HazelcastInstanceImpl.getInstance().getExecutorService("default"));

        while (isRunning()) {

            if (getGm().isAIsTurn()) {

                if (LOGGING) {
                    System.out.println("=== new Round ====");
                }
                long startTime = System.currentTimeMillis();

                // Creating shared object
                final BlockingQueue<Callable<WorkLoadReturn>> workQueue = new LinkedBlockingQueue<Callable<WorkLoadReturn>>(FULL_SEMAPHORE_COUNT);
                final Semaphore semaphore = new Semaphore(FULL_SEMAPHORE_COUNT, true);

                // Creating Producer and Consumer Thread
                final Thread prodThread = new Thread(new Producer(workQueue, this));

                ProducerStillRunning = true;

                Set<Member> members = HazelcastInstanceImpl.getInstance().getCluster().getMembers();
                if (members.size() >= 2) {
                    members.remove(HazelcastInstanceImpl.getInstance().getCluster().getLocalMember());
                }
                final Member[] memberArray = new Member[members.size()];
                final Semaphore[] semaphoreArray = new Semaphore[members.size()];

                int index = 0;
                for (Member m : members) {
                    memberArray[index] = m;
                    semaphoreArray[index] = new Semaphore(SEMAPHORE_COUNT_WORK_FOR_EACH_MEMBER);
                    index++;
                }

                int consumerThreadCount = CONSUMER_THREAD_COUNT_PLUS_MEMBER + members.size();
                final Consumer[] consumerArray = new Consumer[consumerThreadCount];
                final Thread[] consumerThreadArray = new Thread[consumerThreadCount];
                for (int i = 0; i < consumerThreadCount; i++) {
                    consumerArray[i] = new Consumer(workQueue, this, semaphore, memberArray, semaphoreArray);
                    consumerThreadArray[i] = new Thread(consumerArray[i]);
                }

                // Starting producer and Consumer thread
                prodThread.start();
                for (int i = 0; i < consumerThreadCount; i++) {
                    consumerThreadArray[i].start();
                }

                try {
                    prodThread.join();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                if (LOGGING) {
                    System.out.println("Producer finished");
                }

                ProducerStillRunning = false;
                for (int i = 0; i < consumerThreadCount; i++) {
                    try {
                        consumerThreadArray[i].join();
                        if (LOGGING) {
                            System.out.println("Consumer " + i + " finished");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    semaphore.acquire(FULL_SEMAPHORE_COUNT);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                final long endTime = System.currentTimeMillis();
                final long calcTime = endTime - startTime;
                int calc = 0;
                for (int i = 0; i < consumerThreadCount; i++) {
                    calc += consumerArray[i].getCounter();
                }
                final long calcPerSec = (calc / calcTime);

                WorkLoadReturn bestReturned = null;
                for (int i = 0; i < consumerThreadCount; i++) {
                    if (null != consumerArray[i].getInternalReturn()) {
                        if (null == bestReturned) {
                            bestReturned = consumerArray[i].getInternalReturn();
                        }
                        if (bestReturned.getRating() < consumerArray[i].getInternalReturn().getRating()) {
                            bestReturned = consumerArray[i].getInternalReturn();
                        }
                    }
                }

                if (LOGGING) {
                    for (int i = 0; i < consumerThreadCount; i++) {
                        System.out.print(consumerArray[i].getCounter() + " ");
                    }
                    System.out.println("");
                    System.out.println(" workQueue " + workQueue.size());
                    System.out.println("done " + calc + " calculations in " + calcTime + " ms which is " + calcPerSec + " calc/ms");
                }
                if (!LOGGING && MORE_LOGGING) {
                    System.out.println(calc + " " + calcTime);
                }

                if (bestReturned == null) {
                    LinkedList<Cell> ownedCells = new LinkedList<Cell>(grid.getCellsPossessedByAI());
                    int size = ownedCells.size();
                    int chosenCell = new Random().nextInt(size);
                    int nextTurnX = ownedCells.get(chosenCell).getX();
                    int nextTurnY = ownedCells.get(chosenCell).getY();
                    gm.chooseCell(nextTurnX, nextTurnY, Attributes.AI);
                } else {
                    if (LOGGING) {
                        System.out.println("Best choice is: " + bestReturned.getX() + ":" + bestReturned.getY());
                    }
                    gm.chooseCell(bestReturned.getX(), bestReturned.getY(), Attributes.AI);
                }

            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
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

    public int getWork() {
        return work;
    }

    public synchronized void incrementWork() {
        work++;
    }

    public synchronized void incrementWorkDone() {
        workDone++;
    }

    public void setWork(int work) {
        this.work = work;
    }

    public int getWorkDone() {
        return workDone;
    }

    public void setWorkDone(int workDone) {
        this.workDone = workDone;
    }
}
