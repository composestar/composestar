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

import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;
import Composestar.Core.RepositoryImplementation.ContextRepositoryEntity;

/**
 * the message selector as used in filter patterns (nb; the term is selector is
 * sometimes used, but this is confusing with the selector used in
 * superimposition)
 */
public class MessageSelector extends ContextRepositoryEntity
{
	private static final long serialVersionUID = -2126714698590614833L;

	public MessageSelectorAST msAST;

	/**
	 * Public constructor is required for serialization
	 * 
	 * @deprecated use MessageSelector(MessageSelectorAST), an empty
	 *             MessageSelectorAST() is safe to use
	 */
	public MessageSelector()
	{
		super();
		// typeList = new Vector();
	}

	public MessageSelector(MessageSelectorAST amsAST)
	{
		super();
		msAST = amsAST;
		descriptionFileName = msAST.getDescriptionFileName();
		descriptionLineNumber = msAST.getDescriptionLineNumber();
	}

	/**
	 * @return java.lang.String
	 */
	public String getName()
	{
		return msAST.getName();
	}

	/**
	 * @param nameValue
	 */
	public void setName(String nameValue)
	{
		msAST.setName(nameValue);
	}

	/**
	 * typeList
	 * 
	 * @param type
	 * @return boolean
	 */
	public boolean addParameterType(ConcernReference type)
	{
		return msAST.addParameterType(type);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference
	 */
	public ConcernReference removeParameterType(int index)
	{
		return msAST.removeParameterType(index);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference
	 */
	public ConcernReference getParameterType(int index)
	{
		return msAST.getParameterType(index);
	}

	/**
	 * @return java.util.Iterator
	 */
	public Iterator getParameterTypeIterator()
	{
		return msAST.getParameterTypeIterator();
	}

	public MessageSelectorAST getMsAST()
	{
		return msAST;
	}
}
