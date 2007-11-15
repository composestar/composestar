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
	private static final long serialVersionUID = 6361259262102454445L;

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

	protected int sourceLinePos;

	public LogMessage(Object inMsg)
	{
		message = inMsg;
		if (message instanceof LocationProvider)
		{
			sourceFilename = ((LocationProvider) message).getFilename();
			sourceLine = ((LocationProvider) message).getLineNumber();
			sourceLinePos = ((LocationProvider) message).getLinePosition();
		}
	}

	public LogMessage(Object inMsg, Throwable t)
	{
		this(inMsg);
		if (t instanceof LocationProvider)
		{
			sourceFilename = ((LocationProvider) t).getFilename();
			sourceLine = ((LocationProvider) t).getLineNumber();
			sourceLinePos = ((LocationProvider) t).getLinePosition();
		}
	}

	public LogMessage(Object inMsg, String inFN, int inLN, int onLine)
	{
		this(inMsg);
		sourceFilename = inFN;
		sourceLine = inLN;
		sourceLinePos = onLine;
	}

	public LogMessage(Object inMsg, String inFN, int inLN)
	{
		this(inMsg, inFN, inLN, 0);
	}

	public LogMessage(Object inMsg, RepositoryEntity re)
	{
		this(inMsg, re.getDescriptionFileName(), re.getDescriptionLineNumber(), re.getDescriptionLinePosition());
		repoEntity = re;
	}

	public LogMessage(Object inMsg, LocationProvider lp)
	{
		this(inMsg, lp.getFilename(), lp.getLineNumber(), lp.getLinePosition());
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

	public int getLinePosition()
	{
		return sourceLinePos;
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
