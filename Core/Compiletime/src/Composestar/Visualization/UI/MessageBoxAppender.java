/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Visualization.UI;

import java.awt.Frame;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.varia.LevelRangeFilter;

/**
 * Shows a message box on certain log4j events
 * 
 * @author Michiel Hendriks
 */
public class MessageBoxAppender extends AppenderSkeleton
{
	protected Frame owner;
	
	public MessageBoxAppender(Frame inOwner)
	{
		LevelRangeFilter flt = new LevelRangeFilter();
		flt.setLevelMin(Level.WARN);
		flt.setLevelMax(Level.FATAL);
		flt.setAcceptOnMatch(true);
		addFilter(flt);
		owner = inOwner;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
	 */
	@Override
	protected void append(LoggingEvent event)
	{
		new Log4jMessage(owner, event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.AppenderSkeleton#close()
	 */
	@Override
	public void close()
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.AppenderSkeleton#requiresLayout()
	 */
	@Override
	public boolean requiresLayout()
	{
		return false;
	}

}
