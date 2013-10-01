package avs.ai;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
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

    private AtomicInteger concurrentExecution;
    
    private int WORK_COUNTER = 5;
    
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
            concurrentExecution.decrementAndGet();
            t.printStackTrace();
        }
    };

    public Producer(BlockingQueue<WorkLoadReturn> sharedQueue, AICore aiCore, AtomicInteger concurrentExecution) {
        this.futureQueue = sharedQueue;
        this.aiCore = aiCore;
        this.concurrentExecution = concurrentExecution;
    }

    @Override
    public void run() {
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
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