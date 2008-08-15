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

package Composestar.Core.CpsRepository2Impl.SuperImposition;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Composestar.Core.CpsRepository2.SuperImposition.AnnotationBinding;
import Composestar.Core.CpsRepository2.SuperImposition.Condition;
import Composestar.Core.CpsRepository2.SuperImposition.FilterModuleBinding;
import Composestar.Core.CpsRepository2.SuperImposition.FilterModuleConstraint;
import Composestar.Core.CpsRepository2.SuperImposition.Selector;
import Composestar.Core.CpsRepository2.SuperImposition.SuperImposition;
import Composestar.Core.CpsRepository2Impl.AbstractQualifiedRepositoryEntity;

/**
 * Basic implementation of superimposition
 * 
 * @author Michiel Hendriks
 */
public class SuperImpositionImpl extends AbstractQualifiedRepositoryEntity implements SuperImposition
{
	private static final long serialVersionUID = -5676161237343003047L;

	/**
	 * Registered selectors
	 */
	protected Map<String, Selector> selectors;

	/**
	 * Conditions, these are used in filter module constraints of type cond(X,Y)
	 * to provide conditional filter module execution
	 */
	protected Map<String, Condition> conditions;

	/**
	 * Annotation bindings
	 */
	protected Set<AnnotationBinding> annotationBindings;

	/**
	 * Filter module bindings
	 */
	protected Set<FilterModuleBinding> filterModuleBindings;

	/**
	 * Filter module constraint
	 */
	protected Set<FilterModuleConstraint> filterModuleConstraints;

	/**
	 * Create a new instance
	 */
	public SuperImpositionImpl()
	{
		super(NAME);
		annotationBindings = new HashSet<AnnotationBinding>();
		filterModuleBindings = new HashSet<FilterModuleBinding>();
		filterModuleConstraints = new HashSet<FilterModuleConstraint>();
		selectors = new HashMap<String, Selector>();
		conditions = new HashMap<String, Condition>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#addAnnotationBinding(Composestar.Core.CpsRepository2.SuperImposition.AnnotationBinding)
	 */
	public void addAnnotationBinding(AnnotationBinding ab) throws NullPointerException
	{
		if (ab == null)
		{
			throw new NullPointerException();
		}
		annotationBindings.add(ab);
		ab.setOwner(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#addCondition(Composestar.Core.CpsRepository2.SuperImposition.Condition)
	 */
	public boolean addCondition(Condition cond) throws NullPointerException
	{
		if (cond == null)
		{
			throw new NullPointerException();
		}
		if (!isUniqueName(cond.getName()))
		{
			return false;
		}
		conditions.put(cond.getName(), cond);
		cond.setOwner(this);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#addFilterModuleBinding(Composestar.Core.CpsRepository2.SuperImposition.FilterModuleBinding)
	 */
	public void addFilterModuleBinding(FilterModuleBinding fmb) throws NullPointerException
	{
		if (fmb == null)
		{
			throw new NullPointerException();
		}
		filterModuleBindings.add(fmb);
		fmb.setOwner(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#addFilterModuleConstraint(Composestar.Core.CpsRepository2.SuperImposition.FilterModuleConstraint)
	 */
	public void addFilterModuleConstraint(FilterModuleConstraint fmc) throws NullPointerException
	{
		if (fmc == null)
		{
			throw new NullPointerException();
		}
		filterModuleConstraints.add(fmc);
		fmc.setOwner(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#addSelector(Composestar.Core.CpsRepository2.SuperImposition.Selector)
	 */
	public boolean addSelector(Selector newSel) throws NullPointerException
	{
		if (newSel == null)
		{
			throw new NullPointerException();
		}
		if (!isUniqueName(newSel.getName()))
		{
			return false;
		}
		selectors.put(newSel.getName(), newSel);
		newSel.setOwner(this);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#getAnnotationBindings()
	 */
	public Set<AnnotationBinding> getAnnotationBindings()
	{
		return Collections.unmodifiableSet(annotationBindings);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#getCondition(java.lang.String)
	 */
	public Condition getCondition(String name)
	{
		if (name == null)
		{
			return null;
		}
		return conditions.get(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#getConditions()
	 */
	public Set<Condition> getConditions()
	{
		return Collections.unmodifiableSet(new HashSet<Condition>(conditions.values()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#getFilterModuleBindings()
	 */
	public Set<FilterModuleBinding> getFilterModuleBindings()
	{
		return Collections.unmodifiableSet(filterModuleBindings);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#getFilterModuleConstraints()
	 */
	public Set<FilterModuleConstraint> getFilterModuleConstraints()
	{
		return Collections.unmodifiableSet(filterModuleConstraints);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#getSelector(java.lang.String)
	 */
	public Selector getSelector(String name)
	{
		if (name == null)
		{
			return null;
		}
		return selectors.get(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#getSelectors()
	 */
	public Set<Selector> getSelectors()
	{
		return Collections.unmodifiableSet(new HashSet<Selector>(selectors.values()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#removeAnnotationBinding(Composestar.Core.CpsRepository2.SuperImposition.AnnotationBinding)
	 */
	public boolean removeAnnotationBinding(AnnotationBinding ab) throws NullPointerException
	{
		if (ab == null)
		{
			throw new NullPointerException();
		}
		return annotationBindings.remove(ab);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#removeCondition(Composestar.Core.CpsRepository2.SuperImposition.Condition)
	 */
	public boolean removeCondition(Condition cond) throws NullPointerException
	{
		if (cond == null)
		{
			throw new NullPointerException();
		}
		if (conditions.containsValue(cond))
		{
			return conditions.values().remove(cond);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#removeCondition(java.lang.String)
	 */
	public Condition removeCondition(String name)
	{
		if (name == null)
		{
			return null;
		}
		Condition res = conditions.get(name);
		conditions.remove(name);
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#removeFilterModuleBinding(Composestar.Core.CpsRepository2.SuperImposition.FilterModuleBinding)
	 */
	public boolean removeFilterModuleBinding(FilterModuleBinding fmb) throws NullPointerException
	{
		return filterModuleBindings.remove(fmb);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#removeFilterModuleConstraint(Composestar.Core.CpsRepository2.SuperImposition.FilterModuleConstraint)
	 */
	public boolean removeFilterModuleConstraint(FilterModuleConstraint fmc) throws NullPointerException
	{
		return filterModuleConstraints.remove(fmc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#removeSelector(Composestar.Core.CpsRepository2.SuperImposition.Selector)
	 */
	public boolean removeSelector(Selector sel) throws NullPointerException
	{
		if (sel == null)
		{
			throw new NullPointerException();
		}
		if (selectors.containsValue(sel))
		{
			return selectors.values().remove(sel);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#removeSelector(java.lang.String)
	 */
	public Selector removeSelector(String name)
	{
		if (name == null)
		{
			return null;
		}
		Selector res = selectors.get(name);
		selectors.remove(name);
		return res;
	}

	/**
	 * Validates uniqueness of a name within the superimposition. This checks
	 * the condition and selector lists.
	 * 
	 * @param name
	 * @return True when the name is unique within this selector.
	 */
	protected boolean isUniqueName(String name)
	{
		return !selectors.containsKey(name) && !conditions.containsKey(name);
	}
}
