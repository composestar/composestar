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
 *
 * @author Composer
 */
public class MetricAppender extends AppenderSkeleton
{
	protected int fatals;
	protected int errors;
	protected int warnings;
	
	public MetricAppender()
	{
	}
	
	public void reset()
	{
		fatals = 0;
		errors = 0;
		warnings = 0;
	}
	
	public int numFatals()
	{
		return fatals;
	}
	
	public int numErrors()
	{
		return errors;
	}
	
	public int numWarnings()
	{
		return warnings;
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
	 */
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

	/* (non-Javadoc)
	 * @see org.apache.log4j.AppenderSkeleton#close()
	 */
	public void close()
	{
		reset();
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.AppenderSkeleton#requiresLayout()
	 */
	public boolean requiresLayout()
	{
		return false;
	}

}
