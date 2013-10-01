
package avs.hazelcast;

import java.io.Serializable;
import avs.game.Cell;

public class WorkLoadReturn implements Serializable {

    private static final long serialVersionUID = -2581477518340282629L;

    private int x, y, initialX, initialY, ai, player, counter;

    public WorkLoadReturn(int x, int y, int initialX, int initialY, int ai, int player, int counter) {
        setX(x);
        setY(y);
        setInitialX(initialX);
        setInitialY(initialY);
        setAi(ai);
        setPlayer(player);
        setCounter(counter);
    }

    public WorkLoadReturn(Cell cell, int initialX2, int initialY2, int cellsPossessedByAiCount, int cellsPossessedByPlayerCount, int counter2) {
        setX(cell.getX());
        setY(cell.getY());
        setInitialX(initialX2);
        setInitialY(initialY2);
        setPlayer(cellsPossessedByPlayerCount);
        setAi(cellsPossessedByAiCount);
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
