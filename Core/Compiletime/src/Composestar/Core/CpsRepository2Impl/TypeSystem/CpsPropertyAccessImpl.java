/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2011 University of Twente.
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

package Composestar.Core.CpsRepository2Impl.TypeSystem;

import Composestar.Core.CpsRepository2.TypeSystem.CpsPropertyAccess;
import Composestar.Core.CpsRepository2.TypeSystem.CpsValue;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * @author arjan
 */
public class CpsPropertyAccessImpl extends AbstractRepositoryEntity implements CpsPropertyAccess
{
	protected String prefix;

	protected String property;

	protected String key;

	public CpsPropertyAccessImpl(String prefix, String property)
	{
		super();
		this.prefix = prefix;
		this.property = property;
	}

	public CpsPropertyAccessImpl(String prefix, String property, String key)
	{
		super();
		this.prefix = prefix;
		this.property = property;
		this.key = key;
	}

	public String getPrefix()
	{
		return prefix;
	}

	public String getProperty()
	{
		return property;
	}

	public String getKey()
	{
		return key;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.TypeSystem.CpsVariable#compatible(Composestar
	 * .Core.CpsRepository2.TypeSystem.CpsVariable)
	 */
	public boolean compatible(CpsVariable other) throws UnsupportedOperationException
	{
		if (!(other instanceof CpsPropertyAccess))
		{
			return false;
		}
		CpsPropertyAccess o = (CpsPropertyAccess) other;
		return (o.getPrefix().equals(this.prefix) && o.getProperty().equals(this.property) && (o.getKey() == null
				&& this.key == null || o.getKey() != null && o.getKey().equals(this.key)));
	}
}
