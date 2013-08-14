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
	private int turn = 0;
	private LinkedList<LinkedList<CellChanges>> allChanges = new LinkedList<LinkedList<CellChanges>>();


	public GameManager(UserInterface userInterface) {
		this.userInterface = userInterface;
	}

	public void run() {

	}

	private LinkedList<Cell> checkNeighbour(Cell c){
		LinkedList<Cell> neighbours = new LinkedList<Cell>();
		Cell nextNeighbour = gameGrid.getCell(c.getX(), c.getY()-1);
		if(nextNeighbour != null){
			if(nextNeighbour.getDirection() == 2) neighbours.add(nextNeighbour);
		}
		nextNeighbour = null;
		nextNeighbour = gameGrid.getCell(c.getX()+1, c.getY());
		if(nextNeighbour != null){
			if(nextNeighbour.getDirection() == 3) neighbours.add(nextNeighbour);
		}
		nextNeighbour = null;
		nextNeighbour = gameGrid.getCell(c.getX(), c.getY()+1);
		if(nextNeighbour != null){
			if(nextNeighbour.getDirection() == 0) neighbours.add(nextNeighbour);
		}
		nextNeighbour = null;
		nextNeighbour = gameGrid.getCell(c.getX()-1, c.getY());
		if(nextNeighbour != null){
			if(nextNeighbour.getDirection() == 1) neighbours.add(nextNeighbour);
		}

		return neighbours;
	}

	public LinkedList<CellChanges> processChanges(int x, int y) {
		LinkedList<CellChanges> changes = new LinkedList<CellChanges>
		return null;
	}

	private boolean checkTurn(int x, int y, int owner) {
		Cell c1 = new Cell(x, y, owner, 0);
		Cell c2 = gameGrid.getCell(x, y);
		return c1.getOwner() == c2.getOwner();
	}

	public LinkedList<CellChanges> chooseCell(int x, int y, int owner) {
		if ((chooseCell(x, y, owner)) != null) {
			LinkedList<CellChanges> changes = processChanges(x, y);
		//			AI starten

		return changes;

		}
	}


	public boolean isPlayersTurn{
		boolean turn;
		if()
	}

	/**
	 * @return the Game Grid
	 */
	public GameGrid getGrid(){
		synchronized(gameGrid){
			return gameGrid;			
		}
	}

	/**
	 * @return last changes
	 */
	public LinkedList<CellChanges> getChanges(){
		synchronized(allChanges){
			return allChanges.getLast();
		}
	}

	public void initialize() {
		//TODO Auto-generated method stub
		//TODO: New gameGrid, 
		//TODO: new AI
		gameGrid = new GameGrid();
	}




}
