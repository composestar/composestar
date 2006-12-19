/*
 * Created on 20-feb-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.Core.FIRE2.model;

import java.util.Enumeration;

/**
 * @author Arjan
 */
public interface FlowModel
{

	/**
	 * @return Returns the startNode.
	 */
	public FlowNode getStartNode();

	/**
	 * @return Returns the endNode.
	 */
	public FlowNode getEndNode();

	/**
	 * @return Returns all nodes
	 */
	public Enumeration getNodes();

	/**
	 * @return Returns all transitions
	 */
	public Enumeration getTransitions();
}
