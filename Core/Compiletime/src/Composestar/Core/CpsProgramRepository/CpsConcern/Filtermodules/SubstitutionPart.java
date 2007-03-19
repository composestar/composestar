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

public class SubstitutionPart extends AbstractPattern
{
	private static final long serialVersionUID = -7806329462233206287L;

	public SubstitutionPartAST spAST;

	/**
	 * @roseuid 401FAA6900AD
	 * @deprecated
	 */
	public SubstitutionPart()
	{
		super();
	}

	public SubstitutionPart(SubstitutionPartAST aspAST)
	{
		super(aspAST);
		spAST = aspAST;
		target = spAST.getTarget();
		selector = new MessageSelector(spAST.getSelector());
		selector.setParent(this);
		descriptionFileName = aspAST.getDescriptionFileName();
		descriptionLineNumber = aspAST.getDescriptionLineNumber();
	}
}
