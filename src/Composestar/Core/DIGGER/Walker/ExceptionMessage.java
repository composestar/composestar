/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.DIGGER.Walker;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.DIGGER.NOBBIN;
import Composestar.Core.DIGGER.Graph.AbstractConcernNode;
import Composestar.Core.DIGGER.Graph.CondMatchEdge;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.RepositoryImplementation.RepositoryEntity;

/**
 * Exception message
 * 
 * @author Michiel Hendriks
 */
public class ExceptionMessage extends Message
{
	protected static final ExceptionMessage instance = new ExceptionMessage();

	public static final ExceptionMessage getExceptionMessage()
	{
		return instance;
	}

	private ExceptionMessage()
	{
		super(null, null);
	}

	public Concern getConcern()
	{
		return null;
	}

	public void setConcernNode(AbstractConcernNode innode)
	{}

	public void setSelector(String inval)
	{}

	public void setCertainty(int inval)
	{}

	public void setRecursive(boolean inval)
	{}

	public void setRE(RepositoryEntity inRe)
	{}

	public void addClone(Message clone)
	{}

	public boolean matches(CondMatchEdge edge) throws ModuleException
	{
		throw new ModuleException("Exception Message can not be matched to", NOBBIN.MODULE_NAME);
	}

	public String toString()
	{
		return "ComposeStarException";
	}
}
