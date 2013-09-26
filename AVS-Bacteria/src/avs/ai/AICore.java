
package avs.ai;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Future;
import avs.game.Attributes;
import avs.game.Cell;
import avs.game.CellChanges;
import avs.game.GameGrid;
import avs.game.GameManager;
import avs.hazelcast.HazelcastWorker;
import avs.hazelcast.WorkLoadReturn;

public class AICore {

    private GameManager gm;

    private boolean running = true;

    private LinkedList<Future<WorkLoadReturn>> futureQueue;

    private GameGrid grid;

    private HazelcastWorker myWorker;

    public void initialize(GameManager gm) {
        this.gm = gm;
        myWorker = new HazelcastWorker();
    }

    public void setGameGrid(GameGrid grid) {
        this.grid = grid;
    }
    
//    public void updateGrid(LinkedList<CellChanges> cellChanges){
        // We don't need this method as we are working on the same Grid
        
//    	for(CellChanges changedCell: cellChanges){
//    		int o = changedCell.getCell().getOwner();
//    		if(o == Attributes.AI)
//    			grid.addCellAI(changedCell.getCell());
//    		if(o == Attributes.PLAYER)
//    			grid.addCellPlayer(changedCell.getCell());
//    	}
//    }
    
    public void setControl(boolean turn){
    	//TODO: The AIs turn begins, when this method gets called
    	
    }

    public void run() {
//        Tree<Data> resultTree = new Tree<Data>(new Data(grid, 0, -1, -1, 0)); //create Tree from first Result
//        Data currentData = resultTree.getRoot().getData(); //set the root node as first datasource
        while (running) {
            if (!gm.isPlayersTurn()){
//                LinkedList<Cell> possessedCells = currentData.getGrid().getCellsPossessedByAI(); //get all possessed cells from the current datasource
//                // executorService creation
//                IExecutorService executorService = myWorker.getInstance().getExecutorService("default");
//
//                while (!possessedCells.isEmpty()) {
//                    Workload myTmpWorkload = new Workload(currentData, possessedCells.getFirst().getX(), possessedCells.getFirst().getY()); // create workload for all workers
//                    
//                    //distribute work
//                    Future<WorkLoadReturn> future = executorService.submit(myTmpWorkload);
//                    futureQueue.add(future);
//                    possessedCells.removeFirst();
//                }
//
//                Iterator<Future<WorkLoadReturn>> it = futureQueue.iterator(); //get finished workloads
//                while (it.hasNext()) {
//                    Future<WorkLoadReturn> future = it.next();
//                    try {
//                        WorkLoadReturn myReturn = future.get();
//                        
//                    } catch (InterruptedException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    } catch (ExecutionException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
                
                LinkedList<Cell> ownedCells = grid.getCellsPossessedByAI();
                int size = ownedCells.size();
                int chosenCell = new Random().nextInt(size);
                int nextTurnX = ownedCells.get(chosenCell).getX();
                int nextTurnY = ownedCells.get(chosenCell).getY();
                gm.chooseCell(nextTurnX, nextTurnY, Attributes.AI);
            } else {
                try {
                    Thread.sleep(100);
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
}
