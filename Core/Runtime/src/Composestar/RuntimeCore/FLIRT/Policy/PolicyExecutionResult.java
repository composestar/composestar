package Composestar.RuntimeCore.FLIRT.Policy;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * PolicyExecutionResult.java 1518 2006-09-20 13:13:30Z reddog33hummer $ Models
 * the Result of the execution of a FilterPolicy. It says if the message should
 * continue to the next filtermodule, the last action taken, and a stack of the
 * ACT callbacks collected by the sent messages in the Meta Filters
 */
public class PolicyExecutionResult
{

	/**
	 * States if the message was accepted by a filter in the module.
	 */
	private boolean wasAccepted;

	/**
	 * States if the message shouldContinue to the next filtermodule
	 */
	private boolean shouldContinue;

	/**
	 * The result of the last action taken
	 */
	private Object actionResult;

	/**
	 * Constructs a policy execution result with a stack of callbacks
	 * 
	 * @param wasAccepted says if the message was accepted during the filtering
	 * @param shouldContinue says if the message should continue to the next
	 *            filter.
	 * @param actionResult the result of the last action taken
	 * @param stack stack of callbacks
	 * @roseuid 3F3653310057
	 */
	public PolicyExecutionResult(boolean wasAccepted, boolean shouldContinue, Object actionResult)
	{ // , ComposestarStack stack) {
		this.wasAccepted = wasAccepted;
		this.shouldContinue = shouldContinue;
		this.actionResult = actionResult;
		// this.callbacks = stack;
	}

	/**
	 * Constructs a Policy execution result without callbacks
	 * 
	 * @param wasAccepted says if the message was accepted during the filtering
	 * @param shouldContinue says if the message should continue to the next
	 *            filter.
	 * @param actionResult the result of the last action taken
	 * @roseuid 3F365331004D
	 */
	/*
	 * public PolicyExecutionResult(boolean wasAccepted, boolean shouldContinue,
	 * Object actionResult) { this.wasAccepted = wasAccepted;
	 * this.shouldContinue = shouldContinue; this.actionResult = actionResult;
	 * //this.callbacks = null; }
	 */

	/**
	 * Returns the callbacks that resulted from the execution
	 * 
	 * @return the callbacks if there were any, null otherwise
	 * @roseuid 3F3653310061
	 */
	/*
	 * public ComposestarStack getCallbacks() { return callbacks; }
	 */

	/**
	 * Tests if the message was accepted
	 * 
	 * @return TRUE if the message was accepted
	 * @roseuid 3F3653310062
	 */
	public boolean wasAccepted()
	{
		return wasAccepted;
	}

	/**
	 * Tests if the message should continue
	 * 
	 * @return TRUE if the message should continue
	 * @roseuid 3F365331006B
	 */
	public boolean shouldContinue()
	{
		return shouldContinue;
	}

	/**
	 * Returns the result of the last action;
	 * 
	 * @return the result of the last action.
	 * @roseuid 3F365331006C
	 */
	public Object getActionResult()
	{
		return actionResult;
	}
}
