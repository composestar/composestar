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

import java.util.Collection;

import Composestar.Core.CpsRepository2.FilterElements.CanonAssignment;
import Composestar.Core.CpsRepository2.FilterElements.FilterElementExpression;
import Composestar.Core.CpsRepository2.FilterModules.Filter;
import Composestar.Core.CpsRepository2.Filters.FilterType;
import Composestar.Core.CpsRepository2.Instantiatable.Instantiator;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * A proxy to a previously defined filter in the filter module
 * 
 * @author Michiel Hendriks
 */
public class ProxyFilter extends AbstractRepositoryEntity implements Filter
{
	private static final long serialVersionUID = -6918328588720549727L;

	/**
	 * The base filter
	 */
	protected Filter base;

	/**
	 * Create a new filter proxy for a given filter.
	 * 
	 * @param forFilter The filter to create the proxy for
	 * @throws NullPointerException Thrown when the base filter is null
	 */
	public ProxyFilter(Filter forFilter) throws NullPointerException
	{
		super();
		if (forFilter == null)
		{
			throw new NullPointerException("base filter is null");
		}
		base = forFilter;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.Filter#addArgument(Composestar
	 * .Core.CpsRepository2.FilterElements.CanonAssignment)
	 */
	public CanonAssignment addArgument(CanonAssignment argument) throws NullPointerException, IllegalArgumentException
	{
		return base.addArgument(argument);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.Filter#getArgument(java
	 * .lang.String)
	 */
	public CanonAssignment getArgument(String argName)
	{
		return base.getArgument(argName);
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.FilterModules.Filter#getArguments()
	 */
	public Collection<CanonAssignment> getArguments()
	{
		return base.getArguments();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.Filter#getElementExpression
	 * ()
	 */
	public FilterElementExpression getElementExpression()
	{
		return base.getElementExpression();
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.FilterModules.Filter#getType()
	 */
	public FilterType getType()
	{
		return base.getType();
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.FilterModules.Filter#removeArgument(
	 * Composestar.Core.CpsRepository2.FilterElements.CanonAssignment)
	 */
	public CanonAssignment removeArgument(CanonAssignment argument) throws NullPointerException
	{
		return base.removeArgument(argument);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.Filter#setElementExpression
	 * (Composestar.Core.CpsRepository2.FilterElements.FilterElementExpression)
	 */
	public void setElementExpression(FilterElementExpression expr) throws NullPointerException
	{
		base.setElementExpression(expr);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.Filter#setType(Composestar
	 * .Core.CpsRepository2.Filters.FilterType)
	 */
	public void setType(FilterType type) throws NullPointerException
	{
		base.setType(type);
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.QualifiedRepositoryEntity#
	 * getFullyQualifiedName()
	 */
	public String getFullyQualifiedName()
	{
		return base.getFullyQualifiedName();
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.QualifiedRepositoryEntity#getName()
	 */
	public String getName()
	{
		return base.getName();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.Instantiatable.Instantiatable#newInstance
	 * (Composestar.Core.CpsRepository2.Instantiatable.Instantiator)
	 */
	public Filter newInstance(Instantiator instantiator) throws UnsupportedOperationException
	{
		return base.newInstance(instantiator);
	}

}
