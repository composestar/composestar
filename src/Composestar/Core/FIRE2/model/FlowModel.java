/*
 * Created on 20-feb-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.Core.FIRE2.model;

import java.util.Enumeration;
import java.util.Vector;

import Composestar.Core.RepositoryImplementation.RepositoryEntity;

/**
 * @author Arjan
 *
 */
public class FlowModel extends RepositoryEntity{
    private FlowNode startNode;
    private FlowNode endNode;
    private Vector nodes;
    private Vector transitions;
    
    
    public FlowModel(){
        super();
        
        nodes = new Vector();
        transitions = new Vector();
    }
    
    public void addNode( FlowNode node ){
        nodes.addElement( node );
    }
    
    public void addTransition( FlowTransition transition ){
        transitions.addElement( transition );
    }
    
    
    /**
     * @return Returns the startNode.
     */
    public FlowNode getStartNode() {
        return startNode;
    }
    /**
     * @param startNode The startNode to set.
     */
    public void setStartNode(FlowNode startNode) {
        this.startNode = startNode;
    }
    
    
    /**
     * @return Returns the endNode.
     */
    public FlowNode getEndNode() {
        return endNode;
    }
    /**
     * @param endNode The endNode to set.
     */
    public void setEndNode(FlowNode endNode) {
        this.endNode = endNode;
    }
    
    public Enumeration getNodes(){
        return nodes.elements();
    }
    
    public Enumeration getTransitions(){
        return transitions.elements();
    }
}
