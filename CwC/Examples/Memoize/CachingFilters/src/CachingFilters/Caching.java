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

package CachingFilters;

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
import Composestar.Core.SECRET3.SECRETResources;
import Composestar.Core.SECRET3.Model.ConflictRule;
import Composestar.Core.SECRET3.Model.Resource;
import Composestar.Core.SECRET3.Model.RuleType;
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

	private FilterActionImpl cacheAction;

	private FilterActionImpl cacheReturnAction;

	private FilterActionImpl invalidateAction;

	/*
	 * (non-Javadoc)
	 * @see Composestar.CwC.Filters.CustomCwCFilters#getCodeGenerators()
	 */
	public Collection<FilterActionCodeGenerator<String>> getCodeGenerators()
	{
		Set<FilterActionCodeGenerator<String>> facgs = new HashSet<FilterActionCodeGenerator<String>>();
		facgs.add(new CachingFilterActionCodeGen());
		return facgs;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.CwC.Filters.CustomCwCFilters#registerFilters(Composestar.
	 * Core.RepositoryImplementation.DataStore,
	 * Composestar.Core.CpsProgramRepository.Filters.FilterTypeFactory)
	 */
	public void registerFilters(Repository repository, FilterTypeFactory factory)
	{
		addCache(repository, factory);
		addInvalidate(repository, factory);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.CwC.Filters.CustomCwCFilters#registerSecretResources(Composestar
	 * .Core.CKRET.SECRETResources)
	 */
	public void registerSecretResources(SECRETResources resources)
	{
		Resource rsc = new Resource("cache");
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

	public FilterAction getCacheAction(Repository repository)
	{
		if (cacheAction != null)
		{
			return cacheAction;
		}
		cacheAction = new FilterActionImpl(CACHE_ACTION);
		cacheAction.setResourceOperations("target.read;selector.read;arg.read;cache.read;return.write");
		repository.add(cacheAction);
		return cacheAction;
	}

	public FilterAction getCacheReturnAction(Repository repository)
	{
		if (cacheReturnAction != null)
		{
			return cacheReturnAction;
		}
		cacheReturnAction = new FilterActionImpl(CACHE_RETURN_ACTION);
		cacheReturnAction.setResourceOperations("target.read;selector.read;arg.read;cache.write");
		repository.add(cacheReturnAction);
		return cacheReturnAction;
	}

	public void addCache(Repository repository, FilterTypeFactory factory)
	{
		PrimitiveFilterTypeImpl type = new PrimitiveFilterTypeImpl(CACHE_FILTER);
		type.setAcceptCallAction(getCacheAction(repository));
		type.setRejectCallAction(factory.getContinueAction());
		type.setAcceptReturnAction(factory.getContinueAction());
		type.setAcceptReturnAction(getCacheReturnAction(repository));
		type.setRejectReturnAction(factory.getContinueAction());
		repository.add(type);
	}

	public FilterAction getInvalidateAction(Repository repository)
	{
		if (invalidateAction != null)
		{
			return invalidateAction;
		}
		invalidateAction = new FilterActionImpl(INVALIDATE_ACTION);
		invalidateAction.setResourceOperations("target.read;selector.read;arg.read;cache.write");
		repository.add(invalidateAction);
		return invalidateAction;
	}

	public void addInvalidate(Repository repository, FilterTypeFactory factory)
	{
		PrimitiveFilterTypeImpl type = new PrimitiveFilterTypeImpl(INVALIDATE_FILTER);
		type.setAcceptCallAction(factory.getContinueAction());
		type.setRejectCallAction(factory.getContinueAction());
		type.setAcceptReturnAction(getInvalidateAction(repository));
		type.setRejectReturnAction(factory.getContinueAction());
		repository.add(type);
	}
}
