/**
 * 
 */
package avs.ui;

import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author HZU
 *
 */
public class UserInterface extends JPanel{

	@Override
    public void paintComponent(Graphics g) {
		for (int i = 0; i < 20;i++) {
			for (int j = 0;j < 20; j++) {
				
			
			g.drawRect(i*32, j*32, 30, 30);
			}
		}
    	
    	
    }

}
