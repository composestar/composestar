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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import Composestar.Core.CpsRepository2.PropertyPrefix;
import Composestar.Core.CpsRepository2.FilterElements.CanonAssignment;
import Composestar.Core.CpsRepository2.FilterElements.FilterElementExpression;
import Composestar.Core.CpsRepository2.FilterModules.Filter;
import Composestar.Core.CpsRepository2.Filters.FilterType;
import Composestar.Core.CpsRepository2.Instantiatable.Instantiator;
import Composestar.Core.CpsRepository2Impl.AbstractQualifiedRepositoryEntity;

/**
 * Standard implementation of the filter interface
 * 
 * @author Michiel Hendriks
 */
public class FilterImpl extends AbstractQualifiedRepositoryEntity implements Filter
{
	private static final long serialVersionUID = 7204223983558325051L;

	/**
	 * The filter arguments, apply to all filter elements.
	 */
	protected Map<String, CanonAssignment> arguments;

	/**
	 * The filter element expression
	 */
	protected FilterElementExpression elementExpression;

	/**
	 * The used filter type
	 */
	protected FilterType filterType;

	/**
	 * @param entityName
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 */
	public FilterImpl(String entityName) throws NullPointerException, IllegalArgumentException
	{
		super(entityName);
		arguments = new HashMap<String, CanonAssignment>();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.Filter#addArgument(Composestar
	 * .Core.CpsRepository2.FilterElements.CanonAssignment)
	 */
	public CanonAssignment addArgument(CanonAssignment argument) throws NullPointerException, IllegalArgumentException
	{
		if (argument == null)
		{
			throw new NullPointerException();
		}
		if (argument.getProperty() == null)
		{
			throw new IllegalArgumentException("Argument does not assign a variable");
		}
		if (argument.getProperty().getPrefix() != PropertyPrefix.FILTER)
		{
			throw new IllegalArgumentException("Argument does not assign a filter argument");
		}
		CanonAssignment repl = arguments.put(argument.getProperty().getBaseName(), argument);
		argument.setOwner(this);
		return repl;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.Filter#getArgument(java
	 * .lang.String)
	 */
	public CanonAssignment getArgument(String argName)
	{
		return arguments.get(argName);
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.FilterModules.Filter#getArguments()
	 */
	public Collection<CanonAssignment> getArguments()
	{
		return Collections.unmodifiableCollection(arguments.values());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.Filter#getElementExpression
	 * ()
	 */
	public FilterElementExpression getElementExpression()
	{
		return elementExpression;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.FilterModules.Filter#getType()
	 */
	public FilterType getType()
	{
		return filterType;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.FilterModules.Filter#removeArgument(
	 * Composestar.Core.CpsRepository2.FilterElements.CanonAssignment)
	 */
	public CanonAssignment removeArgument(CanonAssignment argument) throws NullPointerException
	{
		if (argument == null)
		{
			throw new NullPointerException();
		}
		if (arguments.values().remove(argument))
		{
			return argument;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.Filter#setElementExpression
	 * (Composestar.Core.CpsRepository2.FilterElements.FilterElementExpression)
	 */
	public void setElementExpression(FilterElementExpression expr) throws NullPointerException
	{
		if (expr == null)
		{
			throw new NullPointerException();
		}
		elementExpression = expr;
		expr.setOwner(this);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.Filter#setType(Composestar
	 * .Core.Config.FilterType)
	 */
	public void setType(FilterType type) throws NullPointerException
	{
		if (type == null)
		{
			throw new NullPointerException();
		}
		filterType = type;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.Instantiatable.Instantiatable#newInstance
	 * (Composestar.Core.CpsRepository2.Instantiatable.Instantiator)
	 */
	public Filter newInstance(Instantiator instantiator) throws UnsupportedOperationException
	{
		return instantiator.instantiate(this);
	}

}
