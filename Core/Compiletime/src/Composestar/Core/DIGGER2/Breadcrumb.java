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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.FIRE2.model.Message;
import Composestar.Core.FIRE2.model.FireModel.FilterDirection;

/**
 * A node in the dispatch graph. A concern has one or more breadcrumbs, for each
 * distinguishable message one. A breadcrumb can lead to one or many trails.
 * Usually at least two because of the default inner dispatch. A trail often
 * leads to a new concern, except in case of an inner dispatch.
 * 
 * @author Michiel Hendriks
 */
public class Breadcrumb implements Serializable
{
	private static final long serialVersionUID = -8442493609569837628L;

	protected Concern concern;

	/**
	 * The message that functioned as entrance message for the execution graph
	 */
	protected Message message;

	/**
	 * input or output filter
	 */
	protected FilterDirection filterPosition;

	protected List<Trail> trails;

	/**
	 * Resolved status of the trails.
	 */
	protected transient boolean resolvedStatus;

	public Breadcrumb(Concern inConcern, Message inMessage, FilterDirection inFilterPosition)
	{
		concern = inConcern;
		message = inMessage;
		filterPosition = inFilterPosition;
		trails = new ArrayList<Trail>();
	}

	public Concern getConcern()
	{
		return concern;
	}

	public Message getMessage()
	{
		return message;
	}

	public FilterDirection getFilterPosition()
	{
		return filterPosition;
	}

	/**
	 * @return true if the breadcrumb has been fully resolved
	 */
	public boolean isResolved()
	{
		if (!resolvedStatus)
		{
			resolvedStatus = true;
			for (Trail trail : trails)
			{
				if (!trail.isResolved())
				{
					resolvedStatus = false;
					break;
				}
			}
		}
		return resolvedStatus;
	}

	/**
	 * Add a new trail to this breadcrumb
	 * 
	 * @return
	 */
	public Trail addTrail()
	{
		Trail trail = new Trail(this);
		trails.add(trail);
		resolvedStatus = false; // new trails are never resolved
		return trail;
	}

	public Trail addTrail(Trail base)
	{
		Trail trail = new Trail(this, base);
		trails.add(trail);
		resolvedStatus = false; // new trails are never resolved
		return trail;
	}

	/**
	 * Remove a trail. Should only be used in case of deadends
	 * 
	 * @param trail
	 */
	public void removeTrail(Trail trail)
	{
		trails.remove(trail);
	}

	public Iterator<Trail> getTrails()
	{
		return trails.iterator();
	}

	public int numTrails()
	{
		return trails.size();
	}

	@Override
	public String toString()
	{
		return concern.getQualifiedName() + " " + message.toString();
	}
}
