/**
 * 
 */

package avs.ui;

import hazelcast.Echo;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import hazelcast.*;

/**
 * @author HZU
 */
public class UserInterface extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 8920280411170317728L;

    private JButton button1;

    private JButton button2;

    private JButton button3;

    private JPanel panelButton;

    private JLabel oben;

    private JTextArea anzeige;

    private HazelcastInstance myInstance;

    public UserInterface() {
        super("Fenster");
        setLocation(300, 300);
        setSize(1000, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Das BorderLayout ist mal das erste - später fügen wir noch ein GridLayout im Westen hinzu
        getContentPane().setLayout(new BorderLayout(5, 5));

        // Buttons erzeugen
        button1 = new JButton("Hazelcast starten");
        button2 = new JButton("Hazelcast stoppen");
        button3 = new JButton("Arbeit clusterweit ausführen");

        // Panels erzeugen auf einem GridLayout
        panelButton = new JPanel(new GridLayout(3, 1));

        // Auf Panel Buttons packen
        panelButton.add(button1);
        panelButton.add(button2);
        panelButton.add(button3);

        // Listener für Buttons
        addButtonListener(button1);
        addButtonListener(button2);
        addButtonListener(button3);

        // Labels erzeugen
        oben = new JLabel("Layout Test");
        // Label zentrieren
        oben.setHorizontalAlignment(JLabel.CENTER);
        anzeige = new JTextArea(5, 20);
        anzeige.setText("Button klicken");

        // Labels auf Frame packen (direkt auf das BorderLayout)
        getContentPane().add(BorderLayout.NORTH, oben);
        getContentPane().add(anzeige);

        // Panels auf Frame packen (das panelButton hat ein GridLayout, dass jetzt in den WestBereich des BorderLayouts kommt)
        getContentPane().add(BorderLayout.WEST, panelButton);

        pack();
        setVisible(true);

    }

    private void addButtonListener(JButton b) {
        b.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                if (ae.getSource() == button1) {
                    callbutton1();
                } else if (ae.getSource() == button2) {
                    callbutton2();
                } else if (ae.getSource() == button3) {
                    callbutton3();
                }
            }
        });
    }

    private synchronized void callbutton1() {
        if (myInstance == null) {
            myInstance = Hazelcast.newHazelcastInstance();
            eingabe("Hazelcast is started");
        } else {
            eingabe("Hazelcast already started");
        }

    }

    private synchronized void callbutton2() {
        if (myInstance != null) {
            Hazelcast.shutdownAll();
            eingabe("all Hazelcast instances are turned off");
            myInstance = null;
        } else {
            eingabe("Hazelcast is already turned off");
        }

    }

    private void callbutton3() {
        if (myInstance != null) {
            try {

                String myString = resultOnClusterSomewhere(myInstance, "Test", 5,1);
                System.out.println(myString);
                myString = resultOnClusterSomewhere(myInstance, "Test2", 5,5);
                System.out.println(myString);

                for (int i = 0; i <= 14; i = (i + 1)) {
                    for (int j = 1; j <= 10; j++) {
                        long startTime = System.currentTimeMillis();
                        resultOnClusterSomewhere(myInstance, "boing", i, j);
                        long time = System.currentTimeMillis() - startTime;
                        System.out.println("Task with deepness of " + i + " count " + j +" took " + time + " ms");
                    }
                }

            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void eingabe(String a) {
        anzeige.setText(a);
    }

    private String resultOnClusterSomewhere(HazelcastInstance myInstance, String myString, int deepness, int count) throws InterruptedException, ExecutionException {
        IExecutorService executorService = myInstance.getExecutorService("default");
        Future<String> future = executorService.submit(new Echo(myInstance.getName(), myString, deepness, count));
        String echoResult = future.get();
        return echoResult;
    }

}
