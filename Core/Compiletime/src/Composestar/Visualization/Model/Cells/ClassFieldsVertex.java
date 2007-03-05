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
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;

import org.jgraph.graph.GraphConstants;

import Composestar.Core.LAMA.FieldInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Vertex for the ClassVertex' fields
 * 
 * @author Michiel Hendriks
 */
public class ClassFieldsVertex extends ClassPartVertex
{
	private static final long serialVersionUID = 4721958173085369567L;

	private static final CPSLogger logger = CPSLogger.getCPSLogger("VisCom.Cells.ClassFieldsVertex");

	public ClassFieldsVertex(Type platformRep)
	{
		super();
		Map attr = getAttributes();
		GraphConstants.setOpaque(attr, true);
		GraphConstants.setBackground(attr, Color.GREEN);

		addFields(platformRep);
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
		if (idx == 0)
		{
			// empty, add dummy entry
			addDummy();
		}
	}
}
