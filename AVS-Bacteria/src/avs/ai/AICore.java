
package avs.ai;

import avs.game.GameManager;
import avs.game.GameGrid;

public class AICore {

    private GameManager gm;
    private boolean running = true;

    public void initialize(GameManager gm) {
        gm = this.gm;
    }

    public void run() {
        GameGrid grid = gm.getGrid();
        
    }
}
