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
import java.util.HashSet;
import java.util.Set;

import Composestar.Core.CKRET.SECRETResources;
import Composestar.Core.CKRET.Config.ConflictRule;
import Composestar.Core.CKRET.Config.Resource;
import Composestar.Core.CKRET.Config.ResourceType;
import Composestar.Core.CKRET.Config.ConflictRule.RuleType;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.Core.CpsProgramRepository.Filters.DefaultFilterFactory;
import Composestar.Core.CpsProgramRepository.Filters.UnsupportedFilterActionException;
import Composestar.Core.CpsProgramRepository.Filters.UnsupportedFilterTypeException;
import Composestar.Core.FIRE2.util.regex.PatternParseException;
import Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator;
import Composestar.Core.Master.Master;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.CwC.INLINE.CodeGen.CTimerActionCodeGenerator;
import Composestar.CwC.INLINE.CodeGen.CTraceActionCodeGenerator;
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

	public static final String TRACE_OUT_ACTION = "TraceOutAction";

	public static final String PROFILING_FILTER = "profile";

	public static final String TIMER_START_ACTION = "TimerStartAction";

	public static final String TIMER_STOP_ACTION = "TimerStopAction";

	protected FilterAction traceInAction;

	protected FilterAction traceOutAction;

	protected FilterAction timerStartAction;

	protected FilterAction timerStopAction;

	public ExtraFilters()
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.CwC.Filters.CustomCwCFilters#getCodeGenerators()
	 */
	public Collection<FilterActionCodeGenerator<String>> getCodeGenerators()
	{
		Set<FilterActionCodeGenerator<String>> result = new HashSet<FilterActionCodeGenerator<String>>();
		result.add(new CTraceActionCodeGenerator());
		result.add(new CTimerActionCodeGenerator());
		return result;
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
			addProfilingFilterType(repository, factory);
		}
		catch (UnsupportedFilterTypeException e)
		{
			logger.error(e, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.CwC.Filters.CustomCwCFilters#registerSecretResources(Composestar.Core.CKRET.SECRETResources)
	 */
	public void registerSecretResources(SECRETResources resources)
	{
		Resource rsc = ResourceType.createResource("timer", false);
		rsc.addVocabulary("read");
		rsc.addVocabulary("write");
		resources.addResource(rsc);
		try
		{
			ConflictRule cr = new ConflictRule(rsc, RuleType.Constraint, ConflictRule.PATTERN_NO_WRITE_WRITE_READ,
					"Timer value is unreliable");
			resources.addRule(cr);
		}
		catch (PatternParseException e)
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

	public FilterAction getTimerStartAction(DataStore repository)
	{
		if (timerStartAction != null)
		{
			return timerStartAction;
		}
		timerStartAction = new FilterAction();
		timerStartAction.setName(TIMER_START_ACTION);
		timerStartAction.setFullName(TIMER_START_ACTION);
		timerStartAction.setFlowBehaviour(FilterAction.FLOW_CONTINUE);
		timerStartAction.setMessageChangeBehaviour(FilterAction.MESSAGE_ORIGINAL);
		timerStartAction.setResourceOperations("timer.write");
		timerStartAction.setCreateJPC(false);
		repository.addObject(timerStartAction);
		return timerStartAction;
	}

	public FilterAction getTimerStopAction(DataStore repository)
	{
		if (timerStopAction != null)
		{
			return timerStopAction;
		}
		timerStopAction = new FilterAction();
		timerStopAction.setName(TIMER_STOP_ACTION);
		timerStopAction.setFullName(TIMER_STOP_ACTION);
		timerStopAction.setFlowBehaviour(FilterAction.FLOW_CONTINUE);
		timerStopAction.setMessageChangeBehaviour(FilterAction.MESSAGE_ORIGINAL);
		timerStopAction.setResourceOperations("timer.read");
		timerStopAction.setCreateJPC(false);
		repository.addObject(timerStopAction);
		return timerStopAction;
	}

	public void addProfilingFilterType(DataStore repository, DefaultFilterFactory factory)
			throws UnsupportedFilterTypeException
	{
		FilterType type = new FilterType();
		type.setType(PROFILING_FILTER);
		try
		{
			type.setAcceptCallAction(getTimerStartAction(repository));
			type.setRejectCallAction(factory.getContinueAction());
			type.setAcceptReturnAction(getTimerStopAction(repository));
			type.setRejectReturnAction(factory.getContinueAction());
		}
		catch (UnsupportedFilterActionException e)
		{
			throw new UnsupportedFilterTypeException(PROFILING_FILTER, e);
		}
		repository.addObject(type);
		if (factory.getTypeMapping() != null)
		{
			factory.getTypeMapping().registerFilterType(type);
		}
	}

}
