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

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

/**
 * The LoggerFactory used for log4j loggers (used by the compose* compiletime).
 * This is a dual LoggerFactory, it's a factory for normal creation of log4j
 * loggers but also serves as factory to create the proper ILogger instance for
 * the SafeLogger class.
 * 
 * @author Michiel Hendriks
 */
public class Log4jLoggerFactory implements LoggerFactory, ILoggerFactory
{
	public Log4jLoggerFactory()
	{
		SafeLogger.setFactory(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.spi.LoggerFactory#makeNewLoggerInstance(java.lang.String)
	 */
	public Logger makeNewLoggerInstance(String name)
	{
		return new CPSLogger(name);
	}

	/**
	 * Will be called by SafeLogger
	 */
	public ILogger createLogger(String name)
	{
		return CPSLogger.getCPSLogger(name);
	}

}
