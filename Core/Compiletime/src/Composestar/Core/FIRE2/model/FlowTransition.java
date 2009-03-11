/*
 * Created on 20-feb-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.Core.FIRE2.model;

import java.io.Serializable;

/**
 * A transition in the flow model
 * 
 * @author Arjan
 */
public interface FlowTransition extends Serializable
{
	/**
	 * A normal transition
	 */
	static final int FLOW_NEXT_TRANSITION = 1;

	/**
	 * The false transition in a branch
	 */
	static final int FLOW_FALSE_TRANSITION = 2;

	/**
	 * The true transition in a branch
	 */
	static final int FLOW_TRUE_TRANSITION = 3;

	/**
	 * @return Returns the type.
	 */
	int getType();

	/**
	 * @return Returns the endNode.
	 */
	FlowNode getEndNode();

	/**
	 * @return Returns the startNode.
	 */
	FlowNode getStartNode();
}
