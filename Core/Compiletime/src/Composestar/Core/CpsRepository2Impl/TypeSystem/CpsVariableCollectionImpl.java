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

package Composestar.Core.CpsRepository2Impl.TypeSystem;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariableCollection;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * @author Michiel Hendriks
 */
public class CpsVariableCollectionImpl extends AbstractRepositoryEntity implements CpsVariableCollection
{
	private static final long serialVersionUID = -8921595216409648452L;

	/**
	 * Delegate storage to this collection
	 */
	protected Collection<CpsVariable> values;

	/**
	 * Create a new instance
	 */
	public CpsVariableCollectionImpl()
	{
		values = new LinkedHashSet<CpsVariable>();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	public boolean add(CpsVariable e)
	{
		if (e == null)
		{
			throw new NullPointerException();
		}
		if (values.add(e))
		{
			if (e.getOwner() == null)
			{
				e.setOwner(this);
			}
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends CpsVariable> c)
	{
		if (c == null)
		{
			throw new NullPointerException();
		}
		if (values.addAll(c))
		{
			for (CpsVariable v : c)
			{
				if (v != null && v.getOwner() == null)
				{
					v.setOwner(this);
				}
			}
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#clear()
	 */
	public void clear()
	{
		values.clear();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	public boolean contains(Object o)
	{
		return values.contains(o);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<?> c)
	{
		return values.containsAll(c);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#isEmpty()
	 */
	public boolean isEmpty()
	{
		return values.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#iterator()
	 */
	public Iterator<CpsVariable> iterator()
	{
		return values.iterator();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	public boolean remove(Object o)
	{
		return values.remove(o);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> c)
	{
		return values.removeAll(c);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> c)
	{
		return values.retainAll(c);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#size()
	 */
	public int size()
	{
		return values.size();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#toArray()
	 */
	public Object[] toArray()
	{
		return values.toArray();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#toArray(T[])
	 */
	public <T> T[] toArray(T[] a)
	{
		return values.toArray(a);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return values.toString();
	}
}
