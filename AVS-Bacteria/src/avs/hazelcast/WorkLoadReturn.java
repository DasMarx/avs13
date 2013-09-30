
package avs.hazelcast;

import java.io.Serializable;
import avs.ai.Data;
import avs.ai.Tree;

public class WorkLoadReturn implements Serializable {

    /**
     * 
     */
    Tree<Data> resultTree;

    private static final long serialVersionUID = -2581477518340282629L;

    private int x, y, initialX, initialY, ai, player, counter;

    public WorkLoadReturn(int x, int y, int initialX, int initialY, int ai, int player, int counter) {
        this.setX(x);
        this.setY(y);
        setInitialX(initialX);
        setInitialY(initialY);
        this.setAi(ai);
        this.setPlayer(player);
        this.setCounter(counter);
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

    public int getAi() {
        return ai;
    }

    public void setAi(int ai) {
        this.ai = ai;
    }

    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }
}
