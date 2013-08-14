
package avs.game;

public class CellChanges {

    private Cell cell;

    private int step;

    /**
     * Initializes a new {@link CellChanges}.
     * 
     * @param cell that was changed
     * @param step in which the cell was changed
     */
    public CellChanges(Cell cell, int step) {
        this.cell = cell;
        this.step = step;
    }

    /**
     * @return the cell
     */
    public Cell getCell() {
        return cell;
    }

    /**
     * @return the step
     */
    public int getStep() {
        return step;
    }
}
