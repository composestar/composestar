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
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.RepositoryImplementation.DeclaredRepositoryEntity;
import Composestar.Utils.CPSIterator;

/**
 * 
 */
public class Filter extends DeclaredRepositoryEntity
{
	private static final long serialVersionUID = 6486690993687288235L;

	/**
	 * extra, reference to type
	 */
	public ConcernReference typeImplementation;

	/**
	 * 
	 */
	// public FilterType type; -> get from AST
	public Vector filterElements;

	// public FilterCompOper rightOperator; -> get from AST
	// public Vector parameters; -> get from AST
	public FilterAST filterAST;

	/**
	 * @deprecated
	 */
	public Filter()
	{
		super();
		filterElements = new Vector();
	}

	public Filter(FilterAST aFilterAST)
	{
		filterAST = aFilterAST;
		descriptionFileName = aFilterAST.getDescriptionFileName();
		descriptionLineNumber = aFilterAST.getDescriptionLineNumber();

		// create unique Filter Elements
		filterElements = new Vector();
		Iterator fei = filterAST.getFilterElementIterator();
		while (fei.hasNext())
		{
			FilterElement fe = new FilterElement((FilterElementAST) fei.next());
			filterElements.add(fe);
			DataStore.instance().addObject(fe);
			fe.setParent(this);
		}
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType
	 */
	public FilterType getFilterType()
	{
		return filterAST.getFilterType();
	}

	/**
	 * @param typeValue
	 */
	public void setFilterType(FilterType typeValue)
	{
		filterAST.setFilterType(typeValue);
	}

	/**
	 * filterelements
	 * 
	 * @param filterelement
	 * @return boolean
	 */
	public boolean addFilterElement(FilterElement filterelement)
	{
		filterElements.add(filterelement);
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
		return filterAST.addParameter(parameter);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.LabeledConcernRe
	 *         ference
	 */
	public String removeParameter(int index)
	{
		return filterAST.removeParameter(index);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.LabeledConcernRe
	 *         ference
	 */
	public String getParameter(int index)
	{
		return filterAST.getParameter(index);
	}

	/**
	 * @return java.util.Iterator
	 */
	public Iterator getParameterIterator()
	{
		return filterAST.getParameterIterator();
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterCompOpe
	 *         r
	 */
	public FilterCompOper getRightOperator()
	{
		return filterAST.getRightOperator();
	}

	/**
	 * @param rightOperatorValue
	 */
	public void setRightOperator(FilterCompOper rightOperatorValue)
	{
		filterAST.setRightOperator(rightOperatorValue);
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference
	 */
	public ConcernReference getTypeImplementation()
	{
		return filterAST.getTypeImplementation();
	}

	/**
	 * @param typeImplementationValue
	 */
	public void setTypeImplementation(ConcernReference typeImplementationValue)
	{
		filterAST.setTypeImplementation(typeImplementationValue);
	}

	public String getName()
	{
		return filterAST.getName();
	}

	public void setName(String nameValue)
	{
		filterAST.setName(nameValue);
	}

	public String getDescriptionFileName()
	{
		return filterAST.getDescriptionFileName();
	}

	public int getDescriptionLineNumber()
	{
		return filterAST.getDescriptionLineNumber();
	}

	public void setDescriptionFileName(String newFileName)
	{
		filterAST.setDescriptionFileName(newFileName);
	}

	public void setDescriptionLineNumber(int newLineNumber)
	{
		filterAST.setDescriptionLineNumber(newLineNumber);
	}

	public Vector getParameters()
	{
		return filterAST.getParameters();
	}

	public FilterAST getFilterAST()
	{
		return filterAST;
	}

	public String asSourceCode()
	{
		return filterAST.asSourceCode();
	}
}
