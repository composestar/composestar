/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Utils.Logging.Log4j;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Keeps counters for the number of warning/error/fatal log entries
 */
public class MetricAppender extends AppenderSkeleton
{
	/**
	 * Fatal count
	 */
	protected int fatals;

	/**
	 * Error count
	 */
	protected int errors;

	/**
	 * Warning count
	 */
	protected int warnings;

	public MetricAppender()
	{}

	/**
	 * Reset the counters
	 */
	public void reset()
	{
		fatals = 0;
		errors = 0;
		warnings = 0;
	}

	/**
	 * @return the number of fatal log entries
	 */
	public int numFatals()
	{
		return fatals;
	}

	/**
	 * @return the number of error log entries
	 */
	public int numErrors()
	{
		return errors;
	}

	/**
	 * @return the number of warning log entries
	 */
	public int numWarnings()
	{
		return warnings;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent
	 * )
	 */
	@Override
	protected void append(LoggingEvent event)
	{
		Level l = event.getLevel();
		if (l.equals(Level.FATAL))
		{
			fatals++;
		}
		else if (l.equals(Level.ERROR))
		{
			errors++;
		}
		else if (l.equals(Level.WARN))
		{
			warnings++;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.log4j.AppenderSkeleton#close()
	 */
	@Override
	public void close()
	{
		reset();
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.log4j.AppenderSkeleton#requiresLayout()
	 */
	@Override
	public boolean requiresLayout()
	{
		return false;
	}

}
