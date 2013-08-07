package avs;

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

		JPanel userInterface= new UserInterface();
		JFrame frame = new JFrame();
		frame.setTitle("AVS - Bacteria");

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.add(userInterface);
		
		frame.setSize(670,690);
		frame.setLocation(50,50);
		frame.setVisible(true);
		
		GameManager gameManager = new GameManager();
		
	}
	
}
