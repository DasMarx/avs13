
package avs.ai;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
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

    private long TIMEOUT_IN_SEC = 10;

    private long TIMEOUT_IN_MS = TIMEOUT_IN_SEC * 1000;

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
        while (running) {
            if (!getGm().isPlayersTurn()) {

                System.out.println("=== new Round ====");

                // Creating shared object
                BlockingQueue<Future<WorkLoadReturn>> futureQueue = new LinkedBlockingQueue<Future<WorkLoadReturn>>(100);
                BlockingQueue<Workload> newWorkQueue = new LinkedBlockingQueue<Workload>(2000);

                // Creating Producer and Consumer Thread
                Thread prodThread = new Thread(new Producer(futureQueue, newWorkQueue, this));
                
                Consumer myConsumer = new Consumer(futureQueue, newWorkQueue, this);
                Thread consThread = new Thread(myConsumer);

                // Starting producer and Consumer thread
                prodThread.start();
                consThread.start();

                try {
                    prodThread.join();
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                try {
                    consThread.join();
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                System.out.println(futureQueue.size() + " still in futureQueue");
                WorkLoadReturn myReturn = myConsumer.getInternalReturn();
                System.out.println(myConsumer.getCounter());
                if (myReturn == null) {
                    LinkedList<Cell> ownedCells = new LinkedList<Cell>(grid.getCellsPossessedByAI());
                    int size = ownedCells.size();
                    int chosenCell = new Random().nextInt(size);
                    int nextTurnX = ownedCells.get(chosenCell).getX();
                    int nextTurnY = ownedCells.get(chosenCell).getY();
                    gm.chooseCell(nextTurnX, nextTurnY, Attributes.AI);
                } else {
                    System.out.println("Best choice is: " + myReturn.getInitialX() + ":" + myReturn.getInitialY());
                    gm.chooseCell(myReturn.getInitialX(), myReturn.getInitialY(), Attributes.AI);
                }

                // futureQueue = new LinkedList<Future<WorkLoadReturn>>();
                // for (Cell c : grid.getCellsPossessedByAI()) {
                // Workload myTmpWorkload = new Workload(grid.getCopy(), c.getX(), c.getY(), 0); // create workload for all workers
                //
                // // distribute work
                // Future<WorkLoadReturn> future = executorService.submit(myTmpWorkload);
                // futureQueue.add(future);
                // }
                //
                // WorkLoadReturn bestReturnedLoad = null;
                // int counter = 0;
                // for (Future<WorkLoadReturn> f : futureQueue) {
                // try {
                //
                // WorkLoadReturn myReturn = f.get();
                // counter += myReturn.getCounter();
                // System.out.println("field " + myReturn.getX() + ":" + myReturn.getY() + " is worth: " + myReturn.getAi());
                // if (null == bestReturnedLoad) {
                // bestReturnedLoad = myReturn;
                // } else if (myReturn.getAi() >= bestReturnedLoad.getAi()) {
                //
                // bestReturnedLoad = myReturn;
                // }
                //
                // } catch (InterruptedException e) {
                // // TODO Auto-generated catch block
                // e.printStackTrace();
                // } catch (ExecutionException e) {
                // // TODO Auto-generated catch block
                // e.printStackTrace();
                // }
                // }
                //
                // if (bestReturnedLoad == null) {
                // LinkedList<Cell> ownedCells = new LinkedList<Cell>(grid.getCellsPossessedByAI());
                // int size = ownedCells.size();
                // int chosenCell = new Random().nextInt(size);
                // int nextTurnX = ownedCells.get(chosenCell).getX();
                // int nextTurnY = ownedCells.get(chosenCell).getY();
                // gm.chooseCell(nextTurnX, nextTurnY, Attributes.AI);
                // } else {
                // System.out.println("== took " + counter + " calls ==");
                // System.out.println("Best choice is: " + bestReturnedLoad.getX() + ":" + bestReturnedLoad.getY());
                // gm.chooseCell(bestReturnedLoad.getX(), bestReturnedLoad.getY(), Attributes.AI);
                // }

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

    public boolean consumerProducerStillRunning() {
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
}

// Producer Class in java
class Producer implements Runnable {

    private final BlockingQueue<Future<WorkLoadReturn>> futureQueue;

    private final BlockingQueue<Workload> newWorkQueue;

    private AICore aiCore;

    public Producer(BlockingQueue<Future<WorkLoadReturn>> sharedQueue, BlockingQueue<Workload> newWorkQueue, AICore aiCore) {
        this.newWorkQueue = newWorkQueue;
        this.futureQueue = sharedQueue;
        this.aiCore = aiCore;
    }

    @Override
    public void run() {

        // while (!Thread.currentThread().isInterrupted()) {
        // if (aiCore.consumerProducerStillRunning()) {
        //
        // // create workload for all workers distributed work
        // // Workload myWorkload = new Workload(aiCore.getGrid().getCopy(), c.getX(), c.getY(), c.getX(), c.getY(), 0);
        // // Future<WorkLoadReturn> future = aiCore.getExecutorService().submit(myWorkload);
        // // futureQueue.add(future);
        // }
        // try {
        for (Cell c : aiCore.getGrid().getCellsPossessedByAI()) {
            GameGrid currentGrid = aiCore.getGrid().getCopy();
            currentGrid.processChanges(c);
            for (Cell innerC : currentGrid.getCellsPossessedByAI()) {
                Workload myWorkload = new Workload(currentGrid.getCopy(), innerC.getX(), innerC.getY(), c.getX(), c.getY(), 1);
                Future<WorkLoadReturn> future = aiCore.getExecutorService().submit(myWorkload);
                while (null != future) {
                    if (futureQueue.offer(future)) {
                        future = null;
                    } else {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                }

                // futureQueue.add(future);
            }
            // }

            // Workload myWorkload = newWorkQueue.take();
            // if (deepness < 3) {
            // LinkedList<Workload> myWorkloadLinkedList = new LinkedList<Workload>();
            // if ((deepness % 2) == 0) {
            // // System.out.println("this should be AI");
            // for (Cell c : grid.getCellsPossessedByAI()) {
            // Workload myWorkload = new Workload(grid.getCopy(), c.getX(), c.getY(), initialX, initialY, deepness+1);
            // myWorkloadLinkedList.add(myWorkload);
            // }
            // } else {
            // // System.out.println("this should be player");
            // for (Cell c : grid.getCellsPossessedByPlayer()) {
            // Workload myWorkload = new Workload(grid.getCopy(), c.getX(), c.getY(), initialX, initialY, deepness+1);
            // myWorkloadLinkedList.add(myWorkload);
            // }
            //
            // }
            // return new WorkLoadReturn(x, y, initialX, initialY, deepness,
            // grid.getCellsPossessedByAiCount()-grid.getCellsPossessedByPlayerCount(),myWorkloadLinkedList);
            // }
            // Future<WorkLoadReturn> future = aiCore.getExecutorService().submit(myWorkload);
            // futureQueue.add(future);
            // } catch (InterruptedException e) {
            // Thread.currentThread().interrupt();
            // }

        }
    }

}

// Consumer Class in Java
class Consumer implements Runnable {

    private final BlockingQueue<Future<WorkLoadReturn>> futureQueue;

    private final BlockingQueue<Workload> newWorkQueue;

    private AICore aiCore;

    private WorkLoadReturn internalReturn = null;

    private int counter = 0;

    public Consumer(BlockingQueue<Future<WorkLoadReturn>> sharedQueue, BlockingQueue<Workload> newWorkQueue, AICore aiCore) {
        this.newWorkQueue = newWorkQueue;
        this.futureQueue = sharedQueue;
        this.aiCore = aiCore;
    }

    @Override
    public void run() {
        // while (!Thread.currentThread().isInterrupted()) {
        // if (aiCore.consumerProducerStillRunning()) {
        boolean running = true;
        while (running) {
            try {
                Future<WorkLoadReturn> myFuture;
                myFuture = futureQueue.poll(1, TimeUnit.SECONDS);
                if (null != myFuture) {
                    WorkLoadReturn myReturn = myFuture.get();
                    setCounter(getCounter() + myReturn.getCounter() + 1);
                    if (null == getInternalReturn()) {
                        setInternalReturn(myReturn);
                    }
                    if (myReturn.getAi() > getInternalReturn().getAi()) {
                        setInternalReturn(myReturn);
                    }
                } else {
                    running = false;
                }

                // for (Workload w : myReturn.getMyWorkloadLinkedList()) {
                // newWorkQueue.add(w);
                // }
                // newWorkQueue.addAll(myReturn.getMyWorkloadLinkedList());
            } catch (InterruptedException ex) {
                running = false;
            } catch (ExecutionException e) {
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
