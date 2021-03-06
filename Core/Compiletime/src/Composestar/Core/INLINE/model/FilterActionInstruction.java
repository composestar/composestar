/*
 * Created on 12-sep-2006
 *
 */
package Composestar.Core.INLINE.model;

import java.util.Collection;
import java.util.Collections;

import Composestar.Core.CpsRepository2.JoinPointContextArgument;
import Composestar.Core.CpsRepository2.FilterElements.CanonAssignment;
import Composestar.Core.CpsRepository2.FilterElements.FilterElement;
import Composestar.Core.CpsRepository2.FilterModules.Filter;
import Composestar.Core.CpsRepository2.TypeSystem.CpsMessage;

/**
 * Instruction indicating a FilterAction.
 * 
 * @author Arjan
 */
public class FilterActionInstruction extends Instruction
{
	/**
	 * The filter definition which is responsible for this filter action
	 * instruction.
	 */
	private Filter filter;

	/**
	 * The filter element that resulted in this filter action. Note that this
	 * will be null when this filter action is the result of a rejection.
	 */
	private FilterElement filterElement;

	/**
	 * The type of the filteraction
	 */
	private String type;

	/**
	 * The current message in the filterset
	 */
	private CpsMessage message;

	/**
	 * Indicates whether the action is on call (true) or on return (false)
	 */
	private boolean onCall;

	/**
	 * Indicates whether the action returns the flow (only when the action is on
	 * call).
	 */
	private boolean returning;

	/**
	 * Collection of filter arguments
	 */
	private Collection<CanonAssignment> arguments;

	/**
	 * If true perform resource operation bookkeeping for this action.
	 * BookKeeping in the FilterAction is subject to the bookkeeping of the
	 * parent FilterCode.
	 */
	private boolean bookkeeping;

	/**
	 * A string containing the resource operations that lead to this filter
	 * action as harvested from the execution model.
	 */
	private String resourceOperations = "";

	/**
	 * Should a JointPointContext be created for this filter action?
	 */
	private JoinPointContextArgument neededJPC = JoinPointContextArgument.UNUSED;

	/**
	 * The constructor
	 * 
	 * @param actionType The type of the filteraction.
	 * @param msg The current message.
	 * @param substitutedMessage The substituted message (substitutionpart
	 *            applied on the current message).
	 * @param onCallAction Indicates whether the action is on call (true) or on
	 *            return (false)
	 * @param returningAction Indicates whether the action returns the flow
	 *            (only when the action is on call).
	 */
	public FilterActionInstruction(String actionType, CpsMessage msg, Collection<CanonAssignment> args,
			boolean onCallAction, boolean returningAction)
	{
		type = actionType;
		message = msg;
		onCall = onCallAction;
		returning = returningAction;
		arguments = args;
	}

	/**
	 * @return the message
	 */
	public CpsMessage getMessage()
	{
		return message;
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * @return the arguments
	 */
	public Collection<CanonAssignment> getArguments()
	{
		return Collections.unmodifiableCollection(arguments);
	}

	/**
	 * @return the onCall
	 */
	public boolean isOnCall()
	{
		return onCall;
	}

	/**
	 * @return the returning
	 */
	public boolean isReturning()
	{
		return returning;
	}

	/**
	 * @param value
	 * @see #bookkeeping
	 */
	public void setBookKeeping(boolean value)
	{
		bookkeeping = value;
	}

	/**
	 * @return
	 * @see #bookkeeping
	 */
	public boolean getBookKeeping()
	{
		return bookkeeping;
	}

	/**
	 * @param value
	 * @see #resourceOperations
	 */
	public void setResourceOperations(String value)
	{
		if (value == null)
		{
			value = "";
		}
		resourceOperations = value;
	}

	/**
	 * @see #resourceOperations
	 * @return
	 */
	public String getResourceOperations()
	{
		return resourceOperations;
	}

	/**
	 * @see Composestar.Core.INLINE.model.Visitable#accept(Composestar.Core.INLINE.model.Visitor)
	 */
	public Object accept(Visitor visitor)
	{
		return visitor.visitFilterAction(this);
	}

	/**
	 * @return the createJPC
	 */
	public JoinPointContextArgument getNeededJPC()
	{
		return neededJPC;
	}

	/**
	 * @param jpc the createJPC to set
	 */
	public void setCreateJPC(JoinPointContextArgument jpc)
	{
		neededJPC = jpc;
	}

	/**
	 * @param value the filter to set
	 */
	public void setFilter(Filter value)
	{
		filter = value;
	}

	/**
	 * @return the filter
	 */
	public Filter getFilter()
	{
		return filter;
	}

	/**
	 * @param value the filterElement to set
	 */
	public void setFilterElement(FilterElement value)
	{
		filterElement = value;
	}

	/**
	 * @return the filterElement
	 */
	public FilterElement getFilterElement()
	{
		return filterElement;
	}
}
