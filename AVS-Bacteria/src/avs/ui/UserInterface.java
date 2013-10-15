/**
 * 
 */
package avs.ui;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import java.util.Set;
import javax.swing.JPanel;
import com.hazelcast.core.Member;
import com.hazelcast.monitor.LocalExecutorStats;
import avs.game.Attributes;
import avs.game.CellChange;
import avs.game.GameGrid;
import avs.game.GameManager;

/**
 * @author HZU
 * 
 */
public class UserInterface extends JPanel implements MouseMotionListener, MouseListener {
	/**
     * 
     */
    private static final long serialVersionUID = -2569343227591412718L;
    private UIRenderer renderer;
	private GameManager gameManager;
	public UserInterface() {
		super();
		renderer = new UIRenderer(this);
		new Thread(renderer).start();

		addMouseMotionListener(this);
		addMouseListener(this);

	}

	public void initialize(GameManager gameManager) {
		this.gameManager = gameManager;
		renderer.initialize(gameManager);
	}

	public void setGameGrid(GameGrid gameGrid) {
		renderer.setGameGrid(gameGrid);
	}

	@Override
	public void paintComponent(Graphics g) {
	    Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
	    Thread.currentThread().setName("AVS:AWT Thread");
		renderer.draw(g);
		g.dispose();
	}

	public void setControl(boolean controlflag) {
		renderer.setControl(controlflag);
	}

	public void updateGrid(LinkedList<CellChange> changes) {
		// TODO Auto-generated method stub
		renderer.updateGrid(changes);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		renderer.mouseLastEvent = e;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		renderer.mouseLastEvent = e;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Point p = renderer.getClickedMouseField(e);
		if (p.x != -1 && p.y != -1 && gameManager.isPlayersTurn()) {
			gameManager.chooseCell(p.x, p.y, Attributes.PLAYER);
		}
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			renderer.mouseButtonR = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			renderer.mouseButtonR = false;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

    public void setWork(int work) {
        renderer.setWork(work);
    }

    public void setWorkDone(int workDone) {
        renderer.setWorkDone(workDone);
    }

    public void setStats(LocalExecutorStats localExecutorStats) {
        renderer.setStats(localExecutorStats);
    }

    public void setMemberStats(Set<Member> members) {
        renderer.setMemberStats(members);
    }

}
