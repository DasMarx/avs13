
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

    private int counter = 0;

    private int bestReturnedInt = Integer.MIN_VALUE;

    public Workload(GameGrid grid, Cell c, int initialX, int initialY, int deepness) {
        this.grid = grid;
        this.cell = c;
        this.initialX = initialX;
        this.initialY = initialY;
        this.deepness = deepness;
    }

    @Override
    public WorkLoadReturn call() throws Exception {
        processCellInsideWorkload(cell, grid, deepness);
        return new WorkLoadReturn(initialX, initialY, bestReturnedInt, counter);
    }


    private void processCellInsideWorkload(final Cell cell, final GameGrid gameGrid, final int currentDeepNess) throws Exception {
        if (USE_OPTIMIZATION_1) {
            final LinkedList<CellChange> changes = gameGrid.processChanges(cell, false);
            counter++;
            if (currentDeepNess < MAX_DEEPNESS) {
                for (Cell c : gameGrid.getCellsPossessedByAI()) {
                    processCellInsideWorkload(c, gameGrid, currentDeepNess + 1);
                }
            } else {
                if (gameGrid.getRating() > bestReturnedInt) {
                    bestReturnedInt = gameGrid.getRating();
                }
            }
            for (CellChange change : changes) {
                gameGrid.consumeCellChange(change);
            }
        } else {
            gameGrid.processChanges(cell, false);
            counter++;
            if (currentDeepNess < MAX_DEEPNESS) {
                for (Cell c : gameGrid.getCellsPossessedByAI()) {
                    processCellInsideWorkload(c, gameGrid.getCopy(), currentDeepNess +1);
                }
            } else {
                if (gameGrid.getRating() > bestReturnedInt) {
                    bestReturnedInt = gameGrid.getRating();
                }
            }
        }
    }

 

}
