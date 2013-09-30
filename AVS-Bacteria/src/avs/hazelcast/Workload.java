
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

    private int x, y, initialX, initialY;

    // TODO: Include Parent-Hash
    private int deepness;

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
        // System.out.println("field " + x + ":" + y + " is worth: " + grid.getCellsPossessedByAiCount() + " at deepness of " + deepness);
        int counter = 1;
        grid.processChanges(x, y,false);
        if (deepness < 1) {
            WorkLoadReturn bestReturned = null;
            for (Cell c : grid.getCellsPossessedByAI()) {
                final Workload myTmpWorkload = new Workload(grid.getCopy(), c.getX(), c.getY(), initialX, initialY, deepness + 1);
                WorkLoadReturn myReturn = myTmpWorkload.call();
                counter += myReturn.getCounter();
                if (null == bestReturned) {
                    bestReturned = myReturn;
                } else if (bestReturned.getAi() < myReturn.getAi()) {
                    bestReturned = myReturn;
                }
            }
            if (null != bestReturned) {
                return new WorkLoadReturn(x, y, initialX, initialY, bestReturned.getAi(), bestReturned.getPlayer(), counter);
            }
        }

        return new WorkLoadReturn(
            x,
            y,
            initialX,
            initialY,
            grid.getCellsPossessedByAiCount(),
            grid.getCellsPossessedByPlayerCount(),
            counter);
    }

}
