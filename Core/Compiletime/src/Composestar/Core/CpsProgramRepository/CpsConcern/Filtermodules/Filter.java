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
 * @modelguid {EF28A1E2-BD65-444F-810F-8B5D590B28AA}
 */
public class Filter extends DeclaredRepositoryEntity
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6486690993687288235L;

	/**
	 * extra, reference to type
	 */
	public ConcernReference typeImplementation;

	/**
	 * @modelguid {BA6E0095-9527-4D09-9DBB-C6C6D14F98CF}
	 */
	// public FilterType type; -> get from AST
	public Vector filterElements;

	// public FilterCompOper rightOperator; -> get from AST
	// public Vector parameters; -> get from AST
	public FilterAST filterAST;

	/**
	 * @modelguid {C7DDAADC-9E56-486C-B4B0-8CDF99C09BA9}
	 * @roseuid 401FAA6300CC
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
	 * @roseuid 401FAA6300E1
	 */
	public FilterType getFilterType()
	{
		return filterAST.getFilterType();
	}

	/**
	 * @param typeValue
	 * @roseuid 401FAA6300F4
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
	 * @modelguid {C3A32E86-43B0-40A3-A470-33F577AFCB58}
	 * @roseuid 401FAA6300FF
	 */
	public boolean addFilterElement(FilterElement filterelement)
	{
		filterElements.add(filterelement);
		return true;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElement
	 * @modelguid {62CC6BF1-5B8B-4956-A74C-2627546D8E08}
	 * @roseuid 401FAA630126
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
	 * @modelguid {D5097D6E-9299-4538-A3BB-F43E543C6B30}
	 * @roseuid 401FAA63013A
	 */
	public FilterElement getFilterElement(int index)
	{
		return (FilterElement) filterElements.elementAt(index);
	}

	/**
	 * @return java.util.Iterator
	 * @modelguid {90E85BF8-4998-47B7-A91F-4B319BFA0D10}
	 * @roseuid 401FAA63014E
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
	 * @modelguid {9AF48E0B-1D38-49D0-8FB5-99DCDA06CCDC}
	 * @roseuid 401FAA630158
	 */
	public boolean addParameter(String parameter)
	{
		return filterAST.addParameter(parameter);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.LabeledConcernRe
	 *         ference
	 * @modelguid {739D2466-FF3E-409E-8856-CC86FB2B0114}
	 * @roseuid 401FAA63016D
	 */
	public String removeParameter(int index)
	{
		return filterAST.removeParameter(index);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.LabeledConcernRe
	 *         ference
	 * @modelguid {E5A2DBE4-A124-47F1-B588-CCCD703B19B0}
	 * @roseuid 401FAA630181
	 */
	public String getParameter(int index)
	{
		return filterAST.getParameter(index);
	}

	/**
	 * @return java.util.Iterator
	 * @modelguid {DA253686-4890-45EE-9D5F-4D74644F27F0}
	 * @roseuid 401FAA630195
	 */
	public Iterator getParameterIterator()
	{
		return filterAST.getParameterIterator();
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterCompOpe
	 *         r
	 * @roseuid 402AA7530225
	 */
	public FilterCompOper getRightOperator()
	{
		return filterAST.getRightOperator();
	}

	/**
	 * @param rightOperatorValue
	 * @roseuid 402AA7790310
	 */
	public void setRightOperator(FilterCompOper rightOperatorValue)
	{
		filterAST.setRightOperator(rightOperatorValue);
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference
	 * @roseuid 40ADD67500CA
	 */
	public ConcernReference getTypeImplementation()
	{
		return filterAST.getTypeImplementation();
	}

	/**
	 * @param typeImplementationValue
	 * @roseuid 40ADD68103CA
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

}
