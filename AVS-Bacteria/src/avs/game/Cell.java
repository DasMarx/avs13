package avs.game;

public class Cell {
	private int x;
	private int y;
	private int owner;
	private int direction;
	/**
	 * @return the direction
	 */
	public int getDirection() {
		return direction;
	}

	/**
	 * @return the owner
	 */
	public int getOwner() {
		return owner;
	}
	/**
	 * @param owner the owner to set
	 */
	public void setOwner(int owner) {
		this.owner = owner;
	}
	
	/**
	 * @return the x value
	 */
	public int getX(){
		return x;
	}
	
	/**
	 * @return the y value
	 */
	public int getY(){
		return y;
	}


	public Cell(int x, int y, int owner, int direction) {
		this.x = x;
		this.y = y;
		this.owner = owner;
		this.direction = direction;
	}

	public void turn () {
		direction = (++direction) % 4;
	}

}
