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
		for (int i = 0; i < 30;i++) {
			for (int j = 0;j < 30; j++) {
				
			
			g.drawRect(i*22, j*22, 20, 20);
			}
		}
    	
    	
    }

}
