
package avs.ai;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.IExecutorService;
import avs.game.Attributes;
import avs.game.Cell;
import avs.game.CellChange;
import avs.game.GameGrid;
import avs.game.GameManager;
import avs.hazelcast.HazelcastWorker;
import avs.hazelcast.WorkLoadReturn;
import avs.hazelcast.Workload;

public class AICore implements Runnable {

    private GameManager gm;

    private boolean running = true;

    // private LinkedList<Future<WorkLoadReturn>> futureQueue = new LinkedList<Future<WorkLoadReturn>>();

    private GameGrid grid;

    private HazelcastWorker myWorker;

    private IExecutorService executorService;

    private Tree<Data> resultTree;

    private boolean ProducerStillRunning = false;

    public void initialize(GameManager gm) {
        this.setGm(gm);
        myWorker = new HazelcastWorker();
    }

    public void setGameGrid(GameGrid grid) {
        this.setGrid(grid);

    }

    public void run() {
        setExecutorService(myWorker.getInstance().getExecutorService("default"));
        while (isRunning()) {
            if (!getGm().isPlayersTurn()) {

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
                        if (bestReturned.getAi() < consumerArray[i].getInternalReturn().getAi()) {
                            bestReturned = consumerArray[i].getInternalReturn();
                        }
                    }
                }
                System.out.println("");
                for (int i = 0; i < THREAD_COUNT; i++) {
                    System.out.print(consumerArray[i].getCounter() + " ");
                }
                System.out.println("");

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
}

// Producer Class in java
class Producer implements Runnable {

    private final BlockingQueue<WorkLoadReturn> futureQueue;

    private AICore aiCore;

    private AtomicInteger concurrentExecution;

    public Producer(BlockingQueue<WorkLoadReturn> sharedQueue, AICore aiCore, AtomicInteger concurrentExecution) {
        this.futureQueue = sharedQueue;
        this.aiCore = aiCore;
        this.concurrentExecution = concurrentExecution;
    }

    @Override
    public void run() {
        for (Cell c : aiCore.getGrid().getCellsPossessedByAI()) {
            GameGrid currentGrid = aiCore.getGrid().getCopy();
            currentGrid.processChanges(c, false);
            for (Cell innerC : currentGrid.getCellsPossessedByAI()) {
                Callable<WorkLoadReturn> task = new Workload(currentGrid.getCopy(), innerC.getX(), innerC.getY(), c.getX(), c.getY(), 0);
                ExecutionCallback<WorkLoadReturn> myCallback = new ExecutionCallback<WorkLoadReturn>() {

                    public void onResponse(WorkLoadReturn response) {
                        try {
                            futureQueue.put(response);
                        } catch (InterruptedException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        concurrentExecution.decrementAndGet();
                    }

                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                    }

                };
                while (concurrentExecution.get() > 100) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                concurrentExecution.incrementAndGet();
                aiCore.getExecutorService().submit(task, myCallback);
            }
        }
    }

}

// Consumer Class in Java
class Consumer implements Runnable {

    private final BlockingQueue<WorkLoadReturn> futureQueue;

    private WorkLoadReturn internalReturn = null;

    private int counter = 0;

    private AtomicInteger concurrentExecution;

    private AICore aiCore;

    public Consumer(BlockingQueue<WorkLoadReturn> futureQueue2, AICore aiCore, AtomicInteger concurrentExecution) {
        this.aiCore = aiCore;
        this.futureQueue = futureQueue2;
        this.concurrentExecution = concurrentExecution;
    }

    @Override
    public void run() {
        // while (!Thread.currentThread().isInterrupted()) {
        // if (aiCore.consumerProducerStillRunning()) {
        boolean running = true;
        while (running) {
            try {
                WorkLoadReturn myReturn;
                myReturn = futureQueue.poll(100, TimeUnit.MILLISECONDS);
                if (null != myReturn) {
//                    concurrentExecution.decrementAndGet();
                    setCounter(getCounter() + myReturn.getCounter() + 1);
                    if (null == getInternalReturn()) {
                        setInternalReturn(myReturn);
                    }
                    if (getInternalReturn().getAi() < myReturn.getAi()) {
                        setInternalReturn(myReturn);
                    }
                } else {
                    if (!aiCore.ProducerStillRunning()) {
                        if (concurrentExecution.get() == 0) {
                            running = false;
                        }
                    }
                }

                // for (Workload w : myReturn.getMyWorkloadLinkedList()) {
                // newWorkQueue.add(w);
                // }
                // newWorkQueue.addAll(myReturn.getMyWorkloadLinkedList());
            } catch (InterruptedException ex) {
                running = false;
            }

        }

    }

    public WorkLoadReturn getInternalReturn() {
        return internalReturn;
    }

    public void setInternalReturn(WorkLoadReturn internalReturn) {
        this.internalReturn = internalReturn;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

}
