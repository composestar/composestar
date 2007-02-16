/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Utils.Logging.Log4j;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;

import Composestar.Utils.Logging.LocationProvider;

/**
 * Custom LoggingEvent class to keep track of file information of source entries
 * 
 * @author Michiel Hendriks
 */
public class CPSLoggingEvent extends LoggingEvent implements LocationProvider
{
	/**
	 * Source filename where this event happend. This is not the program source
	 * location.
	 */
	protected String filename;

	/**
	 * The line number
	 */
	protected int lineNumber;

	public CPSLoggingEvent(String fqnOfCategoryClass, Category logger, Priority level, Object message,
			Throwable throwable)
	{
		super(fqnOfCategoryClass, logger, level, message, throwable);
		if (message instanceof LocationProvider)
		{
			filename = ((LocationProvider) message).getFilename();
			lineNumber = ((LocationProvider) message).getLineNumber();
		}
		else if (throwable instanceof LocationProvider)
		{
			filename = ((LocationProvider) throwable).getFilename();
			lineNumber = ((LocationProvider) throwable).getLineNumber();
		}
	}

	public CPSLoggingEvent(String fqnOfCategoryClass, Category logger, long timeStamp, Priority level, Object message,
			Throwable throwable)
	{
		super(fqnOfCategoryClass, logger, timeStamp, level, message, throwable);
		if (message instanceof LocationProvider)
		{
			filename = ((LocationProvider) message).getFilename();
			lineNumber = ((LocationProvider) message).getLineNumber();
		}
		else if (throwable instanceof LocationProvider)
		{
			filename = ((LocationProvider) throwable).getFilename();
			lineNumber = ((LocationProvider) throwable).getLineNumber();
		}
	}

	public void setFilename(String inname)
	{
		filename = inname;
	}

	/**
	 * Return the filename of the input source where this event occurred
	 */
	public String getFilename()
	{
		return filename;
	}

	public void setLineNumber(int inval)
	{
		lineNumber = inval;
	}

	/**
	 * The line in the input source
	 */
	public int getLineNumber()
	{
		return lineNumber;
	}
}
