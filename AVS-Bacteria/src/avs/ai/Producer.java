
package avs.ai;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import avs.game.Cell;
import avs.game.GameGrid;
import avs.hazelcast.WorkLoadReturn;
import avs.hazelcast.Workload;
import com.hazelcast.core.ISemaphore;
import com.hazelcast.core.Member;
import static avs.game.Constants.*;

// Producer Class in java
class Producer implements Runnable {

    private final BlockingQueue<Callable<WorkLoadReturn>> workQueue;

    private AICore aiCore;

    ISemaphore[] semaphoreArray;

    Member[] memberArray;

    public Producer(BlockingQueue<Callable<WorkLoadReturn>> workQueue, AICore aiCore) {
        this.workQueue = workQueue;
        this.aiCore = aiCore;
    }

    private void doWork(Cell cell, GameGrid gameGrid) {
        if (USE_SMALLER_WORKLOADS) {
            GameGrid internalGrid = gameGrid.getCopy();
            internalGrid.processChanges(cell, false);
            for (Cell c : gameGrid.getCellsPossessedByAI()) {
                Callable<WorkLoadReturn> task = new Workload(internalGrid.getCopy(), c, cell.getX(), cell.getY(), 2);
                try {
                    workQueue.put(task);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                aiCore.incrementWork();
            }
        } else {
            Callable<WorkLoadReturn> task = new Workload(gameGrid.getCopy(), cell, cell.getX(), cell.getY(), 1);
            try {
                workQueue.put(task);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            aiCore.incrementWork();
        }
    }

    @Override
    public void run() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        Thread.currentThread().setName("AVS:Producer Thread");
        aiCore.setWork(0);
        aiCore.setWorkDone(0);
        for (Cell c : aiCore.getGrid().getCellsPossessedByAI()) {
            doWork(c, aiCore.getGrid());
        }
    }

}
