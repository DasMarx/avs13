
package avs.game;

import java.io.Serializable;
import java.util.concurrent.Callable;
import avs.hazelcast.WorkLoadReturn;

public class Cell implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -6516429494910066037L;

    private int x;

    private int y;
//
//    private int owner;
//
//    private int direction;
//
//    /**
//     * @return the direction
//     */
//    public int getDirection() {
//        return direction;
//    }
    
//    /**
//     * 
//     * @param direction
//     */
//    public void setDirection(int direction) {
//        this.direction = direction;
//    }

//    /**
//     * @return the owner
//     */
//    public int getOwner() {
//        return owner;
//    }
//
//    /**
//     * @param owner the owner to set
//     */
//    public void setOwner(int owner) {
//        this.owner = owner;
//    }

    /**
     * @return the x value
     */
    public int getX() {
        return x;
    }

    /**
     * @return the y value
     */
    public int getY() {
        return y;
    }

    /**
     * Initializes a new {@link Cell}.
     * 
     * @param x coordinate of the cell
     * @param y coordinate of the cell
     * @param owner of the cell
     * @param direction of the cell
     */
    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
//        this.owner = owner;
//        this.direction = direction;
    }

    /**
     * Initializes a new {@link Cell}.
     */
    public Cell() {
        x = 0;
        y = 0;
    }
    
    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 1619 + x;
        hash = hash * 1789 + y;
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Cell))
            return false;
        Cell other = (Cell) obj;
        if (other.getX() == getX() && other.getY() == getY()) {
            return true;
        }
        return false;
    }

}
