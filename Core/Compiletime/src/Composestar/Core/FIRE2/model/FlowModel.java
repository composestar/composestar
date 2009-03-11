/*
 * Created on 20-feb-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.Core.FIRE2.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * The base flow model of the filters
 * 
 * @author Arjan
 */
public interface FlowModel extends Serializable
{

	/**
	 * @return Returns the startNode.
	 */
	FlowNode getStartNode();

	/**
	 * @return Returns the endNode.
	 */
	FlowNode getEndNode();

	/**
	 * @return Returns all nodes
	 * @deprecated use getNodesEx()
	 */
	@Deprecated
	Iterator<FlowNode> getNodes();

	/**
	 * @return returns readonly list of all nodes
	 */
	List<FlowNode> getNodesEx();

	/**
	 * @return Returns all transitions
	 * @deprecated use getTransitionsEx();
	 */
	@Deprecated
	Iterator<FlowTransition> getTransitions();

	/**
	 * @return Returns all transitions
	 */
	List<FlowTransition> getTransitionsEx();
}
