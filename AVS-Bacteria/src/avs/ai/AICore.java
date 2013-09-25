
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
        
        //calculate first turn and create first Result from it
        //create Tree from first Result
        Tree<Data> resultTree = new Tree<Data>(new Data(grid, 0, -1, -1, 0));
        Data currentData = resultTree.getRoot().getData();
        while (running) {
            
            LinkedList<Cell> possessedCells = grid.getCellsPossessedByAI();
            // executorService creation
            IExecutorService executorService = myWorker.getInstance().getExecutorService("default");

            // create workload for all workers
            while (!possessedCells.isEmpty()) {
                Workload myTmpWorkload = new Workload(currentData, possessedCells.getFirst().getX(), possessedCells.getFirst().getY());
             // distribute work
                Future<WorkLoadReturn> future = executorService.submit(myTmpWorkload);

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
            
            
            // collect and merge result trees
            // get best result
            // execute turn
            // discard obsolete nodes
            // get grid from leaves
            // get cells from grids

        }

    }
}
