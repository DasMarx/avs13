
package avs;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import com.hazelcast.core.Hazelcast;
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
        
        WindowListener exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showOptionDialog(null, "Are You Sure to Close Application?", "Exit Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == 0) {
                    Hazelcast.shutdownAll();
                   System.exit(0);
                }
            }
        };
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(exitListener);

        UserInterface userInterface = new UserInterface();
        AICore aiCore = new AICore();

        frame.add(userInterface);
        frame.setSize(800, 820);
        frame.setLocation(1, 1);
        frame.setVisible(true);

        GameManager gameManager = new GameManager(userInterface, aiCore);
        gameManager.run();

    }


}
