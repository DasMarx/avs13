/**
 * 
 */
package avs.ui;

import java.awt.Graphics;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

/**
 * @author HZU
 *
 */
public class UserInterface extends JPanel{
private UIRenderer renderer;
	public UserInterface() {
		renderer = new UIRenderer(this);
		new Thread(renderer).start();
	}
	
	
	@Override
    public void paintComponent(Graphics g) {
		renderer.draw(g);
    }



}
