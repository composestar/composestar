/*
 * Created on 22-feb-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.Core.FIRE2.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * A state in the filter execution model
 * 
 * @author Arjan de Roo
 */
public abstract class ExecutionState implements Serializable
{
	/**
	 * The associated flow node
	 * 
	 * @see #getFlowNode()
	 */
	private FlowNode flowNode;

	/**
	 * The incoming message
	 */
	private Message message;

	/**
	 * The substitution message. Depending on the filter this might become the
	 * new message
	 */
	private Message substitutionMessage;

	/**
	 * The type of state: entrance, exit or normal
	 * 
	 * @see #ENTRANCE_STATE
	 * @see #EXIT_STATE
	 * @see #NORMAL_STATE
	 */
	private int stateType;

	/**
	 * The start of an execution model
	 */
	public static final int ENTRANCE_STATE = 1;

	/**
	 * A final state in the execution model
	 */
	public static final int EXIT_STATE = 2;

	/**
	 * A normal state
	 */
	public static final int NORMAL_STATE = 3;

	public ExecutionState(FlowNode inFlowNode, Message inMessage, Message inSubstitutionMessage, int inStateType)
	{
		super();

		flowNode = inFlowNode;
		message = inMessage;
		substitutionMessage = inSubstitutionMessage;
		stateType = inStateType;
	}

	/**
	 * @return Returns the flowNode.
	 */
	public FlowNode getFlowNode()
	{
		return flowNode;
	}

	/**
	 * @return Returns the message.
	 */
	public Message getMessage()
	{
		return message;
	}

	/**
	 * @return the substitutionMessage
	 */
	public Message getSubstitutionMessage()
	{
		return substitutionMessage;
	}

	/**
	 * @return Returns the stateType.
	 */
	public int getStateType()
	{
		return stateType;
	}

	/**
	 * @deprecated use getOutTransitionsEx()
	 * @return
	 */
	@Deprecated
	public final Iterator<ExecutionTransition> getOutTransitions()
	{
		return getOutTransitionsEx().iterator();
	}

	/**
	 * @return all outgoing transitions
	 */
	public abstract List<ExecutionTransition> getOutTransitionsEx();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hashCode = flowNode.hashCode() + message.hashCode();
		return hashCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof ExecutionState))
		{
			return false;
		}

		ExecutionState state = (ExecutionState) obj;
		if (flowNode != state.flowNode)
		{
			return false;
		}

		if (!message.equals(state.message))
		{
			return false;
		}

		if (!substitutionMessage.equals(state.substitutionMessage))
		{
			return false;
		}

		// other fields not important for comparison

		return true;
	}
}
