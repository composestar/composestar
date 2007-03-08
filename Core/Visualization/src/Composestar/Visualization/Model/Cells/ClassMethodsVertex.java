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

import java.util.EnumSet;
import java.util.List;

import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Visualization.Model.Cells.ClassVertex.MemberFlags;

/**
 * Vertex for the ClassVertex' methods
 * 
 * @author Michiel Hendriks
 */
public class ClassMethodsVertex extends ClassMembersVertex
{
	private static final long serialVersionUID = 5614673482878309394L;

	private static final CPSLogger logger = CPSLogger.getCPSLogger("VisCom.Cells.ClassMethodsVertex");

	public ClassMethodsVertex(Type platformRep, EnumSet<MemberFlags> filter)
	{
		super();
		if (filter.size() != 0)
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
			MemberFlags vis = MemberFlags.PROTECTED;
			if (meth.isPublic())
			{
				vis = MemberFlags.PUBLIC;
			}
			if (meth.isPrivate())
			{
				vis = MemberFlags.PRIVATE;
			}
			BaseGraphCell entry = addEntry(getStringRep(meth), idx, vis);
			members.put(meth.getName(), entry);
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
