package avs.ai;

import java.util.LinkedList;
import avs.client.*;

/**
 * AI Client
 * gets workloads from AICore and returns Trees
 * 
 * 
 */
public class AIShard {

    public void run(){
        
        //workload bekommen
        LinkedList<Point> initializingCoordinates = new LinkedList<Point>(); //hier LinkedList aus workload eintragen
        ClientManager CM = new ClientManager(/*GameGrid �bergeben*/);
        CM.initialize(initializingCoordinates);
        CM.processTurn(/*Koordinate �bergeben*/);
        
    }
}
