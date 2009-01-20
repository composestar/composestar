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

package Composestar.Core.CpsRepository2Impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

import Composestar.Core.CpsRepository2.QualifiedRepositoryEntity;
import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.CpsRepository2.RepositoryEntity;

/**
 * Implementation of the repository interface based on a LinkedHashSet for
 * improved performance.
 * 
 * @author Michiel Hendriks
 */
public class RepositoryImpl extends LinkedHashSet<RepositoryEntity> implements Repository
{
	private static final long serialVersionUID = -3840939976918808399L;

	/**
	 * If true the qualified entry map is double checked when requesting
	 * elements.
	 */
	protected boolean fqnGuard = true;

	/**
	 * This map is used for providing a quick lookup of qualified repository
	 * entities by their name. This assumes that their name never changes.
	 */
	protected Map<String, QualifiedRepositoryEntity> qualifiedEntities;

	public RepositoryImpl()
	{
		// a hashmap because random access is used and order is insignificant
		qualifiedEntities = new HashMap<String, QualifiedRepositoryEntity>();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.HashSet#add(java.lang.Object)
	 */
	@Override
	public boolean add(RepositoryEntity e)
	{
		boolean result = super.add(e);
		if (result && e instanceof QualifiedRepositoryEntity)
		{
			result = addFqnEntry((QualifiedRepositoryEntity) e);
		}
		return result;
	}

	/**
	 * Add a qualified element to the map. It will reassign elements that were
	 * overwritten but have a new FQN
	 * 
	 * @param qe
	 * @return true when it was successfully added
	 */
	protected boolean addFqnEntry(QualifiedRepositoryEntity qe)
	{
		QualifiedRepositoryEntity lostQe = qualifiedEntities.put(qe.getFullyQualifiedName(), qe);
		if (lostQe != null && lostQe != qe)
		{
			if (!lostQe.getFullyQualifiedName().equals(qe.getFullyQualifiedName()))
			{
				// FQN was changed, add it again
				addFqnEntry(lostQe);
			}
			// TODO: error?
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.HashSet#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o)
	{
		boolean result = super.remove(o);
		if (o instanceof QualifiedRepositoryEntity)
		{
			String fqn = ((QualifiedRepositoryEntity) o).getFullyQualifiedName();
			if (qualifiedEntities.get(fqn) == o)
			{
				qualifiedEntities.remove(fqn);
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractSet#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c)
	{
		boolean result = super.removeAll(c);
		Iterator<Entry<String, QualifiedRepositoryEntity>> it = qualifiedEntities.entrySet().iterator();
		while (it.hasNext())
		{
			Entry<String, QualifiedRepositoryEntity> entry = it.next();
			if (c.contains(entry.getValue()))
			{
				it.remove();
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> c)
	{
		boolean result = super.retainAll(c);
		Iterator<Entry<String, QualifiedRepositoryEntity>> it = qualifiedEntities.entrySet().iterator();
		while (it.hasNext())
		{
			Entry<String, QualifiedRepositoryEntity> entry = it.next();
			if (!c.contains(entry.getValue()))
			{
				it.remove();
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.HashSet#clear()
	 */
	@Override
	public void clear()
	{
		super.clear();
		qualifiedEntities.clear();
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.Repository#get(java.lang.String)
	 */
	public QualifiedRepositoryEntity get(String fqn)
	{
		QualifiedRepositoryEntity res = qualifiedEntities.get(fqn);
		if (fqnGuard && res != null && !res.getFullyQualifiedName().equals(fqn))
		{
			qualifiedEntities.remove(fqn);
			qualifiedEntities.put(res.getFullyQualifiedName(), res);
			res = null;
			for (QualifiedRepositoryEntity r : getAll(QualifiedRepositoryEntity.class))
			{
				if (r.getFullyQualifiedName().equals(fqn))
				{
					res = r;
					qualifiedEntities.put(res.getFullyQualifiedName(), res);
					break;
				}
			}
		}
		return res;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.Repository#get(java.lang.String,
	 * java.lang.Class)
	 */
	public <T extends QualifiedRepositoryEntity> T get(String fqn, Class<T> type)
	{
		QualifiedRepositoryEntity qre = get(fqn);
		if (type.isInstance(qre))
		{
			return type.cast(qre);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.Repository#getAll(java.lang.Class)
	 */
	public <T extends RepositoryEntity> Iterable<T> getAll(Class<T> type)
	{
		return new RepositoryIterable<T>(type);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.Repository#getAllAsIterator(java.lang
	 * .Class)
	 */
	public <T extends RepositoryEntity> Iterator<T> getAllIterator(Class<T> type)
	{
		return new RepositoryIterator<T>(type);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.Repository#getAllAsSet(java.lang.Class)
	 */
	public <T extends RepositoryEntity> Set<T> getAllSet(Class<T> type)
	{
		Set<T> result = new LinkedHashSet<T>();
		for (T item : getAll(type))
		{
			result.add(item);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.HashSet#iterator()
	 */
	@Override
	public Iterator<RepositoryEntity> iterator()
	{
		// return a specialized iterator that will also remove qualified entries
		// from the qualified entry table
		return new RepositoryIterator<RepositoryEntity>(RepositoryEntity.class);
	}

	/**
	 * @return The super iterator implementation, used by the RepositoryIterator
	 *         implementation
	 */
	public Iterator<RepositoryEntity> superIterator()
	{
		return super.iterator();
	}

	/**
	 * An iterable that creates an filtered iterator when needed
	 * 
	 * @author Michiel Hendriks
	 */
	private final class RepositoryIterable<T> implements Iterable<T>
	{
		/**
		 * The class to filter on
		 */
		private Class<T> filterClass;

		/**
		 * Create a new repository iterator using a given filter
		 * 
		 * @param filter
		 */
		public RepositoryIterable(Class<T> filter)
		{
			filterClass = filter;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		public Iterator<T> iterator()
		{
			return new RepositoryIterator<T>(filterClass);
		}

	}

	/**
	 * A filtering iterator.
	 * 
	 * @author Michiel Hendriks
	 */
	private final class RepositoryIterator<T> implements Iterator<T>
	{
		/**
		 * The base iterator
		 */
		private Iterator<?> it;

		/**
		 * The class to filter on
		 */
		private Class<T> filterClass;

		/**
		 * Holds the next element
		 */
		private T next;

		/**
		 * True if the next object has been set
		 */
		private boolean nextSet = false;

		/**
		 * Create a new repository iterator using a given filter
		 * 
		 * @param filter
		 */
		public RepositoryIterator(Class<T> filter)
		{
			it = superIterator();
			filterClass = filter;
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext()
		{
			if (nextSet)
			{
				return true;
			}
			else
			{
				return getNext();
			}
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		public T next()
		{
			if (!nextSet)
			{
				if (!getNext())
				{
					throw new NoSuchElementException();
				}
			}
			nextSet = false;
			return next;
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		public void remove()
		{
			if (nextSet)
			{
				// nextSet is false after next() was called
				// only allow remove after next() was called
				throw new IllegalStateException("next() has not been called yet");
			}
			if (next instanceof QualifiedRepositoryEntity)
			{
				qualifiedEntities.values().remove(next);
			}
			it.remove();
		}

		/**
		 * Get the next element
		 * 
		 * @return True when there is a next element.
		 */
		private boolean getNext()
		{
			while (it.hasNext())
			{
				Object o = it.next();
				if (filterClass.isInstance(o))
				{
					next = filterClass.cast(o);
					nextSet = true;
					return true;
				}
			}
			return false;
		}

	}
}
