/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: Graph.java,v 1.4 2006/10/05 12:19:15 elmuerte Exp $
 */
package Composestar.Core.DIGGER.Graph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.DIGGER.Graph.SpecialNodes.ExceptionNode;

/**
 * Contains the filter dispatch graph
 * 
 * @author Michiel Hendriks
 */
public class Graph
{
	protected Map concernNodes;

	protected ExceptionNode exceptionNode;

	public Graph()
	{
		concernNodes = new HashMap();
	}

	/**
	 * Get a concern node, create the node if it doesn't exist
	 * 
	 * @param forConcern
	 * @return
	 */
	public AbstractConcernNode getConcernNode(Concern forConcern)
	{
		if (concernNodes.containsKey(forConcern))
		{
			AbstractConcernNode node = (AbstractConcernNode) concernNodes.get(forConcern);
			return node;
		}
		return createConcernNode(forConcern);
	}

	/**
	 * Create a new ConcernNode of the proper type.
	 * 
	 * @param forConcern
	 * @return
	 */
	public AbstractConcernNode createConcernNode(Concern forConcern)
	{
		AbstractConcernNode newNode;
		if (forConcern.getDynObject("SingleOrder") != null)
		{
			newNode = new ConcernNode(this, forConcern);
		}
		else
		{
			newNode = new SimpleConcernNode(this, forConcern);
		}
		concernNodes.put(forConcern, newNode);
		return newNode;
	}

	/**
	 * Return all AbstractConcernNodes
	 * 
	 * @return
	 */
	public Iterator getConcernNodes()
	{
		return concernNodes.values().iterator();
	}

	/**
	 * Return the exception node (if it exists).
	 * 
	 * @return
	 */
	public ExceptionNode getExceptionNode()
	{
		return exceptionNode;
	}

	/**
	 * Return the exception node, create it if required
	 * 
	 * @param bCreate
	 * @return
	 */
	public ExceptionNode getExceptionNode(boolean bCreate)
	{
		if ((exceptionNode == null) && bCreate)
		{
			exceptionNode = new ExceptionNode(this);
		}
		return exceptionNode;
	}
}
