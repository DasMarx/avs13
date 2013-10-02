
package avs.hazelcast;

import java.io.Serializable;
import avs.game.Cell;

public class WorkLoadReturn implements Serializable {

    private static final long serialVersionUID = -2581477518340282629L;

    private int x, y, initialX, initialY, rating, counter;

    public WorkLoadReturn(int x, int y, int initialX, int initialY, int rating, int counter) {
        setX(x);
        setY(y);
        setInitialX(initialX);
        setInitialY(initialY);
        setRating(rating);
        setCounter(counter);
    }

    public WorkLoadReturn(Cell cell, int initialX2, int initialY2, int rating, int counter2) {
        setX(cell.getX());
        setY(cell.getY());
        setInitialX(initialX2);
        setInitialY(initialY2);
        setRating(rating);
        setCounter(counter2);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getInitialY() {
        return initialY;
    }

    public void setInitialY(int initialY) {
        this.initialY = initialY;
    }

    public int getInitialX() {
        return initialX;
    }

    public void setInitialX(int initialX) {
        this.initialX = initialX;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
