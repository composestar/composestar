/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Utils.Logging;

/**
 * An logger creator class that produces the most suitable logger for the
 * current environment. For compiletime this should be an Log4jLogger and during
 * runtime this would be an RuntimeLogger so that there is no dependency on
 * Log4j. Only classes that are used at runtime and that need logging support
 * should use SafeLogger to construct the logger.
 * 
 * @author Michiel Hendriks
 */
public class SafeLogger
{
	private static ILoggerFactory factory = new RuntimeLoggerFactory();

	/**
	 * Create the most suitable logger for the current environment
	 * 
	 * @param name
	 * @return
	 */
	public static ILogger getILogger(String name)
	{
		return factory.createLogger(name);
	}

	/**
	 * Set the ILogger factory
	 * 
	 * @param newFactory
	 */
	public static void setFactory(ILoggerFactory newFactory)
	{
		factory = newFactory;
	}
}
