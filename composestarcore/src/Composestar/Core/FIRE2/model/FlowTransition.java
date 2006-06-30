/*
 * Created on 20-feb-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.Core.FIRE2.model;


/**
 * @author Arjan
 *
 * 
 */
public class FlowTransition{
    /**
     * The type of the transition;
     */
    private int type;
    
    
    /**
     * The startNode
     */
	private FlowNode startNode;
	
	/**
	 * The endNode
	 */
	private FlowNode endNode;

    public static final int FLOW_NEXT_TRANSITION = 1;
    public static final int FLOW_FALSE_TRANSITION = 2;
    public static final int FLOW_TRUE_TRANSITION = 3;
	
	
	/**
	 * The constructor
	 *
	 */
	public FlowTransition( int type, FlowNode startNode, FlowNode endNode )
	{
		super();
		
		this.type = type;
		this.startNode = startNode;
		this.endNode = endNode;
		
		startNode.addTransition( this );
	}
	
	
	
    /**
     * @return Returns the type.
     */
    public int getType() {
        return type;
    }
    /**
     * @return Returns the endNode.
     */
    public FlowNode getEndNode() {
        return endNode;
    }
    
    /**
     * @return Returns the startNode.
     */
    public FlowNode getStartNode() {
        return startNode;
    }
    
    
}
