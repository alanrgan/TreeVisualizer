package application;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

//TODO JAVADOCS
public class Tree<T> {

	private T data;
	private Tree<T> parent = null;
	private List<Tree<T>> children = new ArrayList<Tree<T>>();
    private HashMap<T, Tree<T>> locate = new HashMap<T, Tree<T>>();
        
    public Tree(T data) {
        setData(data);
    }
        
    /*
     * Add a node under a specified root node.
     * If root node does not exist, it will be created.
     */
    public void addNode(T root, T data) {
        if(locate.containsKey(root))
        	locate.get(root).addNode(data);
        else
        	addNode(root).addNode(data);
    }
        
    public Tree<T> addNode(T data) {
        Tree<T> node = new Tree<T>(data);
        children.add(node);
        node.parent = this;
        node.locate = this.locate;
        locate.put(data, node);
        return node;
    }
    
    public Tree<T> setAsParent(T parentRoot) {
    	Tree<T> t = new Tree<T>(parentRoot);
    	t.children.add(this);
    	this.parent = t;
    	t.locate = this.locate;
    	t.locate.put(data, this);
    	t.locate.put(parentRoot, t);
    	return t;
    }
    
    public void setData(T data) {
    	this.data = data;
    	locate.put(data, this);
    }
        
    public T getData() {
        return data;
    }
        
    public Tree<T> getParent() {
      	return parent;
    }
        
    public Tree<T> getTree(T element) {
        return locate.get(element);
    }
        
    /**
     * Returns a list of the data from each child node of the
     * root node.
     * 
     * @param  root the value of the head of the subtree to retrieve 
     * @return 		a list of the values of the immediate children of the root node
     */
    public List<T> getSuccessors(T root) {
        List<T> successors = new ArrayList<T>();
        Tree<T> tree = locate.get(root);
        if(tree != null) {
        	for(Tree<T> node : tree.children) {
        		successors.add(node.getData());
        	}
        }
        return successors;
    }
    
    /**
     * Returns a list of the values of the root leaf and every leaf below the level of the
     * node containing the root value.
     * 
     * @param  root the value of the highest node of reference
     * @return 		a list containing the values of the subleaves
     */
    public List<T> getSubLeafs(T root) {
    	List<T> subleafs = new ArrayList<T>();
    	Tree<T> tree = locate.get(root);
    	
    	if(tree != null) {
    		if(!tree.hasChildren()) {
    			subleafs.add(tree.getData());
    			return subleafs;
    		}
    		else
    			subleafs.add(tree.getData());
    		
    		for(Tree<T> node : tree.children)
    			subleafs.addAll(getSubLeafs(node.getData()));
    	}
    	
    	return subleafs;
    }
        
    public List<Tree<T>> getChildNodes() {
     	return children;
    }
    
    /**
     * Removes a particular element from the entire tree
     * 
     * @param element the element to be removed
     */
    public void remove(T element) {
    	Tree<T> removeElement = getTree(element);
    	
    	recurseRemove(this, removeElement);
    	children.remove(removeElement);
    	locate.remove(element);
    	
    	//Remove all instances of child nodes from the lookup array.
    	for(Tree<T> tree : removeElement.getChildNodes()) {
			locate.remove(tree.getData());
    	}
    }
    
    private void recurseRemove(Tree<T> t, Tree<T> element) {
    	if(t.children.isEmpty() == false) { // Checks to see if the current node has any children.
    		t.children.remove(element);
    		for(Tree<T> subTree : t.children)	// Traverses through each child node and removes all instances of the element.
    			recurseRemove(subTree, element);
    	}
    }
    
    public void clear() {
    	clear(this, children);
        children.clear();
    }
    
    private void clear(Collection<Tree<T>> clearList) {
    	for(Tree<T> t : clearList)
    		locate.remove(t.getData());
    }
    
    private void clear(Tree<T> t, Collection<Tree<T>> clearList) {
    	if(t.getParent() != null) {
    		t.getParent().clear(clearList);
    		clear(t.getParent(), clearList);
    	}
    }
    
    /*
     * Returns the highest number of nodes in a level of the tree
     */
    public int maxWidth() {
    	List<Tree<T>> CurrNodes = new ArrayList<Tree<T>>();
    	CurrNodes.add(this);
    	int maxWidth = 1;
    	List<Tree<T>> NextNodes = new ArrayList<Tree<T>>();
    	while(!CurrNodes.isEmpty()) {
    		for(Tree<T> t : CurrNodes)
    			NextNodes.addAll(t.getChildNodes());
    		if(NextNodes.isEmpty())
    			break;
    		if(NextNodes.size() > maxWidth)
    			maxWidth = NextNodes.size();
    		CurrNodes.clear();
    		CurrNodes.addAll(NextNodes);
    		NextNodes.clear();
    	}
    	
    	return maxWidth;
    }
    
    
    /*
     * Returns the max height of a tree node
     * Recursive implementation 
     */
    public int getHeight(Tree<T> t, int num, int max) {
    	List<Tree<T>> childNodes = t.children;
    	if(t.getChildNodes().isEmpty())
    		return num + 1;
    	if(num > max)
    		max = num;
    	
    	for(int i = 0; i < childNodes.size(); i++)
    		max = Math.max(max, getHeight(childNodes.get(i), num + 1, max));
    	
    	return max;
    }
    
    public boolean hasChildren() {
    	return !children.isEmpty();
    }
        
    @Override
    public String toString() {
        return printTree(0);
    }

    private static final int indent = 2;

    private String printTree(int increment) {
        String s = "";
        String inc = "";
        for (int i = 0; i < increment; ++i) {
        	inc = inc + " ";
        }
        s = inc + data;
        for (Tree<T> child : children) {
            s += "\n" + child.printTree(increment + indent);
        }
        return s;
    }
}
