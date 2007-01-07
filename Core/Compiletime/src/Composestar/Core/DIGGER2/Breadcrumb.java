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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.FIRE2.model.Message;

/**
 * A node in the dispatch graph. A concern has one or more breadcrumbs, for each
 * distinguishable message one. A breadcrumb can lead to one or many trails.
 * Usually at least two because of the default inner dispatch. A trail often
 * leads to a new concern, except in case of an inner dispatch.
 * 
 * @author Michiel Hendriks
 */
public class Breadcrumb
{
	protected Concern concern;

	/**
	 * The message that functioned as entrance message for the execution graph
	 */
	protected Message message;

	/**
	 * input or output filter
	 */
	protected int direction;

	protected List trails;

	public Breadcrumb(Concern inConcern, Message inMessage, int inDirection)
	{
		concern = inConcern;
		message = inMessage;
		direction = inDirection;
		trails = new ArrayList();
	}

	public Concern getConcern()
	{
		return concern;
	}

	public Message getMessage()
	{
		return message;
	}

	public int getDirection()
	{
		return direction;
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

	public Iterator getTrails()
	{
		return trails.iterator();
	}

}