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

import Composestar.Core.LAMA.FieldInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Vertex for the ClassVertex' fields
 * 
 * @author Michiel Hendriks
 */
public class ClassFieldsVertex extends ClassMembersVertex
{
	private static final long serialVersionUID = 4721958173085369567L;

	private static final CPSLogger logger = CPSLogger.getCPSLogger("VisCom.Cells.ClassFieldsVertex");

	public ClassFieldsVertex(Type platformRep, int filter)
	{
		super();
		if (filter != ClassVertex.MEMBERS_NONE)
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
			addEntry(field.getUnitName() + ": " + field.getUnitType(), idx);
			idx++;
		}
	}
}
