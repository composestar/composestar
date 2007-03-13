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

import Composestar.Core.LAMA.FieldInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Visualization.Model.Cells.ClassVertex.MemberFlags;

/**
 * Vertex for the ClassVertex' fields
 * 
 * @author Michiel Hendriks
 */
public class ClassFieldsVertex extends ClassMembersVertex
{
	private static final long serialVersionUID = 4721958173085369567L;

	private static final CPSLogger logger = CPSLogger.getCPSLogger("VisCom.Cells.ClassFieldsVertex");

	public ClassFieldsVertex(Type platformRep, EnumSet<MemberFlags> filter)
	{
		super();
		if (filter.size() != 0)
		{
			addFields(platformRep);
		}
		if (members.size() == 0)
		{
			// empty, add dummy entry
			addDummy();
		}
	}

	@SuppressWarnings("unchecked")
	public void addFields(Type platformRep)
	{
		int idx = 0;
		for (FieldInfo field : (List<FieldInfo>) platformRep.getFields())
		{
			logger.debug("Adding field " + field.getUnitName());
			// is most cases the default visibility is "package protected"
			MemberFlags vis = MemberFlags.PROTECTED;
			if (field.isPublic())
			{
				vis = MemberFlags.PUBLIC;
			}
			if (field.isPrivate())
			{
				vis = MemberFlags.PRIVATE;
			}
			BaseGraphCell entry = addEntry(getStringRep(field), idx, vis);
			members.put(field.getUnitName(), entry);
			idx++;
		}
	}

	protected String getStringRep(FieldInfo fi)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(fi.name());
		sb.append(": ");
		sb.append(fi.getFieldTypeString());
		return sb.toString();
	}
}
