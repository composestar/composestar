/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: FilterNodeFactory.java,v 1.6 2006/10/05 13:16:20 elmuerte Exp $
 */
package Composestar.Core.DIGGER.Graph.Filters;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.DIGGER.DIGGER;
import Composestar.Core.DIGGER.Graph.FilterNode;
import Composestar.Core.DIGGER.Graph.Graph;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Utils.Debug;

/**
 * Factory for creation appropiate filter node types
 * 
 * @author Michiel Hendriks
 */
public final class FilterNodeFactory
{
	private FilterNodeFactory()
	{}

	/**
	 * Create a FilterNode for the given Filter type
	 * 
	 * @param inGraph
	 * @param forFilter
	 * @param inDirection the direction of the filter (input or output)
	 * @return
	 */
	public static FilterNode createFilterNode(Graph inGraph, Filter forFilter, byte inDirection) throws ModuleException
	{
		String filterType = forFilter.getFilterType().getType();
		// if (FilterType.DISPATCH.equals(filterType))
		// {
		// return new DispatchFilterNode(inGraph, forFilter);
		// }
		// else if (FilterType.SEND.equals(filterType))
		// {
		// return new SendFilterNode(inGraph, forFilter);
		// }
		// else if (FilterType.ERROR.equals(filterType))
		// {
		// return new ErrorFilterNode(inGraph, forFilter, inDirection);
		// }
		// else if (FilterType.META.equals(filterType))
		// {
		// return new MetaFilterNode(inGraph, forFilter, inDirection);
		// }
		// else if (FilterType.CUSTOM.equals(filterType))
		// {
		// return new CustomFilterNode(inGraph, forFilter, inDirection);
		// }
		// else if (FilterType.APPEND.equals(filterType))
		// {
		// return new AppendFilterNode(inGraph, forFilter, inDirection);
		// }
		// else if (FilterType.PREPEND.equals(filterType))
		// {
		// return new PrependFilterNode(inGraph, forFilter, inDirection);
		// }

		if (Configuration.instance().getModuleProperty(DIGGER.MODULE_NAME, "acceptUnknownFilter", true))
		{
			Debug.out(Debug.MODE_WARNING, DIGGER.MODULE_NAME, "Unknown filter type: " + filterType);
			return new UnknownFilterNode(inGraph, forFilter, inDirection);
		}
		else
		{
			throw new ModuleException("Unknown filter type: " + filterType, DIGGER.MODULE_NAME);
		}
	}
}
