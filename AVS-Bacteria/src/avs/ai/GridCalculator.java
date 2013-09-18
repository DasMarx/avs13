
package avs.ai;

import java.util.LinkedList;
import avs.game.Attributes;
import avs.game.Cell;
import avs.game.CellChanges;
import avs.game.GameGrid;

public class GridCalculator {

    private GameGrid gameGrid;

    private LinkedList<LinkedList<CellChanges>> allChanges = new LinkedList<LinkedList<CellChanges>>();

    public GridCalculator(GameGrid gameGrid) {
        this.gameGrid = gameGrid.getCopy();
    }

    public boolean chooseCell(int x, int y, int owner) {
        LinkedList<CellChanges> changes = processChanges(x, y);
        allChanges.add(changes);
        return true;
    }

    /**
     * @param x coordinate of the cell to be turned
     * @param y coordinate of the cell to be turned
     * @return list of changes, mutating
     */
    public LinkedList<CellChanges> processChanges(int x, int y) {
        LinkedList<CellChanges> changes = new LinkedList<CellChanges>();
        Cell target = null;
        synchronized (gameGrid) {
            target = gameGrid.getCell(x, y);
            target.turn();
        }

        Cell nextNeighbour = gameGrid.getCell(x, y - 1);
        if (nextNeighbour != null && ((nextNeighbour.getDirection() == Attributes.DOWN) || target.getDirection() == Attributes.UP)) {
            processChanges(target, nextNeighbour, 0, target.getOwner(), changes);
        }
        nextNeighbour = gameGrid.getCell(x + 1, y);
        if (nextNeighbour != null && ((nextNeighbour.getDirection() == Attributes.LEFT) || target.getDirection() == Attributes.RIGHT)) {
            processChanges(target, nextNeighbour, 0, target.getOwner(), changes);
        }
        nextNeighbour = gameGrid.getCell(x, y + 1);
        if (nextNeighbour != null && ((nextNeighbour.getDirection() == Attributes.UP) || target.getDirection() == Attributes.DOWN)) {
            processChanges(target, nextNeighbour, 0, target.getOwner(), changes);
        }
        nextNeighbour = gameGrid.getCell(x - 1, y);
        if (nextNeighbour != null && ((nextNeighbour.getDirection() == Attributes.RIGHT) || target.getDirection() == Attributes.LEFT)) {
            processChanges(target, nextNeighbour, 0, target.getOwner(), changes);
        }
        return changes;
    }

    /**
     * @param origin cell edited before
     * @param target cell to be edited
     * @param rs counter for the recursive step
     * @param owner of the cells
     */
    private void processChanges(Cell origin, Cell target, int rs, int owner, LinkedList<CellChanges> changes) {

        Cell nextNeighbour = gameGrid.getCell(target.getX(), target.getY() - 1);
        if (nextNeighbour != origin && nextNeighbour != null && ((nextNeighbour.getDirection() == Attributes.DOWN) || target.getDirection() == Attributes.UP)) {
            processChanges(target, nextNeighbour, rs + 1, owner, changes);
        }
        nextNeighbour = gameGrid.getCell(target.getX() + 1, target.getY());
        if (nextNeighbour != origin && nextNeighbour != null && ((nextNeighbour.getDirection() == Attributes.LEFT) || target.getDirection() == Attributes.RIGHT)) {
            processChanges(target, nextNeighbour, rs + 1, owner, changes);
        }
        nextNeighbour = gameGrid.getCell(target.getX(), target.getY() + 1);
        if (nextNeighbour != origin && nextNeighbour != null && ((nextNeighbour.getDirection() == Attributes.UP) || target.getDirection() == Attributes.DOWN)) {
            processChanges(target, nextNeighbour, rs + 1, owner, changes);
        }
        nextNeighbour = gameGrid.getCell(target.getX() - 1, target.getY());
        if (nextNeighbour != origin && nextNeighbour != null && ((nextNeighbour.getDirection() == Attributes.RIGHT) || target.getDirection() == Attributes.LEFT)) {
            processChanges(target, nextNeighbour, rs + 1, owner, changes);
        }

        changeOwner(target, owner);
        changes.add(new CellChanges(target, rs));
    }

    /**
     * @param target cell to be edited
     * @param owner to be set
     */
    private void changeOwner(Cell target, int owner) {
        gameGrid.removeCell(target);
        gameGrid.getCell(target.getX(), target.getY()).setOwner(owner);
        if (owner == Attributes.PLAYER)
            gameGrid.addCellPlayer(target);
        else
            gameGrid.addCellAI(target);
    }
    
    private LinkedList<Cell> getBorderCells(){
    	LinkedList<Cell> border = new LinkedList<Cell>();
    	LinkedList<Cell> cells = gameGrid.getCellsPossessedByAI();
    	Cell c;
    	
    	for(int i = 0; i < cells.size(); i++){
    		c = cells.get(i);
    		if ((gameGrid.getCell(c.getX(), c.getY()-1) != null) && (gameGrid.getCell(c.getX(), c.getY()-1).getOwner() != Attributes.AI)) border.add(c); 
    		else if ((gameGrid.getCell(c.getX()+1, c.getY()) != null) && (gameGrid.getCell(c.getX()+1, c.getY()).getOwner() != Attributes.AI)) border.add(c); 
    		else if ((gameGrid.getCell(c.getX(), c.getY()+1) != null) && (gameGrid.getCell(c.getX(), c.getY()+1).getOwner() != Attributes.AI)) border.add(c); 
    		else if ((gameGrid.getCell(c.getX()-1, c.getY()) != null) && (gameGrid.getCell(c.getX()-1, c.getY()).getOwner() != Attributes.AI)) border.add(c); 
    		
    	}
    	
    	return border;
    }
}
