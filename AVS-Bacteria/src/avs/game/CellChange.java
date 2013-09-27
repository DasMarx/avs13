
package avs.game;

import java.io.Serializable;

public class CellChange implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 4398381295539290132L;

    private int owner,x,y,direction;

    /**
     * Initializes a new {@link CellChange}.
     * 
     * @param cell that was changed
     */
    public CellChange(Cell cell) {
        this.x = cell.getX();
        this.y = cell.getY();
        this.owner = cell.getOwner();
        this.direction = cell.getDirection();
    }
    
    /**
     * 
     * Initializes a new {@link CellChange}.
     * @param cell
     * @param owner
     */
    public CellChange(Cell cell,int owner) {
        this.x = cell.getX();
        this.y = cell.getY();
        this.owner = owner;
        this.direction = cell.getDirection();
    }

    /**
     * @return the owner
     */
    public int getOwner() {
        return owner;
    }

    public int getX() {
       return x;
    }

    public int getY() {
        return y;
    }

    public int getDirection() {
        return direction;
    }
}
