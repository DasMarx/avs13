package avs.ai;

import avs.game.GameManager;

public class AICore {

	private GameManager gm;

	public AICore(GameManager gm) {
		gm = this.gm;
	}

	public void run() {
		gm.getGrid();
	}
}
