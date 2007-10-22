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
 * @author Arjan de Roo
 */
public abstract class ExecutionState implements Serializable
{
	private FlowNode flowNode;

	private Message message;

	private Message substitutionMessage;

	private int stateType;

	public final static int ENTRANCE_STATE = 1;

	public final static int EXIT_STATE = 2;

	public final static int NORMAL_STATE = 3;

	public ExecutionState(FlowNode flowNode, Message message, Message substitutionMessage, int stateType)
	{
		super();

		this.flowNode = flowNode;
		this.message = message;
		this.substitutionMessage = substitutionMessage;
		this.stateType = stateType;
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

	public abstract List<ExecutionTransition> getOutTransitionsEx();

	@Override
	public int hashCode()
	{
		int hashCode = flowNode.hashCode() + message.hashCode();

		return hashCode;
	}

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
