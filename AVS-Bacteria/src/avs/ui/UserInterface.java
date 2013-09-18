/**
 * 
 */
package avs.ui;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.util.LinkedList;

import javax.swing.JPanel;

import avs.game.CellChanges;
import avs.game.GameGrid;
import avs.game.GameManager;

/**
 * @author HZU
 * 
 */
public class UserInterface extends JPanel implements MouseMotionListener {

	private UIRenderer renderer;
	private GameManager gameManager;
	private GameGrid gameGrid;

	public UserInterface() {
		super();
		renderer = new UIRenderer(this);
		new Thread(renderer).start();

		addMouseMotionListener(this);

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

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO:
		System.exit(0);
		// TODO Auto-generated method stub

	}

}
