
package avs.game;

public class Cell {

    private int x;

    private int y;

    private EnumOwner owner;

    private EnumDirection direction;

    /**
     * @return the direction
     */
    public EnumDirection getDirection() {
        return direction;
    }

    /**
     * @return the owner
     */
    public EnumOwner getOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(EnumOwner owner) {
        this.owner = owner;
    }

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

    public Cell(int x, int y, EnumOwner owner, EnumDirection direction) {
        this.x = x;
        this.y = y;
        this.owner = owner;
        this.direction = direction;
    }

    public void turn() {
        switch (direction) {
        case UP:
            direction = EnumDirection.RIGHT;
            break;
        case RIGHT:
            direction = EnumDirection.DOWN;
            break;
        case DOWN:
            direction = EnumDirection.LEFT;
            break;
        case LEFT:
            direction = EnumDirection.UP;
            break;
        }
    }

}
