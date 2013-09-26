
package avs.game;

public class CellChanges {

    private Cell cell;

    private int owner;

    /**
     * Initializes a new {@link CellChanges}.
     * 
     * @param cell that was changed
     * @param owner in which the cell was changed
     */
    public CellChanges(Cell cell,int owner) {
        this.cell = cell;
        this.owner = owner;
//        this.step = step;
    }

    /**
     * @return the cell
     */
    public Cell getCell() {
        return cell;
    }

    /**
     * @return the owner
     */
    public int getOwner() {
        return owner;
    }
}
