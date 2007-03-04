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

import org.jgraph.graph.GraphConstants;

import Composestar.Core.LAMA.FieldInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Vertex for the ClassVertex' fields
 * 
 * @author Michiel Hendriks
 */
public class ClassFieldsVertex extends BaseGraphCell
{
	private static final long serialVersionUID = 4721958173085369567L;
	
	private static final CPSLogger logger = CPSLogger.getCPSLogger("VisCom.Cells.ClassFieldsVertex");

	public ClassFieldsVertex(Type platformRep)
	{
		super();		
		addFields(platformRep);
	}
	
	@SuppressWarnings("unchecked")
	public void addFields(Type platformRep)
	{
		int idx = 0;
		for (FieldInfo field : (List<FieldInfo>) platformRep.getFields())
		{
			logger.debug("Adding field "+field.getUnitName());
			BaseGraphCell cell = new BaseGraphCell(field.getUnitName()+": "+field.getUnitType());
			Rectangle2D bounds = new Rectangle2D.Double(idx*14, 0, 14, 60);
			GraphConstants.setBounds(cell.getAttributes(), bounds);
			// TODO: set icon
			
			add(cell);
			cell.setParent(this);
			idx++;
		}
	}
}
