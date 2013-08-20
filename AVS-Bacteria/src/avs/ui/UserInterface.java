/**
 * 
 */
package avs.ui;

import java.awt.Graphics;
import java.util.LinkedList;
import javax.swing.JPanel;
import avs.game.CellChanges;
import avs.game.GameGrid;
import avs.game.GameManager;

/**
 * @author HZU
 *
 */
public class UserInterface extends JPanel{
/**
     * 
     */
    private static final long serialVersionUID = -1381602839153541975L;
    
    
private UIRenderer renderer;
private GameManager gameManager;
private GameGrid gameGrid;

	public UserInterface() {
        super();
        renderer = new UIRenderer(this);
        new Thread(renderer).start();
    }

	public void initialize(GameManager gameManager) {
	    this.gameManager = gameManager;
	    renderer.initialize(gameManager);
	}

	public void setGameGrid(GameGrid gameGrid) {
	    this.gameGrid = gameGrid;
	    renderer.setGameGrid(gameGrid);
	}
	
	
    @Override
    public void paintComponent(Graphics g) {
		renderer.draw(g);
    }

    public void setControl(boolean controlflag) {
        // TODO Auto-generated method stub
        renderer.setControl(controlflag);
    }

    public void updateGrid(LinkedList<CellChanges> changes) {
        // TODO Auto-generated method stub
        renderer.updateGrid(changes);
    }



}
