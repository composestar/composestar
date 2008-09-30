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

package Composestar.Core.CpsRepository2Impl.FilterElements;

import Composestar.Core.CpsRepository2.PropertyNames;
import Composestar.Core.CpsRepository2.PropertyPrefix;
import Composestar.Core.CpsRepository2.FilterElements.CanonProperty;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * Default implementation of a canonical property
 * 
 * @author Michiel Hendriks
 */
public class CanonPropertyImpl extends AbstractRepositoryEntity implements CanonProperty
{
	private static final long serialVersionUID = -1359355252048726437L;

	/**
	 * The base name of the property
	 */
	protected String name;

	/**
	 * The prefix
	 */
	protected PropertyPrefix prefix;

	/**
	 * @param propPrefix The prefix of the property, either
	 *            {@link PropertyNames#FILTER_PREFIX} or
	 *            {@link PropertyNames#MESSAGE_PREFIX}
	 * @param propName The base name of the property
	 * @throws NullPointerException Thrown when the property name is null
	 * @throws IllegalArgumentException Thrown when the name is empty, or when
	 *             the prefix is {@link PropertyPrefix.NONE} and the name is not
	 *             {@link PropertyNames.INNER}
	 */
	public CanonPropertyImpl(PropertyPrefix propPrefix, String propName) throws NullPointerException,
			IllegalArgumentException
	{
		super();
		if (propPrefix == null)
		{
			throw new NullPointerException("prefix can not be null");
		}
		if (propPrefix == PropertyPrefix.NONE && !PropertyNames.INNER.equals(propName))
		{
			throw new IllegalArgumentException("NONE is only allowed with inner as name");
		}
		if (propName == null)
		{
			throw new NullPointerException("name can not be null");
		}
		if (propName.isEmpty())
		{
			throw new IllegalArgumentException("name can not be empty");
		}
		prefix = propPrefix;
		name = propName;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterElements.CanonProperty#getBaseName
	 * ()
	 */
	public String getBaseName()
	{
		return name;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterElements.CanonProperty#getName()
	 */
	public String getName()
	{
		if (prefix == PropertyPrefix.NONE)
		{
			return name;
		}
		return String.format("%s.%s", prefix.toString(), name);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterElements.CanonProperty#getPrefix()
	 */
	public PropertyPrefix getPrefix()
	{
		return prefix;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return getName() + " [" + super.toString() + "]";
	}
}
