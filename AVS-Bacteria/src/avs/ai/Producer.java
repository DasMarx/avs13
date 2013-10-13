package avs.ai;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import avs.game.Cell;
import avs.game.GameGrid;
import avs.hazelcast.WorkLoadReturn;
import avs.hazelcast.Workload;
import com.hazelcast.core.ExecutionCallback;

// Producer Class in java
class Producer implements Runnable {

    private final BlockingQueue<WorkLoadReturn> futureQueue;

    private AICore aiCore;

    private Semaphore concurrentExecution;
    
    private int WORK_COUNTER = 10;
    
    ExecutionCallback<WorkLoadReturn> myCallback = new ExecutionCallback<WorkLoadReturn>() {

        public void onResponse(WorkLoadReturn response) {
            try {
                futureQueue.put(response);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            concurrentExecution.release();
        }

        public void onFailure(Throwable t) {
            concurrentExecution.release();
            t.printStackTrace();
        }
    };

    public Producer(BlockingQueue<WorkLoadReturn> sharedQueue, AICore aiCore, Semaphore semaphore) {
        this.futureQueue = sharedQueue;
        this.aiCore = aiCore;
        this.concurrentExecution = semaphore;
    }

    @Override
    public void run() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        Thread.currentThread().setName("AVS:Producer Thread");
        for (Cell c : aiCore.getGrid().getCellsPossessedByAI()) {
            GameGrid currentGrid = aiCore.getGrid().getCopy();
            currentGrid.processChanges(c, false);
            LinkedList<Cell> workList = null;
            for (Cell innerC : currentGrid.getCellsPossessedByAI()) {
                if (null == workList) {
                    workList = new LinkedList<Cell>();
                }
                workList.add(innerC);
                if (workList.size() >= WORK_COUNTER) {
                    Callable<WorkLoadReturn> task = new Workload(currentGrid.getCopy(),workList , c.getX(), c.getY(), 0);
                    sendToQueue(task);
                    workList = null;
                }
            }
            if (null != workList) {
                Callable<WorkLoadReturn> task = new Workload(currentGrid.getCopy(),workList , c.getX(), c.getY(), 0);
                sendToQueue(task);
                workList = null;
            }
        }
    }

    /**
     * @param myCallback
     * @param task
     */
    private void sendToQueue( Callable<WorkLoadReturn> task) {
        try {
            concurrentExecution.acquire();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        aiCore.getExecutorService().submit(task, myCallback);
    }

}