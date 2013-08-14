package avs.game;

public class CellChanges {
	private Cell cell;
	private int step;
	
	public CellChanges(Cell cell, int step){
		this.cell = cell;
		this.step = step;
	}
	
	/**
	 * @return the cell
	 */
	public Cell getCell(){
		return cell;
	}
	
	/**
	 * @return the step
	 */
	public int getStep(){
		return step;
	}
}
