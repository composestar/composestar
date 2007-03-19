/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: MatchingPatternAST.java
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

import java.util.Vector;

import Composestar.Core.RepositoryImplementation.ContextRepositoryEntity;
import Composestar.Utils.CPSIterator;

public class MatchingPatternAST extends ContextRepositoryEntity
{
	private static final long serialVersionUID = -2971508659284806511L;

	public Vector matchingParts;

	public Vector substitutionParts;

	/**
	 * If true this is a multiple message list. A multiple message list contains
	 * multiple matching and substituion parts. (sequences) When it's not a
	 * multiple message list only matchingParts can contain a list. In the
	 * latter case it's a list of matching parts that individually match instead
	 * of the whole list that should match.
	 */
	public boolean isMessageList;

	/**
	 * 
	 */
	public MatchingPatternAST()
	{
		super();
		matchingParts = new Vector();
		substitutionParts = new Vector();
	}

	public boolean getIsMessageList()
	{
		return isMessageList;
	}

	public void setIsMessageList(boolean isML)
	{
		isMessageList = isML;
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPart
	 */
	public Vector getMatchingParts()
	{
		return matchingParts;
	}

	public java.util.Iterator getMatchingPartsIterator()
	{
		return new CPSIterator(matchingParts);
	}

	/**
	 * @param matchingPartValue
	 */
	public void addMatchingPart(MatchingPartAST matchingPartValue)
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

	public java.util.Iterator getSubstitutionPartsIterator()
	{
		return new CPSIterator(substitutionParts);
	}

	/**
	 * @param substitutionPartValue
	 */
	public void addSubstitutionPart(SubstitutionPartAST substitutionPartValue)
	{
		substitutionParts.addElement(substitutionPartValue);
	}

	public void setMatchingParts(Vector inMatchingParts)
	{
		matchingParts = inMatchingParts;
	}

	public void setSubstitutionParts(Vector inSubstitutionParts)
	{
		substitutionParts = inSubstitutionParts;
	}

	public String asSourceCode()
	{
		StringBuffer sb = new StringBuffer();
		// matching parts
		if (matchingParts.size() > 0)
		{
			if (isMessageList)
			{
				sb.append("#(");
			}
			else if (matchingParts.size() > 1)
			{
				sb.append("{");
			}
			for (int i = 0; i < matchingParts.size(); i++)
			{
				if (i > 0)
				{
					if (isMessageList)
					{
						sb.append("; ");
					}
					else
					{
						sb.append(", ");
					}
				}
				sb.append(((MatchingPartAST) matchingParts.get(i)).asSourceCode());
			}
			if (isMessageList)
			{
				sb.append(")");
			}
			else if (matchingParts.size() > 1)
			{
				sb.append("}");
			}
		}

		// substitution parts
		if (substitutionParts.size() > 0)
		{
			if (sb.length() > 0)
			{
				sb.append(" ");
			}
			if (isMessageList)
			{
				sb.append("#(");
			}
			for (int i = 0; i < substitutionParts.size(); i++)
			{
				if ((i > 0) && isMessageList)
				{
					sb.append("; ");
				}
				sb.append(((SubstitutionPartAST) substitutionParts.get(i)).asSourceCode());
			}
			if (isMessageList)
			{
				sb.append(")");
			}
		}
		return sb.toString();
	}
}
