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

public class LabeledConcernReference extends ConcernReference
{
	public String label;

	/**
	 * @roseuid 404C4B6800F5
	 */
	public LabeledConcernReference()
	{
		super();
	}

	/**
	 * @return java.lang.String
	 * @roseuid 405041A7039B
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * @param labelValue
	 * @roseuid 405041AB0292
	 */
	public void setLabel(String labelValue)
	{
		this.label = labelValue;
	}
}
