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

import Composestar.Core.COPPER3.FilterTypeFactory;
import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.CpsRepository2.Filters.FilterAction;
import Composestar.Core.CpsRepository2Impl.Filters.FilterActionImpl;
import Composestar.Core.CpsRepository2Impl.Filters.PrimitiveFilterTypeImpl;
import Composestar.Core.FIRE2.util.regex.PatternParseException;
import Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator;
import Composestar.Core.Master.Master;
import Composestar.Core.SECRET3.SECRETResources;
import Composestar.Core.SECRET3.Config.ConflictRule;
import Composestar.Core.SECRET3.Config.Resource;
import Composestar.Core.SECRET3.Config.ResourceType;
import Composestar.Core.SECRET3.Config.ConflictRule.RuleType;
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

	protected FilterActionImpl traceInAction;

	protected FilterActionImpl traceOutAction;

	protected FilterActionImpl timerStartAction;

	protected FilterActionImpl timerStopAction;

	public ExtraFilters()
	{}

	/*
	 * (non-Javadoc)
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
	 * @see
	 * Composestar.CwC.Filters.CustomCwCFilters#registerFilters(Composestar.
	 * Core.RepositoryImplementation.DataStore,
	 * Composestar.Core.CpsProgramRepository.Filters.DefaultFilterFactory)
	 */
	public void registerFilters(Repository repository, FilterTypeFactory factory)
	{
		addTraceFilterType(repository, factory);
		addTraceInFilterType(repository, factory);
		addTraceOutFilterType(repository, factory);
		addProfilingFilterType(repository, factory);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.CwC.Filters.CustomCwCFilters#registerSecretResources(Composestar
	 * .Core.CKRET.SECRETResources)
	 */
	public void registerSecretResources(SECRETResources resources)
	{
		Resource rsc = ResourceType.createResource("timer", false);
		rsc.addVocabulary("read");
		rsc.addVocabulary("write");
		resources.addResource(rsc);
		try
		{
			ConflictRule cr =
					new ConflictRule(rsc, RuleType.Constraint, ConflictRule.PATTERN_NO_WRITE_WRITE_READ,
							"Timer value is unreliable");
			resources.addRule(cr);
		}
		catch (PatternParseException e)
		{
			logger.error(e, e);
		}
	}

	public FilterAction getTraceInAction(Repository repository)
	{
		if (traceInAction != null)
		{
			return traceInAction;
		}
		traceInAction = new FilterActionImpl(TRACE_IN_ACTION);
		traceInAction.setResourceOperations("arg.read");
		repository.add(traceInAction);
		return traceInAction;
	}

	public FilterAction getTraceOutAction(Repository repository)
	{
		if (traceOutAction != null)
		{
			return traceOutAction;
		}
		traceOutAction = new FilterActionImpl(TRACE_OUT_ACTION);
		traceOutAction.setResourceOperations("arg.read;return.read");
		repository.add(traceOutAction);
		return traceOutAction;
	}

	public void addTraceFilterType(Repository repository, FilterTypeFactory factory)
	{
		PrimitiveFilterTypeImpl type = new PrimitiveFilterTypeImpl(TRACE_FILTER);
		type.setAcceptCallAction(getTraceInAction(repository));
		type.setRejectCallAction(factory.getContinueAction());
		type.setAcceptReturnAction(getTraceOutAction(repository));
		type.setRejectReturnAction(factory.getContinueAction());
		repository.add(type);
	}

	public void addTraceInFilterType(Repository repository, FilterTypeFactory factory)
	{
		PrimitiveFilterTypeImpl type = new PrimitiveFilterTypeImpl(TRACE_IN_FILTER);
		type.setAcceptCallAction(getTraceInAction(repository));
		type.setRejectCallAction(factory.getContinueAction());
		type.setAcceptReturnAction(factory.getContinueAction());
		type.setRejectReturnAction(factory.getContinueAction());
		repository.add(type);
	}

	public void addTraceOutFilterType(Repository repository, FilterTypeFactory factory)
	{
		PrimitiveFilterTypeImpl type = new PrimitiveFilterTypeImpl(TRACE_OUT_FILTER);
		type.setAcceptCallAction(factory.getContinueAction());
		type.setRejectCallAction(factory.getContinueAction());
		type.setAcceptReturnAction(getTraceOutAction(repository));
		type.setRejectReturnAction(factory.getContinueAction());
		repository.add(type);
	}

	public FilterAction getTimerStartAction(Repository repository)
	{
		if (timerStartAction != null)
		{
			return timerStartAction;
		}
		timerStartAction = new FilterActionImpl(TIMER_START_ACTION);
		timerStartAction.setResourceOperations("timer.write");
		repository.add(timerStartAction);
		return timerStartAction;
	}

	public FilterAction getTimerStopAction(Repository repository)
	{
		if (timerStopAction != null)
		{
			return timerStopAction;
		}
		timerStopAction = new FilterActionImpl(TIMER_STOP_ACTION);
		timerStopAction.setResourceOperations("timer.read");
		repository.add(timerStopAction);
		return timerStopAction;
	}

	public void addProfilingFilterType(Repository repository, FilterTypeFactory factory)
	{
		PrimitiveFilterTypeImpl type = new PrimitiveFilterTypeImpl(PROFILING_FILTER);
		type.setAcceptCallAction(getTimerStartAction(repository));
		type.setRejectCallAction(factory.getContinueAction());
		type.setAcceptReturnAction(getTimerStopAction(repository));
		type.setRejectReturnAction(factory.getContinueAction());
		repository.add(type);
	}

}
