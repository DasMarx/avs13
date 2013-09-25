
package avs.ai;

import java.util.ArrayList;
import java.util.List;

public class Tree<T> {

    private Node<T> root;

    public Tree(T rootData) {
        root = new Node<T>();
        root.setData(rootData);
        root.children = new ArrayList<Node<T>>();
    }

    public static class Node<T> {

        private T data;
        
        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
        
        //private Node<T> parent;

        private List<Node<T>> children;
        

        
    }
    public Node<T> getRoot(){
        return root;
    }

}
