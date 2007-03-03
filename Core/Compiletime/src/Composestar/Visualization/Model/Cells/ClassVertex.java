/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Visualization.Model.Cells;

import java.awt.Color;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;

import Composestar.Core.LAMA.Type;

/**
 * Implements an UML like Class shape.
 * 
 * @author Michiel Hendriks
 */
public class ClassVertex extends BaseGraphCell
{
	private static final long serialVersionUID = -8859175705375540286L;

	protected Type platformRep;
	
	/**
	 * @param userObject
	 */
	public ClassVertex(Type platformRep)
	{
		super(platformRep);

		AttributeMap attrs = getAttributes();
		GraphConstants.setBorderColor(attrs, Color.BLACK);
		GraphConstants.setOpaque(attrs, true);
	}
	
	public String getClassName()
	{
		return platformRep.fullName();
	}
}
