/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition;
import Composestar.Core.RepositoryImplementation.DeclaredRepositoryEntity;
import Composestar.Utils.CPSIterator;

/**
 * 
 */
public class SuperImposition extends DeclaredRepositoryEntity
{
	private static final long serialVersionUID = -2244055769354841823L;

	/**
	 * stores filtermodule bindings
	 */
	private Vector filterModules;

	private Vector annotationBindings;

	/**
	 * @deprecated
	 */
	private Vector methods;

	/**
	 * @deprecated
	 */
	public Vector conditions;

	public Vector selectors;

	/**
	 * Contains all declared filter module conditions
	 */
	public HashMap filterModuleConditions;

	/**
	 * @modelguid {9C81F007-8BBA-44D2-9A5D-8A616EE76A04}
	 * @roseuid 401FAA690139
	 */
	public SuperImposition()
	{
		super();
		filterModules = new Vector();
		annotationBindings = new Vector();
		methods = new Vector();
		conditions = new Vector();
		selectors = new Vector();
		filterModuleConditions = new HashMap();
		setName("@superimposition");
	}

	/**
	 * filterModules
	 * 
	 * @param filtermodule
	 * @return boolean
	 * @modelguid {34373AD7-BAC6-4643-8318-22593357A6E2}
	 * @roseuid 401FAA69013A
	 */
	public boolean addFilterModuleBinding(FilterModuleBinding filtermodule)
	{
		filterModules.addElement(filtermodule);
		return true;
	}

	public void addAnnotationBinding(AnnotationBinding annotBinding)
	{
		annotationBindings.addElement(annotBinding);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.FilterModul
	 *         eBinding
	 * @modelguid {9F8BCB43-9416-42A8-BCB1-1B18BC3BCEB9}
	 * @roseuid 401FAA69014D
	 */
	public FilterModuleBinding removeFilterModuleBinding(int index)
	{
		Object o = filterModules.elementAt(index);
		filterModules.removeElementAt(index);
		return (FilterModuleBinding) o;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.FilterModul
	 *         eBinding
	 * @modelguid {EF1E29C6-615C-434B-B53B-F7BE0650A488}
	 * @roseuid 401FAA690161
	 */
	public FilterModuleBinding getFilterModuleBinding(int index)
	{
		return (FilterModuleBinding) filterModules.elementAt(index);
	}

	/**
	 * @return java.util.Iterator
	 * @modelguid {34ACBB9B-BA69-4A62-8FDE-4F16A6883C6E}
	 * @roseuid 401FAA69016C
	 */
	public Iterator getFilterModuleBindingIterator()
	{
		return new CPSIterator(filterModules);
	}

	/**
	 * @return vector (.NET doesn't allow Collection) of AnnotationBinding
	 *         objects attached to this superimposition part
	 */
	public Vector getAnnotationBindings()
	{
		return annotationBindings;
	}

	/**
	 * methods
	 * 
	 * @param mehod
	 * @return boolean
	 * @deprecated
	 */
	public boolean addMethodBinding(MethodBinding mehod)
	{
		methods.addElement(mehod);
		return true;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.MethodBindi
	 *         ng
	 * @deprecated
	 */
	public MethodBinding removeMehodBinding(int index)
	{
		Object o = methods.elementAt(index);
		methods.removeElementAt(index);
		return (MethodBinding) o;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.MethodBindi
	 *         ng
	 * @deprecated
	 */
	public MethodBinding getMethodBinding(int index)
	{
		return (MethodBinding) methods.elementAt(index);
	}

	/**
	 * @return java.util.Iterator
	 * @deprecated
	 */
	public Iterator getMethodBindingIterator()
	{
		return new CPSIterator(methods);
	}

	/**
	 * conditions
	 * 
	 * @param condition
	 * @return boolean
	 * @deprecated
	 */
	public boolean addConditionBinding(ConditionBinding condition)
	{
		conditions.addElement(condition);
		return true;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.ConditionBi
	 *         nding
	 * @deprecated
	 */
	public ConditionBinding removeConditionBinding(int index)
	{
		Object o = conditions.elementAt(index);
		conditions.removeElementAt(index);
		return (ConditionBinding) o;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.ConditionBi
	 *         nding
	 * @deprecated
	 */
	public ConditionBinding getConditionBinding(int index)
	{
		return (ConditionBinding) conditions.elementAt(index);
	}

	/**
	 * @return java.util.Iterator
	 * @deprecated
	 */
	public Iterator getConditionBindingIterator()
	{
		return new CPSIterator(conditions);
	}

	/**
	 * selectors
	 * 
	 * @param selector
	 * @return boolean
	 * @modelguid {459C1406-50D3-47EF-863B-F5131441796B}
	 * @roseuid 401FAA69020B
	 */
	public boolean addSelectorDefinition(SelectorDefinition selector)
	{
		selectors.addElement(selector);
		return true;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SelectorDef
	 *         inition
	 * @modelguid {D6F8BE91-84D2-44EA-B5AC-8C58EEF5FAE3}
	 * @roseuid 401FAA69021F
	 */
	public SelectorDefinition removeSelectorDefinition(int index)
	{
		Object o = selectors.elementAt(index);
		selectors.removeElementAt(index);
		return (SelectorDefinition) o;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SelectorDef
	 *         inition
	 * @modelguid {205D9195-DE7B-4956-BCDF-C99581DF1979}
	 * @roseuid 401FAA690233
	 */
	public SelectorDefinition getSelectorDefinition(int index)
	{
		return (SelectorDefinition) selectors.elementAt(index);
	}

	public SelectorDefinition getSelectorDefinitionByName(String name)
	{
		for (int i = 0; i < selectors.size(); i++)
		{
			if (((SelectorDefinition) selectors.get(i)).getName().equals(name))
			{
				return (SelectorDefinition) selectors.get(i);
			}
		}
		return null;
	}

	/**
	 * @return java.util.Iterator
	 * @modelguid {62E0EDB0-5C3C-40DA-9531-88230B942DD4}
	 * @roseuid 401FAA690247
	 */
	public Iterator getSelectorIterator()
	{
		return new CPSIterator(selectors);
	}

	/**
	 * Adds a filter module condition.
	 * 
	 * @param condition The filter module condition to add.
	 * @return <code>false</code> if and only if the SuperImposition already
	 *         contains a condition with the same name
	 */
	public boolean addFilterModuleCondition(Condition condition)
	{
		if (filterModuleConditions.containsKey(condition.getName()))
		{
			return false;
		}
		else
		{
			filterModuleConditions.put(condition.getName(), condition);
			return true;
		}
	}

	/**
	 * @return An Iterator to iterate over the filter module conditions.
	 */
	public Iterator getFilterModuleConditions()
	{
		return filterModuleConditions.entrySet().iterator();
	}

	/**
	 * Returns the filter module condition corresponding with the identifier, or
	 * <code>null</code> if no such filter module condition is present.
	 * 
	 * @param identifier
	 * @return
	 */
	public Condition getFilterModuleCondition(String identifier)
	{
		return (Condition) filterModuleConditions.get(identifier);
	}

	/**
	 * Removes a filter module condition.
	 * 
	 * @param condition The filter module condition to remove.
	 * @return true when this SuperImposition contained the filter module
	 *         condition.
	 */
	public boolean removeFilterModuleCondition(Condition condition)
	{
		if (filterModuleConditions.containsValue(condition))
		{
			filterModuleConditions.remove(condition.getName());
			return true;
		}
		else
		{
			return false;
		}
	}
}
