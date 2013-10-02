
package avs.hazelcast;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import avs.game.Cell;
import avs.game.GameGrid;

public class Workload implements Callable<WorkLoadReturn>, Serializable {

    /**
     * This class includes the grid to work on, the coordinates to turn and the hash of the parent data.
     */
    private static final long serialVersionUID = -1157763274200244389L;

    private GameGrid grid;

    private int initialX, initialY;

    // TODO: Include Parent-Hash
    private int deepness;

    private LinkedList<Cell> work;

    public Workload() {
    }

    public Workload(GameGrid grid, LinkedList<Cell> work, int initialX, int initialY, int deepness) {
        this.work = work;
        this.grid = grid;
        this.initialX = initialX;
        this.initialY = initialY;
        this.deepness = deepness;
    }

    @Override
    public WorkLoadReturn call() throws Exception {
        // System.out.println("field " + x + ":" + y + " is worth: " + grid.getCellsPossessedByAiCount() + " at deepness of " + deepness);
        int counter = 1;
        WorkLoadReturn bestReturned = null;
        
        for (Cell outerCell : work) {
            final GameGrid outerGrid = grid.getCopy();
            outerGrid.processChanges(outerCell, false);
            counter++;
            if (deepness < 0) {
                
                for (Cell c : outerGrid.getCellsPossessedByAI()) {
                    LinkedList<Cell> tmpList = new LinkedList<Cell>();
                    tmpList.add(c);
                    final Workload myTmpWorkload = new Workload(outerGrid.getCopy(), tmpList, initialX, initialY, deepness + 1);
                    final WorkLoadReturn myReturn = myTmpWorkload.call();
                    counter += myReturn.getCounter();
                    bestReturned = compareWorkloads(bestReturned, myReturn);
                }
                
            } else {
                bestReturned = compareWorkloads(bestReturned, new WorkLoadReturn(outerCell, initialX, initialY, outerGrid.getRating(), counter));
            }
            
        }
        
        if (null != bestReturned) {
            bestReturned.setCounter(counter);
            return bestReturned;
        }
        return null;
    }

    /**
     * @param bestReturned
     * @param myReturn
     * @return
     */
    private WorkLoadReturn compareWorkloads(WorkLoadReturn bestReturned, final WorkLoadReturn myReturn) {
        if (null == bestReturned) {
            bestReturned = myReturn;
        } else if (bestReturned.getRating() < myReturn.getRating()) {
            bestReturned = myReturn;
        }
        return bestReturned;
    }

}
