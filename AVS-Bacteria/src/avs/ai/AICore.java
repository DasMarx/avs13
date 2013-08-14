
package avs.ai;

import java.util.LinkedList;
import avs.game.GameManager;
import avs.game.GameGrid;
import avs.game.Cell;

public class AICore {

    private GameManager gm;

    private boolean running = true;

    private LinkedList<Workload> workQueue;

    private GameGrid grid;

    public void initialize(GameManager gm) {
        gm = this.gm;
    }

    public void setGameGrid(GameGrid grid) {
        grid = this.grid;
    }

    public void run() {
        LinkedList<Cell> possessedCells = grid.getCellsPossessedByAI();
        while(running){
            while (!possessedCells.isEmpty()) {
                workQueue.add(new Workload(grid, possessedCells.getFirst().getX(), possessedCells.getFirst().getY()));
                possessedCells.removeFirst();
            }
            //Distribute work
            //collect and merge result trees
            //get best result
            //execute turn
            //discard obsolete nodes
            //get grid from leaves
            //get cells from grids
            
        }

    }
}
