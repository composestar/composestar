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

import java.io.Serializable;

import Composestar.Core.CpsRepository2.Concern;
import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.FilterElements.MatchingExpression;
import Composestar.Core.CpsRepository2.TypeSystem.CpsMessage;

/**
 * A trail contains information about the transition to a new breadcrumb.
 * 
 * @author Michiel Hendriks
 */
public class Trail implements Serializable
{
	private static final long serialVersionUID = -4319936184486374241L;

	/**
	 * The owner of this trail
	 */
	protected Breadcrumb owner;

	/**
	 * Condition expression that must be true. Used during recursion checking
	 */
	protected Object /* TODO */condition;

	/**
	 * most valuable repository entity, this is used to refer to a repository
	 * entity that matching this trail the best. In most cases this is a
	 * FilterElement.
	 */
	protected RepositoryEntity re;

	/**
	 * Resulting message. This should be more or less equal to the message of
	 * the breadcrumb it leads to (target value of the breadcrumb is always
	 * inner).
	 */
	protected CpsMessage resultMessage;

	/**
	 * Points to the next breadcrumb. This will be null in the following cases:
	 * <ul>
	 * <li>points to the inner object (in this case the targetConcern is null
	 * too)</li>
	 * <li>points to a concern without superimposition</li>
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

	/**
	 * Copy constructor. Only elements that have been set before a branch will
	 * be set. Since the branch happens on the condition expression node the
	 * condition expression and the reposiroty entry will be copied. The rest
	 * should always be overwritten by other nodes.
	 * 
	 * @param inOwner
	 * @param base
	 */
	public Trail(Breadcrumb inOwner, Trail base)
	{
		this(inOwner);
		condition = base.getCondition();
		re = base.getRE();
	}

	/**
	 * @return the owning breadcrumb
	 */
	public Breadcrumb getOwner()
	{
		return owner;
	}

	/**
	 * @return the associated concern
	 */
	public Concern getSourceConcern()
	{
		return owner.getConcern();
	}

	/**
	 * Set the condition that is responsible for this trail
	 * 
	 * @param inCondition
	 */
	public void setCondition(MatchingExpression inCondition)
	{
		condition = inCondition;
	}

	/**
	 * @return the condition that caused this trail
	 */
	public Object/* TODO */getCondition()
	{
		return condition;
	}

	/**
	 * Sets the new message value when following this trail
	 * 
	 * @param inMessage
	 */
	public void setResultMessage(CpsMessage inMessage)
	{
		resultMessage = inMessage;
	}

	/**
	 * @return the resulting message
	 */
	public CpsMessage getResultMessage()
	{
		return resultMessage;
	}

	/**
	 * @param resolveSelector if true resolve the selector when it's a star
	 *            selector
	 * @return
	 */
	public CpsMessage getResultMessage(boolean resolveSelector)
	{
		// if (Message.STAR_SELECTOR.equals(resultMessage.getSelector()))
		// {
		// return new Message(resultMessage.getTarget(),
		// owner.getMessage().getSelector());
		// }
		return resultMessage;
	}

	/**
	 * Set the concern this crumb points to
	 * 
	 * @param inConcern
	 */
	public void setTargetConcern(Concern inConcern)
	{
		targetConcern = inConcern;
	}

	/**
	 * Get the destination concern
	 * 
	 * @return
	 */
	public Concern getTargetConcern()
	{
		return targetConcern;
	}

	/**
	 * Sets the destination crumb
	 * 
	 * @param inCrumb
	 */
	public void setDestinationCrumb(Breadcrumb inCrumb)
	{
		destinationCrumb = inCrumb;
		resolved = true;
	}

	/**
	 * @return the destination crumb, can be null
	 */
	public Breadcrumb getDestinationCrumb()
	{
		return destinationCrumb;
	}

	/**
	 * @return true if this is the end of the trail.
	 */
	public boolean isEOL()
	{
		return destinationCrumb == null && resolved;
	}

	/**
	 * @return is true this trail is part of a recursive dispatch
	 */
	public boolean isRecursive()
	{
		return recursive;
	}

	/**
	 * Sets the recursiveness of this trail
	 * 
	 * @param rec
	 */
	public void setRecursive(boolean rec)
	{
		recursive = rec;
	}

	/**
	 * @return true if this crumb is resolved
	 */
	public boolean isResolved()
	{
		return resolved;
	}

	/**
	 * Sets the associated repository entity. This is a FilterElement.
	 * 
	 * @param inRe
	 */
	public void setRE(RepositoryEntity inRe)
	{
		re = inRe;
	}

	/**
	 * Get an associated repository entity
	 * 
	 * @return
	 */
	public RepositoryEntity getRE()
	{
		return re;
	}
}
