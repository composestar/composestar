/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2008 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id$
 */

package Composestar.CwC.Filters;

import java.util.Collection;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.Core.CpsProgramRepository.Filters.DefaultFilterFactory;
import Composestar.Core.CpsProgramRepository.Filters.DefaultFilterFactory.UnsupportedFilterActionException;
import Composestar.Core.CpsProgramRepository.Filters.DefaultFilterFactory.UnsupportedFilterTypeException;
import Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator;
import Composestar.Core.Master.Master;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Defines some additional builtin filters for CwC
 * 
 * @author Michiel Hendriks
 */
public class ExtraFilters implements CustomCwCFilters
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(Master.MODULE_NAME + ".FilterLoader");

	public static final String TRACE_FILTER = "trace";

	public static final String TRACE_IN_FILTER = "tracein";

	public static final String TRACE_OUT_FILTER = "traceout";

	public static final String TRACE_IN_ACTION = "TraceInAction";

	public static final String TRACE_OUT_ACTION = "TraceInAction";

	protected FilterAction traceInAction;

	protected FilterAction traceOutAction;

	public ExtraFilters()
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.CwC.Filters.CustomCwCFilters#getCodeGenerators()
	 */
	public Collection<FilterActionCodeGenerator<String>> getCodeGenerators()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.CwC.Filters.CustomCwCFilters#registerFilters(Composestar.Core.RepositoryImplementation.DataStore,
	 *      Composestar.Core.CpsProgramRepository.Filters.DefaultFilterFactory)
	 */
	public void registerFilters(DataStore repository, DefaultFilterFactory factory)
	{
		try
		{
			addTraceFilterType(repository, factory);
			addTraceInFilterType(repository, factory);
			addTraceOutFilterType(repository, factory);
		}
		catch (UnsupportedFilterTypeException e)
		{
			logger.error(e, e);
		}
	}

	public FilterAction getTraceInAction(DataStore repository)
	{
		if (traceInAction != null)
		{
			return traceInAction;
		}
		traceInAction = new FilterAction();
		traceInAction.setName(TRACE_IN_ACTION);
		traceInAction.setFullName(TRACE_IN_ACTION);
		traceInAction.setFlowBehaviour(FilterAction.FLOW_CONTINUE);
		traceInAction.setMessageChangeBehaviour(FilterAction.MESSAGE_ORIGINAL);
		traceInAction.setResourceOperations("arg.read");
		traceInAction.setCreateJPC(false);
		repository.addObject(traceInAction);
		return traceInAction;
	}

	public FilterAction getTraceOutAction(DataStore repository)
	{
		if (traceOutAction != null)
		{
			return traceOutAction;
		}
		traceOutAction = new FilterAction();
		traceOutAction.setName(TRACE_OUT_ACTION);
		traceOutAction.setFullName(TRACE_OUT_ACTION);
		traceOutAction.setFlowBehaviour(FilterAction.FLOW_CONTINUE);
		traceOutAction.setMessageChangeBehaviour(FilterAction.MESSAGE_ORIGINAL);
		traceOutAction.setResourceOperations("arg.read;return.read");
		traceOutAction.setCreateJPC(false);
		repository.addObject(traceOutAction);
		return traceOutAction;
	}

	public void addTraceFilterType(DataStore repository, DefaultFilterFactory factory)
			throws UnsupportedFilterTypeException
	{
		FilterType type = new FilterType();
		type.setType(TRACE_FILTER);
		try
		{
			type.setAcceptCallAction(getTraceInAction(repository));
			type.setRejectCallAction(factory.getContinueAction());
			type.setAcceptReturnAction(getTraceOutAction(repository));
			type.setRejectReturnAction(factory.getContinueAction());
		}
		catch (UnsupportedFilterActionException e)
		{
			throw new UnsupportedFilterTypeException(TRACE_FILTER, e);
		}
		repository.addObject(type);
		if (factory.getTypeMapping() != null)
		{
			factory.getTypeMapping().registerFilterType(type);
		}
	}

	public void addTraceInFilterType(DataStore repository, DefaultFilterFactory factory)
			throws UnsupportedFilterTypeException
	{
		FilterType type = new FilterType();
		type.setType(TRACE_IN_FILTER);
		try
		{
			type.setAcceptCallAction(getTraceInAction(repository));
			type.setRejectCallAction(factory.getContinueAction());
			type.setAcceptReturnAction(factory.getContinueAction());
			type.setRejectReturnAction(factory.getContinueAction());
		}
		catch (UnsupportedFilterActionException e)
		{
			throw new UnsupportedFilterTypeException(TRACE_IN_FILTER, e);
		}
		repository.addObject(type);
		if (factory.getTypeMapping() != null)
		{
			factory.getTypeMapping().registerFilterType(type);
		}
	}

	public void addTraceOutFilterType(DataStore repository, DefaultFilterFactory factory)
			throws UnsupportedFilterTypeException
	{
		FilterType type = new FilterType();
		type.setType(TRACE_OUT_FILTER);
		try
		{
			type.setAcceptCallAction(factory.getContinueAction());
			type.setRejectCallAction(factory.getContinueAction());
			type.setAcceptReturnAction(getTraceOutAction(repository));
			type.setRejectReturnAction(factory.getContinueAction());
		}
		catch (UnsupportedFilterActionException e)
		{
			throw new UnsupportedFilterTypeException(TRACE_OUT_FILTER, e);
		}
		repository.addObject(type);
		if (factory.getTypeMapping() != null)
		{
			factory.getTypeMapping().registerFilterType(type);
		}
	}

}
