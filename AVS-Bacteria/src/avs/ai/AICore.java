
package avs.ai;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import avs.game.Cell;
import avs.game.CellChanges;
import avs.game.GameGrid;
import avs.game.GameManager;
import avs.hazelcast.HazelcastWorker;
import com.hazelcast.core.IExecutorService;

public class AICore {

    private GameManager gm;

    private boolean running = true;

    private LinkedList<Future<WorkLoadReturn>> futureQueue;

    private GameGrid grid;

    private HazelcastWorker myWorker;

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
//        TODO: true  - KI ist dran
//              false - KI ist nicht dran
    }

    public void run() {
        Tree<Data> resultTree = new Tree<Data>(new Data(grid, 0, -1, -1, 0)); //create Tree from first Result
        Data currentData = resultTree.getRoot().getData(); //set the root node as first datasource
        while (running) {
            
            LinkedList<Cell> possessedCells = currentData.grid.getCellsPossessedByAI(); //get all possessed cells from the current datasource
            // executorService creation
            IExecutorService executorService = myWorker.getInstance().getExecutorService("default");

            while (!possessedCells.isEmpty()) {
                Workload myTmpWorkload = new Workload(currentData, possessedCells.getFirst().getX(), possessedCells.getFirst().getY()); // create workload for all workers
                
                //distribute work
                Future<WorkLoadReturn> future = executorService.submit(myTmpWorkload);
                futureQueue.add(future);
                possessedCells.removeFirst();
            }

            Iterator<Future<WorkLoadReturn>> it = futureQueue.iterator(); //get finished workloads
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
            
            
            // collect and merge result trees
            // get best result
            // execute turn
            // discard obsolete nodes
            // get grid from leaves
            // get cells from grids

        }

    }
}
