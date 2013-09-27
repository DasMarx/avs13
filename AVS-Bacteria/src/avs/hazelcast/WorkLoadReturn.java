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
    
    private int x, y, ai, player,counter;
    
    public WorkLoadReturn(int x, int y, int ai, int player, int counter) {
        this.setX(x);
        this.setY(y);
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
