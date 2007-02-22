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
 * @modelguid {45C1BA7F-52B3-49C6-9330-77B3065146D5}
 */
public class SuperImposition extends DeclaredRepositoryEntity
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2244055769354841823L;

	/**
	 * stores filtermodule bindings
	 */
	private Vector filterModules;

	private Vector annotationBindings;

	private Vector methods;

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
		setName("superimposition");
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
	 * @modelguid {15AE1DD2-DD38-4C01-8685-C12B6F6527C0}
	 * @roseuid 401FAA69017F
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
	 * @modelguid {E886BCB7-BDDD-40A5-AA50-A18E4CB2B92C}
	 * @roseuid 401FAA690193
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
	 * @modelguid {39C3F6DF-CF1A-4B5A-8C27-55B1E0DD988A}
	 * @roseuid 401FAA69019E
	 */
	public MethodBinding getMethodBinding(int index)
	{
		return (MethodBinding) methods.elementAt(index);
	}

	/**
	 * @return java.util.Iterator
	 * @modelguid {57BD82D8-B981-42C3-9065-2C5AD37CEC96}
	 * @roseuid 401FAA6901B2
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
	 * @modelguid {6E53A7B6-5D04-485B-8C63-7263B4BF45ED}
	 * @roseuid 401FAA6901C5
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
	 * @modelguid {76EEDDD5-DCF1-4459-A9B0-6EE021EA3E59}
	 * @roseuid 401FAA6901D0
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
	 * @modelguid {8EE9338A-C983-4593-B31D-8214EE6D5ED0}
	 * @roseuid 401FAA6901E4
	 */
	public ConditionBinding getConditionBinding(int index)
	{
		return (ConditionBinding) conditions.elementAt(index);
	}

	/**
	 * @return java.util.Iterator
	 * @modelguid {1A2848B6-B013-4A7F-B526-78E7C3DB5013}
	 * @roseuid 401FAA6901F8
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
