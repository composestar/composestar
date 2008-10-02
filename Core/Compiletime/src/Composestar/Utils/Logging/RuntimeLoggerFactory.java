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

import java.util.HashMap;
import java.util.Map;

/**
 * Factory to produce runtime loggers
 * 
 * @author Michiel Hendriks
 */
public class RuntimeLoggerFactory implements ILoggerFactory
{
	/**
	 * Map of logger name to ILogger instance
	 */
	private Map loggers = new HashMap();

	private static Class loggerClass = RuntimeLogger.class;

	/**
	 * Set the loggerClass to use, must be a RuntimeLogger subclass.
	 * 
	 * @param newLoggerClass
	 */
	public static boolean setLoggerClass(Class newLoggerClass)
	{
		if ((newLoggerClass != null) && newLoggerClass.isAssignableFrom(RuntimeLogger.class))
		{
			loggerClass = newLoggerClass;
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Utils.Logging.ILoggerFactory#createLogger(java.lang.String)
	 */
	public ILogger createLogger(String name)
	{
		if (loggers.containsKey(name))
		{
			return (ILogger) loggers.get(name);
		}
		RuntimeLogger logger;
		try
		{
			logger = (RuntimeLogger) loggerClass.newInstance();
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
			return null;
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
			return null;
		}
		logger.name = name;
		loggers.put(name, logger);
		return logger;
	}

}
