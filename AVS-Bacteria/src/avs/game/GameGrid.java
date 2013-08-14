/**
 * 
 */
package avs.game;

import java.util.LinkedList;
import java.util.Random;

/**
 * @author HZU
 * 
 */
public class GameGrid {

	private Cell[][] gameGrid;

	private int cellsPossessedByPlayer;
	private int cellsPossessedByAI;

	Random r;

	public int getCellsPossessedByPlayer() {
		return cellsPossessedByPlayer;
	}

	public int getCellsPossessedByAI() {
		return cellsPossessedByAI;
	}

	public void initialize() {
		r = new Random();
		gameGrid = new Cell[29][29];
		for (int i = 0; i < 30; i++) {
			for (int j = 0; j < 30; j++) {
				if ((i == 0) && (j == 0))
					gameGrid[i][j] = new Cell(i, j, -1, 0);
				if ((i == 29) && (j == 29))
					gameGrid[i][j] = new Cell(i, j, 1, 2);
				gameGrid[i][j] = new Cell(i, j, 0, r.nextInt(4));
			}
		}

	}

	public Cell getCell(int x, int y) {
		if (x < 0 || y < 0 || x > 29 || y > 29)
			return null;
		return gameGrid[x][y];
	}

}
