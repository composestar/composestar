/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.DIGGER2;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
import Composestar.Core.FIRE2.model.Message;

/**
 * A trail contains information about the transition to a new breadcrumb.
 * 
 * @author Michiel Hendriks
 */
public class Trail
{
	/**
	 * The owner of this trail
	 */
	protected Breadcrumb owner;

	/**
	 * Condition expression that must be true. Used during recursion check.
	 */
	protected ConditionExpression condition;

	/**
	 * Resulting message. This should be more or less equal to the message of
	 * the breadcrumb it leads to (target value of the breadcrumb is always
	 * inner).
	 */
	protected Message resultMessage;

	/**
	 * Points to the next breadcrumb. This will be null in the following cases:
	 * <ul>
	 * <li> points to the inner object (in this case the targetConcern is null too) </li>
	 * <li> points to a concern without superimposition </li>
	 * </ul>
	 * In both cases it's the end of the trail.
	 */
	protected Breadcrumb destinationCrumb;

	/**
	 * The concern this trail points to. This is null in case the trail points
	 * to the inner object.
	 */
	protected Concern targetConcern;

	/**
	 * If true then this trail is part of a recursive trail
	 */
	protected boolean recursive;

	/**
	 * This trail has been resolved to the next crumb (which could be null).
	 * After DIGGER has been run all trails should be resolved.
	 */
	protected boolean resolved;

	public Trail(Breadcrumb inOwner)
	{
		owner = inOwner;
	}

	public void setCondition(ConditionExpression inCondition)
	{
		condition = inCondition;
	}

	public ConditionExpression getCondition()
	{
		return condition;
	}

	public void setResultMessage(Message inMessage)
	{
		resultMessage = inMessage;
	}

	public Message getResultMessage()
	{
		return resultMessage;
	}

	public void setTargetConcern(Concern inConcern)
	{
		targetConcern = inConcern;
	}

	public Concern getTargetConcern()
	{
		return targetConcern;
	}

	public void setDestinationCrumb(Breadcrumb inCrumb)
	{
		destinationCrumb = inCrumb;
		resolved = true;
	}

	public Breadcrumb getDestinationCrumb()
	{
		return destinationCrumb;
	}

	/**
	 * @return true if this is the end of the trail.
	 */
	public boolean isEOL()
	{
		return (destinationCrumb == null) && resolved;
	}

	public boolean isRecursive()
	{
		return recursive;
	}

	public void setRecursive(boolean rec)
	{
		recursive = rec;
	}

	public boolean isResolved()
	{
		return resolved;
	}
}
