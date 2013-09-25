
package avs;

import javax.swing.JFrame;
import avs.ai.AICore;
import avs.game.GameManager;
import avs.ui.UserInterface;

/**
 * 
 */

/**
 * @author HZU
 */
public class Main {

    public static void main(String[] args) {

        JFrame frame = new JFrame();
        frame.setTitle("AVS - Bacteria");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        UserInterface userInterface = new UserInterface(frame);
        AICore aiCore = new AICore();

        frame.add(userInterface);
        frame.setSize(800, 820);
        frame.setLocation(100, 100);
        frame.setVisible(true);

        GameManager gameManager = new GameManager(userInterface, aiCore);
        gameManager.run();

    }

}
