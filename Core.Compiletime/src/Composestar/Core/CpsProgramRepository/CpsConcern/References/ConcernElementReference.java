/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.References;

public class ConcernElementReference extends Reference
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1009972111299460775L;

	public String concern;

	/**
	 * @roseuid 401FAA5701DD
	 */
	public ConcernElementReference()
	{
		super();
	}

	/**
	 * @return java.lang.String
	 * @roseuid 401FAA5701DE
	 */
	public String getConcern()
	{
		return concern;
	}

	/**
	 * @param concernValue
	 * @roseuid 401FAA5701E7
	 */
	public void setConcern(String concernValue)
	{
		this.concern = concernValue;
		this.updateRepositoryReference();
	}

	public String getQualifiedName()
	{
		StringBuffer fname = new StringBuffer();
		int i;

		for (i = 0; i < pack.size(); i++)
		{
			fname.append(pack.elementAt(i));
			fname.append(".");
		}
		fname.append(concern);
		fname.append('.');
		fname.append(name);
		return fname.toString();
	}
}