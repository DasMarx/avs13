/**
 * 
 */
package avs.game;

import java.util.LinkedList;

/**
 * @author HZU
 *
 */
public class GameGrid {

	private Cell[][] gameGrid;
	
	
	private int cellsPossessedByPlayer;
	private int cellsPossessedByAI;
	
	public boolean chooseCell(int x, int y, int owner) {
		//TODO:
		checkTurn(x, y, owner);
		return false;
		//TODO: to complete
	}
	
	private boolean checkTurn(int x, int y, int owner) {
		return false;
		//TODO: to complete
	}

	public int getCellsPossessedByPlayer() {
		return cellsPossessedByPlayer;
	}

	public int getCellsPossessedByAI() {
		return cellsPossessedByAI;
	}

	
	public LinkedList<CellChanges> processChanges(int x, int y) {
		//TODO:
		return null;
	}
	
	
	public void initialize() {
		//TODO: Initialize Gamegrid, Values, Directions, Cells, etc.
	}
	
	public Cell getCell(int x, int y) {
		return gameGrid[x][y];
	}
	
}
