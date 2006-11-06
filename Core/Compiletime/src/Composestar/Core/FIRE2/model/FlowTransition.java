/*
 * Created on 20-feb-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.Core.FIRE2.model;

/**
 * @author Arjan
 */
public interface FlowTransition
{
	public static final int FLOW_NEXT_TRANSITION = 1;

	public static final int FLOW_FALSE_TRANSITION = 2;

	public static final int FLOW_TRUE_TRANSITION = 3;

	/**
	 * @return Returns the type.
	 */
	public int getType();

	/**
	 * @return Returns the endNode.
	 */
	public FlowNode getEndNode();

	/**
	 * @return Returns the startNode.
	 */
	public FlowNode getStartNode();
}
