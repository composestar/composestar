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

public class MatchingPartAST extends AbstractPatternAST
{
	private static final long serialVersionUID = 7064979206296470126L;

	public MatchingType matchType;

	public MatchingPartAST()
	{
		super();
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingType
	 */
	public MatchingType getMatchType()
	{
		return matchType;
	}

	/**
	 * @param matchTypeValue
	 */
	public void setMatchType(MatchingType matchTypeValue)
	{
		this.matchType = matchTypeValue;
	}

	public String asSourceCode()
	{
		StringBuffer sb = new StringBuffer();
		String endtoken = "";
		if (matchType instanceof NameMatchingType)
		{
			sb.append("[");
			endtoken = "]";
		}
		else if (matchType instanceof SignatureMatchingType)
		{
			sb.append("<");
			endtoken = ">";
		}
		sb.append(super.asSourceCode());
		sb.append(endtoken);
		return sb.toString();
	}
}
