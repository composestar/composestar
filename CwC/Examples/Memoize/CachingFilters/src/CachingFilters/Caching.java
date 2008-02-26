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
 * $Id: IDEALSFilters.java 4086 2008-02-19 11:27:00Z elmuerte $
 */

package CachingFilters;

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
import Composestar.Core.INLINE.CodeGen.VoidFilterActionCodeGen;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.CwC.Filters.CustomCwCFilters;

/**
 * @author Michiel Hendriks
 */
public class Caching implements CustomCwCFilters
{
	public static final String CACHE_FILTER = "Cache";

	public static final String CACHE_ACTION = "CacheAction";

	public static final String CACHE_RETURN_ACTION = "CacheReturnAction";

	public static final String INVALIDATE_FILTER = "Invalidate";

	public static final String INVALIDATE_ACTION = "InvalidateAction";

	private FilterAction cacheAction;

	private FilterAction cacheReturnAction;

	private FilterAction invalidateAction;

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.CwC.Filters.CustomCwCFilters#getCodeGenerators()
	 */
	public Collection<FilterActionCodeGenerator<String>> getCodeGenerators()
	{
		String[] stubs = { CACHE_ACTION, CACHE_RETURN_ACTION, INVALIDATE_ACTION };
		Set<FilterActionCodeGenerator<String>> facgs = new HashSet<FilterActionCodeGenerator<String>>();
		facgs.add(new VoidFilterActionCodeGen<String>(stubs));
		return facgs;
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
			addCache(repository, factory);
			addInvalidate(repository, factory);
		}
		catch (UnsupportedFilterTypeException e)
		{
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.CwC.Filters.CustomCwCFilters#registerSecretResources(Composestar.Core.CKRET.SECRETResources)
	 */
	public void registerSecretResources(SECRETResources resources)
	{
		Resource rsc = ResourceType.createResource("cache", false);
		rsc.addVocabulary("read");
		rsc.addVocabulary("write");
		resources.addResource(rsc);
		try
		{
			ConflictRule cr = new ConflictRule(rsc, RuleType.Constraint, ConflictRule.PATTERN_NO_WRITE_WRITE_READ,
					"previous cached value overwritten");
			resources.addRule(cr);
		}
		catch (PatternParseException e)
		{
		}
	}

	public FilterAction getCacheAction(DataStore repository)
	{
		if (cacheAction != null)
		{
			return cacheAction;
		}
		cacheAction = new FilterAction();
		cacheAction.setName(CACHE_ACTION);
		cacheAction.setFullName(CACHE_ACTION);
		// does exit when cached
		cacheAction.setFlowBehaviour(FilterAction.FLOW_CONTINUE);
		cacheAction.setMessageChangeBehaviour(FilterAction.MESSAGE_ORIGINAL);
		cacheAction.setResourceOperations("target.read;selector.read;arg.read;cache.read;");
		cacheAction.setCreateJPC(false);
		repository.addObject(cacheAction);
		return cacheAction;
	}

	public FilterAction getCacheReturnAction(DataStore repository)
	{
		if (cacheReturnAction != null)
		{
			return cacheReturnAction;
		}
		cacheReturnAction = new FilterAction();
		cacheReturnAction.setName(CACHE_RETURN_ACTION);
		cacheReturnAction.setFullName(CACHE_RETURN_ACTION);
		cacheReturnAction.setFlowBehaviour(FilterAction.FLOW_CONTINUE);
		cacheReturnAction.setMessageChangeBehaviour(FilterAction.MESSAGE_ORIGINAL);
		cacheReturnAction.setResourceOperations("target.read;selector.read;arg.read;cache.write");
		cacheReturnAction.setCreateJPC(false);
		repository.addObject(cacheReturnAction);
		return cacheReturnAction;
	}

	public void addCache(DataStore repository, DefaultFilterFactory factory) throws UnsupportedFilterTypeException
	{
		FilterType type = new FilterType();
		type.setType(CACHE_FILTER);
		try
		{
			type.setAcceptCallAction(getCacheAction(repository));
			type.setRejectCallAction(factory.getContinueAction());
			type.setAcceptReturnAction(factory.getContinueAction());
			type.setAcceptReturnAction(getCacheReturnAction(repository));
			type.setRejectReturnAction(factory.getContinueAction());
		}
		catch (UnsupportedFilterActionException e)
		{
			throw new UnsupportedFilterTypeException(CACHE_FILTER, e);
		}
		repository.addObject(type);
		if (factory.getTypeMapping() != null)
		{
			factory.getTypeMapping().registerFilterType(type);
		}
	}

	public FilterAction getInvalidateAction(DataStore repository)
	{
		if (invalidateAction != null)
		{
			return invalidateAction;
		}
		invalidateAction = new FilterAction();
		invalidateAction.setName(INVALIDATE_ACTION);
		invalidateAction.setFullName(INVALIDATE_ACTION);
		invalidateAction.setFlowBehaviour(FilterAction.FLOW_CONTINUE);
		invalidateAction.setMessageChangeBehaviour(FilterAction.MESSAGE_ORIGINAL);
		invalidateAction.setResourceOperations("target.read;selector.read;arg.read;cache.write");
		invalidateAction.setCreateJPC(false);
		repository.addObject(invalidateAction);
		return invalidateAction;
	}

	public void addInvalidate(DataStore repository, DefaultFilterFactory factory) throws UnsupportedFilterTypeException
	{
		FilterType type = new FilterType();
		type.setType(INVALIDATE_FILTER);
		try
		{
			type.setAcceptCallAction(getInvalidateAction(repository));
			type.setRejectCallAction(factory.getContinueAction());
			type.setAcceptReturnAction(factory.getContinueAction());
			type.setRejectReturnAction(factory.getContinueAction());
		}
		catch (UnsupportedFilterActionException e)
		{
			throw new UnsupportedFilterTypeException(INVALIDATE_FILTER, e);
		}
		repository.addObject(type);
		if (factory.getTypeMapping() != null)
		{
			factory.getTypeMapping().registerFilterType(type);
		}
	}
}
