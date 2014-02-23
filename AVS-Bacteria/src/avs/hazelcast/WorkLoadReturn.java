
package avs.hazelcast;

import java.io.Serializable;
import avs.game.Cell;

public class WorkLoadReturn implements Serializable {

    private static final long serialVersionUID = -2581477518340282629L;

    private final int x, y, initialX, initialY, rating;

    private int counter;

    public WorkLoadReturn(int x, int y, int initialX, int initialY, int rating, int counter) {
        this.x = x;
        this.y = y;
        this.initialX = initialX;
        this.initialY = initialY;
        this.rating = rating;
        this.counter = counter;
    }

    public WorkLoadReturn(Cell cell, int initialX, int initialY, int rating, int counter) {
        this.x = cell.getX();
        this.y = cell.getY();
        this.initialX = initialX;
        this.initialY = initialY;
        this.rating = rating;
        this.counter = counter;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getInitialY() {
        return initialY;
    }

    public int getInitialX() {
        return initialX;
    }

    public int getCounter() {
        return counter;
    }

    public int getRating() {
        return rating;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

}
