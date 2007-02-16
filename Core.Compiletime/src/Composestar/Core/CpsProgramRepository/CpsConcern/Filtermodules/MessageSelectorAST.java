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
import Composestar.Core.RepositoryImplementation.ContextRepositoryEntity;
import Composestar.Utils.CPSIterator;

/**
 * the message selector as used in filter patterns (nb; the term is selector is
 * sometimes used, but this is confusing with the selector used in
 * superimposition)
 */
public class MessageSelectorAST extends ContextRepositoryEntity
{
	private static final long serialVersionUID = -4560974741705816846L;

	/**
	 * 
	 */
	public String name;

	public Vector typeList;

	/**
	 * 
	 */
	public MessageSelectorAST()
	{
		super();
		typeList = new Vector();
	}

	/**
	 * @return java.lang.String
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param nameValue
	 */
	public void setName(String nameValue)
	{
		this.name = nameValue;
	}

	/**
	 * typeList
	 * 
	 * @param type
	 * @return boolean
	 */
	public boolean addParameterType(ConcernReference type)
	{
		typeList.addElement(type);
		return true;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference
	 */
	public ConcernReference removeParameterType(int index)
	{
		Object o = typeList.elementAt(index);
		typeList.removeElementAt(index);
		return (ConcernReference) o;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference
	 */
	public ConcernReference getParameterType(int index)
	{
		return (ConcernReference) typeList.elementAt(index);
	}

	/**
	 * @return java.util.Iterator
	 */
	public Iterator getParameterTypeIterator()
	{
		return new CPSIterator(typeList);
	}
}
