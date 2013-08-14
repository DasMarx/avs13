package avs.ai;
import java.io.Serializable;
import java.util.concurrent.Callable;
import avs.game.GameGrid;

public class Workload implements Callable<WorkLoadReturn>, Serializable {
	/**
     * 
     */
    private static final long serialVersionUID = -1157763274200244389L;
    public GameGrid grid;
	public int x, y;
	
	public Workload(){
    }
    public Workload(GameGrid grid, int x, int y){
	    grid = this.grid;
	    x = this.x;
	    y = this.y;
	}
    @Override
    public WorkLoadReturn call() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}