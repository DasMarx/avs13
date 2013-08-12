package avs;

import javax.swing.JFrame;
import javax.swing.JPanel;

import avs.game.GameManager;
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
		JFrame frame = new JFrame();
		frame.setTitle("AVS - Bacteria");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setSize(670,690);
		frame.setLocation(50,50);
		frame.setVisible(true);
		
		UserInterface userInterface= new UserInterface();
		frame.add(userInterface);
		
		GameManager gameManager = new GameManager(userInterface);
		gameManager.initialize();
		gameManager.run();
		
		
	}
	
}