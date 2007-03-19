/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: TypedInstance.java,v 1.3 2004/02/02 19:33:23 vinkes Exp
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

import java.util.Iterator;
import java.util.Vector;

import Composestar.Core.RepositoryImplementation.ContextRepositoryEntity;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.CPSIterator;

public class MatchingPattern extends ContextRepositoryEntity
{
	private static final long serialVersionUID = -84062663527092510L;

	public MatchingPatternAST mpa;

	public Vector matchingParts;

	public Vector substitutionParts;

	/**
	 * @depricated
	 */
	public MatchingPattern()
	{
		super();
		matchingParts = new Vector();
		substitutionParts = new Vector();
	}

	public MatchingPattern(MatchingPatternAST mpAST)
	{
		super();
		mpa = mpAST;
		descriptionFileName = mpa.getDescriptionFileName();
		descriptionLineNumber = mpa.getDescriptionLineNumber();
		matchingParts = new Vector();
		substitutionParts = new Vector();

		Iterator it = mpa.getMatchingPartsIterator();
		while (it.hasNext())
		{
			MatchingPart mp = new MatchingPart((MatchingPartAST) it.next());
			mp.setParent(this);
			matchingParts.add(mp);
			DataStore.instance().addObject(mp);
		}

		it = mpa.getSubstitutionPartsIterator();
		while (it.hasNext())
		{
			SubstitutionPart sp = new SubstitutionPart((SubstitutionPartAST) it.next());
			sp.setParent(this);
			substitutionParts.add(sp);
			DataStore.instance().addObject(sp);
		}
	}

	public boolean getIsMessageList()
	{
		return mpa.getIsMessageList();
	}

	public void setIsMessageList(boolean isML)
	{
		mpa.setIsMessageList(isML);
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPart
	 */
	public Vector getMatchingParts()
	{
		return matchingParts;
	}

	public Iterator getMatchingPartsIterator()
	{
		return new CPSIterator(matchingParts);
	}

	/**
	 * @param matchingPartValue
	 */
	public void addMatchingPart(MatchingPart matchingPartValue)
	{
		matchingParts.addElement(matchingPartValue);
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.SubstitutionP
	 *         art
	 */
	public Vector getSubstitutionParts()
	{
		return substitutionParts;
	}

	public Iterator getSubstitutionPartsIterator()
	{
		return new CPSIterator(substitutionParts);
	}

	/**
	 * @param substitutionPartValue
	 */
	public void addSubstitutionPart(SubstitutionPart substitutionPartValue)
	{
		substitutionParts.addElement(substitutionPartValue);
	}

	public MatchingPatternAST getMpa()
	{
		return mpa;
	}
	
	public String asSourceCode()
	{
		return mpa.asSourceCode();
	}
}
