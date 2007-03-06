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

import java.util.List;

import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Vertex for the ClassVertex' methods
 * 
 * @author Michiel Hendriks
 */
public class ClassMethodsVertex extends ClassMembersVertex
{
	private static final long serialVersionUID = 5614673482878309394L;

	private static final CPSLogger logger = CPSLogger.getCPSLogger("VisCom.Cells.ClassMethodsVertex");

	public ClassMethodsVertex(Type platformRep, int filter)
	{
		super();
		if (filter != ClassVertex.MEMBERS_NONE)
		{
			addMethods(platformRep);
		}
		if (members.size() == 0)
		{
			// empty, add dummy entry
			addDummy();
		}
	}

	@SuppressWarnings("unchecked")
	protected void addMethods(Type platformRep)
	{
		int idx = 0;
		for (MethodInfo meth : (List<MethodInfo>) platformRep.getMethods())
		{
			logger.debug("Adding method " + meth.getName());
			// is most cases the default visibility is "package protected"
			int vis = ClassVertex.MEMBERS_PROTECTED;
			if (meth.isPublic())
			{
				vis = ClassVertex.MEMBERS_PUBLIC;
			}
			if (meth.isPrivate())
			{
				vis = ClassVertex.MEMBERS_PRIVATE;
			}
			addEntry(getStringRep(meth), idx, vis);
			idx++;
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
