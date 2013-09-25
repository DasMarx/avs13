
package avs.ai;

import java.util.HashSet;

public class Tree<T> {

    private Node<T> root;

    public Tree(T rootData) {
        root = new Node<T>();
        root.setData(rootData);
        root.setChildren(new HashSet<Node<T>>());
    }

    public class Node<T> {

        private T data;
        
        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
        
        //private Node<T> parent;

        public HashSet<Node<T>> getChildren() {
            return children;
        }

        public void setChildren(HashSet<Node<T>> children) {
            this.children = children;
        }

        public void addChild(Node<T> child) {
            children.add(child);
        }
        
        public void removeChild(Node<T> child) {
            children.remove(child);
        }
        
        private HashSet<Node<T>> children;
        
        @Override
        public int hashCode() {
            return data.hashCode();
        }

        
    }
    /**
     * 
     * @return The current root node will be returned
     */
    public Node<T> getRoot(){
        return root;
    }
    
    /**
     * This method will set the current root node
     * @param node
     */
    public void setRoot(Node<T> node) {
        this.root = node;
    }

}
