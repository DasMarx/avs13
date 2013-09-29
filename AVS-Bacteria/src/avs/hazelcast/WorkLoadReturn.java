package avs.hazelcast;

import java.io.Serializable;
import java.util.LinkedList;
import avs.ai.Data;
import avs.ai.Tree;


public class WorkLoadReturn implements Serializable {
     
    private static final long serialVersionUID = -2581477518340282629L;
    
    private int x, y,initialX,initialY,deepness,rating;
    
    private LinkedList<Workload> myWorkloadLinkedList = new LinkedList<Workload>();
    
    public WorkLoadReturn(int x, int y, int initialX, int initialY, int deepness, int rating) {
        setX(x);
        setY(y);
        setInitialX(initialX);
        setInitialY(initialY);
        setDeepness(deepness);
        setRating(rating);
    }

    public WorkLoadReturn(int x, int y, int initialX, int initialY, int deepness, int rating, LinkedList<Workload> myWorkloadLinkedList) {
        setX(x);
        setY(y);
        setInitialX(initialX);
        setInitialY(initialY);
        setDeepness(deepness);
        setRating(rating);
        setMyWorkloadLinkedList(myWorkloadLinkedList);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }


    public int getInitialY() {
        return initialY;
    }

    public void setInitialY(int initialY) {
        this.initialY = initialY;
    }

    public int getInitialX() {
        return initialX;
    }

    public void setInitialX(int initialX) {
        this.initialX = initialX;
    }

    public int getDeepness() {
        return deepness;
    }

    public void setDeepness(int deepness) {
        this.deepness = deepness;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public LinkedList<Workload> getMyWorkloadLinkedList() {
        return myWorkloadLinkedList;
    }

    public void setMyWorkloadLinkedList(LinkedList<Workload> myWorkloadLinkedList) {
        this.myWorkloadLinkedList = myWorkloadLinkedList;
    }
}
