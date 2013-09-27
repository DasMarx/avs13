package avs.hazelcast;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import avs.ai.Data;
import avs.game.Attributes;
import avs.game.Cell;
import avs.game.GameGrid;

public class Workload implements Callable<WorkLoadReturn>, Serializable {
	/**
     * This class includes the grid to work on, the coordinates to turn and the hash of the parent data.
     * 
     */
    private static final long serialVersionUID = -1157763274200244389L;
    private GameGrid grid;
	private int x, y;
	//TODO: Include Parent-Hash
    private int deepness;
	
	public Workload(){
    }
    public Workload(GameGrid grid, int x, int y,int deepness){
        this.grid = grid;
	    this.x = x;
	    this.y = y;
	    this.deepness = deepness;
	}
    @Override
    public WorkLoadReturn call() throws Exception {
//        System.out.println("field " + x + ":" + y + " is worth: " + grid.getCellsPossessedByAiCount() + " at deepness of " + deepness);
        int counter = 1;
        grid.processChanges(x, y);
        if (deepness < 2) {
            WorkLoadReturn bestReturnedLoad = null;
            for (Cell c : grid.getCellsPossessedByAI()){
                Workload myTmpWorkload = new Workload(grid.getCopy(), c.getX(), c.getY(),deepness+1); // create workload for all workers
                WorkLoadReturn myReturn = myTmpWorkload.call();
                counter += myReturn.getCounter();
                if (null == bestReturnedLoad) {
                    bestReturnedLoad = myReturn;
                } else if (myReturn.getAi() >= bestReturnedLoad.getAi()) {
                    bestReturnedLoad = myReturn;
                }
            }
            if (null != bestReturnedLoad) {
                return new WorkLoadReturn(x,y,bestReturnedLoad.getAi(),bestReturnedLoad.getPlayer(),counter);
            }
        }
        
        return new WorkLoadReturn(x,y,grid.getCellsPossessedByAiCount(),grid.getCellsPossessedByPlayerCount(),counter);
    }
    
    
}