
package avs.hazelcast;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import avs.game.Cell;
import avs.game.CellChange;
import avs.game.GameGrid;
import static avs.game.Constants.*;

public class Workload implements Callable<WorkLoadReturn>, Serializable {

    /**
     * This class includes the grid to work on, the coordinates to turn and the hash of the parent data.
     */
    private static final long serialVersionUID = -1157763274200244389L;

    private final GameGrid grid;

    private final int initialX, initialY;

    private final int deepness;

    private final Cell cell;

    int counter = 0;

    WorkLoadReturn bestReturned = null;

    public Workload(GameGrid grid, Cell c, int initialX, int initialY, int deepness) {
        this.grid = grid;
        this.cell = c;
        this.initialX = initialX;
        this.initialY = initialY;
        this.deepness = deepness;
    }

    @Override
    public WorkLoadReturn call() throws Exception {
        doWork(cell, grid);

        if (null != bestReturned) {
            bestReturned.setCounter(counter);
            return bestReturned;
        }
        return null;
    }

    //TODO everything should be done as doWork so no new Workloads are created. This should speed up the process significant
    private void doWork(Cell tmpCell, final GameGrid tmpGrid) throws Exception {
        if (USE_OPTIMIZATION_1) {
            final LinkedList<CellChange> changes = tmpGrid.processChanges(tmpCell, false);
            counter++;
            if (deepness < WORK_DEEPNESS) {
                for (Cell c : tmpGrid.getCellsPossessedByAI()) {
                    final Workload myTmpWorkload = new Workload(tmpGrid, c, initialX, initialY, deepness + 1);
                    final WorkLoadReturn myReturn = myTmpWorkload.call();
                    if (null != myReturn) {
                        counter += myReturn.getCounter();
                        bestReturned = compareWorkloads(bestReturned, myReturn);
                    }
                }
            } else {
                bestReturned = new WorkLoadReturn(tmpCell, initialX, initialY, tmpGrid.getRating(), counter);
            }
            for (CellChange change : changes) {
                tmpGrid.consumeCellChange(change);
            }
        } else {
            tmpGrid.processChanges(tmpCell, false);
            counter++;
            if (deepness < WORK_DEEPNESS) {
                for (Cell c : tmpGrid.getCellsPossessedByAI()) {
                    final Workload myTmpWorkload = new Workload(tmpGrid.getCopy(), c, initialX, initialY, deepness + 1);
                    final WorkLoadReturn myReturn = myTmpWorkload.call();
                    if (null != myReturn) {
                        counter += myReturn.getCounter();
                        bestReturned = compareWorkloads(bestReturned, myReturn);
                    }
                }
            } else {
                bestReturned = new WorkLoadReturn(tmpCell, initialX, initialY, tmpGrid.getRating(), counter);
            }
        }
        
        
    }

    /**
     * @param bestReturned
     * @param myReturn
     * @return
     */
    private WorkLoadReturn compareWorkloads(WorkLoadReturn bestReturned, final WorkLoadReturn myReturn) {
        if (null == bestReturned) {
            return myReturn;
        } else if (bestReturned.getRating() < myReturn.getRating()) {
            return myReturn;
        }
        return bestReturned;
    }

}
