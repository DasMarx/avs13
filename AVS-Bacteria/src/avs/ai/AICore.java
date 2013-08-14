
package avs.ai;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import avs.game.*;
import avs.hazelcast.HazelcastWorker;

public class AICore {

    private GameManager gm;

    private boolean running = true;

    private LinkedList<Workload> workQueue;

    private LinkedList<Future<WorkLoadReturn>> futureQueue;

    private GameGrid grid;

    private HazelcastWorker myWorker;

    private Tree<Result> minMaxTree;

    public void initialize(GameManager gm) {
        gm = this.gm;
        myWorker = new HazelcastWorker();
    }

    public void setGameGrid(GameGrid grid) {
        grid = this.grid;
    }
    
    public void updateGrid(LinkedList<CellChanges> cellChanges){
//        TODO    
    }
    public void setControl(boolean Turn){
//        TODO: true - KI ist dran
//                false - KI ist nicht dran
    }

    public void run() {
        LinkedList<Cell> possessedCells = grid.getCellsPossessedByAI();

        while (running) {

            // executorService creation
            IExecutorService executorService = myWorker.getInstance().getExecutorService("default");

            // create workload for all workers
            while (!possessedCells.isEmpty()) {
                Workload myTmpWorkload = new Workload(grid, possessedCells.getFirst().getX(), possessedCells.getFirst().getY());
                Future<WorkLoadReturn> future = executorService.submit(myTmpWorkload);

                // why do we need the workqueue?
                // workQueue.add(myTmpWorkload);

                futureQueue.add(future);
                possessedCells.removeFirst();
            }

            Iterator<Future<WorkLoadReturn>> it = futureQueue.iterator();

            while (it.hasNext()) {
                Future<WorkLoadReturn> future = it.next();
                try {
                    WorkLoadReturn myReturn = future.get();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            // Distribute work
            // collect and merge result trees
            // get best result
            // execute turn
            // discard obsolete nodes
            // get grid from leaves
            // get cells from grids

        }

    }
}
