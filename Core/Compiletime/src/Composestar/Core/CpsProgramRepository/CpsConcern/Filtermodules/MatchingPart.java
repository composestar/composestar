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

public class MatchingPart extends AbstractPattern
{
	private static final long serialVersionUID = -7242696850818180538L;

	// public MatchingType matchType; -> get from MatchingPartAST
	public MatchingPartAST mpa;

	/**
	 * @deprecated
	 */
	public MatchingPart()
	{
		super();
	}

	public MatchingPart(MatchingPartAST abstr)
	{
		super(abstr);
		mpa = abstr;
		descriptionFileName = mpa.getDescriptionFileName();
		descriptionLineNumber = mpa.getDescriptionLineNumber();
		target = mpa.getTarget();
		MessageSelectorAST msAST = mpa.getSelector();
		if (msAST instanceof ParameterizedMessageSelectorAST)
		{
			selector = new ParameterizedMessageSelector(msAST);
			selector.setParent(this);
		}
		else
		{
			selector = new MessageSelector(msAST);
			selector.setParent(this);
		}
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingType
	 */
	public MatchingType getMatchType()
	{
		return mpa.getMatchType();
	}

	/**
	 * @param matchTypeValue
	 */
	public void setMatchType(MatchingType matchTypeValue)
	{
		mpa.setMatchType(matchTypeValue);
	}

	public MatchingPartAST getMpa()
	{
		return mpa;
	}
}
