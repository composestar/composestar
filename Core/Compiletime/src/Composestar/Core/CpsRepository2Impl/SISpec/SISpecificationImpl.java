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

package Composestar.Core.CpsRepository2Impl.SISpec;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Composestar.Core.CpsRepository2.SISpec.AnnotationBinding;
import Composestar.Core.CpsRepository2.SISpec.FilterModuleBinding;
import Composestar.Core.CpsRepository2.SISpec.SICondition;
import Composestar.Core.CpsRepository2.SISpec.SISpecification;
import Composestar.Core.CpsRepository2.SISpec.Selector;
import Composestar.Core.CpsRepository2.SISpec.Constraints.Constraint;
import Composestar.Core.CpsRepository2Impl.AbstractQualifiedRepositoryEntity;

/**
 * Basic implementation of superimposition
 * 
 * @author Michiel Hendriks
 */
public class SISpecificationImpl extends AbstractQualifiedRepositoryEntity implements SISpecification
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
	protected Map<String, SICondition> conditions;

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
	protected Set<Constraint> filterModuleConstraints;

	/**
	 * Create a new instance
	 */
	public SISpecificationImpl()
	{
		super(NAME);
		annotationBindings = new HashSet<AnnotationBinding>();
		filterModuleBindings = new HashSet<FilterModuleBinding>();
		filterModuleConstraints = new HashSet<Constraint>();
		selectors = new HashMap<String, Selector>();
		conditions = new HashMap<String, SICondition>();
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
	 * addAnnotationBinding
	 * (Composestar.Core.CpsRepository2.SISpec.AnnotationBinding)
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
	 * @see Composestar.Core.CpsRepository2.SISpec.SISpecification#addCondition
	 * (Composestar.Core.CpsRepository2.SISpec.Condition)
	 */
	public boolean addCondition(SICondition cond) throws NullPointerException
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
	 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
	 * addFilterModuleBinding
	 * (Composestar.Core.CpsRepository2.SISpec.FilterModuleBinding)
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
	 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
	 * addFilterModuleConstraint
	 * (Composestar.Core.CpsRepository2.SISpec.FilterModuleConstraint)
	 */
	public void addFilterModuleConstraint(Constraint fmc) throws NullPointerException
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
	 * @see Composestar.Core.CpsRepository2.SISpec.SISpecification#addSelector
	 * (Composestar.Core.CpsRepository2.SISpec.Selector)
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
	 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
	 * getAnnotationBindings()
	 */
	public Collection<AnnotationBinding> getAnnotationBindings()
	{
		return Collections.unmodifiableCollection(annotationBindings);
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.SISpec.SISpecification#getCondition
	 * (java.lang.String)
	 */
	public SICondition getCondition(String name)
	{
		if (name == null)
		{
			return null;
		}
		return conditions.get(name);
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.SISpec.SISpecification#getConditions
	 * ()
	 */
	public Collection<SICondition> getConditions()
	{
		return Collections.unmodifiableCollection(conditions.values());
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
	 * getFilterModuleBindings()
	 */
	public Collection<FilterModuleBinding> getFilterModuleBindings()
	{
		return Collections.unmodifiableCollection(filterModuleBindings);
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
	 * getFilterModuleConstraints()
	 */
	public Collection<Constraint> getFilterModuleConstraints()
	{
		return Collections.unmodifiableCollection(filterModuleConstraints);
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.SISpec.SISpecification#getSelector
	 * (java.lang.String)
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
	 * @see Composestar.Core.CpsRepository2.SISpec.SISpecification#getSelectors
	 * ()
	 */
	public Collection<Selector> getSelectors()
	{
		return Collections.unmodifiableCollection(selectors.values());
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
	 * removeAnnotationBinding
	 * (Composestar.Core.CpsRepository2.SISpec.AnnotationBinding)
	 */
	public AnnotationBinding removeAnnotationBinding(AnnotationBinding ab) throws NullPointerException
	{
		if (ab == null)
		{
			throw new NullPointerException();
		}
		if (annotationBindings.remove(ab))
		{
			return ab;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
	 * removeCondition (Composestar.Core.CpsRepository2.SISpec.Condition)
	 */
	public SICondition removeCondition(SICondition cond) throws NullPointerException
	{
		if (cond == null)
		{
			throw new NullPointerException();
		}
		if (conditions.values().remove(cond))
		{
			return cond;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
	 * removeCondition(java.lang.String)
	 */
	public SICondition removeCondition(String name)
	{
		if (name == null)
		{
			return null;
		}
		return conditions.remove(name);
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
	 * removeFilterModuleBinding
	 * (Composestar.Core.CpsRepository2.SISpec.FilterModuleBinding)
	 */
	public FilterModuleBinding removeFilterModuleBinding(FilterModuleBinding fmb) throws NullPointerException
	{
		if (fmb == null)
		{
			throw new NullPointerException();
		}
		if (filterModuleBindings.remove(fmb))
		{
			return fmb;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
	 * removeFilterModuleConstraint
	 * (Composestar.Core.CpsRepository2.SISpec.FilterModuleConstraint)
	 */
	public Constraint removeFilterModuleConstraint(Constraint fmc) throws NullPointerException
	{
		if (fmc == null)
		{
			throw new NullPointerException();
		}
		if (filterModuleConstraints.remove(fmc))
		{
			return fmc;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
	 * removeSelector(Composestar.Core.CpsRepository2.SISpec.Selector)
	 */
	public Selector removeSelector(Selector sel) throws NullPointerException
	{
		if (sel == null)
		{
			throw new NullPointerException();
		}
		if (selectors.values().remove(sel))
		{
			return sel;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
	 * removeSelector(java.lang.String)
	 */
	public Selector removeSelector(String name)
	{
		if (name == null)
		{
			return null;
		}
		return selectors.remove(name);
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
