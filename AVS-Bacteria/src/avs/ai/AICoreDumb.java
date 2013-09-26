
package avs.ai;

import java.util.LinkedList;
import java.util.concurrent.Future;
import avs.game.Attributes;
import avs.game.Cell;
import avs.game.CellChanges;
import avs.game.GameGrid;
import avs.game.GameManager;
import avs.hazelcast.HazelcastWorker;
import avs.hazelcast.WorkLoadReturn;

public class AICoreDumb {

    private GameManager gm;

    private boolean running = true;

    private LinkedList<Future<WorkLoadReturn>> futureQueue;

    private GameGrid grid;

    private HazelcastWorker myWorker;
    
    private int nextTurnX;

    private int nextTurnY;
    
    public void initialize(GameManager gm) {
        this.gm = gm;
        myWorker = new HazelcastWorker();
    }

    public void setGameGrid(GameGrid grid) {
        this.grid = grid;
    }
    
    public void updateGrid(LinkedList<CellChanges> cellChanges){
    	for(CellChanges changedCell: cellChanges){
    		int o = changedCell.getCell().getOwner();
    		if(o == Attributes.AI)
    			grid.addCellAI(changedCell.getCell());
    		if(o == Attributes.PLAYER)
    			grid.addCellPlayer(changedCell.getCell());    			
    	}
    		
        
    }
    public void setControl(boolean turn){
    	System.out.print("Bin ich dran: ");
    	if(gm.isPlayersTurn() == true)
    		System.out.println("Nö");
    	else
    		System.out.println("Jo");            
    	
    	LinkedList<Cell> ownedCells = grid.getCellsPossessedByAI();
        int size = ownedCells.size();
        int chosenCell = (int) (Math.random() * (size));
        nextTurnX = ownedCells.get(chosenCell).getX();
        nextTurnY = ownedCells.get(chosenCell).getY();
        
        if(turn) gm.chooseCell(nextTurnX, nextTurnY, Attributes.AI);
        System.out.println("Chose Cell: " + nextTurnX + " " + nextTurnY);
        System.out.print("Fertig!");
    }

    public void run() {
        while(running){
            LinkedList<Cell> ownedCells = grid.getCellsPossessedByAI();
            int size = ownedCells.size();
            int chosenCell = (int) (Math.random() * (size - 1) + 1);
            nextTurnX = ownedCells.get(chosenCell).getX();
            nextTurnY = ownedCells.get(chosenCell).getY();
        }
    }
}
