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

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;

import org.jgraph.graph.GraphConstants;

import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Vertex for the ClassVertex' methods
 * 
 * @author Michiel Hendriks
 */
public class ClassMethodsVertex extends ClassPartVertex
{
	private static final long serialVersionUID = 5614673482878309394L;

	private static final CPSLogger logger = CPSLogger.getCPSLogger("VisCom.Cells.ClassMethodsVertex");

	public ClassMethodsVertex(Type platformRep)
	{
		super();
		addMethods(platformRep);
	}

	@SuppressWarnings("unchecked")
	protected void addMethods(Type platformRep)
	{
		int idx = 0;
		for (MethodInfo meth : (List<MethodInfo>) platformRep.getMethods())
		{
			logger.debug("Adding method " + meth.getName());
			addEntry(getStringRep(meth), idx);
			idx++;
		}
		if (idx == 0)
		{
			addDummy();
		}
	}

	protected String getStringRep(MethodInfo meth)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(meth.getName());
		sb.append("(");
		if (meth.getParameters().size() > 0)
		{
			sb.append("...");
		}
		sb.append(")");
		String ret = meth.returnTypeName();
		if (ret != null && !ret.toLowerCase().endsWith(".void"))
		{
			sb.append(": ");
			sb.append(ret.substring(ret.lastIndexOf(".") + 1));
		}
		return sb.toString();
	}
}
