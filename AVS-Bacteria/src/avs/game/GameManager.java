/**
 * 
 */
package avs.game;


import java.util.LinkedList;

import avs.ui.UserInterface;
/**
 * @author HZU
 *
 */
public class GameManager {
	private final UserInterface userInterface;
	private GameGrid gameGrid;
	
	public GameManager(UserInterface userInterface) {
		this.userInterface = userInterface;
	}

	public void run() {
		//TODO Auto-generated method stub

	}

	
	public LinkedList<CellChanges> chooseCell(int x, int y, int owner) {
		if (gameGrid.chooseCell(x, y, owner)) {
			LinkedList<CellChanges> changes = gameGrid.processChanges(x, y);
//			AI starten
			
			return changes;
			
		}
		return null;
	}
	

	public void initialize() {
		//TODO Auto-generated method stub
		//TODO: New gameGrid, 
		//TODO: new AI
		gameGrid = new GameGrid();
	}

		
	
	
}
