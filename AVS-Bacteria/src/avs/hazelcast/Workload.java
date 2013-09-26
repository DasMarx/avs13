package avs.hazelcast;
import java.io.Serializable;
import java.util.concurrent.Callable;
import avs.ai.Data;
import avs.game.GameGrid;

public class Workload implements Callable<WorkLoadReturn>, Serializable {
	/**
     * This class includes the grid to work on, the coordinates to turn and the hash of the parent data.
     * 
     */
    private static final long serialVersionUID = -1157763274200244389L;
    public GameGrid grid;
	public int x, y, parentHash;
	//TODO: Include Parent-Hash
	
	public Workload(){
    }
    public Workload(GameGrid grid, int x, int y){
        this.grid = grid;
	    this.x = x;
	    this.y = y;
//	    this.parentHash = data.hashCode();
	}
    @Override
    public WorkLoadReturn call() throws Exception {
        grid.processChanges(x, y);
        return new WorkLoadReturn(x,y,grid.getCellsPossessedByAiCount(),grid.getCellsPossessedByPlayerCount());
    }
}