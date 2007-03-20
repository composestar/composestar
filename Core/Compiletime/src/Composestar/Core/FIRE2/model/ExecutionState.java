/*
 * Created on 22-feb-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.Core.FIRE2.model;

import java.util.Iterator;

/**
 * @author Arjan de Roo
 */
public abstract class ExecutionState
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
	 * @return the original substitution message that hasn't been updated with
	 *         information from the original message. This means that the target
	 *         and selector could contain an '*'.
	 */
	public Message getBaseSubstitutionMessage()
	{
		return getSubstitutionMessage();
	}

	/**
	 * @return Returns the stateType.
	 */
	public int getStateType()
	{
		return stateType;
	}

	public abstract Iterator getOutTransitions();

	public int hashCode()
	{
		int hashCode = flowNode.hashCode() + message.hashCode();

		return hashCode;
	}

	public boolean equals(Object obj)
	{
		if (!(obj instanceof ExecutionState))
		{
			return false;
		}

		ExecutionState state = (ExecutionState) obj;
		if (this.flowNode != state.flowNode)
		{
			return false;
		}

		if (!this.message.equals(state.message))
		{
			return false;
		}

		if (!this.substitutionMessage.equals(state.substitutionMessage))
		{
			return false;
		}

		// other fields not important for comparison

		return true;
	}
}
