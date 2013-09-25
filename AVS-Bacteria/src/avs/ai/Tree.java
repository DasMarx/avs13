
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
        
        /**
         * This method will return the current Data
         * @return the given Data
         */
        public T getData() {
            return data;
        }

        /**
         * This method will set the current Data
         * @param data
         */
        public void setData(T data) {
            this.data = data;
        }

        /**
         * This method will return all current Children
         * @return all children
         */
        public HashSet<Node<T>> getChildren() {
            return children;
        }

        /**
         * This method will set all Children
         * @param children
         */
        public void setChildren(HashSet<Node<T>> children) {
            this.children = children;
        }

        /**
         * This method will add one child
         * @param child
         */
        public void addChild(Node<T> child) {
            children.add(child);
        }
        
        /**
         * This method will remove one specific child
         * @param child
         */
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
