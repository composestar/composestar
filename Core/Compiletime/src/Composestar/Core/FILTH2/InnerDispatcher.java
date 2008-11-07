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

package Composestar.Core.FILTH2;

import java.util.ArrayList;

import Composestar.Core.COPPER3.FilterTypeMapping;
import Composestar.Core.CpsRepository2.CpsConcern;
import Composestar.Core.CpsRepository2.PropertyNames;
import Composestar.Core.CpsRepository2.PropertyPrefix;
import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.CpsRepository2.FilterElements.CanonAssignment;
import Composestar.Core.CpsRepository2.FilterElements.CanonProperty;
import Composestar.Core.CpsRepository2.FilterElements.FilterElement;
import Composestar.Core.CpsRepository2.FilterModules.Filter;
import Composestar.Core.CpsRepository2.FilterModules.FilterModule;
import Composestar.Core.CpsRepository2.Filters.FilterType;
import Composestar.Core.CpsRepository2.Filters.FilterTypeNames;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariableCollection;
import Composestar.Core.CpsRepository2Impl.CpsConcernImpl;
import Composestar.Core.CpsRepository2Impl.FilterElements.CanonAssignmentImpl;
import Composestar.Core.CpsRepository2Impl.FilterElements.CanonPropertyImpl;
import Composestar.Core.CpsRepository2Impl.FilterElements.FilterElementImpl;
import Composestar.Core.CpsRepository2Impl.FilterElements.SignatureMatching;
import Composestar.Core.CpsRepository2Impl.FilterModules.FilterImpl;
import Composestar.Core.CpsRepository2Impl.FilterModules.FilterModuleImpl;
import Composestar.Core.CpsRepository2Impl.TypeSystem.CpsVariableCollectionImpl;

/**
 * Create the inner dispatcher filter module
 * 
 * @author Michiel Hendriks
 */
public final class InnerDispatcher
{
	public static FilterModule createInnerDispatcher(Repository repository, FilterTypeMapping filterTypes)
	{
		CpsConcern concern = new CpsConcernImpl(DefaultInnerDispatchNames.CONCERN, new ArrayList<String>());
		repository.add(concern);
		FilterModule fm = new FilterModuleImpl(DefaultInnerDispatchNames.FILTER_MODULE);
		concern.addFilterModule(fm);
		repository.add(fm);
		fm.setInputFilterExpression(createFilter(repository, fm, DefaultInnerDispatchNames.INPUT_FILTER, filterTypes
				.getFilterType(FilterTypeNames.DISPATCH)));
		fm.setOutputFilterExpression(createFilter(repository, fm, DefaultInnerDispatchNames.OUTPUT_FILTER, filterTypes
				.getFilterType(FilterTypeNames.SEND)));
		return fm;
	}

	protected static Filter createFilter(Repository repository, FilterModule fm, String name, FilterType type)
	{
		Filter filter = new FilterImpl(name);
		filter.setOwner(fm);
		repository.add(filter);

		filter.setType(type);

		FilterElement expr = new FilterElementImpl();
		filter.setElementExpression(expr);
		repository.add(expr);

		SignatureMatching sigm = new SignatureMatching();
		expr.setMatchingExpression(sigm);
		repository.add(sigm);

		CanonProperty prop = new CanonPropertyImpl(PropertyPrefix.MESSAGE, PropertyNames.SELECTOR);
		sigm.setLHS(prop);
		repository.add(prop);

		prop = new CanonPropertyImpl(PropertyPrefix.NONE, PropertyNames.INNER);
		CpsVariableCollection values = new CpsVariableCollectionImpl();
		values.add(prop);
		sigm.setRHS(values);
		repository.add(prop);
		repository.add(values);

		CanonAssignment asgn = new CanonAssignmentImpl();

		prop = new CanonPropertyImpl(PropertyPrefix.MESSAGE, PropertyNames.TARGET);
		asgn.setProperty(prop);
		repository.add(prop);

		prop = new CanonPropertyImpl(PropertyPrefix.NONE, PropertyNames.INNER);
		asgn.setValue(prop);
		repository.add(prop);

		expr.addAssignment(asgn);
		repository.add(asgn);
		return filter;
	}
}
