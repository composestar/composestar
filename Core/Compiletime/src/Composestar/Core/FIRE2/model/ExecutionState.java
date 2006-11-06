/*
 * Created on 22-feb-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.Core.FIRE2.model;

import java.util.Enumeration;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MessageSelector;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;

/**
 * @author Arjan de Roo
 */
public abstract class ExecutionState
{
	private FlowNode flowNode;

	private Target substitutionTarget;

	private MessageSelector substitutionSelector;

	private Message message;

	private int stateType;

	public final static int ENTRANCE_STATE = 1;

	public final static int EXIT_STATE = 2;

	public final static int NORMAL_STATE = 3;

	public ExecutionState(FlowNode flowNode, Message message, MessageSelector substitutionSelector,
			Target substitutionTarget, int stateType)
	{
		super();

		this.flowNode = flowNode;
		this.message = message;
		this.substitutionSelector = substitutionSelector;
		this.substitutionTarget = substitutionTarget;
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
	 * @return Returns the selector.
	 */
	public MessageSelector getSelector()
	{
		return message.getSelector();
	}

	/**
	 * @return Returns the substitutionSelector.
	 */
	public MessageSelector getSubstitutionSelector()
	{
		return substitutionSelector;
	}

	/**
	 * @return Returns the substitutionTarget.
	 */
	public Target getSubstitutionTarget()
	{
		return substitutionTarget;
	}

	/**
	 * @return Returns the target.
	 */
	public Target getTarget()
	{
		return message.getTarget();
	}

	/**
	 * @return Returns the message.
	 */
	public Message getMessage()
	{
		return message;
	}

	/**
	 * @return Returns the stateType.
	 */
	public int getStateType()
	{
		return stateType;
	}

	public abstract Enumeration getOutTransitions();

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

		if (!Message.checkEquals(this.substitutionTarget, state.substitutionTarget))
		{
			return false;
		}

		if (!Message.checkEquals(this.substitutionSelector, state.substitutionSelector))
		{
			return false;
		}

		if (!this.message.equals(state.message))
		{
			return false;
		}

		// other fields not important for comparison

		return true;
	}
}
