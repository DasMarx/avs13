package avs;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

import avs.ui.UserInterface;


/**
 * 
 */

/**
 * @author HZU
 *
 */
public class Main {

	public static void main(String[] args){

		
		JPanel panel= new UserInterface();
		JFrame frame = new JFrame();
		frame.setTitle("AVS - Bacteria2");

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.add(panel);
		
		frame.setSize(650,670);
		frame.setLocation(50,50);
		frame.setVisible(true);
		
		
	}
	
}
