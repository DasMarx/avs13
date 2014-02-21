
package avs.hazelcast;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import avs.game.Cell;
import avs.game.CellChange;
import avs.game.GameGrid;

public class Workload implements Callable<WorkLoadReturn>, Serializable {

    /**
     * This class includes the grid to work on, the coordinates to turn and the hash of the parent data.
     */
    private static final long serialVersionUID = -1157763274200244389L;

    private final GameGrid grid;

    private final int initialX, initialY;
    
    private final int deepness;

//    private LinkedList<Cell> work;

    private Cell cell;

    int counter = 1;
    
    WorkLoadReturn bestReturned = null;

//    public Workload(GameGrid grid, LinkedList<Cell> work, int initialX, int initialY, int deepness) {
//        this.work = work;
//        this.grid = grid;
//        this.initialX = initialX;
//        this.initialY = initialY;
//        this.deepness = deepness;
//    }

    public Workload(GameGrid grid, Cell c, int initialX, int initialY, int deepness) {
        this.grid = grid;
        this.cell = c;
        this.initialX = initialX;
        this.initialY = initialY;
        this.deepness = deepness;
    }

    @Override
    public WorkLoadReturn call() throws Exception {
//        if ( null != work) {
//            for (Cell outerCell : work) {
//                final GameGrid outerGrid = grid.getCopy();
//                doWork(outerCell,outerGrid);
//            }
//        } else {
            doWork(cell,grid);
//        }
        if (null != bestReturned) {
            bestReturned.setCounter(counter);
            return bestReturned;
        }
        return null;
    }

    private void doWork(Cell tmpCell, GameGrid tmpGrid) throws Exception {
        LinkedList<CellChange> changes = tmpGrid.processChanges(tmpCell, false);
        counter++;
        if (deepness < 5) {
            for (Cell c : tmpGrid.getCellsPossessedByAI()) {
                final Workload myTmpWorkload = new Workload(tmpGrid, c, initialX, initialY, deepness + 1);
                final WorkLoadReturn myReturn = myTmpWorkload.call();
                if (null != myReturn) {
                    counter += myReturn.getCounter();
                    bestReturned = compareWorkloads(bestReturned, myReturn);
                }
            }
        } else {
            bestReturned = compareWorkloads(bestReturned, new WorkLoadReturn(tmpCell, initialX, initialY, tmpGrid.getRating(), counter));
        }
        for (CellChange change: changes) {
            tmpGrid.consumeCellChange(change);
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
