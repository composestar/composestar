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

import java.io.File;
import java.io.Serializable;

import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.Meta.FileInformation;
import Composestar.Core.CpsRepository2.Meta.SourceInformation;

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

	/**
	 * Optional source information
	 */
	protected SourceInformation srcInfo;

	public LogMessage(Object inMsg)
	{
		message = inMsg;
		// FIXME
	}

	public LogMessage(Object inMsg, Throwable t)
	{
		this(inMsg);
		// FIXME
	}

	public LogMessage(Object inMsg, String inFN, int inLN, int onLine)
	{
		this(inMsg);
		if (inFN != null && !inFN.isEmpty())
		{
			FileInformation fi = new FileInformation(new File(inFN));
			srcInfo = new SourceInformation(fi);
			srcInfo.setLine(inLN);
			srcInfo.setLinePos(onLine);
		}
	}

	public LogMessage(Object inMsg, String inFN, int inLN)
	{
		this(inMsg, inFN, inLN, 0);
	}

	public LogMessage(Object inMsg, SourceInformation sourceInformation)
	{
		this(inMsg);
		srcInfo = sourceInformation;
	}

	public LogMessage(Object inMsg, RepositoryEntity re)
	{
		this(inMsg, re.getSourceInformation());
		repoEntity = re;
	}

	public LogMessage(Object inMsg, LocationProvider lp)
	{
		this(inMsg, lp.getFilename(), lp.getLineNumber(), lp.getLinePosition());
	}

	/**
	 * @return the message
	 */
	public Object getMessage()
	{
		return message;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Utils.Logging.LocationProvider#getFilename()
	 */
	public String getFilename()
	{
		if (srcInfo == null)
		{
			return null;
		}
		return srcInfo.getFilename().toString();
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Utils.Logging.LocationProvider#getLineNumber()
	 */
	public int getLineNumber()
	{
		if (srcInfo == null)
		{
			return 0;
		}
		return srcInfo.getLine();
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Utils.Logging.LocationProvider#getLinePosition()
	 */
	public int getLinePosition()
	{
		if (srcInfo == null)
		{
			return 0;
		}
		return srcInfo.getLinePos();
	}

	/**
	 * @return the associated repository entity
	 */
	public RepositoryEntity getRepositoryEntity()
	{
		return repoEntity;
	}

	/**
	 * @return the source information
	 */
	public SourceInformation getSourceInformation()
	{
		return srcInfo;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return message.toString();
	}
}
