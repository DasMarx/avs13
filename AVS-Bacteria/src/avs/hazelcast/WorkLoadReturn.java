
package avs.hazelcast;

import java.io.Serializable;

public class WorkLoadReturn implements Serializable {

    private static final long serialVersionUID = -2581477518340282629L;

    private final int x, y, rating;

    private int counter;

    public WorkLoadReturn(int x, int y, int rating, int counter) {
        this.x = x;
        this.y = y;
        this.rating = rating;
        this.counter = counter;
    }


    public int getY() {
        return y;
    }

    public int getX() {
        return x;
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
