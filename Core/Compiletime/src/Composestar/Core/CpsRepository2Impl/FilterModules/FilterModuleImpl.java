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

package Composestar.Core.CpsRepository2Impl.FilterModules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import Composestar.Core.CpsRepository2.FMParams.FMParameter;
import Composestar.Core.CpsRepository2.FilterModules.Condition;
import Composestar.Core.CpsRepository2.FilterModules.External;
import Composestar.Core.CpsRepository2.FilterModules.Filter;
import Composestar.Core.CpsRepository2.FilterModules.FilterExpression;
import Composestar.Core.CpsRepository2.FilterModules.FilterModule;
import Composestar.Core.CpsRepository2.FilterModules.FilterModuleVariable;
import Composestar.Core.CpsRepository2.FilterModules.Internal;
import Composestar.Core.CpsRepository2.Instantiatable.Instantiator;
import Composestar.Core.CpsRepository2Impl.AbstractQualifiedRepositoryEntity;

/**
 * Basic implementation of the filter module interface
 * 
 * @author Michiel Hendriks
 */
public class FilterModuleImpl extends AbstractQualifiedRepositoryEntity implements FilterModule
{
	private static final long serialVersionUID = 4043743753616281947L;

	/**
	 * The filter module parameter list. Could be empty.
	 */
	protected List<FMParameter> parameters;

	/**
	 * Contains all filter module variables
	 */
	protected Map<String, FilterModuleVariable> variables;

	/**
	 * The expression for input filters
	 */
	protected FilterExpression inputFilterExpression;

	/**
	 * The expression for output filters
	 */
	protected FilterExpression outputFilterExpression;

	/**
	 * List of registered filter declarations
	 */
	protected Map<String, Filter> filters;

	/**
	 * Create a new filter module.
	 * 
	 * @param filterModuleName
	 * @throws NullPointerException Thrown when the filter module name is null
	 * @throws IllegalArgumentException Thrown when the filter module name is
	 *             empty
	 */
	public FilterModuleImpl(String filterModuleName) throws NullPointerException, IllegalArgumentException
	{
		super(filterModuleName);
		parameters = new ArrayList<FMParameter>();
		variables = new LinkedHashMap<String, FilterModuleVariable>();
		filters = new LinkedHashMap<String, Filter>();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#addParameter
	 * (Composestar.Core.CpsRepository2.FMParams.FMParameter)
	 */
	public boolean addParameter(FMParameter param) throws NullPointerException
	{
		if (param == null)
		{
			throw new NullPointerException();
		}
		for (FMParameter fmp : parameters)
		{
			if (fmp.getRawName().equals(param.getRawName()))
			{
				return false;
			}
		}
		parameters.add(param);
		param.setOwner(this);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#addVariable
	 * (Composestar.Core.CpsRepository2.FilterModules.FilterModuleVariable)
	 */
	public boolean addVariable(FilterModuleVariable var) throws NullPointerException
	{
		if (var == null)
		{
			throw new NullPointerException();
		}
		if (variables.containsKey(var.getName()))
		{
			return false;
		}
		variables.put(var.getName(), var);
		var.setOwner(this);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getCondition
	 * (java.lang.String)
	 */
	public Condition getCondition(String conditionName)
	{
		FilterModuleVariable var = getVariable(conditionName);
		if (var instanceof Condition)
		{
			return Condition.class.cast(var);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getExternal
	 * (java.lang.String)
	 */
	public External getExternal(String externalName)
	{
		FilterModuleVariable var = getVariable(externalName);
		if (var instanceof External)
		{
			return External.class.cast(var);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.FilterModules.FilterModule#
	 * getInputFilterExpression()
	 */
	public FilterExpression getInputFilterExpression()
	{
		return inputFilterExpression;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getInternal
	 * (java.lang.String)
	 */
	public Internal getInternal(String internalName)
	{
		FilterModuleVariable var = getVariable(internalName);
		if (var instanceof Internal)
		{
			return Internal.class.cast(var);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.FilterModules.FilterModule#
	 * getOutputFilterExpression()
	 */
	public FilterExpression getOutputFilterExpression()
	{
		return outputFilterExpression;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getParameter
	 * (java.lang.String)
	 */
	public FMParameter getParameter(String paramName)
	{
		if (paramName == null || paramName.isEmpty())
		{
			return null;
		}
		for (FMParameter fmp : parameters)
		{
			if (fmp.getName().equals(paramName))
			{
				return fmp;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getParameters
	 * ()
	 */
	public List<FMParameter> getParameters()
	{
		return Collections.unmodifiableList(parameters);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getVariable
	 * (java.lang.String)
	 */
	public FilterModuleVariable getVariable(String varName)
	{
		return variables.get(varName);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getVariables()
	 */
	public Collection<FilterModuleVariable> getVariables()
	{
		return Collections.unmodifiableCollection(variables.values());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#hasParameters
	 * ()
	 */
	public boolean hasParameters()
	{
		return !parameters.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#removeVariable
	 * (Composestar.Core.CpsRepository2.FilterModules.FilterModuleVariable)
	 */
	public FilterModuleVariable removeVariable(FilterModuleVariable var) throws NullPointerException
	{
		if (var == null)
		{
			throw new NullPointerException();
		}
		if (variables.values().remove(var))
		{
			return var;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#removeVariable
	 * (java.lang.String)
	 */
	public FilterModuleVariable removeVariable(String varName)
	{
		return variables.remove(varName);
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.FilterModules.FilterModule#
	 * setInputFilterExpression
	 * (Composestar.Core.CpsRepository2.FilterModules.FilterExpression)
	 */
	public void setInputFilterExpression(FilterExpression expr)
	{
		inputFilterExpression = expr;
		if (expr != null)
		{
			expr.setOwner(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.FilterModules.FilterModule#
	 * setOutputFilterExpression
	 * (Composestar.Core.CpsRepository2.FilterModules.FilterExpression)
	 */
	public void setOutputFilterExpression(FilterExpression expr)
	{
		outputFilterExpression = expr;
		if (expr != null)
		{
			expr.setOwner(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#addFilter(
	 * Composestar.Core.CpsRepository2.FilterModules.Filter)
	 */
	public boolean addFilter(Filter filter) throws NullPointerException
	{
		if (filter == null)
		{
			throw new NullPointerException();
		}
		if (filters.containsKey(filter.getName()))
		{
			return false;
		}
		filters.put(filter.getName(), filter);
		filter.setOwner(this);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getFilter(
	 * java.lang.String)
	 */
	public Filter getFilter(String filterName)
	{
		return filters.get(filterName);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getFilters()
	 */
	public Collection<Filter> getFilters()
	{
		return Collections.unmodifiableCollection(filters.values());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#removeFilter
	 * (Composestar.Core.CpsRepository2.FilterModules.Filter)
	 */
	public Filter removeFilter(Filter filter) throws NullPointerException
	{
		if (filter == null)
		{
			throw new NullPointerException();
		}
		if (filters.containsValue(filter))
		{
			filters.values().remove(filter);
			return filter;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#removeFilter
	 * (java.lang.String)
	 */
	public Filter removeFilter(String filterName)
	{
		return filters.remove(filterName);
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.References.Reference#dereference()
	 */
	public void dereference() throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.References.Reference#getReference()
	 */
	public FilterModule getReference()
	{
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.References.Reference#getReferenceId()
	 */
	public String getReferenceId()
	{
		return getFullyQualifiedName();
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.References.Reference#isResolved()
	 */
	public boolean isResolved()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.References.Reference#isSelfReference()
	 */
	public boolean isSelfReference()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.References.Reference#setReference(java
	 * .lang.Object)
	 */
	public void setReference(FilterModule element) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.Instantiatable.Instantiatable#newInstance
	 * (Composestar.Core.CpsRepository2.Instantiatable.Instantiator)
	 */
	public FilterModule newInstance(Instantiator instantiator) throws UnsupportedOperationException
	{
		return instantiator.instantiate(this);
	}
}
