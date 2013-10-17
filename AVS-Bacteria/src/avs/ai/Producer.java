
package avs.ai;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import avs.game.Cell;
import avs.game.GameGrid;
import avs.hazelcast.WorkLoadReturn;
import avs.hazelcast.Workload;
import com.hazelcast.core.ISemaphore;
import com.hazelcast.core.Member;

// Producer Class in java
class Producer implements Runnable {

//    private final BlockingQueue<WorkLoadReturn> futureQueue;
    
    private final BlockingQueue<Callable<WorkLoadReturn>> workQueue;

    private AICore aiCore;

    private int WORK_COUNTER = 25;

    // private Map<Member, ISemaphore> mySemaphoreMap;

    ISemaphore[] semaphoreArray;

    Member[] memberArray;

    public Producer(BlockingQueue<Callable<WorkLoadReturn>> workQueue, AICore aiCore) {
        this.workQueue = workQueue;
        this.aiCore = aiCore;
    }

    @Override
    public void run() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        Thread.currentThread().setName("AVS:Producer Thread");
        aiCore.setWork(0);
        aiCore.setWorkDone(0);
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
                    Callable<WorkLoadReturn> task = new Workload(currentGrid, workList, c.getX(), c.getY(), 2);
                    try {
                        workQueue.put(task);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    workList = null;
                }
            }
            if (null != workList) {
                Callable<WorkLoadReturn> task = new Workload(currentGrid.getCopy(), workList, c.getX(), c.getY(), 2);
                try {
                    workQueue.put(task);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                workList = null;
            }
        }
    }



}
