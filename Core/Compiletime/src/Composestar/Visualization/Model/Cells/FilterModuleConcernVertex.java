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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgraph.graph.DefaultPort;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FILTH.InnerDispatcher;
import Composestar.Core.SANE.FilterModuleSuperImposition;
import Composestar.Utils.Logging.CPSLogger;

/**
 * ConcernCell class for the ProgramView. The class view shows no class members
 * bu shows the filtermodule list.
 * 
 * @author Michiel Hendriks
 */
public class FilterModuleConcernVertex extends ConcernVertex
{
	private static final long serialVersionUID = -6066054436195352608L;

	private static final CPSLogger logger = CPSLogger.getCPSLogger("VisCom.Cells.FilterModuleConcernVertex");

	protected Map<String, FilterModuleVertex> fmPorts;

	public FilterModuleConcernVertex(Concern concern)
	{
		super(concern);
		fmPorts = new HashMap<String, FilterModuleVertex>();
		//addFmPorts(concern);
	}

	@Override
	public DefaultPort getPortFor(Object obj)
	{
		String key = null;
		if (obj instanceof FilterModule)
		{
			key = ((FilterModule) obj).getQualifiedName();
		}
		else if (obj != null)
		{
			key = obj.toString();
		}
		return fmPorts.get(key).getPort();
	}

	/**
	 * @return true if this concern has filter modules
	 */
	public boolean hasFilterModules()
	{
		return fmPorts.size() > 0;
	}

	@SuppressWarnings("unchecked")
	protected void addFmPorts(Concern concern)
	{
		FilterModuleOrder fmOrder = (FilterModuleOrder) concern.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);
		if (fmOrder == null)
		{
			return;
		}
		for (FilterModuleSuperImposition fmsi : (List<FilterModuleSuperImposition>) fmOrder.filterModuleSIList())
		{
			FilterModule fm = fmsi.getFilterModule().getRef();
			if (InnerDispatcher.isDefaultDispatch(fm))
			{
				continue;
			}
			FilterModuleVertex fmPort = new FilterModuleVertex(fm);
			fmPorts.put(fm.getQualifiedName(), fmPort);
			add(fmPort);
			fmPort.setParent(this);
			logger.debug("Adding port " + fm.getQualifiedName() + " for " + concern);
		}
	}
}
