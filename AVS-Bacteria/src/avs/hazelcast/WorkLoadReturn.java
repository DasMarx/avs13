package avs.hazelcast;

import java.io.Serializable;
import avs.ai.Data;
import avs.ai.Tree;


public class WorkLoadReturn implements Serializable{
    /**
     * 
     */
    Tree<Data> resultTree;
    int parentHash; 
    private static final long serialVersionUID = -2581477518340282629L;

}
