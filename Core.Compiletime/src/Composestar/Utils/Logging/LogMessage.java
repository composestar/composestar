/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Utils.Logging;

import java.io.Serializable;

import Composestar.Core.RepositoryImplementation.RepositoryEntity;

/**
 * A message object to combine a message and source file information.
 * 
 * @author Michiel Hendriks
 */
public class LogMessage implements LocationProvider, Serializable
{
	/**
	 * The message object, this is usually a string but could also be an object
	 * that produces a human readable string
	 */
	protected Object message;

	/**
	 * Optionally linked repository entity
	 */
	protected RepositoryEntity repoEntity;

	protected String sourceFilename = "";

	protected int sourceLine;

	public LogMessage(Object inMsg)
	{
		message = inMsg;
		if (message instanceof LocationProvider)
		{
			sourceFilename = ((LocationProvider) message).getFilename();
			sourceLine = ((LocationProvider) message).getLineNumber();
		}
	}

	public LogMessage(Object inMsg, Throwable t)
	{
		this(inMsg);
		if (t instanceof LocationProvider)
		{
			sourceFilename = ((LocationProvider) t).getFilename();
			sourceLine = ((LocationProvider) t).getLineNumber();
		}
	}

	public LogMessage(Object inMsg, String inFN, int inLN)
	{
		this(inMsg);
		sourceFilename = inFN;
		sourceLine = inLN;
	}

	public LogMessage(Object inMsg, RepositoryEntity re)
	{
		this(inMsg, re.getDescriptionFileName(), re.getDescriptionLineNumber());
		repoEntity = re;
	}

	public LogMessage(Object inMsg, LocationProvider lp)
	{
		this(inMsg, lp.getFilename(), lp.getLineNumber());
	}

	public Object getMessage()
	{
		return message;
	}

	public String getFilename()
	{
		return sourceFilename;
	}

	public int getLineNumber()
	{
		return sourceLine;
	}

	public RepositoryEntity getRepositoryEntity()
	{
		return repoEntity;
	}

	public String toString()
	{
		return message.toString();
	}
}
