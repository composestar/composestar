/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

import java.util.Iterator;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;
import Composestar.Core.RepositoryImplementation.DeclaredRepositoryEntity;
import Composestar.Utils.CPSIterator;

public class FilterAST extends DeclaredRepositoryEntity
{

	private static final long serialVersionUID = 2302850129520568577L;

	/**
	 * extra, reference to type
	 */
	public ConcernReference typeImplementation;

	/**
	 * 
	 */
	public FilterType type;

	public Vector filterElements;

	public FilterCompOper rightOperator;

	public Vector parameters;

	public FilterAST()
	{
		super();
		filterElements = new Vector();
		parameters = new Vector();
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType
	 */
	public FilterType getFilterType()
	{
		return type;
	}

	/**
	 * @param typeValue
	 */
	public void setFilterType(FilterType typeValue)
	{
		this.type = typeValue;
	}

	/**
	 * filterelements
	 * 
	 * @param filterelement
	 * @return boolean
	 */
	public boolean addFilterElement(FilterElementAST filterelement)
	{
		filterElements.addElement(filterelement);
		return true;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElement
	 */
	public FilterElement removeFilterElement(int index)
	{
		Object o = filterElements.elementAt(index);
		filterElements.removeElementAt(index);
		return (FilterElement) o;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElement
	 */
	public FilterElement getFilterElement(int index)
	{
		return (FilterElement) filterElements.elementAt(index);
	}

	/**
	 * @return java.util.Iterator
	 */
	public Iterator getFilterElementIterator()
	{
		return new CPSIterator(filterElements);
	}

	/**
	 * parameters
	 * 
	 * @param parameter
	 * @return boolean
	 */
	public boolean addParameter(String parameter)
	{
		parameters.addElement(parameter);
		return true;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.LabeledConcernRe
	 *         ference
	 */
	public String removeParameter(int index)
	{
		Object o = parameters.elementAt(index);
		parameters.removeElementAt(index);
		return (String) o;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.LabeledConcernRe
	 *         ference
	 */
	public String getParameter(int index)
	{
		return (String) parameters.elementAt(index);
	}

	/**
	 * @return java.util.Iterator
	 */
	public Iterator getParameterIterator()
	{
		return new CPSIterator(parameters);
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterCompOpe
	 *         r
	 */
	public FilterCompOper getRightOperator()
	{
		return rightOperator;
	}

	/**
	 * @param rightOperatorValue
	 */
	public void setRightOperator(FilterCompOper rightOperatorValue)
	{
		this.rightOperator = rightOperatorValue;
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference
	 */
	public ConcernReference getTypeImplementation()
	{
		return typeImplementation;
	}

	/**
	 * @param typeImplementationValue
	 */
	public void setTypeImplementation(ConcernReference typeImplementationValue)
	{
		this.typeImplementation = typeImplementationValue;
	}

	public Vector getParameters()
	{
		return parameters;
	}
}
