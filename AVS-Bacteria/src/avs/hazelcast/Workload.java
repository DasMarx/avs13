
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
     */
    private static final long serialVersionUID = -1157763274200244389L;

    private GameGrid grid;

    private int x, y;

    // TODO: Include Parent-Hash
    private int deepness;

    private int initialX;

    private int initialY;

    public Workload() {
    }

    public Workload(GameGrid grid, int x, int y, int initialX, int initialY, int deepness) {
        this.grid = grid;
        this.x = x;
        this.y = y;
        this.initialX = initialX;
        this.initialY = initialY;
        this.deepness = deepness;
    }

    @Override
    public WorkLoadReturn call() throws Exception {
        grid.processChanges(x, y);
        if (deepness < 3) {
            LinkedList<Workload> myWorkloadLinkedList = new LinkedList<Workload>();
            if ((deepness % 2) == 0) {
//                System.out.println("this should be AI");
                for (Cell c : grid.getCellsPossessedByAI()) {
                    Workload myWorkload = new Workload(grid.getCopy(), c.getX(), c.getY(), initialX, initialY, deepness+1);
                    myWorkloadLinkedList.add(myWorkload);
                }
            } else {
//                System.out.println("this should be player");
                for (Cell c : grid.getCellsPossessedByPlayer()) {
                    Workload myWorkload = new Workload(grid.getCopy(), c.getX(), c.getY(), initialX, initialY, deepness+1);
                    myWorkloadLinkedList.add(myWorkload);
                }
                
            }
            return new WorkLoadReturn(x, y, initialX, initialY, deepness, grid.getCellsPossessedByAiCount()-grid.getCellsPossessedByPlayerCount(),myWorkloadLinkedList);
        }
        
        return new WorkLoadReturn(x, y,initialX,initialY, deepness,grid.getCellsPossessedByAiCount()-grid.getCellsPossessedByPlayerCount());
    }

}
