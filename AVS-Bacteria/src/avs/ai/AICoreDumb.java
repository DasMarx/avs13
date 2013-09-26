
package avs.ai;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import avs.game.Attributes;
import avs.game.Cell;
import avs.game.CellChanges;
import avs.game.GameGrid;
import avs.game.GameManager;
import avs.hazelcast.HazelcastWorker;
import avs.hazelcast.WorkLoadReturn;
import com.hazelcast.core.IExecutorService;
import java.util.Random;

public class AICoreDumb {

    private GameManager gm;

    private boolean running = true;

    private LinkedList<Future<WorkLoadReturn>> futureQueue;

    private GameGrid grid;

    private HazelcastWorker myWorker;
    
    private int nextTurnX;

    private int nextTurnY;
    
    public void initialize(GameManager gm) {
        gm = this.gm;
        myWorker = new HazelcastWorker();
    }

    public void setGameGrid(GameGrid grid) {
        grid = this.grid;
    }
    
    public void updateGrid(LinkedList<CellChanges> cellChanges){
//        TODO    
    }
    public void setControl(boolean turn){
        if(turn) gm.chooseCell(nextTurnX, nextTurnY, Attributes.AI);
    }

    public void run() {
        while(true){
            LinkedList<Cell> ownedCells = grid.getCellsPossessedByAI();
            int size = ownedCells.size();
            int chosenCell = (int) (Math.random() * (size - 1) + 1);
            nextTurnX = ownedCells.get(chosenCell).getX();
            nextTurnY = ownedCells.get(chosenCell).getY();
        }
    }
}
